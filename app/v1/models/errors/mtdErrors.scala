/*
 * Copyright 2021 HM Revenue & Customs
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

import play.api.libs.json.{Json, OWrites}

case class MtdError(code: String, message: String, paths: Option[Seq[String]] = None)

object MtdError {
  implicit val writes: OWrites[MtdError] = Json.writes[MtdError]

  implicit def genericWrites[T <: MtdError]: OWrites[T] =
    writes.contramap[T](c => c: MtdError)
}

object MtdErrorWithCustomMessage {
  def unapply(arg: MtdError): Option[String] = Some(arg.code)
}

// Format Errors
object NinoFormatError extends MtdError(
  code = "FORMAT_NINO",
  message = "The provided NINO is invalid"
)
object TaxYearFormatError extends MtdError(
  code = "FORMAT_TAX_YEAR",
  message = "The provided tax year is invalid"
)

object BusinessIdFormatError extends MtdError(
  code = "FORMAT_BUSINESS_ID",
  message = "The provided Business ID is invalid"
)

object PeriodIdFormatError extends MtdError(
  code = "FORMAT_PERIOD_ID",
  message = "The provided Period ID is invalid"
)

object ValueFormatError extends MtdError(
  code = "FORMAT_VALUE",
  message = "One or more monetary fields are invalid"
)

object FromDateFormatError extends MtdError(
  code = "FORMAT_FROM_DATE",
  message = "The provided From date is invalid"
)

object ToDateFormatError extends MtdError(
  code = "FORMAT_TO_DATE",
  message = "The provided To date is invalid"
)

// Rule Errors
object RuleTaxYearNotSupportedError extends MtdError(
  code = "RULE_TAX_YEAR_NOT_SUPPORTED",
  message = "The tax year specified is before the minimum tax year value"
)

object RuleTaxYearRangeInvalidError extends MtdError(
  code = "RULE_TAX_YEAR_RANGE_INVALID",
  message = "The Tax year range is invalid"
)

object RuleOverlappingPeriod extends MtdError(
  code = "RULE_OVERLAPPING_PERIOD",
  message = "Period summary overlaps with any of the existing period summaries"
)

object RuleMisalignedPeriod extends MtdError(
  code = "RULE_MISALIGNED_PERIOD",
  message = "Period summary is not within the accounting period"
)

object RuleNotContiguousPeriod extends MtdError(
  code = "RULE_NOT_CONTIGUOUS_PERIOD",
  message = "Period summaries are not contiguous"
)

object RuleNotAllowedConsolidatedExpenses extends MtdError(
  code = "RULE_NOT_ALLOWED_CONSOLIDATED_EXPENSES",
  message = "Consolidated expenses are not allowed if the accumulative turnover amount exceeds the threshold"
)

object RuleToDateBeforeFromDateError extends MtdError(
  code = "RULE_TO_DATE_BEFORE_FROM_DATE",
  message = "The To date cannot be earlier than the From date"
)

object RuleBothExpensesSuppliedError extends MtdError(
  code = "RULE_BOTH_EXPENSES_SUPPLIED",
  message = "Both expenses and consolidatedExpenses can not be present at the same time"
)

object RuleIncorrectOrEmptyBodyError extends MtdError(
  code = "RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED",
  message = "An empty or non-matching body was submitted"
)

// Standard Errors
object NotFoundError extends MtdError(
  code = "MATCHING_RESOURCE_NOT_FOUND",
  message = "Matching resource not found"
)

object DownstreamError extends MtdError(
  code = "INTERNAL_SERVER_ERROR",
  message = "An internal server error occurred"
)

object BadRequestError extends MtdError(
  code = "INVALID_REQUEST",
  message = "Invalid request"
)

object BVRError extends MtdError(
  code = "BUSINESS_ERROR",
  message = "Business validation error"
)

object ServiceUnavailableError extends MtdError(
  code = "SERVICE_UNAVAILABLE",
  message = "Internal server error"
)

// Authorisation Errors
object UnauthorisedError extends MtdError(
  code = "CLIENT_OR_AGENT_NOT_AUTHORISED",
  message = "The client and/or agent is not authorised"
)

object InvalidBearerTokenError extends MtdError(
  code = "UNAUTHORIZED",
  message = "Bearer token is missing or not authorized"
)

// Accept header Errors
object InvalidAcceptHeaderError extends MtdError(
  code = "ACCEPT_HEADER_INVALID",
  message = "The accept header is missing or invalid"
)

object UnsupportedVersionError extends MtdError(
  code = "NOT_FOUND",
  message = "The requested resource could not be found"
)

object InvalidBodyTypeError extends MtdError(
  code = "INVALID_BODY_TYPE",
  message = "Expecting text/json or application/json body"
)