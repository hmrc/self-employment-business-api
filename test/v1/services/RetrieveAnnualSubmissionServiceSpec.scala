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

import api.controllers.EndpointLogContext
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import v1.mocks.connectors.MockRetrieveAnnualSubmissionConnector
import v1.models.request.retrieveAnnual.RetrieveAnnualSubmissionRequestData
import v1.models.response.retrieveAnnual._

import scala.concurrent.Future

class RetrieveAnnualSubmissionServiceSpec extends ServiceSpec with RetrieveAnnualSubmissionFixture {

  val nino: String                   = "AA123456A"
  val businessId: String             = "XAIS12345678910"
  val taxYear: String                = "2019-20"
  implicit val correlationId: String = "X-123"

  val response: RetrieveAnnualSubmissionResponse = RetrieveAnnualSubmissionResponse(
    allowances = Some(Allowances(None, None, None, None, None, None, None, None, None, None, None, None, None)),
    adjustments = Some(Adjustments(None, None, None, None, None, None, None, None, None)),
    nonFinancials = Some(NonFinancials(businessDetailsChangedRecently = true, None))
  )

  private val requestData = RetrieveAnnualSubmissionRequestData(
    nino = Nino(nino),
    BusinessId(businessId),
    TaxYear.fromMtd(taxYear)
  )

  trait Test extends MockRetrieveAnnualSubmissionConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new RetrieveAnnualSubmissionService(
      connector = mockRetrieveAnnualSubmissionConnector
    )

  }

  "service" should {
    "service call successful" when {
      "return mapped result" in new Test {
        MockRetrieveConnector
          .retrieveAnnualSubmission(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieveAnnualSubmission(requestData)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockRetrieveConnector
            .retrieveAnnualSubmission(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.retrieveAnnualSubmission(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = Seq(
        "INVALID_NINO"            -> NinoFormatError,
        "INVALID_INCOMESOURCEID"  -> BusinessIdFormatError,
        "INVALID_TAX_YEAR"        -> TaxYearFormatError,
        "INCOME_SOURCE_NOT_FOUND" -> NotFoundError,
        "NOT_FOUND_PERIOD"        -> NotFoundError,
        "INVALID_CORRELATIONID"   -> InternalError,
        "SERVER_ERROR"            -> InternalError,
        "SERVICE_UNAVAILABLE"     -> InternalError
      )

      val extraTysErrors = Seq(
        "INVALID_INCOMESOURCE_ID"       -> BusinessIdFormatError,
        "INVALID_DELETED_RETURN_PERIOD" -> InternalError,
        "INVALID_CORRELATION_ID"        -> InternalError,
        "INCOME_DATA_SOURCE_NOT_FOUND"  -> NotFoundError,
        "SUBMISSION_DATA_NOT_FOUND"     -> NotFoundError,
        "TAX_YEAR_NOT_SUPPORTED"        -> RuleTaxYearNotSupportedError
      )

      (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
    }
  }

}
