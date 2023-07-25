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

package v3.controllers.requestParsers.validators

import api.controllers.requestParsers.validators.Validator
import api.controllers.requestParsers.validators.validations._
import api.models.errors.MtdError
import v3.models.request.amendPeriodSummary._

import scala.annotation.nowarn

class AmendPeriodSummaryValidator extends Validator[AmendPeriodSummaryRawData] {

  private val validations = List(parameterFormatValidation, parameterRuleValidation, bodyFormatValidation, bodyFieldValidation)

  override def validate(data: AmendPeriodSummaryRawData): List[MtdError] = {
    run(validations, data).distinct
  }

  private def parameterFormatValidation: AmendPeriodSummaryRawData => List[List[MtdError]] = (data: AmendPeriodSummaryRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.businessId),
      PeriodIdValidation.validate(data.periodId),
      data.taxYear.map(TaxYearValidation.validate).getOrElse(Nil)
    )
  }

  private def parameterRuleValidation: AmendPeriodSummaryRawData => List[List[MtdError]] = (data: AmendPeriodSummaryRawData) => {
    List(
      data.taxYear.map(TaxYearTYSParameterValidation.validate).getOrElse(Nil)
    )
  }

  @nowarn("cat=lint-byname-implicit")
  private def bodyFormatValidation: AmendPeriodSummaryRawData => List[List[MtdError]] = { data =>
    JsonFormatValidation.validateAndCheckNonEmpty[AmendPeriodSummaryBody](data.body) match {
      case Nil          => NoValidationErrors
      case schemaErrors => List(schemaErrors)
    }
  }

  private def bodyFieldValidation: AmendPeriodSummaryRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendPeriodSummaryBody]
    List(
      Validator.flattenErrors(
        List(
          body.periodIncome.map(validatePeriodIncome).getOrElse(Nil),
          body.periodExpenses.map(e => validateExpenses(e, data.includeNegatives)).getOrElse(Nil),
          body.periodDisallowableExpenses.map(e => validateDisallowableExpenses(e, data.includeNegatives)).getOrElse(Nil)
        ).flatten
      )
    )
  }

  private def validatePeriodIncome(income: PeriodIncome): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptional(
        field = income.turnover,
        path = s"/periodIncome/turnover"
      ),
      NumberValidation.validateOptional(
        field = income.other,
        path = s"/periodIncome/other"
      ),
      NumberValidation.validateOptional(
        field = income.taxTakenOffTradingIncome,
        path = s"/periodIncome/taxTakenOffTradingIncome"
      )
    )
  }

  private def validateExpenses(expenses: PeriodExpenses, includeNegatives: Boolean): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptional(
        field = expenses.consolidatedExpenses,
        path = s"/periodExpenses/consolidatedExpenses",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.costOfGoods,
        path = s"/periodExpenses/costOfGoods",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.paymentsToSubcontractors,
        path = s"/periodExpenses/paymentsToSubcontractors",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.wagesAndStaffCosts,
        path = s"/periodExpenses/wagesAndStaffCosts",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.carVanTravelExpenses,
        path = s"/periodExpenses/carVanTravelExpenses",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.premisesRunningCosts,
        path = s"/periodExpenses/premisesRunningCosts",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.maintenanceCosts,
        path = s"/periodExpenses/maintenanceCosts",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.adminCosts,
        path = s"/periodExpenses/adminCosts",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.businessEntertainmentCosts,
        path = s"/periodExpenses/businessEntertainmentCosts",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.advertisingCosts,
        path = s"/periodExpenses/advertisingCosts",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.interestOnBankOtherLoans,
        path = s"/periodExpenses/interestOnBankOtherLoans",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.financeCharges,
        path = s"/periodExpenses/financeCharges",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.irrecoverableDebts,
        path = s"/periodExpenses/irrecoverableDebts",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.professionalFees,
        path = s"/periodExpenses/professionalFees",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.depreciation,
        path = s"/periodExpenses/depreciation",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.otherExpenses,
        path = s"/periodExpenses/otherExpenses",
        includeNegatives = includeNegatives
      )
    )
  }

  private def validateDisallowableExpenses(expenses: PeriodDisallowableExpenses, includeNegatives: Boolean): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptional(
        field = expenses.costOfGoodsDisallowable,
        path = s"/periodDisallowableExpenses/costOfGoodsDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.paymentsToSubcontractorsDisallowable,
        path = s"/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.wagesAndStaffCostsDisallowable,
        path = s"/periodDisallowableExpenses/wagesAndStaffCostsDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.carVanTravelExpensesDisallowable,
        path = s"/periodDisallowableExpenses/carVanTravelExpensesDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.premisesRunningCostsDisallowable,
        path = s"/periodDisallowableExpenses/premisesRunningCostsDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.maintenanceCostsDisallowable,
        path = s"/periodDisallowableExpenses/maintenanceCostsDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.adminCostsDisallowable,
        path = s"/periodDisallowableExpenses/adminCostsDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.businessEntertainmentCostsDisallowable,
        path = s"/periodDisallowableExpenses/businessEntertainmentCostsDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.advertisingCostsDisallowable,
        path = s"/periodDisallowableExpenses/advertisingCostsDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.interestOnBankOtherLoansDisallowable,
        path = s"/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.financeChargesDisallowable,
        path = s"/periodDisallowableExpenses/financeChargesDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.irrecoverableDebtsDisallowable,
        path = s"/periodDisallowableExpenses/irrecoverableDebtsDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.professionalFeesDisallowable,
        path = s"/periodDisallowableExpenses/professionalFeesDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = expenses.depreciationDisallowable,
        path = s"/periodDisallowableExpenses/depreciationDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.otherExpensesDisallowable,
        path = s"/periodDisallowableExpenses/otherExpensesDisallowable",
        includeNegatives = includeNegatives
      )
    )
  }

}
