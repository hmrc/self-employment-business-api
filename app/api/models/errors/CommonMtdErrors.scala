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

package api.models.errors

import play.api.http.Status._

// Format Errors
object NinoFormatError extends MtdError(code = "FORMAT_NINO", message = "The provided NINO is invalid", BAD_REQUEST)

object TaxYearFormatError extends MtdError(code = "FORMAT_TAX_YEAR", message = "The provided tax year is invalid", BAD_REQUEST)

object BusinessIdFormatError extends MtdError(code = "FORMAT_BUSINESS_ID", message = "The provided Business ID is invalid", BAD_REQUEST)

object PeriodIdFormatError extends MtdError(code = "FORMAT_PERIOD_ID", message = "The provided Period ID is invalid", BAD_REQUEST)

object ValueFormatError extends MtdError(code = "FORMAT_VALUE", message = "The value must be between 0 and 99999999999.99", BAD_REQUEST) {

  def forPathAndRange(path: String, min: String, max: String): MtdError =
    ValueFormatError.copy(paths = Some(Seq(path)), message = s"The value must be between $min and $max")

}

object FromDateFormatError extends MtdError(code = "FORMAT_FROM_DATE", message = "The provided From date is invalid", BAD_REQUEST)

object ToDateFormatError extends MtdError(code = "FORMAT_TO_DATE", message = "The provided To date is invalid", BAD_REQUEST)

object StartDateFormatError extends MtdError(code = "FORMAT_START_DATE", message = "The provided Start date is invalid", BAD_REQUEST)

object EndDateFormatError extends MtdError(code = "FORMAT_END_DATE", message = "The provided End date is invalid", BAD_REQUEST)

object StringFormatError extends MtdError(code = "FORMAT_STRING", message = "The supplied string format is not valid", BAD_REQUEST)

object DateFormatError extends MtdError(code = "FORMAT_DATE", message = "The supplied date format is not valid", BAD_REQUEST)

object Class4ExemptionReasonFormatError
    extends MtdError(
      code = "FORMAT_CLASS_4_EXEMPTION_REASON",
      message = "The format of the supplied Class 4 National Insurance exemption reason is not valid",
      BAD_REQUEST)

// Rule Errors
object RuleInvalidSubmissionPeriodError
    extends MtdError(
      code = "RULE_INVALID_SUBMISSION_PERIOD",
      message = "Self Employment submissions cannot be more than 10 days before the end of the Period",
      BAD_REQUEST)

object RuleInvalidSubmissionEndDateError
    extends MtdError(code = "RULE_INVALID_SUBMISSION_END_DATE", message = "The submitted end date must be the end of the quarter", BAD_REQUEST)

object RuleTaxYearNotSupportedError
    extends MtdError(code = "RULE_TAX_YEAR_NOT_SUPPORTED", message = "The tax year specified does not lie within the supported range", BAD_REQUEST)

object RuleTaxYearRangeInvalidError
    extends MtdError(code = "RULE_TAX_YEAR_RANGE_INVALID", message = "Tax year range invalid. A tax year range of one year is required", BAD_REQUEST)

object RuleOverlappingPeriod
    extends MtdError(code = "RULE_OVERLAPPING_PERIOD", message = "Period summary overlaps with any of the existing period summaries", BAD_REQUEST)

object RuleMisalignedPeriod
    extends MtdError(code = "RULE_MISALIGNED_PERIOD", message = "Period summary is not within the accounting period", BAD_REQUEST)

object RuleNotContiguousPeriod extends MtdError(code = "RULE_NOT_CONTIGUOUS_PERIOD", message = "Period summaries are not contiguous", BAD_REQUEST)

object RuleNotAllowedConsolidatedExpenses
    extends MtdError(
      code = "RULE_NOT_ALLOWED_CONSOLIDATED_EXPENSES",
      message = "Consolidated expenses are not allowed if the accumulative turnover amount exceeds the threshold",
      BAD_REQUEST)

object RuleToDateBeforeFromDateError
    extends MtdError(code = "RULE_TO_DATE_BEFORE_FROM_DATE", message = "The To date cannot be earlier than the From date", BAD_REQUEST)

