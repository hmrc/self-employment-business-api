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

package v4.createAmendCumulativePeriodSummary

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v4.createAmendCumulativePeriodSummary.def1.model.request.PeriodDates
import v4.createAmendCumulativePeriodSummary.model.request.{
  CreateAmendCumulativePeriodSummaryRequestData,
  Def1_CreateAmendCumulativePeriodSummaryRequestBody,
  Def1_CreateAmendCumulativePeriodSummaryRequestData
}

import scala.concurrent.Future

class CreateAmendCumulativePeriodSummaryConnectorSpec extends ConnectorSpec {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"

  val periodDates: Option[PeriodDates] = Some(
    PeriodDates(
      periodStartDate = "2025-08-24",
      periodEndDate = "2026-08-24"
    ))

  private val body = Def1_CreateAmendCumulativePeriodSummaryRequestBody(periodDates, None, None, None)

  trait Test {
    self: ConnectorTest =>

    val request: CreateAmendCumulativePeriodSummaryRequestData = Def1_CreateAmendCumulativePeriodSummaryRequestData(
      nino = Nino(nino),
      businessId = BusinessId(businessId),
      taxYear = taxYear,
      body = body
    )

    protected val connector: CreateAmendCumulativePeriodSummaryConnector = new CreateAmendCumulativePeriodSummaryConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

    def taxYear: TaxYear

  }

  "AmendCumulativePeriodSummaryConnector for a Tax Year Specific tax year" must {
    "return a 200 status for a success scenario" in
      new IfsTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2025-26")

        val outcome = Right(ResponseWrapper(correlationId, ()))
        willPut(url"$baseUrl/income-tax/${taxYear.asTysDownstream}/self-employments/periodic/$nino/$businessId", body) returns Future
          .successful(outcome)

        val result: DownstreamOutcome[Unit] = await(connector.amendCumulativePeriodSummary(request))
        result shouldBe outcome
      }
  }

}
