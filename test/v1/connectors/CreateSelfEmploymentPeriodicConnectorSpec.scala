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
import uk.gov.hmrc.domain.Nino
import v1.mocks.MockHttpClient
import v1.models.outcomes.ResponseWrapper
import v1.models.request.createSEPeriodic._
import v1.models.response.createSEPeriodic.CreateSelfEmploymentPeriodicResponse

import scala.concurrent.Future

class CreateSelfEmploymentPeriodicConnectorSpec extends ConnectorSpec {

  val nino = "AA123456A"
  val businessId = "XAIS12345678910"

  val request = CreateSelfEmploymentPeriodicRequest(
    nino = Nino(nino),
    businessId = businessId,
    body = CreateSelfEmploymentPeriodicBody(
      "2017-01-25",
      "2018-01-24",
      Some(Incomes(
        Some(IncomesAmountObject(500.25)),
        Some(IncomesAmountObject(500.25))
      )),
      Some(ConsolidatedExpenses(
        500.25
      )),
      Some(Expenses(
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25)))
      ))
    )
  )

  val response = CreateSelfEmploymentPeriodicResponse("2017090920170909")

  class Test extends MockHttpClient with MockAppConfig {

    val connector: CreateSelfEmploymentPeriodicConnector = new CreateSelfEmploymentPeriodicConnector(
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

  "CreateSEPeriodicConnector" when {
    "createPeriodic" must {
      "return a 200 status for a success scenario" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, response))

        MockedHttpClient
          .post(
            url = s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries",
            body = request.body,
            requiredHeaders = desRequestHeaders: _*
          ).returns(Future.successful(outcome))

        await(connector.createPeriodic(request)) shouldBe outcome
      }
    }
  }
}
