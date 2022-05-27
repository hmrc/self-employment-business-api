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
import v1.models.errors.{EndDateFormatError, MtdError, StartDateFormatError}
import v1.models.request.createPeriodSummary._

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
        allowableExpenses = body.periodAllowableExpenses,
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
          body.periodAllowableExpenses.map(validateAllowableExpenses).getOrElse(NoValidationErrors),
          body.periodDisallowableExpenses.map(validateDisallowableExpenses).getOrElse(NoValidationErrors)
        )
      ))
  }

  private def dateRuleValidation: CreatePeriodSummaryRawData => List[List[MtdError]] = (data: CreatePeriodSummaryRawData) => {
    val body = data.body.as[CreatePeriodSummaryBody].periodDates
    List(
      DateValidation.validateToDateBeforeFromDate(body.periodStartDate, body.periodEndDate)
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

  private def validateAllowableExpenses(periodAllowableExpenses: PeriodAllowableExpenses): List[MtdError] =
    List(
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.consolidatedExpenses,
        path = s"/periodAllowableExpenses/consolidatedExpenses"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.costOfGoodsAllowable,
        path = s"/periodAllowableExpenses/costOfGoodsAllowable"
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.paymentsToSubcontractorsAllowable,
        path = s"/periodAllowableExpenses/paymentsToSubcontractorsAllowable"
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.wagesAndStaffCostsAllowable,
        path = s"/periodAllowableExpenses/wagesAndStaffCostsAllowable"
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.carVanTravelExpensesAllowable,
        path = s"/periodAllowableExpenses/carVanTravelExpensesAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.premisesRunningCostsAllowable,
        path = s"/periodAllowableExpenses/premisesRunningCostsAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.maintenanceCostsAllowable,
        path = s"/periodAllowableExpenses/maintenanceCostsAllowable"
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.adminCostsAllowable,
        path = s"/periodAllowableExpenses/adminCostsAllowable"
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.businessEntertainmentCostsAllowable,
        path = s"/periodAllowableExpenses/businessEntertainmentCostsAllowable"
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.advertisingCostsAllowable,
        path = s"/periodAllowableExpenses/advertisingCostsAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.interestOnBankOtherLoansAllowable,
        path = s"/periodAllowableExpenses/interestOnBankOtherLoansAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.financeChargesAllowable,
        path = s"/periodAllowableExpenses/financeChargesAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.irrecoverableDebtsAllowable,
        path = s"/periodAllowableExpenses/irrecoverableDebtsAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.professionalFeesAllowable,
        path = s"/periodAllowableExpenses/professionalFeesAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.depreciationAllowable,
        path = s"/periodAllowableExpenses/depreciationAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = periodAllowableExpenses.otherExpensesAllowable,
        path = s"/periodAllowableExpenses/otherExpensesAllowable"
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
