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
import shared.models.errors.MtdError

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

object RuleInvalidSubmissionPeriodError
    extends MtdError(
      code = "RULE_INVALID_SUBMISSION_PERIOD",
      message = "Self Employment submissions cannot be more than 10 days before the end of the Period",
      BAD_REQUEST)

object RuleInvalidSubmissionEndDateError
    extends MtdError(code = "RULE_INVALID_SUBMISSION_END_DATE", message = "The submitted end date must be the end of the quarter", BAD_REQUEST)

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
