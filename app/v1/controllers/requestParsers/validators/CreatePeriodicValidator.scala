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
import v1.models.errors.{FromDateFormatError, MtdError, RuleIncorrectOrEmptyBodyError, ToDateFormatError}
import v1.models.request.createPeriodic.{ConsolidatedExpenses, CreatePeriodicBody, CreatePeriodicRawData, Expenses, Incomes}

class CreatePeriodicValidator extends Validator[CreatePeriodicRawData] {

  private val validationSet = List(parameterFormatValidation, incorrectOrEmptyBodySubmittedValidation, bodyFieldValidation, dateRuleValidation, consolidatedExpensesRuleValidation)

  private def parameterFormatValidation: CreatePeriodicRawData => List[List[MtdError]] = (data: CreatePeriodicRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.businessId),
      JsonFormatValidation.validate[CreatePeriodicBody](data.body)
    )
  }

  private def incorrectOrEmptyBodySubmittedValidation: CreatePeriodicRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[CreatePeriodicBody]
    if (body.isEmpty) List(List(RuleIncorrectOrEmptyBodyError)) else NoValidationErrors
  }

  private def consolidatedExpensesRuleValidation: CreatePeriodicRawData => List[List[MtdError]] = (data: CreatePeriodicRawData) => {
    val body = data.body.as[CreatePeriodicBody]

    List(
      ConsolidatedExpensesValidation.validate(body.consolidatedExpenses, body.expenses)
    )
  }

  private def bodyFieldValidation: CreatePeriodicRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[CreatePeriodicBody]

    List(flattenErrors(
      List(
        validateDates(body.periodFromDate, body.periodToDate),
        body.incomes.map(validateIncome).getOrElse(NoValidationErrors),
        body.consolidatedExpenses.map(validateConsolidatedExpenses).getOrElse(NoValidationErrors),
        body.expenses.map(validateExpenses).getOrElse(NoValidationErrors)
      )
    ))
  }

  private def dateRuleValidation: CreatePeriodicRawData => List[List[MtdError]] = (data: CreatePeriodicRawData) => {
    val body = data.body.as[CreatePeriodicBody]
    List(
      DateValidation.validateToDateBeforeFromDate(body.periodFromDate, body.periodToDate),
    )
  }

  private def validateDates(fromDate: String, toDate: String): List[MtdError] = {
    List(
      DateValidation.validate(
        field = fromDate,
        error = FromDateFormatError
      ),
      DateValidation.validate(
        field = toDate,
        error = ToDateFormatError
      )
    ).flatten
  }

  private def validateIncome(incomes: Incomes): List[MtdError] = {
    List(
      NumberValidation.validateOptional(
        field = incomes.turnover.map(_.amount),
        path = s"/incomes/turnover/amount"
      ),
      NumberValidation.validateOptional(
        field = incomes.other.map(_.amount),
        path = s"/incomes/other/amount"
      )
    ).flatten
  }


  private def validateConsolidatedExpenses(consolidatedExpenses: ConsolidatedExpenses): List[MtdError] = {
    List(
      NumberValidation.validateOptionalIncludeNegatives(
        field = Some(consolidatedExpenses.consolidatedExpenses),
        path = s"/consolidatedExpenses/consolidatedExpenses"
      )
    ).flatten
  }

  private def validateExpenses(expenses: Expenses): List[MtdError] = {
    List(
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.costOfGoodsBought.map(_.amount),
        path = s"/expenses/costOfGoodsBought/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.costOfGoodsBought.flatMap(_.disallowableAmount),
        path = s"/expenses/costOfGoodsBought/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.cisPaymentsTo.map(_.amount),
        path = s"/expenses/cisPaymentsTo/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.cisPaymentsTo.flatMap(_.disallowableAmount),
        path = s"/expenses/cisPaymentsTo/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.staffCosts.map(_.amount),
        path = s"/expenses/staffCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.staffCosts.flatMap(_.disallowableAmount),
        path = s"/expenses/staffCosts/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.travelCosts.map(_.amount),
        path = s"/expenses/travelCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.travelCosts.flatMap(_.disallowableAmount),
        path = s"/expenses/travelCosts/disallowableAmount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.premisesRunningCosts.map(_.amount),
        path = s"/expenses/premisesRunningCosts/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.premisesRunningCosts.flatMap(_.disallowableAmount),
        path = s"/expenses/premisesRunningCosts/disallowableAmount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.maintenanceCosts.map(_.amount),
        path = s"/expenses/maintenanceCosts/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.maintenanceCosts.flatMap(_.disallowableAmount),
        path = s"/expenses/maintenanceCosts/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.adminCosts.map(_.amount),
        path = s"/expenses/adminCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.adminCosts.flatMap(_.disallowableAmount),
        path = s"/expenses/adminCosts/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.advertisingCosts.map(_.amount),
        path = s"/expenses/advertisingCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.advertisingCosts.flatMap(_.disallowableAmount),
        path = s"/expenses/advertisingCosts/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.businessEntertainmentCosts.map(_.amount),
        path = s"/expenses/businessEntertainmentCosts/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.businessEntertainmentCosts.flatMap(_.disallowableAmount),
        path = s"/expenses/businessEntertainmentCosts/disallowableAmount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.interestOnLoans.map(_.amount),
        path = s"/expenses/interestOnLoans/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.interestOnLoans.flatMap(_.disallowableAmount),
        path = s"/expenses/interestOnLoans/disallowableAmount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.financialCharges.map(_.amount),
        path = s"/expenses/financialCharges/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.financialCharges.flatMap(_.disallowableAmount),
        path = s"/expenses/financialCharges/disallowableAmount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.badDebt.map(_.amount),
        path = s"/expenses/badDebt/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.badDebt.flatMap(_.disallowableAmount),
        path = s"/expenses/badDebt/disallowableAmount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.professionalFees.map(_.amount),
        path = s"/expenses/professionalFees/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.professionalFees.flatMap(_.disallowableAmount),
        path = s"/expenses/professionalFees/disallowableAmount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.depreciation.map(_.amount),
        path = s"/expenses/depreciation/amount"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.depreciation.flatMap(_.disallowableAmount),
        path = s"/expenses/depreciation/disallowableAmount"
      ),
      NumberValidation.validateOptional(
        field = expenses.other.map(_.amount),
        path = s"/expenses/other/amount"
      ),
      NumberValidation.validateOptional(
        field = expenses.other.flatMap(_.disallowableAmount),
        path = s"/expenses/other/disallowableAmount"
      )
    ).flatten
  }

  override def validate(data: CreatePeriodicRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }
}