object RuleEndDateBeforeStartDateError
    extends MtdError(code = "RULE_END_DATE_BEFORE_START_DATE", message = "The End date cannot be earlier than the Start date", BAD_REQUEST)

object RuleBothExpensesSuppliedError
    extends MtdError(
      code = "RULE_BOTH_EXPENSES_SUPPLIED",
      message = "Both expenses and consolidatedExpenses can not be present at the same time",
      BAD_REQUEST)

object RuleBothAllowancesSuppliedError
    extends MtdError(
      code = "RULE_BOTH_ALLOWANCES_SUPPLIED",
      message = "Both allowances and trading allowances must not be present at the same time",
      BAD_REQUEST)

object RuleAllowanceNotSupportedError
    extends MtdError(
      code = "RULE_ALLOWANCE_NOT_SUPPORTED",
      message = "One or more of supplied allowances (electricChargePointAllowance, zeroEmissionsCarAllowance, structuredBuildingAllowance, enhancedStructuredBuildingAllowance) is not supported for the supplied tax year",
      BAD_REQUEST)

object RuleBuildingNameNumberError
    extends MtdError(code = "RULE_BUILDING_NAME_NUMBER", message = "Postcode must be supplied along with at least one of name or number", BAD_REQUEST)

object RuleIncorrectOrEmptyBodyError
    extends MtdError(code = "RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED", message = "An empty or non-matching body was submitted", BAD_REQUEST)

object RuleDuplicateSubmissionError
    extends MtdError(code = "RULE_DUPLICATE_SUBMISSION", message = "A summary has already been submitted for the period specified", BAD_REQUEST)

object BVRError extends MtdError(code = "BUSINESS_ERROR", message = "Business validation error", BAD_REQUEST)

object BadRequestError extends MtdError(code = "INVALID_REQUEST", message = "Invalid request", BAD_REQUEST)

object InvalidTaxYearParameterError
    extends MtdError(code = "INVALID_TAX_YEAR_PARAMETER", message = "A tax year before 2023-24 was supplied", BAD_REQUEST)

// Standard Errors
object NotFoundError
    extends MtdError(
      code = "MATCHING_RESOURCE_NOT_FOUND",
      message = "Matching resource not found",
      NOT_FOUND
    )

object InternalError
    extends MtdError(
      code = "INTERNAL_SERVER_ERROR",
      message = "An internal server error occurred",
      INTERNAL_SERVER_ERROR
    )

object ServiceUnavailableError
    extends MtdError(
      code = "SERVICE_UNAVAILABLE",
      message = "Internal server error",
      INTERNAL_SERVER_ERROR
    )

// Authorisation Errors
object ClientNotAuthorisedError
    extends MtdError(code = "CLIENT_OR_AGENT_NOT_AUTHORISED", message = "The client and/or agent is not authorised", FORBIDDEN)

object ClientNotAuthenticatedError
    extends MtdError(
      code = "CLIENT_OR_AGENT_NOT_AUTHORISED",
      message = "The client and/or agent is not authorised",
      UNAUTHORIZED
    )

object InvalidBearerTokenError
    extends MtdError(
      code = "UNAUTHORIZED",
      message = "Bearer token is missing or not authorized",
      UNAUTHORIZED
    )

// Accept header Errors
object InvalidAcceptHeaderError
    extends MtdError(
      code = "ACCEPT_HEADER_INVALID",
      message = "The accept header is missing or invalid",
      NOT_ACCEPTABLE
    )

object UnsupportedVersionError
    extends MtdError(
      code = "NOT_FOUND",
      message = "The requested resource could not be found",
      NOT_FOUND
    )

object InvalidBodyTypeError
    extends MtdError(
      code = "INVALID_BODY_TYPE",
      message = "Expecting text/json or application/json body",
      UNSUPPORTED_MEDIA_TYPE
    )

//Stub Errors
object RuleIncorrectGovTestScenarioError extends MtdError("RULE_INCORRECT_GOV_TEST_SCENARIO", "The Gov-Test-Scenario was not found", BAD_REQUEST)
