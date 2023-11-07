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

package v3.controllers.requestParsers.validators

import api.controllers.requestParsers.validators.Validator
import api.controllers.requestParsers.validators.validations._
import api.models.errors.{EndDateFormatError, MtdError, StartDateFormatError}
import v3.models.request.createPeriodSummary._

import scala.annotation.nowarn

class CreatePeriodSummaryValidator extends Validator[CreatePeriodSummaryRawData] {

  private val validations =
    List(parameterFormatValidation, bodyFormatValidation, bodyFieldValidation, dateRuleValidation)

  override def validate(data: CreatePeriodSummaryRawData): List[MtdError] = run(validations, data)

  private def parameterFormatValidation: CreatePeriodSummaryRawData => List[List[MtdError]] = (data: CreatePeriodSummaryRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.businessId)
    )
  }

  @nowarn("cat=lint-byname-implicit")
  private def bodyFormatValidation: CreatePeriodSummaryRawData => List[List[MtdError]] = { data =>
    JsonFormatValidation.validateAndCheckNonEmpty[CreatePeriodSummaryRequestBody](data.body) match {
      case Nil          => NoValidationErrors
      case schemaErrors => List(schemaErrors)
    }
  }

  private def bodyFieldValidation: CreatePeriodSummaryRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[CreatePeriodSummaryRequestBody]

    List(
      Validator.flattenErrors(
        List(
          validateDates(body.periodDates.periodStartDate, body.periodDates.periodEndDate),
          body.periodIncome.map(validateIncome).getOrElse(NoValidationErrors),
          body.periodExpenses.map(e => validateExpenses(e, data.includeNegatives)).getOrElse(NoValidationErrors),
          body.periodDisallowableExpenses.map(e => validateDisallowableExpenses(e, data.includeNegatives)).getOrElse(NoValidationErrors)
        )
      ))
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
      ),
      NumberValidation.validateOptional(
        field = periodIncome.taxTakenOffTradingIncome,
        path = s"/periodIncome/taxTakenOffTradingIncome"
      )
    ).flatten
  }

  private def validateExpenses(periodExpenses: PeriodExpenses, includeNegatives: Boolean): List[MtdError] =
    List(
      NumberValidation.validateOptional(
        field = periodExpenses.consolidatedExpenses,
        path = s"/periodExpenses/consolidatedExpenses",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.costOfGoods,
        path = s"/periodExpenses/costOfGoods",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.paymentsToSubcontractors,
        path = s"/periodExpenses/paymentsToSubcontractors",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.wagesAndStaffCosts,
        path = s"/periodExpenses/wagesAndStaffCosts",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.carVanTravelExpenses,
        path = s"/periodExpenses/carVanTravelExpenses",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.premisesRunningCosts,
        path = s"/periodExpenses/premisesRunningCosts",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.maintenanceCosts,
        path = s"/periodExpenses/maintenanceCosts",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.adminCosts,
        path = s"/periodExpenses/adminCosts",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.businessEntertainmentCosts,
        path = s"/periodExpenses/businessEntertainmentCosts",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.advertisingCosts,
        path = s"/periodExpenses/advertisingCosts",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.interestOnBankOtherLoans,
        path = s"/periodExpenses/interestOnBankOtherLoans",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.financeCharges,
        path = s"/periodExpenses/financeCharges",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.irrecoverableDebts,
        path = s"/periodExpenses/irrecoverableDebts",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.professionalFees,
        path = s"/periodExpenses/professionalFees",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.depreciation,
        path = s"/periodExpenses/depreciation",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodExpenses.otherExpenses,
        path = s"/periodExpenses/otherExpenses",
        includeNegatives = includeNegatives
      )
    ).flatten

  private def validateDisallowableExpenses(periodDisallowableExpenses: PeriodDisallowableExpenses, includeNegatives: Boolean): List[MtdError] = {
    List(
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.costOfGoodsDisallowable,
        path = s"/periodDisallowableExpenses/costOfGoodsDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.paymentsToSubcontractorsDisallowable,
        path = s"/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.wagesAndStaffCostsDisallowable,
        path = s"/periodDisallowableExpenses/wagesAndStaffCostsDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.carVanTravelExpensesDisallowable,
        path = s"/periodDisallowableExpenses/carVanTravelExpensesDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.premisesRunningCostsDisallowable,
        path = s"/periodDisallowableExpenses/premisesRunningCostsDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.maintenanceCostsDisallowable,
        path = s"/periodDisallowableExpenses/maintenanceCostsDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.adminCostsDisallowable,
        path = s"/periodDisallowableExpenses/adminCostsDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.businessEntertainmentCostsDisallowable,
        path = s"/periodDisallowableExpenses/businessEntertainmentCostsDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.advertisingCostsDisallowable,
        path = s"/periodDisallowableExpenses/advertisingCostsDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.interestOnBankOtherLoansDisallowable,
        path = s"/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.financeChargesDisallowable,
        path = s"/periodDisallowableExpenses/financeChargesDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.irrecoverableDebtsDisallowable,
        path = s"/periodDisallowableExpenses/irrecoverableDebtsDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.professionalFeesDisallowable,
        path = s"/periodDisallowableExpenses/professionalFeesDisallowable",
        includeNegatives = includeNegatives
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.depreciationDisallowable,
        path = s"/periodDisallowableExpenses/depreciationDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.otherExpensesDisallowable,
        path = s"/periodDisallowableExpenses/otherExpensesDisallowable",
        includeNegatives = includeNegatives
      )
    ).flatten
  }

  private def dateRuleValidation: CreatePeriodSummaryRawData => List[List[MtdError]] = (data: CreatePeriodSummaryRawData) => {
    val body = data.body.as[CreatePeriodSummaryRequestBody].periodDates
    List(
      DateValidation.validateEndDateBeforeStartDate(body.periodStartDate, body.periodEndDate)
    )
  }

}
