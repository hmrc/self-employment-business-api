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
import v1.models.errors.MtdError
import v1.models.request.amendPeriodSummary._

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
          body.periodAllowableExpenses.map(validateAllowableExpenses).getOrElse(Nil),
          validateConsolidatedExpenses(body.periodAllowableExpenses, body.periodDisallowableExpenses),
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

  private def validateAllowableExpenses(expenses: PeriodAllowableExpenses): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptional(
        field = expenses.consolidatedExpenses,
        path = s"/periodAllowableExpenses/consolidatedExpenses"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.costOfGoodsAllowable,
        path = s"/periodAllowableExpenses/costOfGoodsAllowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.paymentsToSubcontractorsAllowable,
        path = s"/periodAllowableExpenses/paymentsToSubcontractorsAllowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.wagesAndStaffCostsAllowable,
        path = s"/periodAllowableExpenses/wagesAndStaffCostsAllowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.carVanTravelExpensesAllowable,
        path = s"/periodAllowableExpenses/carVanTravelExpensesAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.premisesRunningCostsAllowable,
        path = s"/periodAllowableExpenses/premisesRunningCostsAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.maintenanceCostsAllowable,
        path = s"/periodAllowableExpenses/maintenanceCostsAllowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.adminCostsAllowable,
        path = s"/periodAllowableExpenses/adminCostsAllowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.businessEntertainmentCostsAllowable,
        path = s"/periodAllowableExpenses/businessEntertainmentCostsAllowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.advertisingCostsAllowable,
        path = s"/periodAllowableExpenses/advertisingCostsAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.interestOnBankOtherLoansAllowable,
        path = s"/periodAllowableExpenses/interestOnBankOtherLoansAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.financeChargesAllowable,
        path = s"/periodAllowableExpenses/financeChargesAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.irrecoverableDebtsAllowable,
        path = s"/periodAllowableExpenses/irrecoverableDebtsAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.professionalFeesAllowable,
        path = s"/periodAllowableExpenses/professionalFeesAllowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.depreciationAllowable,
        path = s"/periodAllowableExpenses/depreciationAllowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.otherExpensesAllowable,
        path = s"/periodAllowableExpenses/otherExpensesAllowable"
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

  private def validateConsolidatedExpenses(allowableExpenses: Option[PeriodAllowableExpenses],
                                           disallowableExpenses: Option[PeriodDisallowableExpenses]): List[List[MtdError]] = {
    List(AmendConsolidatedExpensesValidation.validate(allowableExpenses, disallowableExpenses))
  }

  override def validate(data: AmendPeriodSummaryRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

}
