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

package v3.connectors

import api.connectors.{ConnectorSpec, DownstreamOutcome}
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.outcomes.ResponseWrapper
import v3.models.request.listPeriodSummaries.ListPeriodSummariesRequestData
import v3.models.response.listPeriodSummaries.{ListPeriodSummariesResponse, PeriodDetails}

import scala.concurrent.Future

class ListPeriodSummariesConnectorSpec extends ConnectorSpec {

  private val nino       = "AA123456A"
  private val businessId = "XAIS12345678910"
  private val tysTaxYear = "2024-25"
  private val taxYear    = "2022-23"

  private val response = ListPeriodSummariesResponse(
    List(
      PeriodDetails(
        "2020-01-01_2020-01-01",
        "2020-01-01",
        "2020-01-01"
//        Some("2020-01-02") // To be reinstated, see MTDSA-15595
      ))
  )

  "connector" must {
    "send a request and return a body" in new DesTest with Test {
      val outcome: Right[Nothing, ResponseWrapper[ListPeriodSummariesResponse[PeriodDetails]]] = Right(ResponseWrapper(correlationId, response))

      willGet(s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries")
        .returns(Future.successful(outcome))

      val result: DownstreamOutcome[ListPeriodSummariesResponse[PeriodDetails]] =
        await(connector.listPeriodSummaries(request(Nino(nino), BusinessId(businessId), None)))

      result shouldBe outcome
    }

    "send a request and return a body for a TYS year" in new TysIfsTest with Test {
      val outcome: Right[Nothing, ResponseWrapper[ListPeriodSummariesResponse[PeriodDetails]]] = Right(ResponseWrapper(correlationId, response))

      willGet(s"$baseUrl/income-tax/${TaxYear.fromMtd(tysTaxYear).asTysDownstream}/$nino/self-employments/$businessId/periodic-summaries")
        .returns(Future.successful(outcome))

      val result: DownstreamOutcome[ListPeriodSummariesResponse[PeriodDetails]] =
        await(connector.listPeriodSummaries(request(Nino(nino), BusinessId(businessId), Some(TaxYear.fromMtd(tysTaxYear)))))

      result shouldBe outcome
    }

    "send a request and return a body for a non TYS year" in new DesTest with Test {
      val outcome: Right[Nothing, ResponseWrapper[ListPeriodSummariesResponse[PeriodDetails]]] = Right(ResponseWrapper(correlationId, response))

      willGet(s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries")
        .returns(Future.successful(outcome))

      val result: DownstreamOutcome[ListPeriodSummariesResponse[PeriodDetails]] =
        await(connector.listPeriodSummaries(request(Nino(nino), BusinessId(businessId), Some(TaxYear.fromMtd(taxYear)))))

      result shouldBe outcome
    }
  }

  trait Test {
    _: ConnectorTest =>

    protected val connector: ListPeriodSummariesConnector = new ListPeriodSummariesConnector(mockHttpClient, mockAppConfig)

    protected def request(nino: Nino, businessId: BusinessId, taxYear: Option[TaxYear]): ListPeriodSummariesRequestData =
      ListPeriodSummariesRequestData(nino, businessId, taxYear)

  }

}
