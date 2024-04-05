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

package v2.controllers.validators

import api.controllers.validators.RulesValidator
import api.controllers.validators.resolvers.ResolveParsedNumber
import api.models.domain.TaxYear
import api.models.errors.{MtdError, RuleBothExpensesSuppliedError, RuleNotAllowedConsolidatedExpenses}
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits.toFoldableOps
import v2.models.request.amendPeriodSummary.{AmendPeriodSummaryRequestData, PeriodDisallowableExpenses, PeriodExpenses, PeriodIncome}

import scala.util.Try

case class AmendPeriodSummaryRulesValidator(includeNegatives: Boolean, taxYear: Option[String])
    extends RulesValidator[AmendPeriodSummaryRequestData] {

  private val resolveNonNegativeParsedNumber   = ResolveParsedNumber()
  private val resolveMaybeNegativeParsedNumber = ResolveParsedNumber(min = -99999999999.99)

  def validateBusinessRules(parsed: AmendPeriodSummaryRequestData): Validated[Seq[MtdError], AmendPeriodSummaryRequestData] = {
    import parsed.body._

    combine(
      validateExpenses(periodExpenses, periodDisallowableExpenses, taxYear),
      periodIncome.map(validatePeriodIncome).getOrElse(valid),
      periodExpenses.map(validatePeriodExpenses(includeNegatives)).getOrElse(valid),
      periodDisallowableExpenses.map(validatePeriodDisallowableExpenses(includeNegatives)).getOrElse(valid)
    ).onSuccess(parsed)
  }

  private def validateExpenses(allowableExpenses: Option[PeriodExpenses],
                               periodDisallowableExpenses: Option[PeriodDisallowableExpenses],
                               taxYear: Option[String]): Validated[Seq[MtdError], Unit] = {

    val tysTaxYear = taxYear.flatMap(year => Try(TaxYear.fromMtd(year)).toOption)

    (allowableExpenses, periodDisallowableExpenses, tysTaxYear) match {
      case (Some(allowable), Some(_), _) if allowable.consolidatedExpenses.isDefined => Invalid(List(RuleBothExpensesSuppliedError))
      case (Some(allowable), None, None) if allowable.consolidatedExpenses.isDefined =>
        allowable match {
          case PeriodExpenses(Some(_), None, None, None, None, None, None, None, None, None, None, None, None, None, None, None) => valid
          case _ => Invalid(List(RuleBothExpensesSuppliedError))
        }
      case (Some(allowable), None, Some(taxYear)) if (taxYear.isTys && allowable.consolidatedExpenses.isDefined) =>
        Invalid(List(RuleNotAllowedConsolidatedExpenses))
      case _ => valid
    }
  }

  private def validatePeriodIncome(income: PeriodIncome): Validated[Seq[MtdError], Unit] =
    List(
      (income.turnover, "/periodIncome/turnover"),
      (income.other, "/periodIncome/other")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path = Some(path))
    }

  private def validatePeriodExpenses(includeNegatives: Boolean)(expenses: PeriodExpenses): Validated[Seq[MtdError], Unit] = {
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
      resolveNonNegativeParsedNumber(value, path = Some(path))
    }

    val validatedMaybeNegatives =
      (if (includeNegatives) conditionalMaybeNegativeExpenses ++ maybeNegativeExpenses else maybeNegativeExpenses).traverse_ { case (value, path) =>
        resolveMaybeNegativeParsedNumber(value, path = Some(path))
      }

    combine(validatedNonNegatives, validatedMaybeNegatives)
  }

  private def validatePeriodDisallowableExpenses(includeNegatives: Boolean)(expenses: PeriodDisallowableExpenses): Validated[Seq[MtdError], Unit] = {
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
