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

package v2.connectors

import api.connectors.{ConnectorSpec, DownstreamOutcome}
import api.models.domain.{BusinessId, Nino, PeriodId, TaxYear}
import api.models.outcomes.ResponseWrapper
import v2.models.request.retrievePeriodSummary.RetrievePeriodSummaryRequestData
import v2.models.response.retrievePeriodSummary.{PeriodDates, RetrievePeriodSummaryResponse}

import scala.concurrent.Future

class RetrievePeriodSummaryConnectorSpec extends ConnectorSpec {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"
  val periodId: String   = "2019-01-25_2020-01-25"
  val tysTaxYear: String = "2023-24"
  val fromDate: String   = "2019-01-25"
  val toDate: String     = "2020-01-25"

  val request: RetrievePeriodSummaryRequestData =
    RetrievePeriodSummaryRequestData(Nino(nino), BusinessId(businessId), PeriodId(periodId), None)

  val response: RetrievePeriodSummaryResponse = RetrievePeriodSummaryResponse(
    PeriodDates("2019-01-25", "2020-01-25"),
    None,
    None,
    None
  )

  trait Test {
    _: ConnectorTest =>

    protected val connector: RetrievePeriodSummaryConnector = new RetrievePeriodSummaryConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    protected def request(nino: Nino, businessId: BusinessId, periodId: PeriodId, taxYear: Option[TaxYear]): RetrievePeriodSummaryRequestData =
      RetrievePeriodSummaryRequestData(nino, businessId, periodId, taxYear)

  }

  "connector" must {
    "return a 200 status for a success scenario" in new DesTest with Test {
      val outcome: Right[Nothing, ResponseWrapper[RetrievePeriodSummaryResponse]] = Right(ResponseWrapper(correlationId, response))

      willGet(s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summary-detail?from=$fromDate&to=$toDate")
        .returns(Future.successful(outcome))

      await(connector.retrievePeriodSummary(this.request(Nino(nino), BusinessId(businessId), PeriodId(periodId), None))) shouldBe outcome
    }

    "return a 200 status for a success TYS scenario" in new TysIfsTest with Test {
      val taxYear: String                                                         = TaxYear.fromMtd(tysTaxYear).asTysDownstream
      val outcome: Right[Nothing, ResponseWrapper[RetrievePeriodSummaryResponse]] = Right(ResponseWrapper(correlationId, response))
      val url = s"$baseUrl/income-tax/$taxYear/$nino/self-employments/$businessId/periodic-summary-detail?from=$fromDate&to=$toDate"

      willGet(url).returns(Future.successful(outcome))

      val result: DownstreamOutcome[RetrievePeriodSummaryResponse] =
        await(connector.retrievePeriodSummary(this.request(Nino(nino), BusinessId(businessId), PeriodId(periodId), Some(TaxYear.fromMtd(tysTaxYear)))))
      result shouldBe outcome
    }
  }

}
