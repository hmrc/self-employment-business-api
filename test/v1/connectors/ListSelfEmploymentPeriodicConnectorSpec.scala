/*
 * Copyright 2021 HM Revenue & Customs
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
import v1.models.domain.Nino
import v1.mocks.MockHttpClient
import v1.models.outcomes.ResponseWrapper
import v1.models.request.listSEPeriodic.ListSelfEmploymentPeriodicRequest
import v1.models.response.listSEPeriodic.{ListSelfEmploymentPeriodicResponse, PeriodDetails}

import scala.concurrent.Future

class ListSelfEmploymentPeriodicConnectorSpec extends ConnectorSpec {

  val nino: String = "AA123456A"
  val businessId: String = "XAIS12345678910"

  val request: ListSelfEmploymentPeriodicRequest = ListSelfEmploymentPeriodicRequest(Nino(nino), businessId)

  val response: ListSelfEmploymentPeriodicResponse[PeriodDetails] = ListSelfEmploymentPeriodicResponse(
    Seq(PeriodDetails(
      "2020-01-01_2020-01-01",
      "2020-01-01",
      "2020-01-01"
    )))

  class Test extends MockHttpClient with MockAppConfig {
    val connector: ListSelfEmploymentPeriodicConnector =
      new ListSelfEmploymentPeriodicConnector(http = mockHttpClient, appConfig = mockAppConfig)

    val desRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockAppConfig.desBaseUrl returns baseUrl
    MockAppConfig.desToken returns "des-token"
    MockAppConfig.desEnvironment returns "des-environment"
    MockAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
  }

  "connector" must {
    "send a request and return a body" in new Test {

      val outcome = Right(ResponseWrapper(correlationId, response))
      MockedHttpClient
        .get(
          url = s"$baseUrl/income-tax/nino/${request.nino}/self-employments/${request.businessId}/periodic-summaries",
          config = dummyDesHeaderCarrierConfig,
          requiredHeaders = requiredDesHeaders,
          excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
        )
        .returns(Future.successful(outcome))

      await(connector.listSEPeriodic(request)) shouldBe outcome
    }
  }
}
