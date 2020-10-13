/*
 * Copyright 2020 HM Revenue & Customs
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
import v1.models.request.amendSample._

class AmendSEAnnualSummaryValidator extends Validator[AmendAnnualSummaryRawData] {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation, bodyFormatValidation, incorrectOrEmptyBodySubmittedValidation, bodyFieldValidation, exemptionCodeValidation)

  private def parameterFormatValidation: AmendAnnualSummaryRawData => List[List[MtdError]] = (data: AmendAnnualSummaryRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.businessId),
      TaxYearValidation.validate(data.taxYear)
    )
  }

  private def parameterRuleValidation: AmendAnnualSummaryRawData => List[List[MtdError]] = { data =>
    List(
      TaxYearNotSupportedValidation.validate(data.taxYear)
    )
  }

  private def bodyFormatValidation: AmendAnnualSummaryRawData => List[List[MtdError]] = { data =>
    List(
      JsonFormatValidation.validate[AmendAnnualSummaryBody](data.body, RuleIncorrectOrEmptyBodyError),
      ExemptionCodeValidation.validate[AmendAnnualSummaryBody](data.body, RuleExemptionCode)
    )
  }

  private def incorrectOrEmptyBodySubmittedValidation: AmendAnnualSummaryRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendAnnualSummaryBody]
    if (body.isIncorrectOrEmptyBody) List(List(RuleIncorrectOrEmptyBodyError)) else NoValidationErrors
  }

  private def bodyFieldValidation: AmendAnnualSummaryRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendAnnualSummaryBody]

    List(flattenErrors(
      List(
        body.adjustments.map(validateAdjustments).getOrElse(NoValidationErrors),
        body.allowances.map(validateAllowances).getOrElse(NoValidationErrors),
        body.nonFinancials.map(validateNonFinancials).getOrElse(NoValidationErrors)
        ).getOrElse(NoValidationErrors).toList)
    )
  }

  private def validateAdjustments(adjustments: Adjustments): List[MtdError] = {
    List(
      NumberValidation.validateOptional(
        field = adjustments.includedNonTaxableProfits,
        path = s"/adjustments/includedNonTaxableProfits"
      ),
      NumberValidation.validateOptional(
        field = Some(adjustments.basisAdjustment),
        path = s"/adjustments/basisAdjustment"
      ),
      NumberValidation.validateOptional(
        field = Some(adjustments.overlapReliefUsed),
        path = s"/adjustments/overlapReliefUsed"
      ),
      NumberValidation.validateOptional(
        field = Some(adjustments.accountingAdjustment),
        path = s"/adjustments/accountingAdjustment"
      ),
      NumberValidation.validateOptional(
        field = Some(adjustments.averagingAdjustment),
        path = s"/adjustments/averagingAdjustment"
      ),
      NumberValidation.validateOptional(
        field = Some(adjustments.lossBroughtForward),
        path = s"/adjustments/lossBroughtForward"
      ),
      NumberValidation.validateOptional(
        field = Some(adjustments.outstandingBusinessIncome),
        path = s"/adjustments/outstandingBusinessIncome"
      ),
      NumberValidation.validateOptional(
        field = Some(adjustments.balancingChargeBPRA),
        path = s"/adjustments/balancingChargeBPRA"
      ),
      NumberValidation.validateOptional(
        field = Some(adjustments.balancingChargeOther),
        path = s"/adjustments/balancingChargeOther"
      ),
      NumberValidation.validateOptional(
        field = Some(adjustments.goodsAndServicesOwnUse),
        path = s"/adjustments/goodsAndServicesOwnUse"
      )
    ).flatten
  }

  private def validateAllowances(allowances: Allowances): List[MtdError] = {
    List(
      NumberValidation.validateOptional(
        field = allowances.annualInvestmentAllowance,
        path = s"/allowances/annualInvestmentAllowance"
      ),
      NumberValidation.validateOptional(
        field = Some(allowances.businessPremisesRenovationAllowance),
        path = s"/allowances/businessPremisesRenovationAllowance"
      ),
      NumberValidation.validateOptional(
        field = Some(allowances.capitalAllowanceMainPool),
        path = s"/allowances/capitalAllowanceMainPool"
      ),
      NumberValidation.validateOptional(
        field = Some(allowances.capitalAllowanceSpecialRatePool),
        path = s"/allowances/capitalAllowanceSpecialRatePool"
      ),
      NumberValidation.validateOptional(
        field = Some(allowances.zeroEmissionGoodsVehicleAllowance),
        path = s"/allowances/zeroEmissionGoodsVehicleAllowance"
      ),
      NumberValidation.validateOptional(
        field = Some(allowances.enhancedCapitalAllowance),
        path = s"/allowances/enhancedCapitalAllowance"
      ),
      NumberValidation.validateOptional(
        field = Some(allowances.allowanceOnSales),
        path = s"/allowances/allowanceOnSales"
      ),
      NumberValidation.validateOptional(
        field = Some(allowances.capitalAllowanceSingleAssetPool),
        path = s"/allowances/capitalAllowanceSingleAssetPool"
      ),
      NumberValidation.validateOptional(
        field = Some(allowances.tradingAllowance),
        path = s"/allowances/tradingAllowance"
      )
    ).flatten
  }

  private def validateNonFinancials(nonFinancials: NonFinancials): List[MtdError] = {
    List(
      ExemptionCodesValidation.validateOptional(
        field = nonFinancials.class4NicInfo.flatMap(_.exemptionCode),
        path = "/nonFinancials/class4NicInfo/exemptionCode"
      )
    )
  }


  override def validate(data: AmendAnnualSummaryRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }
}
