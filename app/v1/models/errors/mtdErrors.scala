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

package v1.models.errors

import play.api.libs.json.{ Json, OWrites }

sealed case class MtdError(code: String, message: String, paths: Option[Seq[String]] = None)

sealed trait FormatError
sealed trait RuleError
sealed trait InternalError
sealed trait AuthorisationError
sealed trait UserError

object MtdError {
  implicit val writes: OWrites[MtdError] = Json.writes[MtdError]

  implicit def genericWrites[T <: MtdError]: OWrites[T] =
    writes.contramap[T](c => c: MtdError)
}

object MtdErrorWithCustomMessage {
  def unapply(arg: MtdError): Option[String] = Some(arg.code)
}

// Format Errors
object NinoFormatError
    extends MtdError(
      code = "FORMAT_NINO",
      message = "The provided NINO is invalid"
    )
    with FormatError

object TaxYearFormatError
    extends MtdError(
      code = "FORMAT_TAX_YEAR",
      message = "The provided tax year is invalid"
    )
    with FormatError

object BusinessIdFormatError
    extends MtdError(
      code = "FORMAT_BUSINESS_ID",
      message = "The provided Business ID is invalid"
    )
    with FormatError

object PeriodIdFormatError
    extends MtdError(
      code = "FORMAT_PERIOD_ID",
      message = "The provided Period ID is invalid"
    )
    with FormatError

object ValueFormatError
    extends MtdError(
      code = "FORMAT_VALUE",
      message = "One or more monetary fields are invalid"
    )
    with FormatError

object FromDateFormatError
    extends MtdError(
      code = "FORMAT_FROM_DATE",
      message = "The provided From date is invalid"
    )
    with FormatError

object ToDateFormatError
    extends MtdError(
      code = "FORMAT_TO_DATE",
      message = "The provided To date is invalid"
    )
    with FormatError

// Rule Errors
object RuleTaxYearNotSupportedError
    extends MtdError(
      code = "RULE_TAX_YEAR_NOT_SUPPORTED",
      message = "The tax year specified is before the minimum tax year value"
    )
    with RuleError

object RuleTaxYearRangeInvalidError
    extends MtdError(
      code = "RULE_TAX_YEAR_RANGE_INVALID",
      message = "The Tax year range is invalid"
    )
    with RuleError

object RuleOverlappingPeriod
    extends MtdError(
      code = "RULE_OVERLAPPING_PERIOD",
      message = "Period summary overlaps with any of the existing period summaries"
    )
    with RuleError

object RuleMisalignedPeriod
    extends MtdError(
      code = "RULE_MISALIGNED_PERIOD",
      message = "Period summary is not within the accounting period"
    )
    with RuleError

object RuleNotContiguousPeriod
    extends MtdError(
      code = "RULE_NOT_CONTIGUOUS_PERIOD",
      message = "Period summaries are not contiguous"
    )
    with RuleError

object RuleNotAllowedConsolidatedExpenses
    extends MtdError(
      code = "RULE_NOT_ALLOWED_CONSOLIDATED_EXPENSES",
      message = "Consolidated expenses are not allowed if the accumulative turnover amount exceeds the threshold"
    )
    with RuleError

object RuleToDateBeforeFromDateError
    extends MtdError(
      code = "RULE_TO_DATE_BEFORE_FROM_DATE",
      message = "The To date cannot be earlier than the From date"
    )
    with RuleError

object RuleBothExpensesSuppliedError
    extends MtdError(
      code = "RULE_BOTH_EXPENSES_SUPPLIED",
      message = "Both expenses and consolidatedExpenses can not be present at the same time"
    )
    with RuleError

object RuleIncorrectOrEmptyBodyError
    extends MtdError(
      code = "RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED",
      message = "An empty or non-matching body was submitted"
    )
    with RuleError

object BVRError
    extends MtdError(
      code = "BUSINESS_ERROR",
      message = "Business validation error"
    )
    with RuleError

object BadRequestError
    extends MtdError(
      code = "INVALID_REQUEST",
      message = "Invalid request"
    )
    with RuleError

// Standard Errors
object NotFoundError
    extends MtdError(
      code = "MATCHING_RESOURCE_NOT_FOUND",
      message = "Matching resource not found"
    ) with UserError

object DownstreamError
    extends MtdError(
      code = "INTERNAL_SERVER_ERROR",
      message = "An internal server error occurred"
    )
    with InternalError

object ServiceUnavailableError
    extends MtdError(
      code = "SERVICE_UNAVAILABLE",
      message = "Internal server error"
    )
    with InternalError

// Authorisation Errors
object UnauthorisedError
    extends MtdError(
      code = "CLIENT_OR_AGENT_NOT_AUTHORISED",
      message = "The client and/or agent is not authorised"
    )
    with InternalError

object InvalidBearerTokenError
    extends MtdError(
      code = "UNAUTHORIZED",
      message = "Bearer token is missing or not authorized"
    )
    with InternalError

// Accept header Errors
object InvalidAcceptHeaderError
    extends MtdError(
      code = "ACCEPT_HEADER_INVALID",
      message = "The accept header is missing or invalid"
    )
    with InternalError

object UnsupportedVersionError
    extends MtdError(
      code = "NOT_FOUND",
      message = "The requested resource could not be found"
    )
    with InternalError

object InvalidBodyTypeError
    extends MtdError(
      code = "INVALID_BODY_TYPE",
      message = "Expecting text/json or application/json body"
    )
    with InternalError
