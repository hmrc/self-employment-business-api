/*
 * Copyright 2020 HM Revenue & Customs
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
import uk.gov.hmrc.domain.Nino
import v1.mocks.MockHttpClient
import v1.models.domain.DesTaxYear
import v1.models.outcomes.ResponseWrapper
import v1.models.request.deleteSEAnnual.DeleteSelfEmploymentAnnualSummaryRequest

import scala.concurrent.Future

class DeleteSelfEmploymentAnnualSummaryConnectorSpec extends ConnectorSpec {

  val taxYear = "2017-18"
  val nino = Nino("AA123456A")
  val businessId = "XAIS12345678910"

  class Test extends MockHttpClient with MockAppConfig {
    val connector: DeleteSelfEmploymentAnnualSummaryConnector = new DeleteSelfEmploymentAnnualSummaryConnector(http = mockHttpClient, appConfig = mockAppConfig)

    val desRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnvironment returns "des-environment"
  }

  "delete" should {
    val request = DeleteSelfEmploymentAnnualSummaryRequest(nino, businessId, taxYear)

    "return a 204 with no body" when {
      "the downstream call is successful" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        MockedHttpClient.
          put(
            url = s"$baseUrl/income-tax/nino/${request.nino}/self-employments/${request.businessId}/annual-summaries/${DesTaxYear.fromMtd(request.taxYear)}",
            body = """{}""",
            requiredHeaders = "Environment" -> "des-environment", "Authorization" -> s"Bearer des-token"
          ).returns(Future.successful(outcome))

        await(connector.deleteSEAnnual(request)) shouldBe outcome
      }
    }
  }
}
