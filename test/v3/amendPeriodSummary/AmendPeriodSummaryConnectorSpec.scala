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

package v3.amendPeriodSummary

import api.models.domain.PeriodId
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v3.amendPeriodSummary.def1.model.request.Def1_Amend_PeriodExpenses
import v3.amendPeriodSummary.def2.model.request.Def2_Amend_PeriodExpenses
import v3.amendPeriodSummary.model.request._

import scala.concurrent.Future

class AmendPeriodSummaryConnectorSpec extends ConnectorSpec {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"
  val periodId: String   = "2020-01-01_2020-01-01"

  val nonTysRequest: AmendPeriodSummaryRequestData = Def1_AmendPeriodSummaryRequestData(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    periodId = PeriodId(periodId),
    body = Def1_AmendPeriodSummaryRequestBody(
      None,
      Some(Def1_Amend_PeriodExpenses(Some(200.10), None, None, None, None, None, None, None, None, None, None, None, None, None, None, None)),
      None
    )
  )

  val tysRequest: AmendPeriodSummaryRequestData = Def2_AmendPeriodSummaryRequestData(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    periodId = PeriodId(periodId),
    taxYear = TaxYear.fromMtd("2023-24"),
    body = Def2_AmendPeriodSummaryRequestBody(
      None,
      Some(
        Def2_Amend_PeriodExpenses(
          Some(200.10),
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None
        )),
      None
    )
  )

  private val outcome = Right(ResponseWrapper(correlationId, ()))

  trait Test {
    _: ConnectorTest =>

    protected val connector: AmendPeriodSummaryConnector = new AmendPeriodSummaryConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

  }

  "amendPeriodSummary" when {

    "given a non-TYS request" should {
      "call the non-TYS downstream URL and return 204" in new DesTest with Test {
        val expectedDownstreamUrl =
          s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries?from=2020-01-01&to=2020-01-01"

        willPut(expectedDownstreamUrl, nonTysRequest.body).returns(Future.successful(outcome))

        val result: DownstreamOutcome[Unit] = await(connector.amendPeriodSummary(nonTysRequest))
        result shouldBe outcome
      }
    }

    "given a TYS request" should {
      "call the TYS downstream URL and return 204" in new TysIfsTest with Test {
        val expectedDownstreamUrl =
          s"$baseUrl/income-tax/23-24/$nino/self-employments/$businessId/periodic-summaries?from=2020-01-01&to=2020-01-01"

        willPut(expectedDownstreamUrl, tysRequest.body).returns(Future.successful(outcome))

        val result: DownstreamOutcome[Unit] = await(connector.amendPeriodSummary(tysRequest))
        result shouldBe outcome
      }
    }
  }

}
