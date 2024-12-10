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

package v4.createAmendCumulativePeriodSummary.def1

import api.models.errors.RuleBothExpensesSuppliedError
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.toFoldableOps
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.{ResolveDateRange, ResolveParsedNumber}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v4.createAmendCumulativePeriodSummary.def1.model.request.{PeriodDisallowableExpenses, PeriodExpenses}
import v4.createAmendCumulativePeriodSummary.model.request.{Create_PeriodIncome, Def1_CreateAmendCumulativePeriodSummaryRequestData}

case class Def1_CreateAmendCumulativePeriodSummaryRulesValidator(taxYear: TaxYear)
    extends RulesValidator[Def1_CreateAmendCumulativePeriodSummaryRequestData] {

  private val resolveZeroPositiveParsedNumber  = ResolveParsedNumber()
  private val resolveMaybeNegativeParsedNumber = ResolveParsedNumber(min = -99999999999.99)
  private val resolveDateRange                 = ResolveDateRange()

  def validateBusinessRules(
      parsed: Def1_CreateAmendCumulativePeriodSummaryRequestData): Validated[Seq[MtdError], Def1_CreateAmendCumulativePeriodSummaryRequestData] = {
    import parsed.body._

    combine(
      periodDates.fold(valid)(dates => resolveDateRange(dates.periodStartDate -> dates.periodEndDate).toUnit),
      validateExpenses(periodExpenses, periodDisallowableExpenses),
      periodIncome.fold(valid)(validatePeriodIncomeNumericFields()),
      periodExpenses.fold(valid)(validateAllowableNumericFields()),
      periodDisallowableExpenses.fold(valid)(validateDisallowableNumericFields())
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

  private def validatePeriodIncomeNumericFields()(periodIncome: Create_PeriodIncome): Validated[Seq[MtdError], Unit] =
    List(
      (periodIncome.other, "/periodIncome/other"),
      (periodIncome.turnover, "/periodIncome/turnover"),
      (periodIncome.taxTakenOffTradingIncome, "/periodIncome/taxTakenOffTradingIncome")
    ).traverse_ { case (value, path) =>
      resolveZeroPositiveParsedNumber(value, path)
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

  private def validateDisallowableNumericFields()(expenses: PeriodDisallowableExpenses): Validated[Seq[MtdError], Unit] = {
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
