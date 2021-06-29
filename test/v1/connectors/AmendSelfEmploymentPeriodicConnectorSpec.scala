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
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.MockHttpClient
import v1.models.outcomes.ResponseWrapper
import v1.models.request.amendSEPeriodic.{AmendPeriodicBody, AmendPeriodicRequest, ConsolidatedExpenses}

import scala.concurrent.Future

class AmendSelfEmploymentPeriodicConnectorSpec extends ConnectorSpec {

  val nino: String = "AA123456A"
  val businessId: String = "XAIS12345678910"
  val periodId: String = "2020-01-01_2020-01-01"

  val request: AmendPeriodicRequest = AmendPeriodicRequest(
    nino = Nino(nino),
    businessId = businessId,
    periodId = periodId,
    body = AmendPeriodicBody(
      None,
      Some(ConsolidatedExpenses(200.10)),
      None
    ))

  class Test extends MockHttpClient with MockAppConfig {

    val connector: AmendSelfEmploymentPeriodicConnector = new AmendSelfEmploymentPeriodicConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockAppConfig.desBaseUrl returns baseUrl
    MockAppConfig.desToken returns "des-token"
    MockAppConfig.desEnvironment returns "des-environment"
    MockAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
  }

  "AmendSelfEmploymentPeriodicConnector" when {
    "amendPeriodicUpdates" must {
      "return a 204 status for a success scenario" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        val fromDate: String = request.periodId.substring(0, 10)
        val toDate: String= request.periodId.substring(11, 21)

        implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))
        val requiredHeadersPut: Seq[(String, String)] = requiredDesHeaders ++ Seq("Content-Type" -> "application/json")

        MockedHttpClient
          .put(
            url = s"$baseUrl/income-store/nino/${request.nino}/self-employments/${request.businessId}/periodic-summaries?from=$fromDate&to=$toDate",
            config = dummyDesHeaderCarrierConfig,
            body = request.body,
            requiredHeaders = requiredHeadersPut,
            excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
          ).returns(Future.successful(outcome))

        await(connector.amendPeriodicUpdates(request)) shouldBe outcome
      }
    }
  }
}