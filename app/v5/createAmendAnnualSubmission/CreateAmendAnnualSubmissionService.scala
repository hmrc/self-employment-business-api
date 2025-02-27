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

import api.models.errors.{RuleAllowanceNotSupportedError, RuleOutsideAmendmentWindowError, RuleWrongTpaAmountSubmittedError}
import cats.implicits._
import shared.controllers.RequestContext
import shared.models.errors._
import shared.services.{BaseService, ServiceOutcome}
import v5.createAmendAnnualSubmission.model.request.CreateAmendAnnualSubmissionRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateAmendAnnualSubmissionService @Inject() (connector: CreateAmendAnnualSubmissionConnector) extends BaseService {

  def createAmendAnnualSubmission(
      request: CreateAmendAnnualSubmissionRequestData
  )(implicit ctx: RequestContext, ec: ExecutionContext): Future[ServiceOutcome[Unit]] = {

    connector
      .amendAnnualSubmission(request)
      .map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))
  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors = Map(
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

    val extraTysErrors = Map(
      "INVALID_INCOME_SOURCE_ID"   -> BusinessIdFormatError,
      "INVALID_CORRELATION_ID"     -> InternalError,
      "INCOME_SOURCE_NOT_FOUND"    -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED"     -> RuleTaxYearNotSupportedError,
      "WRONG_TPA_AMOUNT_SUBMITTED" -> RuleWrongTpaAmountSubmittedError,
      "OUTSIDE_AMENDMENT_WINDOW"   -> RuleOutsideAmendmentWindowError
    )

    errors ++ extraTysErrors
  }

}
