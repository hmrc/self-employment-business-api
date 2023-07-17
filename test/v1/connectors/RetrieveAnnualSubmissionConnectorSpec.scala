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

package v1.connectors

import api.connectors.ConnectorSpec
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.outcomes.ResponseWrapper
import v1.models.request.retrieveAnnual.RetrieveAnnualSubmissionRequest
import v1.models.response.retrieveAnnual._

import scala.concurrent.Future

class RetrieveAnnualSubmissionConnectorSpec extends ConnectorSpec with RetrieveAnnualSubmissionFixture {

  val nino: String       = "AA123456A"
  val businessId: String = "XAIS12345678910"
  val nonTysRequest = makeRequest("2019-20")
  val tysRequest    = makeRequest("2023-24")
  val response: RetrieveAnnualSubmissionResponse = RetrieveAnnualSubmissionResponse(
    adjustments = Some(adjustments),
    allowances = Some(allowances),
    nonFinancials = Some(nonFinancials)
  )

  def makeRequest(taxYear: String): RetrieveAnnualSubmissionRequest = RetrieveAnnualSubmissionRequest(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    taxYear = TaxYear.fromMtd(taxYear)
  )

  trait Test {
    _: ConnectorTest =>

    protected val connector: RetrieveAnnualSubmissionConnector = new RetrieveAnnualSubmissionConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

  }

  "connector" must {
    "send a request and return a body" in new IfsTest with Test {
      val outcome = Right(ResponseWrapper(correlationId, response))

      willGet(s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/2020")
        .returns(Future.successful(outcome))

      await(connector.retrieveAnnualSubmission(nonTysRequest)) shouldBe outcome
    }

    "send a request and return a body for a TYS tax year" in new TysIfsTest with Test {
      val outcome = Right(ResponseWrapper(correlationId, response))

      willGet(s"$baseUrl/income-tax/23-24/$nino/self-employments/$businessId/annual-summaries")
        .returns(Future.successful(outcome))

      await(connector.retrieveAnnualSubmission(tysRequest)) shouldBe outcome
    }
  }

}
