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

package v4.retrieveAnnualSubmission

import shared.connectors.ConnectorSpec
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v4.retrieveAnnualSubmission.def1.model.Def1_RetrieveAnnualSubmissionFixture
import v4.retrieveAnnualSubmission.def1.model.request.Def1_RetrieveAnnualSubmissionRequestData
import v4.retrieveAnnualSubmission.def1.model.response.Def1_RetrieveAnnualSubmissionResponse

import scala.concurrent.Future

class RetrieveAnnualSubmissionConnectorSpec extends ConnectorSpec with Def1_RetrieveAnnualSubmissionFixture {

  private val nino          = "AA123456A"
  private val businessId    = "XAIS12345678910"
  private val nonTysRequest = makeRequest("2019-20")
  private val tysRequest    = makeRequest("2023-24")

  private val response = Def1_RetrieveAnnualSubmissionResponse(
    adjustments = Some(adjustments),
    allowances = Some(allowances),
    nonFinancials = Some(nonFinancials)
  )

  def makeRequest(taxYear: String): Def1_RetrieveAnnualSubmissionRequestData = Def1_RetrieveAnnualSubmissionRequestData(
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
