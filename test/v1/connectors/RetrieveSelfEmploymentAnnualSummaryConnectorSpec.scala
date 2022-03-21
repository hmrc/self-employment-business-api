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
import v1.models.domain.Nino
import v1.models.domain.ex.MtdNicExemption._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.retrieveSEAnnual.RetrieveSelfEmploymentAnnualSummaryRequest
import v1.models.response.retrieveSEAnnual._

import scala.concurrent.Future

class RetrieveSelfEmploymentAnnualSummaryConnectorSpec extends ConnectorSpec {

  val nino: String = "AA123456A"
  val businessId: String = "XAIS12345678910"
  val taxYear: String = "2019-20"
  val downstreamTaxYear: String = "2020"

  val request: RetrieveSelfEmploymentAnnualSummaryRequest = RetrieveSelfEmploymentAnnualSummaryRequest(
    nino = Nino(nino),
    businessId = businessId,
    taxYear = taxYear
  )

  val response: RetrieveSelfEmploymentAnnualSummaryResponse = RetrieveSelfEmploymentAnnualSummaryResponse(
    Some(Adjustments(
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25)
    )),
    Some(Allowances(
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25)
    )),
    Some(NonFinancials(
      Some(Class4NicInfo(
        Some(`non-resident`)
      ))
    ))
  )

  class Test extends MockHttpClient with MockAppConfig {
    val connector: RetrieveSelfEmploymentAnnualSummaryConnector = new RetrieveSelfEmploymentAnnualSummaryConnector(
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
          url = s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/$downstreamTaxYear",
          config = dummyDesHeaderCarrierConfig,
          requiredHeaders = requiredDesHeaders,
          excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
        ).returns(Future.successful(outcome))

      await(connector.retrieveSEAnnual(request)) shouldBe outcome
    }
  }
}
