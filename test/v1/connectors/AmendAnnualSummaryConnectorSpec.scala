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
import v1.models.domain.DesTaxYear
import v1.models.domain.ex.MtdEx
import v1.models.outcomes.ResponseWrapper
import v1.models.request.amendSEAnnual.{Adjustments, Allowances, AmendAnnualSummaryBody, AmendAnnualSummaryRequest, Class4NicInfo, NonFinancials}

import scala.concurrent.Future

class AmendAnnualSummaryConnectorSpec extends ConnectorSpec {

  val nino: String = "AA123456A"
  val taxYear: String = "2018-19"
  val businessId: String = "XAIS12345678910"

  val request: AmendAnnualSummaryRequest = AmendAnnualSummaryRequest(
    nino = Nino(nino),
    businessId = businessId,
    taxYear = taxYear,
    body = AmendAnnualSummaryBody(
        Some(Adjustments(Some(1.11), Some(2.22), Some(3.33), Some(4.44), Some(5.55), Some(6.66), Some(7.77), Some(8.88), Some(9.99), Some(10.10))),
        Some(Allowances(Some(1.11), Some(2.22), Some(3.33), Some(4.44), Some(5.55), Some(6.66), Some(7.77), Some(8.88), Some(9.99), Some(10.10), Some(11.11))),
        Some(NonFinancials(Some(Class4NicInfo(Some(MtdEx.`001 - Non Resident`)))))
    )
  )

  class Test extends MockHttpClient with MockAppConfig {

    val connector: AmendAnnualSummaryConnector = new AmendAnnualSummaryConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    val desRequestHeaders: Seq[(String, String)] = Seq(
      "Environment" -> "des-environment",
      "Authorization" -> s"Bearer des-token"
    )

    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnvironment returns "des-environment"
  }

  "AmendAnnualSummaryConnector" when {
    "amendAnnualSummary" must {
      "return a 204 status for a success scenario" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        MockedHttpClient
          .put(
            url = s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${DesTaxYear.fromMtd(taxYear)}",
            body = request.body,
            requiredHeaders = desRequestHeaders: _*
          ).returns(Future.successful(outcome))

        await(connector.amendAnnualSummary(request)) shouldBe outcome
      }
    }
  }
}
