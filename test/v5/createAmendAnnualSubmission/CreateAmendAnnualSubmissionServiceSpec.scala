/*
 * Copyright 2026 HM Revenue & Customs
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

import api.models.errors.*
import shared.connectors.DownstreamOutcome
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors.DownstreamErrors.single
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v5.createAmendAnnualSubmission.def1.model.request.*
import v5.createAmendAnnualSubmission.model.request.Def1_CreateAmendAnnualSubmissionRequestData

import scala.concurrent.Future.successful

class CreateAmendAnnualSubmissionServiceSpec extends ServiceSpec with Def1_CreateAmendAnnualSubmissionFixture {
  val nino: String       = "AA987654A"
  val businessId: String = "XAIS10987654321"
  val taxYear: String    = "2017-18"

  private val requestData = Def1_CreateAmendAnnualSubmissionRequestData(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    taxYear = TaxYear.fromMtd(taxYear),
    body = createAmendAnnualSubmissionRequestBody()
  )

  private trait Test extends MockCreateAmendAnnualSubmissionConnector {
    val service = new CreateAmendAnnualSubmissionService(connector = mockAmendAnnualSubmissionConnector)
  }

  "CreateAmendAnnualSubmissionService" when {
    "the connector call is successful" should {
      "return Right" in new Test {
        val outcome: DownstreamOutcome[Unit] = Right(ResponseWrapper(correlationId, ()))

        MockAmendAnnualSubmissionConnector
          .amendAnnualSubmission(requestData)
          .returns(successful(outcome))

        await(service.createAmendAnnualSubmission(requestData)) shouldBe outcome
      }
    }

    "the connector call is unsuccessful" should {
      "map errors according to spec" when {
        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a code $downstreamErrorCode error is returned from the service" in new Test {
            MockAmendAnnualSubmissionConnector
              .amendAnnualSubmission(requestData)
              .returns(successful(Left(ResponseWrapper(correlationId, single(DownstreamErrorCode(downstreamErrorCode))))))

            await(service.createAmendAnnualSubmission(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors: Seq[(String, MtdError)] = Seq(
          "INVALID_NINO"                -> NinoFormatError,
          "INVALID_TAX_YEAR"            -> TaxYearFormatError,
          "INVALID_INCOME_SOURCE"       -> BusinessIdFormatError,
          "INVALID_PAYLOAD"             -> InternalError,
          "INVALID_CORRELATIONID"       -> InternalError,
          "MISSING_EXEMPTION_REASON"    -> InternalError,
          "MISSING_EXEMPTION_INDICATOR" -> InternalError,
          "ALLOWANCE_NOT_SUPPORTED"     -> RuleAllowanceNotSupportedError,
          "NOT_FOUND"                   -> NotFoundError,
          "NOT_FOUND_INCOME_SOURCE"     -> NotFoundError,
          "GONE"                        -> InternalError,
          "SERVER_ERROR"                -> InternalError,
          "BAD_GATEWAY"                 -> InternalError,
          "SERVICE_UNAVAILABLE"         -> InternalError
        )

        val extraTysErrors: Seq[(String, MtdError)] = Seq(
          "INVALID_INCOMESOURCE_ID"    -> BusinessIdFormatError,
          "INVALID_CORRELATION_ID"     -> InternalError,
          "INCOME_SOURCE_NOT_FOUND"    -> NotFoundError,
          "TAX_YEAR_NOT_SUPPORTED"     -> RuleTaxYearNotSupportedError,
          "WRONG_TPA_AMOUNT_SUBMITTED" -> RuleWrongTpaAmountSubmittedError,
          "OUTSIDE_AMENDMENT_WINDOW"   -> RuleOutsideAmendmentWindowError
        )

        (errors ++ extraTysErrors).foreach(serviceError.tupled)
      }
    }
  }

}
