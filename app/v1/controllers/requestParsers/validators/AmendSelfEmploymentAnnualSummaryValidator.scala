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
import v1.models.errors._
import v1.models.request.amendAnnualSummary.{Adjustments, Allowances, AmendAnnualSummaryBody, AmendAnnualSummaryRawData, NonFinancials}

class AmendSelfEmploymentAnnualSummaryValidator extends Validator[AmendAnnualSummaryRawData] {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation, bodyFormatValidation, bodyFieldValidation)

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
      JsonFormatValidation.validate[AmendAnnualSummaryBody](data.body)
    )
  }

  private def nonFinancialsValidation: AmendAnnualSummaryRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendAnnualSummaryBody]
    List(
      (body.nonFinancials.get.class4NicInfo.get.isExempt)
    )
  }

  private def bodyFieldValidation: AmendAnnualSummaryRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendAnnualSummaryBody]
    val errorsO: Option[List[List[MtdError]]] = for {
      adjustmentsErrors <- body.adjustments.map(validateAdjustments)
      allowancesErrors <- body.allowances.map(validateAllowances)
      nonFinancialsErrors <- body.nonFinancials.get.class4NicInfo.map(nonFinancialsValidation)
    } yield List(adjustmentsErrors ++ allowancesErrors ++ nonFinancialsErrors)
    List(errorsO.map(Validator.flattenErrors)).flatten
  }

  private def validateAdjustments(adjustments: Adjustments): List[MtdError] = {
    List(
      NumberValidation.validateOptional(
        field = adjustments.includedNonTaxableProfits,
        path = s"/adjustments/includedNonTaxableProfits"
      ),
      NumberValidation.validateOptional(
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
      NumberValidation.validateOptional(
        field = adjustments.averagingAdjustment,
        path = s"/adjustments/averagingAdjustment"
      ),
      NumberValidation.validateOptional(
        field = adjustments.lossBroughtForward,
        path = s"/adjustments/lossBroughtForward"
      ),
      NumberValidation.validateOptional(
        field = adjustments.outstandingBusinessIncome,
        path = s"/adjustments/outstandingBusinessIncome"
      ),
      NumberValidation.validateOptional(
        field = adjustments.balancingChargeBPRA,
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
    ).flatten
  }

  private def validateAllowances(allowances: Allowances): List[MtdError] = {
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
        field = allowances.zeroEmissionGoodsVehicleAllowance,
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
        field = allowances.tradingAllowance,
        path = s"/allowances/tradingAllowance"
      )
    ).flatten
  }

  override def validate(data: AmendAnnualSummaryRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }
}
