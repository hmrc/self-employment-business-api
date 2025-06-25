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

package v4.createAmendAnnualSubmission

import shared.connectors.{ConnectorSpec, DownstreamOutcome}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.StringContextOps
import v4.createAmendAnnualSubmission.def1.model.request.Def1_CreateAmendAnnualSubmissionRequestBody
import v4.createAmendAnnualSubmission.model.request.{CreateAmendAnnualSubmissionRequestData, Def1_CreateAmendAnnualSubmissionRequestData}

import scala.concurrent.Future

class CreateAmendAnnualSubmissionConnectorSpec extends ConnectorSpec {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"
  private val body       = Def1_CreateAmendAnnualSubmissionRequestBody(None, None, None)

  trait Test {
    _: ConnectorTest =>

    val request: CreateAmendAnnualSubmissionRequestData = Def1_CreateAmendAnnualSubmissionRequestData(
      nino = Nino(nino),
      businessId = BusinessId(businessId),
      taxYear = taxYear,
      body = body
    )

    protected val connector: CreateAmendAnnualSubmissionConnector = new CreateAmendAnnualSubmissionConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

    def taxYear: TaxYear

  }

  "AmendAnnualSubmissionConnector" when {
    "amendAnnualSubmission called" must {
      "return a 200 status for a success scenario" in
        new IfsTest with Test {
          def taxYear: TaxYear = TaxYear.fromMtd("2019-20")

          val outcome = Right(ResponseWrapper(correlationId, ()))

          willPut(url"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}", body) returns Future
            .successful(outcome)

          val result: DownstreamOutcome[Unit] = await(connector.amendAnnualSubmission(request))
          result shouldBe outcome
        }
    }
  }

  "AmendAnnualSubmissionConnector for a Tax Year Specific tax year" must {
    "return a 200 status for a success scenario" in
      new IfsTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2023-24")

        val outcome = Right(ResponseWrapper(correlationId, ()))

        willPut(url"$baseUrl/income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries", body) returns Future
          .successful(outcome)

        val result: DownstreamOutcome[Unit] = await(connector.amendAnnualSubmission(request))
        result shouldBe outcome
      }
  }

}
