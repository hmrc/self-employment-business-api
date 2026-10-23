/*
 * Copyright 2025 HM Revenue & Customs
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

import play.api.http.Status.*

// MtdError types that are common across MTD APIs.

// Format Errors
object NinoFormatError extends MtdError("FORMAT_NINO", "The NINO format is invalid", BAD_REQUEST)

object TaxYearFormatError extends MtdError("FORMAT_TAX_YEAR", "The taxYear format is invalid", BAD_REQUEST)

object StartDateFormatError extends MtdError("FORMAT_START_DATE", "The provided Start date is invalid", BAD_REQUEST)

object EndDateFormatError extends MtdError("FORMAT_END_DATE", "The provided End date is invalid", BAD_REQUEST)

object DateFormatError extends MtdError(code = "FORMAT_DATE", message = "The supplied date format is not valid", BAD_REQUEST)

object BusinessIdFormatError extends MtdError("FORMAT_BUSINESS_ID", "The Business ID format is invalid", BAD_REQUEST)

object ValueFormatError extends MtdError("FORMAT_VALUE", "The value must be between 0 and 99999999999.99", BAD_REQUEST) {

  def forPathAndRange(path: String, min: String, max: String): MtdError =
    ValueFormatError.copy(paths = Some(Seq(path)), message = s"The value must be between $min and $max")

  def forPathAndMin(path: String, min: String): MtdError =
    ValueFormatError.copy(paths = Some(Seq(path)), message = s"The value must be $min or more")

}

object StringFormatError extends MtdError(code = "FORMAT_STRING", message = "The supplied string format is not valid", BAD_REQUEST)

//Standard Errors
object NotFoundError extends MtdError("MATCHING_RESOURCE_NOT_FOUND", "Matching resource not found", NOT_FOUND)

object GatewayTimeoutError extends MtdError("GATEWAY_TIMEOUT", "The request has timed out", GATEWAY_TIMEOUT)

object InternalError extends MtdError("INTERNAL_SERVER_ERROR", "An internal server error occurred", INTERNAL_SERVER_ERROR)

object BadRequestError extends MtdError("INVALID_REQUEST", "Invalid request", BAD_REQUEST)

object BVRError extends MtdError("BUSINESS_ERROR", "Business validation error", BAD_REQUEST)

object InvalidHttpMethodError extends MtdError("INVALID_HTTP_METHOD", "Invalid HTTP method", METHOD_NOT_ALLOWED)

object InvalidBodyTypeError extends MtdError("INVALID_BODY_TYPE", "Expecting text/json or application/json body", UNSUPPORTED_MEDIA_TYPE)

object InvalidTaxYearParameterError
    extends MtdError(code = "INVALID_TAX_YEAR_PARAMETER", message = "A tax year before 2023-24 was supplied", BAD_REQUEST)

//Authentication/Authorisation errors

/** Authentication OK but not allowed access to the requested resource
  */
object ClientOrAgentNotAuthorisedError extends MtdError("CLIENT_OR_AGENT_NOT_AUTHORISED", "The client or agent is not authorised", FORBIDDEN) {
  def withStatus401: MtdError = copy(httpStatus = UNAUTHORIZED)
}

object InvalidBearerTokenError extends MtdError("UNAUTHORIZED", "Bearer token is missing or not authorized", UNAUTHORIZED)

// Accept header Errors
object InvalidAcceptHeaderError extends MtdError("ACCEPT_HEADER_INVALID", "The accept header is missing or invalid", NOT_ACCEPTABLE)

object UnsupportedVersionError extends MtdError("NOT_FOUND", "The requested resource could not be found", NOT_FOUND)

// Common rule errors
object RuleTaxYearNotSupportedError
    extends MtdError("RULE_TAX_YEAR_NOT_SUPPORTED", "The tax year specified does not lie within the supported range", BAD_REQUEST)

object RuleIncorrectOrEmptyBodyError
    extends MtdError("RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED", "An empty or non-matching body was submitted", BAD_REQUEST)

object RuleTaxYearRangeInvalidError extends MtdError("RULE_TAX_YEAR_RANGE_INVALID", "A tax year range of one year is required", BAD_REQUEST)

object RuleTaxYearNotEndedError extends MtdError("RULE_TAX_YEAR_NOT_ENDED", "The specified tax year has not yet ended", BAD_REQUEST)

