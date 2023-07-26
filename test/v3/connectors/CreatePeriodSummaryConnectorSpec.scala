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

package v3.connectors

import api.connectors.ConnectorSpec
import api.models.domain.{BusinessId, Nino}
import api.models.outcomes.ResponseWrapper
import v3.models.request.createPeriodSummary._
import v3.models.response.createPeriodSummary.CreatePeriodSummaryResponse

import scala.concurrent.Future

class CreatePeriodSummaryConnectorSpec extends ConnectorSpec {

  "CreatePeriodSummaryConnectorSpec" when {
    "createPeriodSummary" must {
      "return a 200 status for a success scenario" in new DesTest with Test {
        def periodDates: PeriodDates                                              = PeriodDates("2019-08-24", "2019-08-24")
        val outcome: Right[Nothing, ResponseWrapper[CreatePeriodSummaryResponse]] = Right(ResponseWrapper(correlationId, response))

        val url =
          s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries"
        willPost(url, request.body).returns(Future.successful(outcome))

        await(connector.createPeriodSummary(request)) shouldBe outcome
      }

      "return a 200 status for a success TYS scenario" in new TysIfsTest with Test {
        def periodDates: PeriodDates                                              = PeriodDates("2023-04-05", "2024-04-05")
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
      appConfig = mockAppConfig
    )

    val nino: String       = "AA123456A"
    val businessId: String = "XAIS12345678910"
    val downstreamTaxYear  = "2023-24"

    def periodDates: PeriodDates

    def response: CreatePeriodSummaryResponse = CreatePeriodSummaryResponse("2017090920170909")

    def request: CreatePeriodSummaryRequest = CreatePeriodSummaryRequest(
      nino = Nino(nino),
      businessId = BusinessId(businessId),
      body = CreatePeriodSummaryBody(
        periodDates,
        None,
        None,
        None
      )
    )

  }

}
