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

package v1.models.request.amendSEAnnual

import api.models.domain.ex.MtdNicExemption
import play.api.libs.json.{JsArray, JsObject, JsValue, Json}

trait AmendAnnualSubmissionFixture extends StructuredBuildingAllowanceFixture {

  val adjustments: Adjustments =
    Adjustments(
      includedNonTaxableProfits = Some(1.12),
      basisAdjustment = Some(2.12),
      overlapReliefUsed = Some(3.12),
      accountingAdjustment = Some(4.12),
      averagingAdjustment = Some(5.12),
      outstandingBusinessIncome = Some(6.12),
      balancingChargeBpra = Some(7.12),
      balancingChargeOther = Some(8.12),
      goodsAndServicesOwnUse = Some(9.12)
    )

  val adjustmentsMtdJson: JsValue = Json.parse(s"""{
       |  "includedNonTaxableProfits": 1.12,
       |  "basisAdjustment": 2.12,
       |  "overlapReliefUsed": 3.12,
       |  "accountingAdjustment": 4.12,
       |  "averagingAdjustment": 5.12,
       |  "outstandingBusinessIncome": 6.12,
       |  "balancingChargeBpra": 7.12,
       |  "balancingChargeOther": 8.12,
       |  "goodsAndServicesOwnUse": 9.12
       |}
       |""".stripMargin)

  val adjustmentsDownstreamJson: JsValue = Json.parse(s"""{
       |  "includedNonTaxableProfits": 1.12,
       |  "basisAdjustment": 2.12,
       |  "overlapReliefUsed": 3.12,
       |  "accountingAdjustment": 4.12,
       |  "averagingAdjustment": 5.12,
       |  "outstandingBusinessIncome": 6.12,
       |  "balancingChargeBpra": 7.12,
       |  "balancingChargeOther": 8.12,
       |  "goodsAndServicesOwnUse": 9.12
       |}
       |""".stripMargin)

  def allowancesWith(structuredBuildingAllowances: Seq[StructuredBuildingAllowance] = Seq(structuredBuildingAllowance),
                     enhancedStructuredBuildingAllowances: Seq[StructuredBuildingAllowance] = Seq(structuredBuildingAllowance)): Allowances =
    Allowances(
      annualInvestmentAllowance = Some(1.12),
      capitalAllowanceMainPool = Some(2.12),
      capitalAllowanceSpecialRatePool = Some(3.12),
      zeroEmissionsGoodsVehicleAllowance = Some(4.12),
      businessPremisesRenovationAllowance = Some(5.12),
      enhancedCapitalAllowance = Some(6.12),
      allowanceOnSales = Some(7.12),
      capitalAllowanceSingleAssetPool = Some(8.12),
      electricChargePointAllowance = Some(9.12),
      tradingIncomeAllowance = None,
      zeroEmissionsCarAllowance = Some(11.12),
      structuredBuildingAllowance = Some(structuredBuildingAllowances),
      enhancedStructuredBuildingAllowance = Some(enhancedStructuredBuildingAllowances)
    )

  val allowances: Allowances = allowancesWith()

  def allowancesMtdJsonWith(structuredBuildingAllowances: Seq[JsValue] = Seq(structuredBuildingAllowanceMtdJson),
                            enhancedStructuredBuildingAllowances: Seq[JsValue] = Seq(structuredBuildingAllowanceMtdJson)): JsValue = Json.parse(s"""{
       |  "annualInvestmentAllowance": 1.12,
       |  "capitalAllowanceMainPool": 2.12,
       |  "capitalAllowanceSpecialRatePool": 3.12,
       |  "zeroEmissionsGoodsVehicleAllowance": 4.12,
       |  "businessPremisesRenovationAllowance": 5.12,
       |  "enhancedCapitalAllowance": 6.12,
       |  "allowanceOnSales": 7.12,
       |  "capitalAllowanceSingleAssetPool": 8.12,
       |  "electricChargePointAllowance": 9.12,
       |  "zeroEmissionsCarAllowance": 11.12,
       |  "structuredBuildingAllowance": ${JsArray(structuredBuildingAllowances)},
       |  "enhancedStructuredBuildingAllowance": ${JsArray(enhancedStructuredBuildingAllowances)}
       |}
       |""".stripMargin)

