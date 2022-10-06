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
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.retrieveAnnual.RetrieveAnnualSubmissionRequest
import v1.models.response.retrieveAnnual._

import scala.concurrent.Future

class RetrieveAnnualSubmissionConnectorSpec extends ConnectorSpec with RetrieveAnnualSubmissionFixture {

  val nino: String              = "AA123456A"
  val businessId: String        = "XAIS12345678910"
  val taxYear: String           = "2019-20"
  val downstreamTaxYear: String = "2020"

  val request: RetrieveAnnualSubmissionRequest = RetrieveAnnualSubmissionRequest(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    taxYear = TaxYear.fromMtd(taxYear)
  )

  val response: RetrieveAnnualSubmissionResponse = RetrieveAnnualSubmissionResponse(
    adjustments = Some(adjustments),
    allowances = Some(allowances),
    nonFinancials = Some(nonFinancials)
  )

  class Test extends MockHttpClient with MockAppConfig {

    val connector: RetrieveAnnualSubmissionConnector = new RetrieveAnnualSubmissionConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    MockAppConfig.ifsBaseUrl returns baseUrl
    MockAppConfig.ifsToken returns "ifs-token"
    MockAppConfig.ifsEnvironment returns "ifs-environment"
    MockAppConfig.ifsEnvironmentHeaders returns Some(allowedIfsHeaders)
  }

  "connector" must {
    "send a request and return a body" in new Test {
      val outcome = Right(ResponseWrapper(correlationId, response))

      MockHttpClient
        .get(
          url = s"$baseUrl/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/$downstreamTaxYear",
          config = dummyHeaderCarrierConfig,
          requiredHeaders = requiredIfsHeaders,
          excludedHeaders = Seq("AnotherHeader" -> "HeaderValue")
        )
        .returns(Future.successful(outcome))

      await(connector.retrieveAnnualSubmission(request)) shouldBe outcome
    }
  }

}
