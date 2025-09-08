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

package v5.retrieveCumulativePeriodSummary

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v5.retrieveCumulativePeriodSummary.def1.model.request.Def1_RetrieveCumulativePeriodSummaryRequestData
import v5.retrieveCumulativePeriodSummary.def1.model.response.{Def1_RetrieveCumulativePeriodSummaryResponse, Def1_Retrieve_PeriodDates}
import v5.retrieveCumulativePeriodSummary.model.request.RetrieveCumulativePeriodSummaryRequestData
import v5.retrieveCumulativePeriodSummary.model.response.RetrieveCumulativePeriodSummaryResponse
import scala.concurrent.Future

class RetrieveCumulativePeriodSummaryConnectorSpec extends ConnectorSpec {

  private val nino       = Nino("AA123456A")
  private val businessId = BusinessId("XAIS12345678910")
  private val taxYear    = TaxYear.fromMtd("2025-26")
  private val fromDate   = "2025-07-08"
  private val toDate     = "2025-09-10"

  private val def1Response: RetrieveCumulativePeriodSummaryResponse = Def1_RetrieveCumulativePeriodSummaryResponse(
    Def1_Retrieve_PeriodDates(fromDate, toDate),
    None,
    None,
    None
  )

  "retrieveCumulativePeriodSummary()" when {

    "given a def1 request" should {
      "call the IFS URL and return a 200 status" in new IfsTest with Test {

        val outcome: Right[Nothing, ResponseWrapper[RetrieveCumulativePeriodSummaryResponse]] = Right(ResponseWrapper(correlationId, def1Response))

        val expectedDownstreamUrl = url"$baseUrl/income-tax/${taxYear.asTysDownstream}/self-employments/periodic-summary-detail/$nino/$businessId"

        willGet(expectedDownstreamUrl).returns(Future.successful(outcome))

        val request: RetrieveCumulativePeriodSummaryRequestData = Def1_RetrieveCumulativePeriodSummaryRequestData(nino, businessId, taxYear)
        val result: DownstreamOutcome[RetrieveCumulativePeriodSummaryResponse] = await(connector.retrieveCumulativePeriodSummary(request))
        result.shouldBe(outcome)
      }
    }

  }

  trait Test {
    self: ConnectorTest =>

    protected val connector: RetrieveCumulativePeriodSummaryConnector = new RetrieveCumulativePeriodSummaryConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

  }

}
