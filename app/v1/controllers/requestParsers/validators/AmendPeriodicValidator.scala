/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators

import v1.controllers.requestParsers.validators.validations._
import v1.models.errors.{MtdError, RuleIncorrectOrEmptyBodyError}
import v1.models.request.amendSEPeriodic._

class AmendPeriodicValidator extends Validator[AmendPeriodicRawData] {

  private val validationSet = List(parameterFormatValidation, bodyFormatValidation, bodyFieldValidation)

  private def parameterFormatValidation: AmendPeriodicRawData => List[List[MtdError]] = (data: AmendPeriodicRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.businessId),
      PeriodIdValidation.validate(data.periodId)
    )
  }

  private def bodyFormatValidation: AmendPeriodicRawData => List[List[MtdError]] = { data =>
    val baseValidation = List(JsonFormatValidation.validate[AmendPeriodicBody](data.body))

    val  extraValidation: List[List[MtdError]] = {
      data.body.asOpt[AmendPeriodicBody].map(_.isEmpty).map {
        case true => List(List(RuleIncorrectOrEmptyBodyError))
        case false => NoValidationErrors
      }.getOrElse(NoValidationErrors)
    }

    baseValidation ++ extraValidation
  }

  private def bodyFieldValidation: AmendPeriodicRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendPeriodicBody]
    val errorsO: List[List[MtdError]] = List(
      body.incomes.map(validateIncomes).getOrElse(Nil),
      body.consolidatedExpenses.map(validateConsolidatedExpenses).getOrElse(Nil),
      body.expenses.map(validateExpenses).getOrElse(Nil)
    ).flatten
    List(Validator.flattenErrors(errorsO))
  }

  private def validateIncomes(incomes: Incomes): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptional(
        field = incomes.turnover.amount,
        path = s"/incomes/turnover/amount"
      ),
      NumberValidation.validateOptional(
        field = incomes.other.amount,
        path = s"/incomes/other/amount"
      )
    )
  }

  private def validateConsolidatedExpenses(consolidatedExpenses: ConsolidatedExpenses): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptionaIncludeNegatives(
        field = consolidatedExpenses.consolidatedExpenses,
        path = s"/consolidatedExpenses/consolidatedExpenses"
      )
    )
  }

  private def validateExpenses(expenses: Expenses): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.costOfGoodsBought.amount,
        path = s"/expenses/costOfGoodsBought/amount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.costOfGoodsBought.disallowableAmount,
        path = s"/expenses/costOfGoodsBought/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.cisPaymentsTo.amount,
        path = s"/expenses/cisPaymentsTo/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.cisPaymentsTo.disallowableAmount,
        path = s"/expenses/cisPaymentsTo/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.staffCosts.amount,
        path = s"/expenses/staffCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.staffCosts.disallowableAmount,
        path = s"/expenses/staffCosts/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.travelCosts.amount,
        path = s"/expenses/travelCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.travelCosts.disallowableAmount,
        path = s"/expenses/travelCosts/disallowableAmount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.premisesRunningCosts.amount,
        path = s"/expenses/premisesRunningCosts/amount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.premisesRunningCosts.disallowableAmount,
        path = s"/expenses/premisesRunningCosts/disallowableAmount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.maintenanceCosts.amount,
        path = s"/expenses/maintenanceCosts/amount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.maintenanceCosts.disallowableAmount,
        path = s"/expenses/maintenanceCosts/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.adminCosts.amount,
        path = s"/expenses/adminCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.adminCosts.disallowableAmount,
        path = s"/expenses/adminCosts/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.advertisingCosts.amount,
        path = s"/expenses/advertisingCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.advertisingCosts.disallowableAmount,
        path = s"/expenses/advertisingCosts/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.businessEntertainmentCosts.amount,
        path = s"/expenses/businessEntertainmentCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.businessEntertainmentCosts.disallowableAmount,
        path = s"/expenses/businessEntertainmentCosts/disallowableAmount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.interestOnLoans.amount,
        path = s"/expenses/interestOnLoans/amount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.interestOnLoans.disallowableAmount,
        path = s"/expenses/interestOnLoans/disallowableAmount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.financialCharges.amount,
        path = s"/expenses/financialCharges/amount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.financialCharges.disallowableAmount,
        path = s"/expenses/financialCharges/disallowableAmount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.badDebt.amount,
        path = s"/expenses/badDebt/amount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.badDebt.disallowableAmount,
        path = s"/expenses/badDebt/disallowableAmount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.professionalFees.amount,
        path = s"/expenses/professionalFees/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.professionalFees.disallowableAmount,
        path = s"/expenses/professionalFees/disallowableAmount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.depreciation.amount,
        path = s"/expenses/depreciation/amount"
      ),
      NumberValidation.validateOptionaIncludeNegatives(
        field = expenses.depreciation.disallowableAmount,
        path = s"/expenses/depreciation/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.other.amount,
        path = s"/expenses/other/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.other.disallowableAmount,
        path = s"/expenses/other/disallowableAmount"
      )
    )
  }


  override def validate(data: AmendPeriodicRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }
}
