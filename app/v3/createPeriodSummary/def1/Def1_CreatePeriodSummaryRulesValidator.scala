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

package v3.createPeriodSummary.def1

import api.controllers.validators.RulesValidator
import api.controllers.validators.resolvers.{ResolveDateRange, ResolveParsedNumber}
import api.models.errors.{MtdError, RuleBothExpensesSuppliedError}
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.toFoldableOps
import v3.createPeriodSummary.def1.model.request.{
  Def1_Create_PeriodDisallowableExpenses,
  Def1_Create_PeriodExpenses,
  Def1_Create_PeriodIncome
}
import v3.createPeriodSummary.model.request.Def1_CreatePeriodSummaryRequestData

case class Def1_CreatePeriodSummaryRulesValidator(includeNegatives: Boolean) extends RulesValidator[Def1_CreatePeriodSummaryRequestData] {

  private val minYear = 1900
  private val maxYear = 2100

  private val resolveNonNegativeParsedNumber   = ResolveParsedNumber()
  private val resolveMaybeNegativeParsedNumber = ResolveParsedNumber(min = -99999999999.99)

  def validateBusinessRules(parsed: Def1_CreatePeriodSummaryRequestData): Validated[Seq[MtdError], Def1_CreatePeriodSummaryRequestData] = {
    import parsed.body._
    combine(
      validateDates(periodDates.periodStartDate, periodDates.periodEndDate),
      validateExpenses(periodExpenses, periodDisallowableExpenses),
      periodIncome.map(validatePeriodIncomeNumericFields(includeNegatives)).getOrElse(valid),
      periodExpenses.map(validateAllowableNumericFields(includeNegatives)).getOrElse(valid),
      periodDisallowableExpenses.map(validateDisllowableNumericFields(includeNegatives)).getOrElse(valid)
    ).onSuccess(parsed)
  }

  private def validateExpenses(allowableExpenses: Option[Def1_Create_PeriodExpenses],
                               disallowableExpenses: Option[Def1_Create_PeriodDisallowableExpenses]): Validated[Seq[MtdError], Unit] =
    (allowableExpenses, disallowableExpenses) match {
      case (Some(allowable), Some(_)) if allowable.consolidatedExpenses.isDefined =>
        Invalid(List(RuleBothExpensesSuppliedError))
      case (Some(allowable), None) if allowable.consolidatedExpenses.isDefined =>
        allowable match {
          case Def1_Create_PeriodExpenses(Some(_), None, None, None, None, None, None, None, None, None, None, None, None, None, None, None) =>
            valid
          case _ =>
            Invalid(List(RuleBothExpensesSuppliedError))
        }
      case _ =>
        valid
    }

  private def validateDates(periodStartDate: String, periodEndDate: String): Validated[Seq[MtdError], Unit] =
    ResolveDateRange.withLimits(minYear, maxYear)(periodStartDate -> periodEndDate).toUnit

  private def validatePeriodIncomeNumericFields(includeNegatives: Boolean)(periodIncome: Def1_Create_PeriodIncome): Validated[Seq[MtdError], Unit] =
    List(
      (periodIncome.other, "/periodIncome/other"),
      (periodIncome.turnover, "/periodIncome/turnover")
    ).traverse_ { case (value, path) =>
      if (includeNegatives)
        resolveMaybeNegativeParsedNumber(value, path = Some(path))
      else
        resolveNonNegativeParsedNumber(value, path = Some(path))
    }

  private def validateAllowableNumericFields(includeNegatives: Boolean)(expenses: Def1_Create_PeriodExpenses): Validated[Seq[MtdError], Unit] = {
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

    val validatedNonNegatives = (if (includeNegatives) Nil else conditionalMaybeNegativeExpenses).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path = Some(path))
    }

    val validatedMaybeNegatives =
      (if (includeNegatives) conditionalMaybeNegativeExpenses ++ maybeNegativeExpenses else maybeNegativeExpenses).traverse_ { case (value, path) =>
        resolveMaybeNegativeParsedNumber(value, path = Some(path))
      }

    combine(validatedNonNegatives, validatedMaybeNegatives)
  }

  private def validateDisllowableNumericFields(includeNegatives: Boolean)(
      expenses: Def1_Create_PeriodDisallowableExpenses): Validated[Seq[MtdError], Unit] = {
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

    val validatedNonNegatives = (if (includeNegatives) Nil else conditionalMaybeNegativeExpenses).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path = Some(path))
    }

    val validatedMaybeNegatives =
      (if (includeNegatives) conditionalMaybeNegativeExpenses ++ maybeNegativeExpenses else maybeNegativeExpenses).traverse_ { case (value, path) =>
        resolveMaybeNegativeParsedNumber(value, path = Some(path))
      }

    combine(validatedNonNegatives, validatedMaybeNegatives)
  }

}
