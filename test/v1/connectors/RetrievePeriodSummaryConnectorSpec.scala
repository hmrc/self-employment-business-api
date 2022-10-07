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

import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.retrievePeriodSummary.RetrievePeriodSummaryRequest
import v1.models.response.retrievePeriodSummary.{PeriodDates, RetrievePeriodSummaryResponse}

import scala.concurrent.Future

class RetrievePeriodSummaryConnectorSpec extends ConnectorSpec {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"
  val periodId: String   = "2019-01-25_2020-01-25"
  val tysPeriodId        = "2024-05-01_2024_08-01"

  trait Test extends { _: ConnectorTest =>

    val connector: RetrievePeriodSummaryConnector = new RetrievePeriodSummaryConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

  }

  "RetrievePeriodSummaryConnector" when {
    "retrievePeriodSummary called " must {
      "send a request and return a body" in new DesTest with Test {
        val request: RetrievePeriodSummaryRequest = RetrievePeriodSummaryRequest(Nino(nino), BusinessId(businessId), periodId)

        val fromDate: String = request.periodId.substring(0, 10)
        val toDate: String   = request.periodId.substring(11, 21)
        val response: RetrievePeriodSummaryResponse = RetrievePeriodSummaryResponse(
          PeriodDates(fromDate, toDate),
          None,
          None,
          None
        )

        val outcome = Right(ResponseWrapper(correlationId, response))

        willGet(s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summary-detail?from=$fromDate&to=$toDate")
          .returns(Future.successful(outcome))

        await(connector.retrievePeriodSummary(request)) shouldBe outcome
      }
    }

    "retrievePeriodSummary called for a TYS year " must {
      "send a request and return a body" in new TysIfsTest with Test {
        val request: RetrievePeriodSummaryRequest = RetrievePeriodSummaryRequest(Nino(nino), BusinessId(businessId), tysPeriodId)

        val fromDate: String = request.periodId.substring(0, 10)
        val toDate: String   = request.periodId.substring(11, 21)
        val response: RetrievePeriodSummaryResponse = RetrievePeriodSummaryResponse(
          PeriodDates(fromDate, toDate),
          None,
          None,
          None
        )
        val taxYear = TaxYear.fromDate(fromDate)

        val outcome = Right(ResponseWrapper(correlationId, response))

        willGet(
          s"$baseUrl/income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/periodic-summary-detail?from=$fromDate&to=$toDate")
          .returns(Future.successful(outcome))

        await(connector.retrievePeriodSummary(request)) shouldBe outcome
      }
    }
  }

}
