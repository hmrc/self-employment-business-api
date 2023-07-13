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
import api.models.errors._
import v1.controllers.requestParsers.validators.validations._
import v1.models.request.amendSEAnnual._

import scala.annotation.nowarn

class AmendAnnualSubmissionValidator extends Validator[AmendAnnualSubmissionRawData] {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation, enumValidation, bodyFormatValidation, bodyFieldValidation)

  override def validate(data: AmendAnnualSubmissionRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

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

  @nowarn("cat=lint-byname-implicit")
  private def bodyFormatValidation: AmendAnnualSubmissionRawData => List[List[MtdError]] = { data =>
    JsonFormatValidation.validateAndCheckNonEmpty[AmendAnnualSubmissionBody](data.body) match {
      case Nil          => NoValidationErrors
      case schemaErrors => List(schemaErrors)
    }
  }

  private def enumValidation: AmendAnnualSubmissionRawData => List[List[MtdError]] = { data =>
    val class4 = (data.body \ "nonFinancials" \ "class4NicsExemptionReason").asOpt[String]
    List(Class4ExemptionReasonValidation.validateOptional(class4))
  }

  private def bodyFieldValidation: AmendAnnualSubmissionRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendAnnualSubmissionBody]
    List(
      Validator.flattenErrors(List(
        body.adjustments.map(validateAdjustments).getOrElse(Nil),
        body.allowances.map(validateAllowances).getOrElse(Nil),
        body.allowances.map(validateTradingIncomeAllowance).getOrElse(Nil),
        body.allowances
          .flatMap(_.structuredBuildingAllowance.map(_.zipWithIndex.toList.flatMap { case (entry, i) =>
            validateStructuredBuildingAllowance(entry, i, "structuredBuildingAllowance")
          }))
          .getOrElse(NoValidationErrors),
        body.allowances
          .flatMap(_.enhancedStructuredBuildingAllowance.map(_.zipWithIndex.toList.flatMap { case (entry, i) =>
            validateStructuredBuildingAllowance(entry, i, "enhancedStructuredBuildingAllowance")
          }))
          .getOrElse(NoValidationErrors)
      )))
  }

  private def validateAdjustments(adjustments: Adjustments): List[MtdError] = {
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
        path = s"/allowances/tradingIncomeAllowance",
        max = 1000
      ),
      NumberValidation.validateOptional(
        field = allowances.electricChargePointAllowance,
        path = s"/allowances/electricChargePointAllowance"
      ),
      NumberValidation.validateOptional(
        field = allowances.zeroEmissionsCarAllowance,
        path = s"/allowances/zeroEmissionsCarAllowance"
      )
    ).flatten
  }

  private def validateStructuredBuildingAllowance(structuredBuildingAllowance: StructuredBuildingAllowance,
                                                  index: Int,
                                                  typeOfBuildingAllowance: String): List[MtdError] = {
    List(
      NumberValidation.validate(
        field = structuredBuildingAllowance.amount,
        path = s"/allowances/$typeOfBuildingAllowance/$index/amount"
      ),
      DateValidation.validateOptional(
        field = structuredBuildingAllowance.firstYear.map(_.qualifyingDate),
        path = s"/allowances/$typeOfBuildingAllowance/$index/firstYear/qualifyingDate"
      ),
      NumberValidation.validateOptional(
        field = structuredBuildingAllowance.firstYear.map(_.qualifyingAmountExpenditure),
        path = s"/allowances/$typeOfBuildingAllowance/$index/firstYear/qualifyingAmountExpenditure"
      ),
      StringValidation.validateOptional(
        field = structuredBuildingAllowance.building.name,
        path = s"/allowances/$typeOfBuildingAllowance/$index/building/name"
      ),
      StringValidation.validateOptional(
        field = structuredBuildingAllowance.building.number,
        path = s"/allowances/$typeOfBuildingAllowance/$index/building/number"
      ),
      StringValidation.validate(
        field = structuredBuildingAllowance.building.postcode,
        path = s"/allowances/$typeOfBuildingAllowance/$index/building/postcode"
      ),
      BuildingNameNumberValidation.validate(
        building = structuredBuildingAllowance.building,
        path = s"/allowances/$typeOfBuildingAllowance/$index/building"
      )
    ).flatten
  }

  private def validateTradingIncomeAllowance(allowances: Allowances): List[MtdError] = {
    BothAllowancesValidation.validate(allowances)
  }

}
