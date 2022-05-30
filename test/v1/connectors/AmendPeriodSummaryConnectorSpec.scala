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
import v1.models.domain.{BusinessId, Nino}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.amendPeriodic._

import scala.concurrent.Future

class AmendPeriodSummaryConnectorSpec extends ConnectorSpec {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"
  val periodId: String   = "2020-01-01_2020-01-01"

  val request: AmendPeriodSummaryRequest = AmendPeriodSummaryRequest(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    periodId = periodId,
    body = AmendPeriodSummaryBody(
      None,
      Some(
        PeriodAllowableExpenses(
          Some(200.10),
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None
        )),
      None
    )
  )

  class Test extends MockHttpClient with MockAppConfig {

    val connector: AmendPeriodSummaryConnector = new AmendPeriodSummaryConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockAppConfig.desBaseUrl returns baseUrl
    MockAppConfig.desToken returns "des-token"
    MockAppConfig.desEnvironment returns "des-environment"
    MockAppConfig.desEnvironmentHeaders returns Some(allowedDesHeaders)
  }

  "AmendPeriodSummaryConnector" when {
    "amendPeriodSummary" must {
      "return a 204 status for a success scenario" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        val fromDate: String = request.periodId.substring(0, 10)
        val toDate: String   = request.periodId.substring(11, 21)

        implicit val hc: HeaderCarrier                   = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))
        val requiredDesHeadersPut: Seq[(String, String)] = requiredDesHeaders ++ Seq("Content-Type" -> "application/json")

        MockHttpClient
          .put(
            url = s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries?from=$fromDate&to=$toDate",
            config = dummyDesHeaderCarrierConfig,
            body = request.body,
            requiredHeaders = requiredDesHeadersPut,
            excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
          )
          .returns(Future.successful(outcome))

        await(connector.amendPeriodSummary(request)) shouldBe outcome
      }
    }
  }

}
