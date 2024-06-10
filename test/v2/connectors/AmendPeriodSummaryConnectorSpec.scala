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

import api.models.domain.PeriodId
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v2.models.request.amendPeriodSummary._

import scala.concurrent.Future

class AmendPeriodSummaryConnectorSpec extends ConnectorSpec {

  private val nino = "AA123456A"
  private val businessId = "XAIS12345678910"
  private val periodId = "2020-01-01_2020-01-01"
  private val nonTysRequest = makeRequest(None)
  private val tysRequest = makeRequest(Some("2023-24"))

  def makeRequest(taxYear: Option[String]): AmendPeriodSummaryRequestData = AmendPeriodSummaryRequestData(
    Nino(nino),
    BusinessId(businessId),
    PeriodId(periodId),
    taxYear.map(TaxYear.fromMtd),
    // @formatter:off
    AmendPeriodSummaryBody(
      None, Some(PeriodExpenses(
          Some(200.10), None, None, None, None,
          None, None, None, None, None, None,
          None, None, None, None, None
        )), None
    )
    // @formatter:on
  )

  trait Test {
    _: ConnectorTest =>

    protected val connector: AmendPeriodSummaryConnector = new AmendPeriodSummaryConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

  }

  "AmendPeriodSummaryConnector" when {
    "amendPeriodSummary" must {
      "return a 204 status for a success TYS scenario" in new TysIfsTest with Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        val url =
          s"$baseUrl/income-tax/23-24/$nino/self-employments/$businessId/periodic-summaries?from=2020-01-01&to=2020-01-01"

        willPut(url, tysRequest.body).returns(Future.successful(outcome))

        val result: DownstreamOutcome[Unit] = await(connector.amendPeriodSummary(tysRequest))

        result shouldBe outcome
      }

      "return a 204 status for a success non-TYS scenario" in new DesTest with Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        val url =
          s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries?from=2020-01-01&to=2020-01-01"

        willPut(url, nonTysRequest.body).returns(Future.successful(outcome))

        val result: DownstreamOutcome[Unit] = await(connector.amendPeriodSummary(nonTysRequest))

        result shouldBe outcome
      }
    }
  }

}
