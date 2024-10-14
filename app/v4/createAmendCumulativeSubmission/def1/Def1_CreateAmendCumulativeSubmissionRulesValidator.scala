/*
 * Copyright 2024 HM Revenue & Customs
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

package v4.createAmendCumulativeSubmission.def1

import api.models.errors.RuleBothExpensesSuppliedError
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.toFoldableOps
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.{ResolveDateRange, ResolveParsedNumber}
import shared.models.errors.{MtdError, RuleEndBeforeStartDateError, RuleTaxYearNotSupportedError}
import v4.createAmendCumulativeSubmission.def1.model.request.{PeriodDisallowableExpenses, PeriodExpenses}
import v4.createAmendCumulativeSubmission.model.request.{Create_PeriodIncome, Def1_CreateAmendCumulativeSubmissionRequestData}

import java.time.LocalDate

case class Def1_CreateAmendCumulativeSubmissionRulesValidator() extends RulesValidator[Def1_CreateAmendCumulativeSubmissionRequestData] {

  private val minDate            = LocalDate.of(1900, 4, 6)
  private val maxDate: LocalDate = LocalDate.of(3025, 4, 5)

  private val resolveMaybeNegativeParsedNumber = ResolveParsedNumber(min = -99999999999.99)

  private val resolveDateRange = ResolveDateRange(RuleTaxYearNotSupportedError, RuleTaxYearNotSupportedError, RuleEndBeforeStartDateError)

  def validateBusinessRules(
      parsed: Def1_CreateAmendCumulativeSubmissionRequestData): Validated[Seq[MtdError], Def1_CreateAmendCumulativeSubmissionRequestData] = {
    import parsed.body._

    combine(
      validateDates(periodDates.periodStartDate, periodDates.periodEndDate),
      validateExpenses(periodExpenses, periodDisallowableExpenses),
      periodIncome.map(validatePeriodIncomeNumericFields()).getOrElse(valid),
      periodExpenses.map(validateAllowableNumericFields()).getOrElse(valid),
      periodDisallowableExpenses.map(validateDisllowableNumericFields()).getOrElse(valid)
    ).onSuccess(parsed)
  }

  private def validateExpenses(allowableExpenses: Option[PeriodExpenses],
                               disallowableExpenses: Option[PeriodDisallowableExpenses]): Validated[Seq[MtdError], Unit] =
    (allowableExpenses, disallowableExpenses) match {
      case (Some(allowable), Some(_)) if allowable.consolidatedExpenses.isDefined =>
        Invalid(List(RuleBothExpensesSuppliedError))
      case (Some(allowable), None) if allowable.consolidatedExpenses.isDefined =>
        allowable match {
          case PeriodExpenses(Some(_), None, None, None, None, None, None, None, None, None, None, None, None, None, None, None) =>
            valid
          case _ =>
            Invalid(List(RuleBothExpensesSuppliedError))
        }
      case _ =>
        valid
    }

  private def validateDates(periodStartDate: String, periodEndDate: String): Validated[Seq[MtdError], Unit] = {
    resolveDateRange.withDatesLimitedTo(minDate, maxDate)(periodStartDate -> periodEndDate).toUnit
  }

  private def validatePeriodIncomeNumericFields()(periodIncome: Create_PeriodIncome): Validated[Seq[MtdError], Unit] =
    List(
      (periodIncome.other, "/periodIncome/other"),
      (periodIncome.turnover, "/periodIncome/turnover")
    ).traverse_ { case (value, path) =>
      resolveMaybeNegativeParsedNumber(value, path)
    }

  private def validateAllowableNumericFields()(expenses: PeriodExpenses): Validated[Seq[MtdError], Unit] = {
    import expenses._

    val conditionalMaybeNegativeExpenses = List(
      (consolidatedExpenses, "/periodExpenses/consolidatedExpenses"),
      (paymentsToSubcontractors, "/periodExpenses/paymentsToSubcontractors"),
      (wagesAndStaffCosts, "/periodExpenses/wagesAndStaffCosts"),
      (carVanTravelExpenses, "/periodExpenses/carVanTravelExpenses"),
      (adminCosts, "/periodExpenses/adminCosts"),
      (businessEntertainmentCosts, "/periodExpenses/businessEntertainmentCosts"),
      (advertisingCosts, "/periodExpenses/advertisingCosts")
    )

    val maybeNegativeExpenses = List(
      (costOfGoods, "/periodExpenses/costOfGoods"),
      (premisesRunningCosts, "/periodExpenses/premisesRunningCosts"),
      (maintenanceCosts, "/periodExpenses/maintenanceCosts"),
      (interestOnBankOtherLoans, "/periodExpenses/interestOnBankOtherLoans"),
      (financeCharges, "/periodExpenses/financeCharges"),
      (irrecoverableDebts, "/periodExpenses/irrecoverableDebts"),
      (professionalFees, "/periodExpenses/professionalFees"),
      (depreciation, "/periodExpenses/depreciation"),
      (otherExpenses, "/periodExpenses/otherExpenses")
    )

    (conditionalMaybeNegativeExpenses ++ maybeNegativeExpenses).traverse_ { case (value, path) =>
      resolveMaybeNegativeParsedNumber(value, path)
    }

  }

  private def validateDisllowableNumericFields()(expenses: PeriodDisallowableExpenses): Validated[Seq[MtdError], Unit] = {
    import expenses._

    val conditionalMaybeNegativeExpenses = List(
      (paymentsToSubcontractorsDisallowable, "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable"),
      (wagesAndStaffCostsDisallowable, "/periodDisallowableExpenses/wagesAndStaffCostsDisallowable"),
      (carVanTravelExpensesDisallowable, "/periodDisallowableExpenses/carVanTravelExpensesDisallowable"),
      (adminCostsDisallowable, "/periodDisallowableExpenses/adminCostsDisallowable"),
      (businessEntertainmentCostsDisallowable, "/periodDisallowableExpenses/businessEntertainmentCostsDisallowable"),
      (advertisingCostsDisallowable, "/periodDisallowableExpenses/advertisingCostsDisallowable"),
      (professionalFeesDisallowable, "/periodDisallowableExpenses/professionalFeesDisallowable"),
      (otherExpensesDisallowable, "/periodDisallowableExpenses/otherExpensesDisallowable")
    )

    val maybeNegativeExpenses = List(
      (costOfGoodsDisallowable, "/periodDisallowableExpenses/costOfGoodsDisallowable"),
      (premisesRunningCostsDisallowable, "/periodDisallowableExpenses/premisesRunningCostsDisallowable"),
      (maintenanceCostsDisallowable, "/periodDisallowableExpenses/maintenanceCostsDisallowable"),
      (interestOnBankOtherLoansDisallowable, "/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable"),
      (financeChargesDisallowable, "/periodDisallowableExpenses/financeChargesDisallowable"),
      (irrecoverableDebtsDisallowable, "/periodDisallowableExpenses/irrecoverableDebtsDisallowable"),
      (depreciationDisallowable, "/periodDisallowableExpenses/depreciationDisallowable")
    )

    (conditionalMaybeNegativeExpenses ++ maybeNegativeExpenses).traverse_ { case (value, path) =>
      resolveMaybeNegativeParsedNumber(value, path)
    }

  }

}
