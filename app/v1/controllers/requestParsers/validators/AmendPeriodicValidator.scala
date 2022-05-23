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

    // TODO use JsonFormatValidation.validateAndCheckNonEmpty[AmendAnnualSubmissionBody] which does not require isEmpty methods
    val extraValidation: List[List[MtdError]] = {
      data.body.asOpt[AmendPeriodicBody].isEmpty match {
        case true => List(List(RuleIncorrectOrEmptyBodyError))
        case false => NoValidationErrors
      }
    }
    baseValidation  ++ extraValidation
  }


  private def bodyFieldValidation: AmendPeriodicRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendPeriodicBody]
    List(
      Validator.flattenErrors(
        List(
          body.periodIncome.map(validatePeriodIncome).getOrElse(Nil),
          body.periodAllowableExpenses.map(validateAllowableExpenses).getOrElse(Nil),
          body.periodAllowableExpenses.map(validateConsolidatedExpenses).getOrElse(Nil),
          body.periodDisallowableExpenses.map(validateDisallowableExpenses).getOrElse(Nil)
        ).flatten
      )
    )
  }


  private def validatePeriodIncome(income: PeriodIncome): List[List[MtdError]] = {
    List(
      NumberValidation.
        validateOptional(
          field = income.turnover,
          path = s"/periodIncome/turnover"
      ),
      NumberValidation.
        validateOptional(
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
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.paymentsToSubcontractorsDisallowable,
        path = s"/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.wagesAndStaffCostsDisallowable,
        path = s"/periodDisallowableExpenses/wagesAndStaffCostsDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
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
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.adminCostsDisallowable,
        path = s"/periodDisallowableExpenses/adminCostsDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.businessEntertainmentCostsDisallowable,
        path = s"/periodDisallowableExpenses/businessEntertainmentCostsDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
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
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.professionalFeesDisallowable,
        path = s"/periodDisallowableExpenses/professionalFeesDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.depreciationDisallowable,
        path = s"/periodDisallowableExpenses/depreciationDisallowable"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = expenses.otherExpensesDisallowable,
        path = s"/periodDisallowableExpenses/otherExpensesDisallowable"
      )
    )
  }

  private def validateConsolidatedExpenses(expenses: PeriodAllowableExpenses): List[List[MtdError]] = {
    List(AmendConsolidatedExpensesValidation.validate(expenses))
  }


  override def validate(data: AmendPeriodicRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

}
