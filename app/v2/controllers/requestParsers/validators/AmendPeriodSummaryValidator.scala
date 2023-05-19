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

package v2.controllers.requestParsers.validators

import anyVersion.models.request.amendPeriodSummary.{AmendPeriodSummaryRawData, PeriodDisallowableExpenses, PeriodIncome}
import api.controllers.requestParsers.validators.Validator
import api.controllers.requestParsers.validators.validations._
import api.models.errors.MtdError
import v2.controllers.requestParsers.validators.validations.AmendConsolidatedExpensesValidation
import v2.models.request.amendPeriodSummary._

import scala.annotation.nowarn

class AmendPeriodSummaryValidator extends Validator[AmendPeriodSummaryRawData] {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation, bodyFormatValidation, bodyFieldValidation)

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
          body.periodExpenses.map(validateExpenses).getOrElse(Nil),
          validateConsolidatedExpenses(body.periodExpenses, body.periodDisallowableExpenses),
          body.periodDisallowableExpenses.map(validateDisallowableExpenses).getOrElse(Nil)
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
      )
    )
  }

  private def validateExpenses(expenses: PeriodExpenses): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptional(
        field = expenses.consolidatedExpenses,
        path = s"/periodExpenses/consolidatedExpenses"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.costOfGoods,
        path = s"/periodExpenses/costOfGoods"
      ),
      NumberValidation.validateOptional(
        field = expenses.paymentsToSubcontractors,
        path = s"/periodExpenses/paymentsToSubcontractors"
      ),
      NumberValidation.validateOptional(
        field = expenses.wagesAndStaffCosts,
        path = s"/periodExpenses/wagesAndStaffCosts"
      ),
      NumberValidation.validateOptional(
        field = expenses.carVanTravelExpenses,
        path = s"/periodExpenses/carVanTravelExpenses"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.premisesRunningCosts,
        path = s"/periodExpenses/premisesRunningCosts"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.maintenanceCosts,
        path = s"/periodExpenses/maintenanceCosts"
      ),
      NumberValidation.validateOptional(
        field = expenses.adminCosts,
        path = s"/periodExpenses/adminCosts"
      ),
      NumberValidation.validateOptional(
        field = expenses.businessEntertainmentCosts,
        path = s"/periodExpenses/businessEntertainmentCosts"
      ),
      NumberValidation.validateOptional(
        field = expenses.advertisingCosts,
        path = s"/periodExpenses/advertisingCosts"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.interestOnBankOtherLoans,
        path = s"/periodExpenses/interestOnBankOtherLoans"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.financeCharges,
        path = s"/periodExpenses/financeCharges"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.irrecoverableDebts,
        path = s"/periodExpenses/irrecoverableDebts"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.professionalFees,
        path = s"/periodExpenses/professionalFees"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.depreciation,
        path = s"/periodExpenses/depreciation"
      ),
      NumberValidation.validateOptional(
        field = expenses.otherExpenses,
        path = s"/periodExpenses/otherExpenses"
      )
    )
  }

  private def validateDisallowableExpenses(expenses: PeriodDisallowableExpenses): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.costOfGoodsDisallowable,
        path = s"/periodDisallowableExpenses/costOfGoodsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.paymentsToSubcontractorsDisallowable,
        path = s"/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.wagesAndStaffCostsDisallowable,
        path = s"/periodDisallowableExpenses/wagesAndStaffCostsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.carVanTravelExpensesDisallowable,
        path = s"/periodDisallowableExpenses/carVanTravelExpensesDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.premisesRunningCostsDisallowable,
        path = s"/periodDisallowableExpenses/premisesRunningCostsDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.maintenanceCostsDisallowable,
        path = s"/periodDisallowableExpenses/maintenanceCostsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.adminCostsDisallowable,
        path = s"/periodDisallowableExpenses/adminCostsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.businessEntertainmentCostsDisallowable,
        path = s"/periodDisallowableExpenses/businessEntertainmentCostsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.advertisingCostsDisallowable,
        path = s"/periodDisallowableExpenses/advertisingCostsDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.interestOnBankOtherLoansDisallowable,
        path = s"/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.financeChargesDisallowable,
        path = s"/periodDisallowableExpenses/financeChargesDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.irrecoverableDebtsDisallowable,
        path = s"/periodDisallowableExpenses/irrecoverableDebtsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.professionalFeesDisallowable,
        path = s"/periodDisallowableExpenses/professionalFeesDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.depreciationDisallowable,
        path = s"/periodDisallowableExpenses/depreciationDisallowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.otherExpensesDisallowable,
        path = s"/periodDisallowableExpenses/otherExpensesDisallowable"
      )
    )
  }

  private def validateConsolidatedExpenses(expenses: Option[PeriodExpenses],
                                           disallowableExpenses: Option[PeriodDisallowableExpenses]): List[List[MtdError]] = {
    List(AmendConsolidatedExpensesValidation.validate(expenses, disallowableExpenses))
  }

  override def validate(data: AmendPeriodSummaryRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

}
