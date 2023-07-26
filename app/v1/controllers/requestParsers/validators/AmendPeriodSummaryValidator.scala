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

package v1.controllers.requestParsers.validators

import api.controllers.requestParsers.validators.Validator
import api.controllers.requestParsers.validators.validations._
import api.models.errors.MtdError
import v1.controllers.requestParsers.validators.validations.AmendConsolidatedExpensesValidation
import v1.models.request.amendPeriodSummary._

import scala.annotation.nowarn

class AmendPeriodSummaryValidator extends Validator[AmendPeriodSummaryRawData] {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation, bodyFormatValidation, bodyFieldValidation)

  override def validate(data: AmendPeriodSummaryRawData): List[MtdError] = {
    run(validationSet, data).distinct
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
      NumberValidation.validateOptional(
        field = expenses.costOfGoodsAllowable,
        path = s"/periodAllowableExpenses/costOfGoodsAllowable",
        includeNegatives = true
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
      NumberValidation.validateOptional(
        field = expenses.premisesRunningCostsAllowable,
        path = s"/periodAllowableExpenses/premisesRunningCostsAllowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.maintenanceCostsAllowable,
        path = s"/periodAllowableExpenses/maintenanceCostsAllowable",
        includeNegatives = true
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
      NumberValidation.validateOptional(
        field = expenses.interestOnBankOtherLoansAllowable,
        path = s"/periodAllowableExpenses/interestOnBankOtherLoansAllowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.financeChargesAllowable,
        path = s"/periodAllowableExpenses/financeChargesAllowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.irrecoverableDebtsAllowable,
        path = s"/periodAllowableExpenses/irrecoverableDebtsAllowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.professionalFeesAllowable,
        path = s"/periodAllowableExpenses/professionalFeesAllowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.depreciationAllowable,
        path = s"/periodAllowableExpenses/depreciationAllowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = expenses.otherExpensesAllowable,
        path = s"/periodAllowableExpenses/otherExpensesAllowable"
      )
    )
  }

  private def validateDisallowableExpenses(expenses: PeriodDisallowableExpenses): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptional(
        field = expenses.costOfGoodsDisallowable,
        path = s"/periodDisallowableExpenses/costOfGoodsDisallowable",
        includeNegatives = true
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
        path = s"/periodDisallowableExpenses/professionalFeesDisallowable"
      ),
      NumberValidation.validateOptional(
        field = expenses.depreciationDisallowable,
        path = s"/periodDisallowableExpenses/depreciationDisallowable",
        includeNegatives = true
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

}
