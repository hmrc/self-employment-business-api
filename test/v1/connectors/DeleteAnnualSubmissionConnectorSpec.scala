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

import mocks.MockAppConfig
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.MockHttpClient
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.deleteAnnual.DeleteAnnualSubmissionRequest

import scala.concurrent.Future

class DeleteAnnualSubmissionConnectorSpec extends ConnectorSpec {

  val taxYear: String = "2017-18"
  val downstreamTaxYear: String = "2018"
  val nino: String = "AA123456A"
  val businessId: String = "XAIS12345678910"

  val request: DeleteAnnualSubmissionRequest = DeleteAnnualSubmissionRequest(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    taxYear = TaxYear.fromMtd(taxYear)
  )

  class Test extends MockHttpClient with MockAppConfig {
    val connector: DeleteAnnualSubmissionConnector = new DeleteAnnualSubmissionConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockAppConfig.desBaseUrl returns baseUrl
    MockAppConfig.desToken returns "des-token"
    MockAppConfig.desEnvironment returns "des-environment"
    MockAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
  }

  "deleteAnnualSubmission" should {
    "return a 204 with no body" when {
      "the downstream call is successful" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))
        val requiredDesHeadersPut: Seq[(String, String)] = requiredDesHeaders ++ Seq("Content-Type" -> "application/json")

        MockHttpClient
          .put(
            url = s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/$downstreamTaxYear",
            config = dummyDesHeaderCarrierConfig,
            body = JsObject.empty,
            requiredHeaders = requiredDesHeadersPut,
            excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
          ).returns(Future.successful(outcome))

        await(connector.deleteAnnualSubmission(request)) shouldBe outcome
      }
    }
  }
}