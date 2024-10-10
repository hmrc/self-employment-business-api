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

package v4.retrievePeriodSummary

import api.models.domain.PeriodId
import config.MockSeBusinessFeatureSwitches
import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v4.retrievePeriodSummary.def1.model.response.{Def1_RetrievePeriodSummaryResponse, Retrieve_PeriodDates}
import v4.retrievePeriodSummary.model.request.{Def1_RetrievePeriodSummaryRequestData, RetrievePeriodSummaryRequestData}
import v4.retrievePeriodSummary.model.response.RetrievePeriodSummaryResponse

import scala.concurrent.Future

class RetrievePeriodSummaryConnectorSpec extends ConnectorSpec with MockSeBusinessFeatureSwitches {

  private val nino       = Nino("AA123456A")
  private val businessId = BusinessId("XAIS12345678910")
  private val periodId   = PeriodId("2019-01-25_2020-01-25")
  private val tysTaxYear = TaxYear.fromMtd("2023-24")
  private val fromDate   = "2019-01-25"
  private val toDate     = "2020-01-25"

  private val def2Response: RetrievePeriodSummaryResponse = Def1_RetrievePeriodSummaryResponse(
    Retrieve_PeriodDates("2019-01-25", "2020-01-25"),
    None,
    None,
    None
  )

  "retrievePeriodSummary()" when {
    "given a def2 (TYS) request" should {
      "call the TYS URL and return a 200 status" in new TysIfsTest with Test {
        val outcome: Right[Nothing, ResponseWrapper[RetrievePeriodSummaryResponse]] = Right(ResponseWrapper(correlationId, def2Response))

        val expectedDownstreamUrl =
          s"$baseUrl/income-tax/${tysTaxYear.asTysDownstream}/$nino/self-employments/$businessId/periodic-summary-detail?from=$fromDate&to=$toDate"

        willGet(expectedDownstreamUrl).returns(Future.successful(outcome))

        val request: RetrievePeriodSummaryRequestData = Def1_RetrievePeriodSummaryRequestData(nino, businessId, periodId, tysTaxYear)

        val result: DownstreamOutcome[RetrievePeriodSummaryResponse] =
          await(connector.retrievePeriodSummary(request))

        result shouldBe outcome
      }
    }
  }

  trait Test {
    _: ConnectorTest =>

    protected val connector: RetrievePeriodSummaryConnector = new RetrievePeriodSummaryConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

  }

}
