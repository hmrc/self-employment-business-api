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

import uk.gov.hmrc.http.HeaderCarrier
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.amendPeriodSummary._

import scala.concurrent.Future

class AmendPeriodSummaryConnectorSpec extends ConnectorSpec {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"
  val periodId: String   = "2020-01-01_2020-01-01"

  def makeRequest(taxYear: Option[String]): AmendPeriodSummaryRequest = AmendPeriodSummaryRequest(
    nino       = Nino(nino),
    businessId = BusinessId(businessId),
    periodId   = periodId,
    taxYear    = taxYear match {
      case Some(taxYear) => Some(TaxYear.fromMtd(taxYear))
      case _             => None
    },
    body        = AmendPeriodSummaryBody(
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

  val nonTysRequest = makeRequest(None)
  val tysRequest    = makeRequest(Some("2023-24"))


  trait Test {
    _: ConnectorTest =>

    protected val connector: AmendPeriodSummaryConnector = new AmendPeriodSummaryConnector(
      http      = mockHttpClient,
      appConfig = mockAppConfig
    )
  }

  "AmendPeriodSummaryConnector" when {

    "amendPeriodSummary" must {

      "return a 204 status for a success TYS scenario" in new TysIfsTest with Test {

        val outcome = Right(ResponseWrapper(correlationId, ()))

        val fromDate: String = tysRequest.periodId.substring(0, 10)
        val toDate: String   = tysRequest.periodId.substring(11, 21)
        val url              =
          s"$baseUrl/income-tax/23-24/$nino/self-employments/$businessId/periodic-summaries?from=$fromDate&to=$toDate"

        override implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))

        willPut(url, tysRequest.body).returns(Future.successful(outcome))

        await(connector.amendPeriodSummary(tysRequest)) shouldBe outcome
      }

      "return a 204 status for a success non-TYS scenario" in new DesTest with Test {

        val outcome = Right(ResponseWrapper(correlationId, ()))

        val fromDate: String = nonTysRequest.periodId.substring(0, 10)
        val toDate: String   = nonTysRequest.periodId.substring(11, 21)
        val url              =
          s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries?from=$fromDate&to=$toDate"

        override implicit val hc: HeaderCarrier = HeaderCarrier(otherHeaders = otherHeaders ++ Seq("Content-Type" -> "application/json"))

        willPut(url, nonTysRequest.body).returns(Future.successful(outcome))

        await(connector.amendPeriodSummary(nonTysRequest)) shouldBe outcome
      }
    }
  }

}
