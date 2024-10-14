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

package v4.createAmendCumulativeSubmission

import api.models.errors._
import cats.implicits._
import shared.controllers.RequestContext
import shared.models.errors._
import shared.services.{BaseService, ServiceOutcome}
import v4.createAmendCumulativeSubmission.model.request.CreateAmendCumulativeSubmissionRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateAmendCumulativeSubmissionService @Inject() (connector: CreateAmendCumulativeSubmissionConnector) extends BaseService {

  def createAmendCumulativeSubmission(
      request: CreateAmendCumulativeSubmissionRequestData
  )(implicit ctx: RequestContext, ec: ExecutionContext): Future[ServiceOutcome[Unit]] = {

    connector
      .amendCumulativeSubmission(request)
      .map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))
  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_NINO"                       -> NinoFormatError,
      "INVALID_TAX_YEAR"                   -> TaxYearFormatError,
      "INVALID_INCOME_SOURCE"              -> BusinessIdFormatError,
      "INVALID_PAYLOAD"                    -> InternalError,
      "INVALID_CORRELATIONID"              -> InternalError,
      "MISSING_EXEMPTION_REASON"           -> InternalError,
      "MISSING_EXEMPTION_INDICATOR"        -> InternalError,
      "ALLOWANCE_NOT_SUPPORTED"            -> RuleAllowanceNotSupportedError,
      "NOT_FOUND"                          -> NotFoundError,
      "NOT_FOUND_INCOME_SOURCE"            -> NotFoundError,
      "GONE"                               -> InternalError,
      "SERVER_ERROR"                       -> InternalError,
      "BAD_GATEWAY"                        -> InternalError,
      "SERVICE_UNAVAILABLE"                -> InternalError,
      "EARLY_DATA_SUBMISSION_NOT_ACCEPTED" -> RuleEarlyDataSubmissionNotAcceptedError,
      "INVALID_SUBMISSION_END_DATE"        -> RuleAdvanceSubmissionRequiresPeriodEndDateError,
      "SUBMISSION_END_DATE_VALUE"          -> RuleSubmissionEndDateCannotMoveBackwardsError,
      "INVALID_START_DATE"                 -> RuleStartDateNotAlignedWithReportingTypeError,
      "START_DATE_NOT_ALIGNED"             -> RuleStartDateNotAlignedToCommencementDateError,
      "END_DATE_NOT_ALIGNED"               -> RuleEndDateNotAlignedWithReportingTypeError,
      "MISSING_SUBMISSION_DATES"           -> RuleMissingSubmissionDatesError,
      "START_END_DATE_NOT_ACCEPTED"        -> RuleStartAndEndDateNotAllowedError,
      "OUTSIDE_AMENDMENT_WINDOW"           -> RuleOutsideAmendmentWindowError
    )

    val extraTysErrors = Map(
      "INVALID_INCOME_SOURCE_ID"   -> BusinessIdFormatError,
      "INVALID_CORRELATION_ID"     -> InternalError,
      "INCOME_SOURCE_NOT_FOUND"    -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED"     -> RuleTaxYearNotSupportedError,
      "WRONG_TPA_AMOUNT_SUBMITTED" -> RuleWrongTpaAmountSubmittedError
    )

    errors ++ extraTysErrors
  }

}
