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
import v1.mocks.connectors.MockDeleteAnnualSubmissionConnector
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.deleteAnnual.DeleteAnnualSubmissionRequest

import scala.concurrent.Future

class DeleteAnnualSubmissionServiceSpec extends ServiceSpec {

  val taxYear: String                = "2017-18"
  val nino: String                   = "AA123456A"
  val businessId: String             = "XAIS12345678910"
  implicit val correlationId: String = "X-123"

  private val requestData = DeleteAnnualSubmissionRequest(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    taxYear = TaxYear.fromMtd(taxYear)
  )

  trait Test extends MockDeleteAnnualSubmissionConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new DeleteAnnualSubmissionService(
      connector = mockDeleteAnnualSubmissionConnector
    )

  }

  "service" should {
    "service call successful" when {
      "return mapped result" in new Test {
        MockDeleteAnnualSubmissionConnector
          .deleteAnnualSubmission(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        await(service.deleteAnnualSubmission(requestData)) shouldBe Right(ResponseWrapper(correlationId, ()))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {
      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockDeleteAnnualSubmissionConnector
            .deleteAnnualSubmission(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(desErrorCode))))))

          await(service.deleteAnnualSubmission(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(
        "INVALID_NINO"                -> NinoFormatError,
        "INVALID_TAX_YEAR"            -> TaxYearFormatError,
        "INVALID_INCOME_SOURCE"       -> BusinessIdFormatError,
        "INVALID_CORRELATIONID"       -> InternalError,
        "INVALID_PAYLOAD"             -> InternalError,
        "MISSING_EXEMPTION_REASON"    -> InternalError,
        "MISSING_EXEMPTION_INDICATOR" -> InternalError,
        "ALLOWANCE_NOT_SUPPORTED"     -> InternalError,
        "NOT_FOUND"                   -> NotFoundError,
        "NOT_FOUND_INCOME_SOURCE"     -> NotFoundError,
        "GONE"                        -> NotFoundError,
        "SERVER_ERROR"                -> InternalError,
        "BAD_GATEWAY"                 -> InternalError,
        "SERVICE_UNAVAILABLE"         -> InternalError
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }

}
