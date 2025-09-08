/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package v4.listPeriodSummaries

import shared.connectors.ConnectorSpec
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v4.listPeriodSummaries.def1.model.request.Def1_ListPeriodSummariesRequestData
import v4.listPeriodSummaries.def1.model.response.{Def1_ListPeriodSummariesResponse, Def1_PeriodDetails}
import v4.listPeriodSummaries.model.request.ListPeriodSummariesRequestData
import v4.listPeriodSummaries.model.response.{ListPeriodSummariesResponse, PeriodDetails}

import scala.concurrent.Future

class ListPeriodSummariesConnectorSpec extends ConnectorSpec {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"
  val tysTaxYear: String = "2024-25"
  val taxYear: String    = "2022-23"

  val response: ListPeriodSummariesResponse[PeriodDetails] = Def1_ListPeriodSummariesResponse(
    Seq(
      Def1_PeriodDetails(
        "2020-01-01_2020-01-01",
        "2020-01-01",
        "2020-01-01"
        //        Some("2020-01-02") // To be reinstated, see MTDSA-15595
      ))
  )

  trait Test { self: ConnectorTest =>

    protected val connector: ListPeriodSummariesConnector = new ListPeriodSummariesConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

    protected def request(nino: Nino, businessId: BusinessId, taxYear: TaxYear): ListPeriodSummariesRequestData =
      Def1_ListPeriodSummariesRequestData(nino, businessId, taxYear)

  }

  "connector" must {

    "send a request and return a body for a TYS year" in new IfsTest with Test {
      val outcome: Right[Nothing, ResponseWrapper[ListPeriodSummariesResponse[PeriodDetails]]] = Right(ResponseWrapper(correlationId, response))
      willGet(url"$baseUrl/income-tax/${TaxYear.fromMtd(tysTaxYear).asTysDownstream}/$nino/self-employments/$businessId/periodic-summaries")
        .returns(Future.successful(outcome))

      await(connector.listPeriodSummaries(request(Nino(nino), BusinessId(businessId), TaxYear.fromMtd(tysTaxYear)))) shouldBe outcome
    }

    "send a request and return a body for a non TYS year" in new IfsTest with Test {

      val outcome: Right[Nothing, ResponseWrapper[ListPeriodSummariesResponse[PeriodDetails]]] = Right(ResponseWrapper(correlationId, response))
      willGet(url"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries")
        .returns(Future.successful(outcome))

      await(connector.listPeriodSummaries(request(Nino(nino), BusinessId(businessId), TaxYear.fromMtd(taxYear)))) shouldBe outcome
    }
  }

}
