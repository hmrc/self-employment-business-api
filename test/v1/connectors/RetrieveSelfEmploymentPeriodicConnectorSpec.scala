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
import v1.models.request.retrieveSEPeriodic.RetrieveSelfEmploymentPeriodicRequest
import v1.models.response.retrieveSEPeriodic.{ConsolidatedExpenses, Incomes, IncomesAmountObject, RetrieveSelfEmploymentPeriodicResponse}

import scala.concurrent.Future

class RetrieveSelfEmploymentPeriodicConnectorSpec extends ConnectorSpec {

  val nino = Nino("AA123456A")
  val businessId = "XAIS12345678910"
  val periodId = "2019-01-25_2020-01-25"

  val request = RetrieveSelfEmploymentPeriodicRequest(nino, businessId, periodId)

  val response = RetrieveSelfEmploymentPeriodicResponse(
    "2019-01-25",
    "2020-01-25",
    Some(Incomes(
      Some(IncomesAmountObject(
        1000.20
      )),
      Some(IncomesAmountObject(
        1000.20
      ))
    )),
    Some(ConsolidatedExpenses(
      1000.20
    )),
    None
  )

  class Test extends MockHttpClient with MockAppConfig {
    val connector: RetrieveSelfEmploymentPeriodicConnector =
      new RetrieveSelfEmploymentPeriodicConnector(http = mockHttpClient, appConfig = mockAppConfig)

    val desRequestHeaders: Seq[(String, String)] = Seq("Environment" -> "des-environment", "Authorization" -> s"Bearer des-token")
    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnvironment returns "des-environment"
  }

  "connector" must {
    "send a request and return a body" in new Test {

      val fromDate = request.periodId.substring(0, 10)
      val toDate = request.periodId.substring(11, 21)

      val outcome = Right(ResponseWrapper(correlationId, response))
      MockedHttpClient
        .get(
          url = s"$baseUrl/income-store/nino/${request.nino}/self-employments/${request.businessId}/periodic-summary-detail?from=$fromDate&to=$toDate",
          requiredHeaders = "Environment" -> "des-environment", "Authorization" -> s"Bearer des-token"
        )
        .returns(Future.successful(outcome))

      await(connector.retrieveSEAnnual(request)) shouldBe outcome
    }
  }
}
