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
import v1.models.domain.ex.MtdEx._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.retrieveSEAnnual.RetrieveSelfEmploymentAnnualSummaryRequest
import v1.models.response.retrieveSEAnnual._

import scala.concurrent.Future

class RetrieveSelfEmploymentAnnualSummaryConnectorSpec extends ConnectorSpec {

  val nino = Nino("AA123456A")
  val businessId = "XAIS12345678910"
  val taxYear = "2019-20"

  val request = RetrieveSelfEmploymentAnnualSummaryRequest(nino, businessId, taxYear)

  val response = RetrieveSelfEmploymentAnnualSummaryResponseBody(
    Some(Adjustments(
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25))),
    Some(Allowances(
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25))),
    Some(NonFinancials(
      Some(Class4NicInfo(
        true,
        Some(`001 - Non Resident`))))))

  class Test extends MockHttpClient with MockAppConfig {
    val connector: RetrieveSelfEmploymentAnnualSummaryConnector =
      new RetrieveSelfEmploymentAnnualSummaryConnector(http = mockHttpClient, appConfig = mockAppConfig)

    val desRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnvironment returns "des-environment"
  }

  "connector" must {
    "send a request and return a body" in new Test {

        val outcome = Right(ResponseWrapper(correlationId, response))
        MockedHttpClient
          .get(
            url = s"$baseUrl/income-tax/nino/${request.nino}/self-employments/${request.businessId}/annual-summaries/${DesTaxYear.fromMtd(request.taxYear)}",
            requiredHeaders = "Environment" -> "des-environment", "Authorization" -> s"Bearer des-token"
          )
          .returns(Future.successful(outcome))

        await(connector.retrieveSEAnnual(request)) shouldBe outcome
      }
    }
}