  val allowancesMtdJson: JsValue = allowancesMtdJsonWith()

  def allowancesDownstreamJsonWith(structuredBuildingAllowances: Seq[JsValue] = Seq(structuredBuildingAllowanceDownstreamJson),
                                   enhancedStructuredBuildingAllowances: Seq[JsValue] = Seq(structuredBuildingAllowanceDownstreamJson)): JsValue =
    Json.parse(s"""{
       |  "annualInvestmentAllowance": 1.12,
       |  "capitalAllowanceMainPool": 2.12,
       |  "capitalAllowanceSpecialRatePool": 3.12,
       |  "zeroEmissionGoodsVehicleAllowance": 4.12,
       |  "businessPremisesRenovationAllowance": 5.12,
       |  "enhanceCapitalAllowance": 6.12,
       |  "allowanceOnSales": 7.12,
       |  "capitalAllowanceSingleAssetPool": 8.12,
       |  "electricChargePointAllowance": 9.12,
       |  "zeroEmissionsCarAllowance": 11.12,
       |  "structuredBuildingAllowance": ${JsArray(structuredBuildingAllowances)},
       |  "enhancedStructuredBuildingAllowance": ${JsArray(enhancedStructuredBuildingAllowances)}
       |}
       |""".stripMargin)

  val allowancesDownstreamJson: JsValue = allowancesDownstreamJsonWith()

  val allowancesTradingIncomeAllowance: Allowances = Allowances(
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    None,
    tradingIncomeAllowance = Some(10.12),
    None,
    None,
    None,
    None
  )

  val allowancesTradingIncomeAllowanceMtdJson: JsValue = Json.parse(s"""{
       |  "tradingIncomeAllowance": 10.12
       |}
       |""".stripMargin)

  val allowancesTradingIncomeAllowanceDownstreamJson: JsValue = Json.parse(s"""{
       |  "tradingIncomeAllowance": 10.12
       |}
       |""".stripMargin)

  val nonFinancials: NonFinancials =
    NonFinancials(businessDetailsChangedRecently = true, class4NicsExemptionReason = Some(MtdNicExemption.`non-resident`))

  val nonFinancialsMtdJson: JsValue = Json.parse(s"""
       |{
       |    "businessDetailsChangedRecently": true,
       |    "class4NicsExemptionReason": "non-resident"
       |  }
       |""".stripMargin)

  val nonFinancialsDownstreamJson: JsValue = Json.parse(s"""
       |{
       |  "businessDetailsChangedRecently": true,
       |  "exemptFromPayingClass4Nics": true,
       |  "class4NicsExemptionReason": "001"
       |}
       |""".stripMargin)

  def amendAnnualSubmissionBody(adjustmentsModel: Option[Adjustments] = Some(adjustments),
                                allowancesModel: Option[Allowances] = Some(allowances),
                                nonFinancialsModel: Option[NonFinancials] = Some(nonFinancials)): AmendAnnualSubmissionBody =
    AmendAnnualSubmissionBody(adjustmentsModel, allowancesModel, nonFinancialsModel)

  def amendAnnualSubmissionBodyMtdJson(adjustments: Option[JsValue] = Some(adjustmentsMtdJson),
                                       allowances: Option[JsValue] = Some(allowancesMtdJson),
                                       nonFinancials: Option[JsValue] = Some(nonFinancialsMtdJson)): JsValue =
    JsObject(
      Seq(
        adjustments.map("adjustments"     -> _),
        allowances.map("allowances"       -> _),
        nonFinancials.map("nonFinancials" -> _)
      ).collect { case Some((a, b)) => a -> b })

  def amendAnnualSubmissionBodyDownstreamJson(adjustments: Option[JsValue] = Some(adjustmentsDownstreamJson),
                                              allowances: Option[JsValue] = Some(allowancesDownstreamJson),
                                              nonFinancials: Option[JsValue] = Some(nonFinancialsDownstreamJson)): JsValue =
    JsObject(
      Seq(
        adjustments.map("annualAdjustments"     -> _),
        allowances.map("annualAllowances"       -> _),
        nonFinancials.map("annualNonFinancials" -> _)
      ).collect { case Some((a, b)) => a -> b })

}
