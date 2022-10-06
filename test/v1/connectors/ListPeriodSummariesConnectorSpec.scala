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
import v1.mocks.MockHttpClient
import v1.models.domain.{BusinessId, Nino}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.listPeriodSummaries.ListPeriodSummariesRequest
import v1.models.response.listPeriodSummaries.{ListPeriodSummariesResponse, PeriodDetails}

import scala.concurrent.Future

class ListPeriodSummariesConnectorSpec extends ConnectorSpec {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"

  val request: ListPeriodSummariesRequest = ListPeriodSummariesRequest(
    nino = Nino(nino),
    businessId = BusinessId(businessId)
  )

  val response: ListPeriodSummariesResponse[PeriodDetails] = ListPeriodSummariesResponse(
    Seq(
      PeriodDetails(
        "2020-01-01_2020-01-01",
        "2020-01-01",
        "2020-01-01"
      ))
  )

  class Test extends MockHttpClient with MockAppConfig {

    val connector: ListPeriodSummariesConnector = new ListPeriodSummariesConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockAppConfig.desBaseUrl returns baseUrl
    MockAppConfig.desToken returns "des-token"
    MockAppConfig.desEnvironment returns "des-environment"
    MockAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
  }

  "connector" must {
    "send a request and return a body" in new Test {
      val outcome = Right(ResponseWrapper(correlationId, response))

      MockHttpClient
        .get(
          url = s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries",
          config = dummyHeaderCarrierConfig,
          requiredHeaders = requiredDesHeaders,
          excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
        )
        .returns(Future.successful(outcome))

      await(connector.listPeriodSummaries(request)) shouldBe outcome
    }
  }

}
