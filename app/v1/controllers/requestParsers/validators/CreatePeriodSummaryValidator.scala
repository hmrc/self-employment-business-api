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

import anyVersion.models.request.createPeriodSummary.{CreatePeriodSummaryRawData, PeriodDisallowableExpenses, PeriodIncome}
import api.controllers.requestParsers.validators.Validator
import api.controllers.requestParsers.validators.validations._
import api.models.errors.{EndDateFormatError, MtdError, StartDateFormatError}
import v1.controllers.requestParsers.validators.validations.ConsolidatedExpensesValidation
import v1.models.request.createPeriodSummary._

import scala.annotation.nowarn

class CreatePeriodSummaryValidator extends Validator[CreatePeriodSummaryRawData] {

  private val validationSet =
    List(parameterFormatValidation, bodyFormatValidation, bodyFieldValidation, dateRuleValidation, consolidatedExpensesRuleValidation)

  override def validate(data: CreatePeriodSummaryRawData): List[MtdError] = run(validationSet, data)

  private def parameterFormatValidation: CreatePeriodSummaryRawData => List[List[MtdError]] = (data: CreatePeriodSummaryRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.businessId)
    )
  }

  @nowarn("cat=lint-byname-implicit")
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
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.costOfGoodsAllowable,
        path = s"/periodAllowableExpenses/costOfGoodsAllowable",
        includeNegatives = true
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
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.premisesRunningCostsAllowable,
        path = s"/periodAllowableExpenses/premisesRunningCostsAllowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.maintenanceCostsAllowable,
        path = s"/periodAllowableExpenses/maintenanceCostsAllowable",
        includeNegatives = true
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
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.interestOnBankOtherLoansAllowable,
        path = s"/periodAllowableExpenses/interestOnBankOtherLoansAllowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.financeChargesAllowable,
        path = s"/periodAllowableExpenses/financeChargesAllowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.irrecoverableDebtsAllowable,
        path = s"/periodAllowableExpenses/irrecoverableDebtsAllowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.professionalFeesAllowable,
        path = s"/periodAllowableExpenses/professionalFeesAllowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.depreciationAllowable,
        path = s"/periodAllowableExpenses/depreciationAllowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodAllowableExpenses.otherExpensesAllowable,
        path = s"/periodAllowableExpenses/otherExpensesAllowable",
        includeNegatives = true
      )
    ).flatten

  private def validateDisallowableExpenses(periodDisallowableExpenses: PeriodDisallowableExpenses): List[MtdError] = {
    List(
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.costOfGoodsDisallowable,
        path = s"/periodDisallowableExpenses/costOfGoodsDisallowable",
        includeNegatives = true
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
        path = s"/periodDisallowableExpenses/professionalFeesDisallowable"
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.depreciationDisallowable,
        path = s"/periodDisallowableExpenses/depreciationDisallowable",
        includeNegatives = true
      ),
      NumberValidation.validateOptional(
        field = periodDisallowableExpenses.otherExpensesDisallowable,
        path = s"/periodDisallowableExpenses/otherExpensesDisallowable"
      )
    ).flatten
  }

  private def dateRuleValidation: CreatePeriodSummaryRawData => List[List[MtdError]] = (data: CreatePeriodSummaryRawData) => {
    val body = data.body.as[CreatePeriodSummaryBody].periodDates
    List(
      DateValidation.validateEndDateBeforeStartDate(body.periodStartDate, body.periodEndDate)
    )
  }

}
