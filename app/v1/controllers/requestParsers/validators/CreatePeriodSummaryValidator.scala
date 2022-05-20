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

class CreatePeriodSummaryValidator extends Validator[CreatePeriodicRawData] {

  private val validationSet = List(
    parameterFormatValidation,
    incorrectOrEmptyBodySubmittedValidation,
    bodyFieldValidation,
    dateRuleValidation,
    consolidatedExpensesRuleValidation)

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

    List(
      Validator.flattenErrors(
        List(
          validateDates(body.periodFromDate, body.periodToDate),
          body.incomes.map(validateIncome).getOrElse(NoValidationErrors),
          body.consolidatedExpenses.map(validateConsolidatedExpenses).getOrElse(NoValidationErrors),
          body.expenses.map(validateAllowableExpenses).getOrElse(NoValidationErrors),
          body.expenses.map(validateDisallowableExpenses).getOrElse(NoValidationErrors)
        )
      ))
  }

  private def dateRuleValidation: CreatePeriodicRawData => List[List[MtdError]] = (data: CreatePeriodicRawData) => {
    val body = data.body.as[CreatePeriodicBody]
    List(
      DateValidation.validateToDateBeforeFromDate(body.periodFromDate, body.periodToDate)
    )
  }

  private def validateDates(periodEndDate: String, periodStartDate: String): List[MtdError] = {
    List(
      DateValidation.validate(
        field = periodEndDate,
        error = FromDateFormatError
      ),
      DateValidation.validate(
        field = periodStartDate,
        error = ToDateFormatError
      )
    ).flatten
  }

  private def validateIncome(periodIncome:PeriodIncome): List[MtdError] = {
    List(
      NumberValidation.validateOptional(
        field = periodIncome.map(_.turnover),
        path = s"/periodIncome/turnover"
      ),
      NumberValidation.validateOptional(
        field = periodIncome.map(_.other),
        path = s"/periodIncome/other"
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

  private def validateAllowableExpenses(periodAllowableExpenses: PeriodAllowableExpenses): List[MtdError] = {
    List(
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.map(_.costOfGoodsAllowable),
        path = s"/periodAllowableExpenses/costOfGoodsAllowable"
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.flatMap(_.paymentsToSubcontractorsAllowable),
        path = s"/periodAllowableExpenses/paymentsToSubcontractorsAllowable"
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.map(_.wagesAndStaffCostsAllowable),
        path = s"/periodAllowableExpenses/wagesAndStaffCostsAllowable"
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.flatMap(_.carVanTravelExpensesAllowable),
        path = s"/periodAllowableExpenses/carVanTravelExpensesAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.map(_.premisesRunningCostsAllowable),
        path = s"/periodAllowableExpenses/premisesRunningCostsAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.flatMap(_.maintenanceCostsAllowable),
        path = s"/periodAllowableExpenses/maintenanceCostsAllowable"
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.map(_.adminCostsAllowable),
        path = s"/periodAllowableExpenses/adminCostsAllowable"
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.flatMap(_.businessEntertainmentCostsAllowable),
        path = s"/periodAllowableExpenses/businessEntertainmentCostsAllowable"
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.map(_.advertisingCostsAllowable),
        path = s"/periodAllowableExpenses/advertisingCostsAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.flatMap(_.interestOnBankOtherLoansAllowable),
        path = s"/periodAllowableExpenses/interestOnBankOtherLoansAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.map(_.financeChargesAllowable),
        path = s"/periodAllowableExpenses/financeChargesAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.flatMap(_.irrecoverableDebtsAllowable),
        path = s"/periodAllowableExpenses/irrecoverableDebtsAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.map(_.professionalFeesAllowable),
        path = s"/periodAllowableExpenses/professionalFeesAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.flatMap(_.depreciationAllowable),
        path = s"/periodAllowableExpenses/depreciationAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.map(_.otherExpensesAllowable),
        path = s"/periodAllowableExpenses/otherExpensesAllowable"
      )
    ).flatten
  }

  private def validateDisallowableExpenses(periodDisallowableExpenses: PeriodDisallowableExpenses): List[MtdError] = {
    List(
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.map(_.costOfGoodsDisallowable),
        path = s"/periodDisallowableExpenses/costOfGoodsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.flatMap(_.paymentsToSubcontractorsDisallowable),
        path = s"/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.map(_.wagesAndStaffCostsDisallowable),
        path = s"/periodDisallowableExpenses/wagesAndStaffCostsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.flatMap(_.carVanTravelExpensesDisallowable),
        path = s"/periodDisallowableExpenses/carVanTravelExpensesDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.map(_.premisesRunningCostsDisallowable),
        path = s"/periodDisallowableExpenses/premisesRunningCostsDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.flatMap(_.maintenanceCostsDisallowable),
        path = s"/periodDisallowableExpenses/maintenanceCostsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.map(_.adminCostsDisallowable),
        path = s"/periodDisallowableExpenses/adminCostsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.flatMap(_.businessEntertainmentCostsDisallowable),
        path = s"/periodDisallowableExpenses/businessEntertainmentCostsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.map(_.advertisingCostsDisallowable),
        path = s"/periodDisallowableExpenses/advertisingCostsDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.flatMap(_.interestOnBankOtherLoansDisallowable),
        path = s"/periodAlperiodDisallowableExpenseslowableExpenses/interestOnBankOtherLoansDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.map(_.financeChargesDisallowable),
        path = s"/periodDisallowableExpenses/financeChargesDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.flatMap(_.irrecoverableDebtsDisallowable),
        path = s"/periodDisallowableExpenses/irrecoverableDebtsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.map(_.professionalFeesDisallowable),
        path = s"/periodDisallowableExpenses/professionalFeesDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.flatMap(_.depreciationDisallowable),
        path = s"/periodDisallowableExpenses/depreciationDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.map(_.otherExpensesDisallowable),
        path = s"/periodDisallowableExpenses/otherExpensesDisallowable"
      )
    ).flatten
  }

  override def validate(data: CreatePeriodicRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

}
