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
import v1.models.errors._
import v1.models.request.amendSEAnnual._

class CreateAmendSelfEmploymentAnnualSubmissionValidator extends Validator[AmendAnnualSubmissionRawData] {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation, bodyFormatValidation, bodyFieldValidation)

  private def parameterFormatValidation: AmendAnnualSubmissionRawData => List[List[MtdError]] = (data: AmendAnnualSubmissionRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.businessId),
      TaxYearValidation.validate(data.taxYear)
    )
  }

  private def parameterRuleValidation: AmendAnnualSubmissionRawData => List[List[MtdError]] = { data =>
    List(
      TaxYearNotSupportedValidation.validate(data.taxYear)
    )
  }

  private def bodyFormatValidation: AmendAnnualSubmissionRawData => List[List[MtdError]] = { data =>
    val baseValidation = List(JsonFormatValidation.validate[AmendAnnualSubmissionBody](data.body))

    // TODO use JsonFormatValidation.validateAndCheckNonEmpty[AmendAnnualSubmissionBody] which does not require isEmpty methods
//    val  extraValidation: List[List[MtdError]] = {
//      data.body.asOpt[AmendAnnualSubmissionBody].map(_.isEmpty).map {
//        case true => List(List(RuleIncorrectOrEmptyBodyError))
//        case false => NoValidationErrors
//      }.getOrElse(NoValidationErrors)
//    }

    baseValidation // ++ extraValidation
  }

  private def bodyFieldValidation: AmendAnnualSubmissionRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendAnnualSubmissionBody]
    val errorsO: List[List[MtdError]] = List(
      body.adjustments.map(validateAdjustments).getOrElse(Nil),
      body.allowances.map(validateAllowances).getOrElse(Nil),
      body.nonFinancials.map(validateNonFinancials).getOrElse(Nil)
    ).flatten
    List(Validator.flattenErrors(errorsO))
  }

  private def validateAdjustments(adjustments: Adjustments): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptional(
        field = adjustments.includedNonTaxableProfits,
        path = s"/adjustments/includedNonTaxableProfits"
      ),
      NumberValidation.validateOptional(
        field = adjustments.basisAdjustment,
        path = s"/adjustments/basisAdjustment",
        min = -99999999999.99
      ),
      NumberValidation.validateOptional(
        field = adjustments.overlapReliefUsed,
        path = s"/adjustments/overlapReliefUsed"
      ),
      NumberValidation.validateOptional(
        field = adjustments.accountingAdjustment,
        path = s"/adjustments/accountingAdjustment"
      ),
      NumberValidation.validateOptional(
        field = adjustments.averagingAdjustment,
        path = s"/adjustments/averagingAdjustment",
        min = -99999999999.99
      ),
      NumberValidation.validateOptional(
        field = adjustments.outstandingBusinessIncome,
        path = s"/adjustments/outstandingBusinessIncome"
      ),
      NumberValidation.validateOptional(
        field = adjustments.balancingChargeBpra,
        path = s"/adjustments/balancingChargeBpra"
      ),
      NumberValidation.validateOptional(
        field = adjustments.balancingChargeOther,
        path = s"/adjustments/balancingChargeOther"
      ),
      NumberValidation.validateOptional(
        field = adjustments.goodsAndServicesOwnUse,
        path = s"/adjustments/goodsAndServicesOwnUse"
      )
    )
  }

  private def validateAllowances(allowances: Allowances): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptional(
        field = allowances.annualInvestmentAllowance,
        path = s"/allowances/annualInvestmentAllowance"
      ),
      NumberValidation.validateOptional(
        field = allowances.businessPremisesRenovationAllowance,
        path = s"/allowances/businessPremisesRenovationAllowance"
      ),
      NumberValidation.validateOptional(
        field = allowances.capitalAllowanceMainPool,
        path = s"/allowances/capitalAllowanceMainPool"
      ),
      NumberValidation.validateOptional(
        field = allowances.capitalAllowanceSpecialRatePool,
        path = s"/allowances/capitalAllowanceSpecialRatePool"
      ),
      NumberValidation.validateOptional(
        field = allowances.zeroEmissionsGoodsVehicleAllowance,
        path = s"/allowances/zeroEmissionsGoodsVehicleAllowance"
      ),
      NumberValidation.validateOptional(
        field = allowances.enhancedCapitalAllowance,
        path = s"/allowances/enhancedCapitalAllowance"
      ),
      NumberValidation.validateOptional(
        field = allowances.allowanceOnSales,
        path = s"/allowances/allowanceOnSales"
      ),
      NumberValidation.validateOptional(
        field = allowances.capitalAllowanceSingleAssetPool,
        path = s"/allowances/capitalAllowanceSingleAssetPool"
      ),
      NumberValidation.validateOptional(
        field = allowances.tradingIncomeAllowance,
        path = s"/allowances/tradingIncomeAllowance"
      ),
      NumberValidation.validateOptional(
        field = allowances.electricChargePointAllowance,
        path = s"/allowances/electricChargePointAllowance"
      ),
      NumberValidation.validateOptional(
        field = allowances.zeroEmissionsCarAllowance,
        path = s"/allowances/zeroEmissionsCarAllowance"
      ),
      NumberValidation.validateOptional(
        field = structuredBuildingAllowance.amount,
        path = s"/allowances/structuredBuildingAllowance/amount"
      ),
      DateValidation.validateOptional(
        field = structuredBuildingAllowance.firstYear.qualifyingDate,
        path = s"/allowances/structuredBuildingAllowance/firstYear/qualifyingDate"
      ),
      NumberValidation.validateOptional(
        field = structuredBuildingAllowance.firstYear.qualifyingAmountExpenditure,
        path = s"/allowances/structuredBuildingAllowance/firstYear/qualifyingAmountExpenditure"
      ),
      NameValidation.validateOptional(
        field = structuredBuildingAllowance.building.name,
        path = s"/allowances/structuredBuildingAllowance/building/name"
      ),
      NumberValidation.validateOptional(
        field = structuredBuildingAllowance.building.number,
        path = s"/allowances/structuredBuildingAllowance/building/number"
      ),
      PostCodeValidation.validateOptional(
        field = structuredBuildingAllowance.building.postcode,
        path = s"/allowances/structuredBuildingAllowance/building/postcode"
      ),
      NumberValidation.validateOptional(
        field = enhancedStructuredBuildingAllowance.amount,
        path = s"/allowances/enhancedStructuredBuildingAllowance/amount"
      ),
      DateValidation.validateOptional(
        field = enhancedStructuredBuildingAllowance.firstYear.qualifyingDate,
        path = s"/allowances/enhancedStructuredBuildingAllowance/firstYear/qualifyingDate"
      ),
      NumberValidation.validateOptional(
        field = enhancedStructuredBuildingAllowance.firstYear.qualifyingAmountExpenditure,
        path = s"/allowances/enhancedStructuredBuildingAllowance/firstYear/qualifyingAmountExpenditure"
      ),
      NameValidation.validateOptional(
        field = enhancedStructuredBuildingAllowance.building.name,
        path = s"/allowances/enhancedStructuredBuildingAllowance/building/name"
      ),
      NumberValidation.validateOptional(
        field = enhancedStructuredBuildingAllowance.building.number,
        path = s"/allowances/enhancedStructuredBuildingAllowance/building/number"
      ),
      PostCodeValidation.validateOptional(
        field = enhancedStructuredBuildingAllowance.building.postcode,
        path = s"/allowances/enhancedStructuredBuildingAllowance/building/postcode"
      )
    )
  }

  private def validateNonFinancials(nonFinancials: NonFinancials): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptional(
        field = allowances.annualInvestmentAllowance,
        path = s"/nonFinancials/businessDetailsChangedRecently"
      ),
      NumberValidation.validateOptional(
        field = allowances.businessPremisesRenovationAllowance,
        path = s"/nonFinancials/class4NicsExemptionReason"
      )
    )
  }

  override def validate(data: AmendAnnualSubmissionRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }
}
