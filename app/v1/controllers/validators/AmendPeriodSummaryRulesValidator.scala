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

package v1.controllers.validators

import api.controllers.validators.RulesValidator
import api.controllers.validators.resolvers.ResolveParsedNumber
import api.models.errors.{MtdError, RuleBothExpensesSuppliedError}
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.toFoldableOps
import v1.models.request.amendPeriodSummary.{AmendPeriodSummaryRequestData, PeriodAllowableExpenses, PeriodDisallowableExpenses, PeriodIncome}

object AmendPeriodSummaryRulesValidator extends RulesValidator[AmendPeriodSummaryRequestData] {

  private val resolveNonNegativeParsedNumber   = ResolveParsedNumber()
  private val resolveMaybeNegativeParsedNumber = ResolveParsedNumber(min = -99999999999.99)

  def validateBusinessRules(parsed: AmendPeriodSummaryRequestData): Validated[Seq[MtdError], AmendPeriodSummaryRequestData] = {
    import parsed.body._

    combine(
      validateExpenses(periodAllowableExpenses, periodDisallowableExpenses),
      periodIncome.map(validatePeriodIncome).getOrElse(valid),
      periodAllowableExpenses.map(validatePeriodAllowableExpenses).getOrElse(valid),
      periodDisallowableExpenses.map(validatePeriodDisallowableExpenses).getOrElse(valid)
    ).onSuccess(parsed)
  }

  private def validateExpenses(allowableExpenses: Option[PeriodAllowableExpenses],
                               periodDisallowableExpenses: Option[PeriodDisallowableExpenses]): Validated[Seq[MtdError], Unit] =
    (allowableExpenses, periodDisallowableExpenses) match {
      case (Some(allowable), Some(_)) if allowable.consolidatedExpenses.isDefined => Invalid(List(RuleBothExpensesSuppliedError))
      case (Some(allowable), None) if allowable.consolidatedExpenses.isDefined =>
        allowable match {
          case PeriodAllowableExpenses(Some(_), None, None, None, None, None, None, None, None, None, None, None, None, None, None, None) => valid
          case _ => Invalid(List(RuleBothExpensesSuppliedError))
        }
      case _ => valid

    }

  private def validatePeriodIncome(income: PeriodIncome): Validated[Seq[MtdError], Unit] =
    List(
      (income.turnover, "/periodIncome/turnover"),
      (income.other, "/periodIncome/other")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path = Some(path))
    }

  private def validatePeriodAllowableExpenses(expenses: PeriodAllowableExpenses): Validated[Seq[MtdError], Unit] = {
    import expenses._

    val validatedNonNegatives = List(
      (consolidatedExpenses, "/periodAllowableExpenses/consolidatedExpenses"),
      (paymentsToSubcontractorsAllowable, "/periodAllowableExpenses/paymentsToSubcontractorsAllowable"),
      (wagesAndStaffCostsAllowable, "/periodAllowableExpenses/wagesAndStaffCostsAllowable"),
      (carVanTravelExpensesAllowable, "/periodAllowableExpenses/carVanTravelExpensesAllowable"),
      (adminCostsAllowable, "/periodAllowableExpenses/adminCostsAllowable"),
      (businessEntertainmentCostsAllowable, "/periodAllowableExpenses/businessEntertainmentCostsAllowable"),
      (advertisingCostsAllowable, "/periodAllowableExpenses/advertisingCostsAllowable"),
      (otherExpensesAllowable, "/periodAllowableExpenses/otherExpensesAllowable")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path = Some(path))
    }

    val validatedMaybeNegatives = List(
      (costOfGoodsAllowable, "/periodAllowableExpenses/costOfGoodsAllowable"),
      (premisesRunningCostsAllowable, "/periodAllowableExpenses/premisesRunningCostsAllowable"),
      (maintenanceCostsAllowable, "/periodAllowableExpenses/maintenanceCostsAllowable"),
      (interestOnBankOtherLoansAllowable, "/periodAllowableExpenses/interestOnBankOtherLoansAllowable"),
      (financeChargesAllowable, "/periodAllowableExpenses/financeChargesAllowable"),
      (irrecoverableDebtsAllowable, "/periodAllowableExpenses/irrecoverableDebtsAllowable"),
      (professionalFeesAllowable, "/periodAllowableExpenses/professionalFeesAllowable"),
      (depreciationAllowable, "/periodAllowableExpenses/depreciationAllowable")
    ).traverse_ { case (value, path) =>
      resolveMaybeNegativeParsedNumber(value, path = Some(path))
    }

    combine(validatedNonNegatives, validatedMaybeNegatives)
  }

  private def validatePeriodDisallowableExpenses(expenses: PeriodDisallowableExpenses): Validated[Seq[MtdError], Unit] = {
    import expenses._

    val validatedNonNegatives = List(
      (paymentsToSubcontractorsDisallowable, "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable"),
      (wagesAndStaffCostsDisallowable, "/periodDisallowableExpenses/wagesAndStaffCostsDisallowable"),
      (carVanTravelExpensesDisallowable, "/periodDisallowableExpenses/carVanTravelExpensesDisallowable"),
      (adminCostsDisallowable, "/periodDisallowableExpenses/adminCostsDisallowable"),
      (businessEntertainmentCostsDisallowable, "/periodDisallowableExpenses/businessEntertainmentCostsDisallowable"),
      (advertisingCostsDisallowable, "/periodDisallowableExpenses/advertisingCostsDisallowable"),
      (professionalFeesDisallowable, "/periodDisallowableExpenses/professionalFeesDisallowable"),
      (otherExpensesDisallowable, "/periodDisallowableExpenses/otherExpensesDisallowable")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path = Some(path))
    }

    val validatedMaybeNegatives = List(
      (costOfGoodsDisallowable, "/periodDisallowableExpenses/costOfGoodsDisallowable"),
      (premisesRunningCostsDisallowable, "/periodDisallowableExpenses/premisesRunningCostsDisallowable"),
      (maintenanceCostsDisallowable, "/periodDisallowableExpenses/maintenanceCostsDisallowable"),
      (interestOnBankOtherLoansDisallowable, "/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable"),
      (financeChargesDisallowable, "/periodDisallowableExpenses/financeChargesDisallowable"),
      (irrecoverableDebtsDisallowable, "/periodDisallowableExpenses/irrecoverableDebtsDisallowable"),
      (depreciationDisallowable, "/periodDisallowableExpenses/depreciationDisallowable")
    ).traverse_ { case (value, path) =>
      resolveMaybeNegativeParsedNumber(value, path = Some(path))
    }

    combine(validatedNonNegatives, validatedMaybeNegatives)
  }

}
