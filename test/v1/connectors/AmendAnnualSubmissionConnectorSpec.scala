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

package v1.connectors

import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.amendSEAnnual.{AmendAnnualSubmissionBody, AmendAnnualSubmissionRequest}

import scala.concurrent.Future

class AmendAnnualSubmissionConnectorSpec extends ConnectorSpec {

  val nino: String = "AA123456A"
  val businessId: String = "XAIS12345678910"
  private val body = AmendAnnualSubmissionBody(None, None, None)

  trait Test {
    _: ConnectorTest =>
    def taxYear: TaxYear

    protected val connector: AmendAnnualSubmissionConnector = new AmendAnnualSubmissionConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    val request: AmendAnnualSubmissionRequest = AmendAnnualSubmissionRequest(
      nino = Nino(nino),
      businessId = BusinessId(businessId),
      taxYear = taxYear,
      body = body
    )
  }

  "AmendAnnualSubmissionConnector" when {
    "amendAnnualSubmission called" must {
      "return a 200 status for a success scenario" in
      new DesTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2019-20")

        val outcome = Right(ResponseWrapper(correlationId, ()))

        willPut(s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}", body) returns Future.successful(outcome)

        val result: DownstreamOutcome[Unit] = await(connector.amendAnnualSubmission(request))
        result shouldBe outcome
      }
    }
  }

  "AmendAnnualSubmissionConnector for a Tax Year Specific tax year" must {
    "return a 200 status for a success scenario" in
      new TysIfsTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2023-24")

        val outcome = Right(ResponseWrapper(correlationId, ()))

        willPut(s"$baseUrl/income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries", body) returns Future.successful(outcome)

        val result: DownstreamOutcome[Unit] = await(connector.amendAnnualSubmission(request))
        result shouldBe outcome
      }
  }

}
