/*
 * Copyright 2023 HM Revenue & Customs
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

package v5.createPeriodSummary

import shared.connectors.ConnectorSpec
import shared.models.domain.{BusinessId, Nino}
import shared.models.outcomes.ResponseWrapper
import v5.createPeriodSummary.def1.model.request.Def1_Create_PeriodDates
import v5.createPeriodSummary.def2.model.request.Def2_Create_PeriodDates
import v5.createPeriodSummary.model.request._
import v5.createPeriodSummary.model.response.CreatePeriodSummaryResponse

import scala.concurrent.Future

class CreatePeriodSummaryConnectorSpec extends ConnectorSpec {

  "CreatePeriodSummaryConnectorSpec" when {
    "createPeriodSummary" must {
      "return a 200 status for a success scenario" in new DesTest with Test {
        def request: CreatePeriodSummaryRequestData = Def1_CreatePeriodSummaryRequestData(
          nino = Nino(nino),
          businessId = BusinessId(businessId),
          body = Def1_CreatePeriodSummaryRequestBody(Def1_Create_PeriodDates("2019-08-24", "2019-08-24"), None, None, None)
        )

        val outcome: Right[Nothing, ResponseWrapper[CreatePeriodSummaryResponse]] = Right(ResponseWrapper(correlationId, response))

        val url =
          s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries"
        willPost(url, request.body).returns(Future.successful(outcome))

        await(connector.createPeriodSummary(request)) shouldBe outcome

      }

      "return a 200 status for a success TYS scenario" in new IfsTest with Test {
        def request: CreatePeriodSummaryRequestData = Def2_CreatePeriodSummaryRequestData(
          nino = Nino(nino),
          businessId = BusinessId(businessId),
          body = Def2_CreatePeriodSummaryRequestBody(Def2_Create_PeriodDates("2023-04-05", "2024-04-05"), None, None, None)
        )

        val outcome: Right[Nothing, ResponseWrapper[CreatePeriodSummaryResponse]] = Right(ResponseWrapper(correlationId, response))

        val url =
          s"$baseUrl/income-tax/23-24/$nino/self-employments/$businessId/periodic-summaries"
        willPost(url, request.body).returns(Future.successful(outcome))

        await(connector.createPeriodSummary(request)) shouldBe outcome
      }
    }
  }

  trait Test {
    _: ConnectorTest =>

    protected val connector: CreatePeriodSummaryConnector = new CreatePeriodSummaryConnector(
      http = mockHttpClient,
      appConfig = mockSharedAppConfig
    )

    val nino: String       = "AA123456A"
    val businessId: String = "XAIS12345678910"
    val downstreamTaxYear  = "2023-24"

    def request: CreatePeriodSummaryRequestData

    def response: CreatePeriodSummaryResponse = CreatePeriodSummaryResponse("2017090920170909")
  }

}
