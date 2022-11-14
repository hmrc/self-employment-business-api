/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json.JsObject
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.deleteAnnual.DeleteAnnualSubmissionRequest

import scala.concurrent.Future

class DeleteAnnualSubmissionConnectorSpec extends ConnectorSpec {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"

  trait Test {
    _: ConnectorTest =>
    def taxYear: TaxYear

    protected val connector: DeleteAnnualSubmissionConnector = new DeleteAnnualSubmissionConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    protected val request: DeleteAnnualSubmissionRequest = DeleteAnnualSubmissionRequest(
      nino = Nino(nino),
      businessId = BusinessId(businessId),
      taxYear = taxYear
    )

  }

  "deleteAnnualSubmission" should {
    "return a 204 with no body" when {
      "the downstream call is successful when not taxYearSpecific" in new DesTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2017-18")

        val outcome = Right(ResponseWrapper(correlationId, ()))

        willPut(
          body = JsObject.empty,
          url = s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}") returns Future.successful(outcome)

        val result = await(connector.deleteAnnualSubmission(request))

        result shouldBe outcome

      }

      "the downstream call is successful for a TYS year" in new TysIfsTest with Test {
        def taxYear: TaxYear = TaxYear.fromMtd("2023-24")

        val outcome = Right(ResponseWrapper(correlationId, ()))

        willDelete(s"$baseUrl/income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries") returns Future.successful(outcome)

        val result = await(connector.deleteAnnualSubmission(request))

        result shouldBe outcome
      }
    }
  }

}
