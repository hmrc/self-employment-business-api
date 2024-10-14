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

package v4.createAmendCumulativeSubmission

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v4.createAmendCumulativeSubmission.def1.model.request.PeriodDates
import v4.createAmendCumulativeSubmission.model.request.{
  CreateAmendCumulativeSubmissionRequestData,
  Def1_CreateAmendCumulativeSubmissionRequestBody,
  Def1_CreateAmendCumulativeSubmissionRequestData
}

import scala.concurrent.Future

class CreateAmendCumulativeSubmissionConnectorSpec extends ConnectorSpec {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"

  val periodDates: PeriodDates = PeriodDates(
    periodStartDate = "2025-08-24",
    periodEndDate = "2026-08-24"
  )

  private val body = Def1_CreateAmendCumulativeSubmissionRequestBody(periodDates, None, None, None)

  trait Test {
    _: ConnectorTest =>

    val request: CreateAmendCumulativeSubmissionRequestData = Def1_CreateAmendCumulativeSubmissionRequestData(
      nino = Nino(nino),
      businessId = BusinessId(businessId),
      taxYear = taxYear,
      body = body
    )

    protected val connector: CreateAmendCumulativeSubmissionConnector = new CreateAmendCumulativeSubmissionConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

    def taxYear: TaxYear

  }

  "AmendCumulativeSubmissionConnector" when {
    "amendCumulativeSubmission called" must {
      "return a 200 status for a success scenario" in
        new IfsTest with Test {
          def taxYear: TaxYear = TaxYear.fromMtd("2019-20")

          val outcome = Right(ResponseWrapper(correlationId, ()))

          willPut(s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/cumulative-summaries/${taxYear.asDownstream}", body) returns Future
            .successful(outcome)

          val result: DownstreamOutcome[Unit] = await(connector.amendCumulativeSubmission(request))
          result shouldBe outcome
        }
    }
  }

  "AmendCumulativeSubmissionConnector for a Tax Year Specific tax year" must {
    "return a 200 status for a success scenario" in
      new TysIfsTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2023-24")

        val outcome = Right(ResponseWrapper(correlationId, ()))

        willPut(s"$baseUrl/income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/cumulative-summaries", body) returns Future
          .successful(outcome)

        val result: DownstreamOutcome[Unit] = await(connector.amendCumulativeSubmission(request))
        result shouldBe outcome
      }
  }

}
