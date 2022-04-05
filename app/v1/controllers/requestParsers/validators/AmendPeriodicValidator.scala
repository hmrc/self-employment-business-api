/*
 * Copyright 2022 HM Revenue & Customs
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
import v1.models.request.amendPeriodic._

class AmendPeriodicValidator extends Validator[AmendPeriodicRawData] {

  private val validationSet = List(parameterFormatValidation, bodyFormatValidation, consolidatedExpensesRuleValidation, bodyFieldValidation)

  private def parameterFormatValidation: AmendPeriodicRawData => List[List[MtdError]] = (data: AmendPeriodicRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.businessId),
      PeriodIdValidation.validate(data.periodId)
    )
  }

  private def bodyFormatValidation: AmendPeriodicRawData => List[List[MtdError]] = { data =>
    val baseValidation = List(JsonFormatValidation.validate[AmendPeriodicBody](data.body))

    val extraValidation: List[List[MtdError]] = {
      data.body
        .asOpt[AmendPeriodicBody]
        .map(_.isEmpty)
        .map {
          case true  => List(List(RuleIncorrectOrEmptyBodyError))
          case false => NoValidationErrors
        }
        .getOrElse(NoValidationErrors)
    }

    baseValidation ++ extraValidation
  }

  private def consolidatedExpensesRuleValidation: AmendPeriodicRawData => List[List[MtdError]] = (data: AmendPeriodicRawData) => {
    val body = data.body.as[AmendPeriodicBody]

    List(
      AmendConsolidatedExpensesValidation.validate(body.consolidatedExpenses, body.expenses)
    )
  }

  private def bodyFieldValidation: AmendPeriodicRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendPeriodicBody]
    List(
      Validator.flattenErrors(
        List(
          body.incomes.map(validateIncomes).getOrElse(Nil),
          body.consolidatedExpenses.map(validateConsolidatedExpenses).getOrElse(Nil),
          body.expenses.map(validateExpenses).getOrElse(Nil)
        ).flatten
      )
    )
  }

  private def validateIncomes(incomes: Incomes): List[List[MtdError]] = {
    List(
      NumberValidation.validate(
        field = incomes.turnover.get.amount,
        path = s"/incomes/turnover/amount"
      ),
      NumberValidation.validate(
        field = incomes.other.get.amount,
        path = s"/incomes/other/amount"
      )
    )
  }

  private def validateConsolidatedExpenses(consolidatedExpenses: ConsolidatedExpenses): List[List[MtdError]] = {
    List(
      NumberValidation.validateIncludeNegatives(
        field = consolidatedExpenses.consolidatedExpenses,
        path = s"/consolidatedExpenses/consolidatedExpenses"
      )
    )
  }

  private def validateExpenses(expenses: Expenses): List[List[MtdError]] = {
    List(
      NumberValidation.validateIncludeNegatives(
        field = expenses.costOfGoodsBought.get.amount,
        path = s"/expenses/costOfGoodsBought/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.costOfGoodsBought.get.disallowableAmount,
        path = s"/expenses/costOfGoodsBought/disallowableAmount"
      ),
      NumberValidation.validate(
        field = expenses.cisPaymentsTo.get.amount,
        path = s"/expenses/cisPaymentsTo/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.cisPaymentsTo.get.disallowableAmount,
        path = s"/expenses/cisPaymentsTo/disallowableAmount"
      ),
      NumberValidation.validate(
        field = expenses.staffCosts.get.amount,
        path = s"/expenses/staffCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.staffCosts.get.disallowableAmount,
        path = s"/expenses/staffCosts/disallowableAmount"
      ),
      NumberValidation.validate(
        field = expenses.travelCosts.get.amount,
        path = s"/expenses/travelCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.travelCosts.get.disallowableAmount,
        path = s"/expenses/travelCosts/disallowableAmount"
      ),
      NumberValidation.validateIncludeNegatives(
        field = expenses.premisesRunningCosts.get.amount,
        path = s"/expenses/premisesRunningCosts/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.premisesRunningCosts.get.disallowableAmount,
        path = s"/expenses/premisesRunningCosts/disallowableAmount"
      ),
      NumberValidation.validateIncludeNegatives(
        field = expenses.maintenanceCosts.get.amount,
        path = s"/expenses/maintenanceCosts/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.maintenanceCosts.get.disallowableAmount,
        path = s"/expenses/maintenanceCosts/disallowableAmount"
      ),
      NumberValidation.validate(
        field = expenses.adminCosts.get.amount,
        path = s"/expenses/adminCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.adminCosts.get.disallowableAmount,
        path = s"/expenses/adminCosts/disallowableAmount"
      ),
      NumberValidation.validate(
        field = expenses.advertisingCosts.get.amount,
        path = s"/expenses/advertisingCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.advertisingCosts.get.disallowableAmount,
        path = s"/expenses/advertisingCosts/disallowableAmount"
      ),
      NumberValidation.validate(
        field = expenses.businessEntertainmentCosts.get.amount,
        path = s"/expenses/businessEntertainmentCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.businessEntertainmentCosts.get.disallowableAmount,
        path = s"/expenses/businessEntertainmentCosts/disallowableAmount"
      ),
      NumberValidation.validateIncludeNegatives(
        field = expenses.interestOnLoans.get.amount,
        path = s"/expenses/interestOnLoans/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.interestOnLoans.get.disallowableAmount,
        path = s"/expenses/interestOnLoans/disallowableAmount"
      ),
      NumberValidation.validateIncludeNegatives(
        field = expenses.financialCharges.get.amount,
        path = s"/expenses/financialCharges/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.financialCharges.get.disallowableAmount,
        path = s"/expenses/financialCharges/disallowableAmount"
      ),
      NumberValidation.validateIncludeNegatives(
        field = expenses.badDebt.get.amount,
        path = s"/expenses/badDebt/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.badDebt.get.disallowableAmount,
        path = s"/expenses/badDebt/disallowableAmount"
      ),
      NumberValidation.validateIncludeNegatives(
        field = expenses.professionalFees.get.amount,
        path = s"/expenses/professionalFees/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.professionalFees.get.disallowableAmount,
        path = s"/expenses/professionalFees/disallowableAmount"
      ),
      NumberValidation.validateIncludeNegatives(
        field = expenses.depreciation.get.amount,
        path = s"/expenses/depreciation/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.depreciation.get.disallowableAmount,
        path = s"/expenses/depreciation/disallowableAmount"
      ),
      NumberValidation.validate(
        field = expenses.other.get.amount,
        path = s"/expenses/other/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.other.get.disallowableAmount,
        path = s"/expenses/other/disallowableAmount"
      )
    )
  }

  override def validate(data: AmendPeriodicRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

}
