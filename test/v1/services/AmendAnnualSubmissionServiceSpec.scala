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

package v1.services

import v1.controllers.EndpointLogContext
import v1.mocks.connectors.MockAmendAnnualSubmissionConnector
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.amendSEAnnual.{AmendAnnualSubmissionFixture, AmendAnnualSubmissionRequest}

import scala.concurrent.Future

class AmendAnnualSubmissionServiceSpec extends ServiceSpec with AmendAnnualSubmissionFixture {

  val nino: String                   = "AA123456A"
  val businessId: String             = "XAIS12345678910"
  implicit val correlationId: String = "X-123"

  private val requestBody = amendAnnualSubmissionBody()

  private val requestData = AmendAnnualSubmissionRequest(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    taxYear = TaxYear.fromMtd("2023-24"),
    body = requestBody
  )

  trait Test extends MockAmendAnnualSubmissionConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new AmendAnnualSubmissionService(
      connector = mockAmendAnnualSubmissionConnector
    )

  }

  "AmendAnnualSubmissionService" when {
    "amendAnnualSubmission called" must {
      "return correct result for a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        MockAmendAnnualSubmissionConnector
          .amendAnnualSubmission(requestData)
          .returns(Future.successful(outcome))

        await(service.amendAnnualSubmission(requestData)) shouldBe outcome
      }

      "map errors according to spec" when {
        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockAmendAnnualSubmissionConnector
              .amendAnnualSubmission(requestData)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            await(service.amendAnnualSubmission(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = Seq(
          ("INVALID_NINO", NinoFormatError),
          ("INVALID_TAX_YEAR", TaxYearFormatError),
          ("INVALID_INCOME_SOURCE", BusinessIdFormatError),
          ("INVALID_PAYLOAD", InternalError),
          ("INVALID_CORRELATIONID", InternalError),
          ("MISSING_EXEMPTION_REASON", InternalError),
          ("MISSING_EXEMPTION_INDICATOR", InternalError),
          ("ALLOWANCE_NOT_SUPPORTED", RuleAllowanceNotSupportedError),
          ("NOT_FOUND", NotFoundError),
          ("NOT_FOUND_INCOME_SOURCE", NotFoundError),
          ("GONE", InternalError),
          ("SERVER_ERROR", InternalError),
          ("BAD_GATEWAY", InternalError),
          ("SERVICE_UNAVAILABLE", InternalError)
        )

        val extraTysErrors = Seq(
          ("INVALID_INCOME_SOURCE_ID", BusinessIdFormatError),
          ("INVALID_CORRELATION_ID", InternalError),
          ("INCOME_SOURCE_NOT_FOUND", NotFoundError),
          ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError),
        )

        (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
      }
    }
  }

}
