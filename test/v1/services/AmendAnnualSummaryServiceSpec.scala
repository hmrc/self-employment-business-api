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

package v1.services

import v1.controllers.EndpointLogContext
import v1.mocks.connectors.MockAmendAnnualSummaryConnector
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.amendSEAnnual.{AmendAnnualSubmissionFixture, AmendAnnualSubmissionRequest}

import scala.concurrent.Future

class AmendAnnualSummaryServiceSpec extends ServiceSpec with AmendAnnualSubmissionFixture {

  val nino: String = "AA123456A"
  val businessId: String = "XAIS12345678910"
  val taxYear: String = "2017-18"
  implicit val correlationId: String = "X-123"

  private val requestBody =  amendAnnualSubmissionBody()

  private val requestData = AmendAnnualSubmissionRequest(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    taxYear = TaxYear.fromMtd(taxYear),
    body = requestBody
  )

  trait Test extends MockAmendAnnualSummaryConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new AmendAnnualSummaryService(
      connector = mockAmendAnnualSummaryConnector
    )
  }

  "AmendAnnualSummaryService" when {
    "amendAnnualSubmission" must {
      "return correct result for a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        MockAmendAnnualSummaryConnector.amendAnnualSummary(requestData)
          .returns(Future.successful(outcome))

        await(service.amendAnnualSummary(requestData)) shouldBe outcome
      }

      "map errors according to spec" when {
        def serviceError(desErrorCode: String, error: MtdError): Unit =
          s"a $desErrorCode error is returned from the service" in new Test {

            MockAmendAnnualSummaryConnector.amendAnnualSummary(requestData)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

            await(service.amendAnnualSummary(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val input = Seq(
          ("INVALID_NINO", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_INCOME_SOURCE", BusinessIdFormatError),
          ("INVALID_PAYLOAD", DownstreamError),
          ("INVALID_CORRELATIONID", DownstreamError),
          ("MISSING_EXEMPTION_REASON", DownstreamError),
          ("MISSING_EXEMPTION_INDICATOR", DownstreamError),
          ("ALLOWANCE_NOT_SUPPORTED", RuleTaxYearNotSupportedError),
          ("NOT_FOUND", NotFoundError),
          ("NOT_FOUND_INCOME_SOURCE", NotFoundError),
          ("GONE", DownstreamError),
          ("SERVER_ERROR", DownstreamError),
          ("BAD_GATEWAY", DownstreamError),
          ("SERVICE_UNAVAILABLE", DownstreamError)
        )

        input.foreach(args => (serviceError _).tupled(args))
      }
    }
  }
}
