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

package v3.amendPeriodSummary.def2

import api.models.errors.RuleBothExpensesSuppliedError
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.toFoldableOps
import config.SeBusinessFeatureSwitches
import shared.config.SharedAppConfig
import shared.controllers.validators.RulesValidator
import shared.controllers.validators.resolvers.ResolveParsedNumber
import shared.models.errors.{MtdError, RuleIncorrectOrEmptyBodyError}
import v3.amendPeriodSummary.def2.model.request.{Def2_Amend_PeriodDisallowableExpenses, Def2_Amend_PeriodExpenses, Def2_Amend_PeriodIncome}
import v3.amendPeriodSummary.model.request.Def2_AmendPeriodSummaryRequestData

class Def2_AmendPeriodSummaryRulesValidator(includeNegatives: Boolean)(implicit appConfig: SharedAppConfig)
    extends RulesValidator[Def2_AmendPeriodSummaryRequestData] {

  private lazy val featureSwitches = SeBusinessFeatureSwitches()

  private val resolveNonNegativeParsedNumber   = ResolveParsedNumber()
  private val resolveMaybeNegativeParsedNumber = ResolveParsedNumber(min = -99999999999.99)

  def validateBusinessRules(parsed: Def2_AmendPeriodSummaryRequestData): Validated[Seq[MtdError], Def2_AmendPeriodSummaryRequestData] = {

    import parsed.body._

    combine(
      validateExpenses(periodExpenses, periodDisallowableExpenses),
      periodIncome.map(validatePeriodIncome).getOrElse(valid),
      periodIncome.map(validatePeriodIncomeCL290).getOrElse(valid),
      periodExpenses.map(validatePeriodExpenses(includeNegatives)).getOrElse(valid),
      periodDisallowableExpenses.map(validatePeriodDisallowableExpenses(includeNegatives)).getOrElse(valid)
    ).onSuccess(parsed)
  }

  /** This can be removed once CL290 is released -- see Feature Release Roadmap page.
    */
  private def validatePeriodIncomeCL290(periodIncome: Def2_Amend_PeriodIncome): Validated[Seq[MtdError], Unit] =
    if (!featureSwitches.isCl290Enabled && periodIncome.taxTakenOffTradingIncome.isDefined)
      Invalid(
        List(
          RuleIncorrectOrEmptyBodyError.withPath("/periodIncome/taxTakenOffTradingIncome")
        ))
    else
      valid

  private def validateExpenses(allowableExpenses: Option[Def2_Amend_PeriodExpenses],
                               periodDisallowableExpenses: Option[Def2_Amend_PeriodDisallowableExpenses]): Validated[Seq[MtdError], Unit] =
    (allowableExpenses, periodDisallowableExpenses) match {
      case (Some(allowable), Some(_)) if allowable.consolidatedExpenses.isDefined => Invalid(List(RuleBothExpensesSuppliedError))
      case (Some(allowable), None) if allowable.consolidatedExpenses.isDefined =>
        allowable match {
          case Def2_Amend_PeriodExpenses(Some(_), None, None, None, None, None, None, None, None, None, None, None, None, None, None, None) => valid
          case _ => Invalid(List(RuleBothExpensesSuppliedError))
        }
      case _ => valid

    }

  private def validatePeriodIncome(income: Def2_Amend_PeriodIncome): Validated[Seq[MtdError], Unit] =
    List(
      (income.turnover, "/periodIncome/turnover"),
      (income.other, "/periodIncome/other")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }

  private def validatePeriodExpenses(includeNegatives: Boolean)(expenses: Def2_Amend_PeriodExpenses): Validated[Seq[MtdError], Unit] = {
    import expenses._

    val conditionalMaybeNegativeExpenses = List(
      (consolidatedExpenses, "/periodExpenses/consolidatedExpenses"),
      (paymentsToSubcontractors, "/periodExpenses/paymentsToSubcontractors"),
      (wagesAndStaffCosts, "/periodExpenses/wagesAndStaffCosts"),
      (carVanTravelExpenses, "/periodExpenses/carVanTravelExpenses"),
      (adminCosts, "/periodExpenses/adminCosts"),
      (businessEntertainmentCosts, "/periodExpenses/businessEntertainmentCosts"),
      (advertisingCosts, "/periodExpenses/advertisingCosts"),
      (otherExpenses, "/periodExpenses/otherExpenses")
    )

    val maybeNegativeExpenses = List(
      (costOfGoods, "/periodExpenses/costOfGoods"),
      (premisesRunningCosts, "/periodExpenses/premisesRunningCosts"),
      (maintenanceCosts, "/periodExpenses/maintenanceCosts"),
      (interestOnBankOtherLoans, "/periodExpenses/interestOnBankOtherLoans"),
      (financeCharges, "/periodExpenses/financeCharges"),
      (irrecoverableDebts, "/periodExpenses/irrecoverableDebts"),
      (professionalFees, "/periodExpenses/professionalFees"),
      (depreciation, "/periodExpenses/depreciation")
    )

    val validatedNonNegatives = (if (includeNegatives) Nil else conditionalMaybeNegativeExpenses).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path)
    }

    val validatedMaybeNegatives =
      (if (includeNegatives) conditionalMaybeNegativeExpenses ++ maybeNegativeExpenses else maybeNegativeExpenses).traverse_ { case (value, path) =>
        resolveMaybeNegativeParsedNumber(value, path)
      }

    combine(validatedNonNegatives, validatedMaybeNegatives)
  }

  private def validatePeriodDisallowableExpenses(includeNegatives: Boolean)(
      expenses: Def2_Amend_PeriodDisallowableExpenses): Validated[Seq[MtdError], Unit] = {
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
      resolveNonNegativeParsedNumber(value, path)
    }

    val validatedMaybeNegatives =
      (if (includeNegatives) conditionalMaybeNegativeExpenses ++ maybeNegativeExpenses else maybeNegativeExpenses).traverse_ { case (value, path) =>
        resolveMaybeNegativeParsedNumber(value, path)
      }

    combine(validatedNonNegatives, validatedMaybeNegatives)
  }

}
