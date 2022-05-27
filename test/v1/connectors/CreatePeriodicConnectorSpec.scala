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
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.MockHttpClient
import v1.models.domain.Nino
import v1.models.outcomes.ResponseWrapper
import v1.models.request.createPeriodSummary._
import v1.models.response.createPeriodic.CreatePeriodicResponse

import scala.concurrent.Future

class CreatePeriodicConnectorSpec extends ConnectorSpec {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"

  val request: CreatePeriodSummaryRequest = CreatePeriodSummaryRequest(
    nino = Nino(nino),
    businessId = businessId,
    body =  CreatePeriodSummaryBody(
      PeriodDates(
        "2019-08-24",
        "2019-08-24"),
      Some(PeriodIncome(
        Some(1000.99),
        Some(1000.99)
      )),
      Some(PeriodAllowableExpenses(
        None,
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(-99999.99),
        Some(-1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(-1000.99),
        Some(-1000.99),
        Some(-1000.99),
        Some(-99999999999.99),
        Some(-1000.99),
        Some(1000.99)
      )),
      Some(PeriodDisallowableExpenses(
        None,
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(-1000.99),
        Some(-999.99),
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(-1000.99),
        Some(-9999.99),
        Some(-1000.99),
        Some(-99999999999.99),
        Some(-99999999999.99),
        Some(1000.99)
      ))
    )
  )

  val response: CreatePeriodicResponse = CreatePeriodicResponse("2017090920170909")

  class Test extends MockHttpClient with MockAppConfig {

    val connector: CreatePeriodicConnector = new CreatePeriodicConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockAppConfig.desBaseUrl returns baseUrl
    MockAppConfig.desToken returns "des-token"
    MockAppConfig.desEnvironment returns "des-environment"
    MockAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
  }

  "CreateSEPeriodicConnector" when {
    "createPeriodicSummary" must {
      "return a 200 status for a success scenario" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, response))

        implicit val hc: HeaderCarrier                    = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))
        val requiredDesHeadersPost: Seq[(String, String)] = requiredDesHeaders ++ Seq("Content-Type" -> "application/json")

        MockHttpClient
          .post(
            url = s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries",
            config = dummyDesHeaderCarrierConfig,
            body = request.body,
            requiredHeaders = requiredDesHeadersPost,
            excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
          )
          .returns(Future.successful(outcome))

        await(connector.createPeriodicSummary(request)) shouldBe outcome
      }
    }
  }

}
