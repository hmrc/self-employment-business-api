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

import anyVersion.models.request.createPeriodSummary.{CreatePeriodSummaryRawData, PeriodDisallowableExpenses, PeriodIncome}
import api.controllers.requestParsers.validators.Validator
import api.controllers.requestParsers.validators.validations._
import api.models.errors.{EndDateFormatError, MtdError, StartDateFormatError}
import v2.controllers.requestParsers.validators.validations.ConsolidatedExpensesValidation
import v2.models.request.createPeriodSummary._

class CreatePeriodSummaryValidator extends Validator[CreatePeriodSummaryRawData] {

  private val validationSet =
    List(parameterFormatValidation, bodyFormatValidation, bodyFieldValidation, dateRuleValidation, consolidatedExpensesRuleValidation)

  private def parameterFormatValidation: CreatePeriodSummaryRawData => List[List[MtdError]] = (data: CreatePeriodSummaryRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.businessId)
    )
  }

  private def bodyFormatValidation: CreatePeriodSummaryRawData => List[List[MtdError]] = { data =>
    JsonFormatValidation.validateAndCheckNonEmpty[CreatePeriodSummaryBody](data.body) match {
      case Nil          => NoValidationErrors
      case schemaErrors => List(schemaErrors)
    }
  }

  private def consolidatedExpensesRuleValidation: CreatePeriodSummaryRawData => List[List[MtdError]] = (data: CreatePeriodSummaryRawData) => {
    val body = data.body.as[CreatePeriodSummaryBody]
    List(
      ConsolidatedExpensesValidation.validate(
        Expenses = body.periodExpenses,
        disallowableExpenses = body.periodDisallowableExpenses
      )
    )
  }

  private def bodyFieldValidation: CreatePeriodSummaryRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[CreatePeriodSummaryBody]

    List(
      Validator.flattenErrors(
        List(
          validateDates(body.periodDates.periodStartDate, body.periodDates.periodEndDate),
          body.periodIncome.map(validateIncome).getOrElse(NoValidationErrors),
          body.periodExpenses.map(validateExpenses).getOrElse(NoValidationErrors),
          body.periodDisallowableExpenses.map(validateDisallowableExpenses).getOrElse(NoValidationErrors)
        )
      ))
  }

  private def dateRuleValidation: CreatePeriodSummaryRawData => List[List[MtdError]] = (data: CreatePeriodSummaryRawData) => {
    val body = data.body.as[CreatePeriodSummaryBody].periodDates
    List(
      DateValidation.validateEndDateBeforeStartDate(body.periodStartDate, body.periodEndDate)
    )
  }

  private def validateDates(periodStartDate: String, periodEndDate: String): List[MtdError] = {
    List(
      DateValidation.validate(
        field = periodStartDate,
        error = StartDateFormatError
      ),
      DateValidation.validate(
        field = periodEndDate,
        error = EndDateFormatError
      )
    ).flatten
  }

  private def validateIncome(periodIncome: PeriodIncome): List[MtdError] = {
    List(
      NumberValidation.validateOptional(
        field = periodIncome.turnover,
        path = s"/periodIncome/turnover"
      ),
      NumberValidation.validateOptional(
        field = periodIncome.other,
        path = s"/periodIncome/other"
      )
    ).flatten
  }

  private def validateExpenses(periodExpenses: PeriodExpenses): List[MtdError] =
    List(
      NumberValidation.validateOptional(
        field = periodExpenses.consolidatedExpenses,
        path = s"/periodExpenses/consolidatedExpenses"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodExpenses.costOfGoods,
        path = s"/periodExpenses/costOfGoods"
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.paymentsToSubcontractors,
        path = s"/periodExpenses/paymentsToSubcontractors"
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.wagesAndStaffCosts,
        path = s"/periodExpenses/wagesAndStaffCosts"
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.carVanTravelExpenses,
        path = s"/periodExpenses/carVanTravelExpenses"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodExpenses.premisesRunningCosts,
        path = s"/periodExpenses/premisesRunningCosts"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodExpenses.maintenanceCosts,
        path = s"/periodExpenses/maintenanceCosts"
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.adminCosts,
        path = s"/periodExpenses/adminCosts"
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.businessEntertainmentCosts,
        path = s"/periodExpenses/businessEntertainmentCosts"
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.advertisingCosts,
        path = s"/periodExpenses/advertisingCosts"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodExpenses.interestOnBankOtherLoans,
        path = s"/periodExpenses/interestOnBankOtherLoans"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodExpenses.financeCharges,
        path = s"/periodExpenses/financeCharges"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodExpenses.irrecoverableDebts,
        path = s"/periodExpenses/irrecoverableDebts"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodExpenses.professionalFees,
        path = s"/periodExpenses/professionalFees"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodExpenses.depreciation,
        path = s"/periodExpenses/depreciation"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodExpenses.otherExpenses,
        path = s"/periodExpenses/otherExpenses"
      )
    ).flatten

  private def validateDisallowableExpenses(periodDisallowableExpenses: PeriodDisallowableExpenses): List[MtdError] = {
    List(
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.costOfGoodsDisallowable,
        path = s"/periodDisallowableExpenses/costOfGoodsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.paymentsToSubcontractorsDisallowable,
        path = s"/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.wagesAndStaffCostsDisallowable,
        path = s"/periodDisallowableExpenses/wagesAndStaffCostsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.carVanTravelExpensesDisallowable,
        path = s"/periodDisallowableExpenses/carVanTravelExpensesDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.premisesRunningCostsDisallowable,
        path = s"/periodDisallowableExpenses/premisesRunningCostsDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.maintenanceCostsDisallowable,
        path = s"/periodDisallowableExpenses/maintenanceCostsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.adminCostsDisallowable,
        path = s"/periodDisallowableExpenses/adminCostsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.businessEntertainmentCostsDisallowable,
        path = s"/periodDisallowableExpenses/businessEntertainmentCostsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.advertisingCostsDisallowable,
        path = s"/periodDisallowableExpenses/advertisingCostsDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.interestOnBankOtherLoansDisallowable,
        path = s"/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.financeChargesDisallowable,
        path = s"/periodDisallowableExpenses/financeChargesDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.irrecoverableDebtsDisallowable,
        path = s"/periodDisallowableExpenses/irrecoverableDebtsDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.professionalFeesDisallowable,
        path = s"/periodDisallowableExpenses/professionalFeesDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodDisallowableExpenses.depreciationDisallowable,
        path = s"/periodDisallowableExpenses/depreciationDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.otherExpensesDisallowable,
        path = s"/periodDisallowableExpenses/otherExpensesDisallowable"
      )
    ).flatten
  }

  override def validate(data: CreatePeriodSummaryRawData): List[MtdError] = run(validationSet, data)

}
