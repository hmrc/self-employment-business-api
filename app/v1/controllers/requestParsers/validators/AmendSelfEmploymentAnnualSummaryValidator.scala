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

class AmendSelfEmploymentAnnualSummaryValidator extends Validator[AmendAnnualSubmissionRawData] {

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

    val  extraValidation: List[List[MtdError]] = {
      data.body.asOpt[AmendAnnualSubmissionBody].map(_.isEmpty).map {
        case true => List(List(RuleIncorrectOrEmptyBodyError))
        case false => NoValidationErrors
      }.getOrElse(NoValidationErrors)
    }

    baseValidation ++ extraValidation
  }

  private def bodyFieldValidation: AmendAnnualSubmissionRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendAnnualSubmissionBody]
    val errorsO: List[List[MtdError]] = List(
      body.adjustments.map(validateAdjustments).getOrElse(Nil),
      body.allowances.map(validateAllowances).getOrElse(Nil)
    ).flatten
    List(Validator.flattenErrors(errorsO))
  }

  private def validateAdjustments(adjustments: Adjustments): List[List[MtdError]] = {
    List(
      NumberValidation.validateOptional(
        field = adjustments.includedNonTaxableProfits,
        path = s"/adjustments/includedNonTaxableProfits"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = adjustments.basisAdjustment,
        path = s"/adjustments/basisAdjustment"
      ),
      NumberValidation.validateOptional(
        field = adjustments.overlapReliefUsed,
        path = s"/adjustments/overlapReliefUsed"
      ),
      NumberValidation.validateOptional(
        field = adjustments.accountingAdjustment,
        path = s"/adjustments/accountingAdjustment"
      ),
      NumberValidation.validateOptionalIncludeNegatives(
        field = adjustments.averagingAdjustment,
        path = s"/adjustments/averagingAdjustment"
      ),
      NumberValidation.validateOptional(
        field = adjustments.outstandingBusinessIncome,
        path = s"/adjustments/outstandingBusinessIncome"
      ),
      NumberValidation.validateOptional(
        field = adjustments.balancingChargeBpra,
        path = s"/adjustments/balancingChargeBPRA"
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
        path = s"/allowances/zeroEmissionGoodsVehicleAllowance"
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
        path = s"/allowances/tradingAllowance"
      ),
      NumberValidation.validateOptional(
        field = allowances.electricChargePointAllowance,
        path = s"/allowances/electricChargePointAllowance"
      )
    )
  }

  override def validate(data: AmendAnnualSubmissionRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }
}