object RuleDateRangeInvalidError extends MtdError(code = "RULE_DATE_RANGE_INVALID", message = "The specified date range is invalid", BAD_REQUEST)

object RuleEndBeforeStartDateError
    extends MtdError("RULE_END_DATE_BEFORE_START_DATE", "The supplied accounting period end date is before the start date", BAD_REQUEST)

//Stub Errors
object RuleIncorrectGovTestScenarioError
    extends MtdError("RULE_INCORRECT_GOV_TEST_SCENARIO", "The supplied Gov-Test-Scenario is not valid", BAD_REQUEST)

object PeriodIdFormatError extends MtdError(code = "FORMAT_PERIOD_ID", message = "The provided Period ID is invalid", BAD_REQUEST)

object RuleBusinessIncomePeriodRestriction
    extends MtdError(
      "RULE_BUSINESS_INCOME_PERIOD_RESTRICTION",
      "For customers with ITSA status 'Annual' or a latent business income source, submission period has to be 6 April to 5 April",
      BAD_REQUEST)

object Class4ExemptionReasonFormatError
    extends MtdError(
      code = "FORMAT_CLASS_4_EXEMPTION_REASON",
      message = "The format of the supplied Class 4 National Insurance exemption reason is not valid",
      BAD_REQUEST)

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
      message =
        "One or more of supplied allowances (electricChargePointAllowance, zeroEmissionsCarAllowance, structuredBuildingAllowance, enhancedStructuredBuildingAllowance) is not supported for the supplied tax year",
      BAD_REQUEST)

object RuleBuildingNameNumberError
    extends MtdError(code = "RULE_BUILDING_NAME_NUMBER", message = "Postcode must be supplied along with at least one of name or number", BAD_REQUEST)

object RuleDuplicateSubmissionError
    extends MtdError(code = "RULE_DUPLICATE_SUBMISSION", message = "A summary has already been submitted for the period specified", BAD_REQUEST)

//Stub Errors
object RuleWrongTpaAmountSubmittedError
    extends MtdError(
      code = "RULE_WRONG_TPA_AMOUNT_SUBMITTED",
      message = "Transition profit acceleration value cannot be submitted without a transition profit value",
      BAD_REQUEST)

object RuleEarlyDataPeriodSummaryNotAcceptedError
    extends MtdError(
      code = "RULE_EARLY_DATA_SUBMISSION_NOT_ACCEPTED",
      message = "Cannot submit data more than 10 days before end of Period",
      BAD_REQUEST)

object RuleAdvancePeriodSummaryRequiresPeriodEndDateError
    extends MtdError(
      code = "RULE_ADVANCE_SUBMISSION_REQUIRES_PERIOD_END_DATE",
      message = "For advance submission end date must be the end of the period",
      BAD_REQUEST)

object RulePeriodSummaryEndDateCannotMoveBackwardsError
    extends MtdError(
      code = "RULE_SUBMISSION_END_DATE_CANNOT_MOVE_BACKWARDS",
      message = "Submission end date cannot be earlier than existing submission",
      BAD_REQUEST)

object RuleStartDateNotAlignedWithReportingTypeError
    extends MtdError(
      code = "RULE_START_DATE_NOT_ALIGNED_WITH_REPORTING_TYPE",
      message = "Start date does not align to the reporting type",
      BAD_REQUEST)

object RuleStartDateNotAlignedToCommencementDateError
    extends MtdError(
      code = "RULE_START_DATE_NOT_ALIGNED_TO_COMMENCEMENT_DATE",
      message = "Start date does not align to the commencement date",
      BAD_REQUEST)

object RuleEndDateNotAlignedWithReportingTypeError
    extends MtdError(code = "RULE_END_DATE_NOT_ALIGNED_WITH_REPORTING_TYPE", message = "End date does not align to the reporting type", BAD_REQUEST)

object RuleMissingPeriodSummaryDatesError
    extends MtdError(code = "RULE_MISSING_SUBMISSION_DATES", message = "Submission start/end date not provided", BAD_REQUEST)

object RuleStartAndEndDateNotAllowedError
    extends MtdError(code = "RULE_START_AND_END_DATE_NOT_ALLOWED", message = "Start/end date not accepted for annual/latent submission", BAD_REQUEST)

object RuleOutsideAmendmentWindowError
    extends MtdError(code = "RULE_OUTSIDE_AMENDMENT_WINDOW", message = "You are outside the amendment window", BAD_REQUEST)
