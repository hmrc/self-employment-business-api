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

package v5.createAmendAnnualSubmission

import api.models.errors.{RuleAllowanceNotSupportedError, RuleOutsideAmendmentWindowError}
import shared.controllers.EndpointLogContext
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors.DownstreamErrors.single
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v5.createAmendAnnualSubmission.def1.model.request.{Def1_CreateAmendAnnualSubmissionFixture, Def1_CreateAmendAnnualSubmissionRequestBody}
import v5.createAmendAnnualSubmission.model.request.Def1_CreateAmendAnnualSubmissionRequestData

import scala.concurrent.Future.successful

class CreateAmendAnnualSubmissionServiceSpec extends ServiceSpec with Def1_CreateAmendAnnualSubmissionFixture {
  val nino: String                            = "AA987654A"
  val businessId: String                      = "XAIS10987654321"
  val taxYear: String                         = "2017-18"
  override implicit val correlationId: String = "X-123"

  private val requestData = Def1_CreateAmendAnnualSubmissionRequestData(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    taxYear = TaxYear.fromMtd(taxYear),
    body = Def1_CreateAmendAnnualSubmissionRequestBody(
      adjustments = Some(adjustments),
      allowances = Some(allowances),
      nonFinancials = Some(nonFinancials)
    )
  )

  trait Test extends MockCreateAmendAnnualSubmissionConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new CreateAmendAnnualSubmissionService(
      connector = mockAmendAnnualSubmissionConnector
    )

  }

  "service" should {
    "service call successful" when {
      "return mapped result" in new Test {
        val outcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))

        MockAmendAnnualSubmissionConnector
          .amendAnnualSubmission(requestData)
          .returns(successful(outcome))

        await(service.createAmendAnnualSubmission(requestData)) shouldBe outcome
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {
          MockAmendAnnualSubmissionConnector
            .amendAnnualSubmission(requestData)
            .returns(successful(Left(ResponseWrapper(correlationId, single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.createAmendAnnualSubmission(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input: Seq[(String, MtdError)] = Seq(
        "INVALID_NINO"                -> NinoFormatError,
        "INVALID_TAX_YEAR"            -> TaxYearFormatError,
        "INVALID_INCOME_SOURCE"       -> BusinessIdFormatError,
        "OUTSIDE_AMENDMENT_WINDOW"    -> RuleOutsideAmendmentWindowError,
        "ALLOWANCE_NOT_SUPPORTED"     -> RuleAllowanceNotSupportedError,
        "INVALID_CORRELATIONID"       -> InternalError,
        "INVALID_PAYLOAD"             -> InternalError,
        "MISSING_EXEMPTION_REASON"    -> InternalError,
        "MISSING_EXEMPTION_INDICATOR" -> InternalError,
        "NOT_FOUND"                   -> NotFoundError,
        "NOT_FOUND_INCOME_SOURCE"     -> NotFoundError,
        "GONE"                        -> InternalError,
        "SERVER_ERROR"                -> InternalError,
        "BAD_GATEWAY"                 -> InternalError,
        "SERVICE_UNAVAILABLE"         -> InternalError
      )

      input.foreach(args => serviceError.tupled(args))
    }
  }

}
