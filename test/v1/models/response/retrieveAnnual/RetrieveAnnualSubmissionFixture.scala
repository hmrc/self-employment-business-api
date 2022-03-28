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

package v1.models.response.retrieveAnnual

import play.api.libs.json.{JsObject, JsValue, Json}
import v1.models.domain.ex.MtdNicExemption

trait RetrieveAnnualSubmissionFixture {

  val structuredBuildingAllowance: StructuredBuildingAllowance =
    StructuredBuildingAllowance(
      3000.30,
      Some(FirstYear(
        "2020-01-01",
        3000.40
      )),
      Building(
        Some("house name"),
        Some("house number"),
        "GF49JH"
      )
    )

  val structuredBuildingAllowanceMtdJson: JsValue = Json.parse(
    """
      |{
      |  "amount": 3000.30,
      |  "firstYear": {
      |    "qualifyingDate": "2020-01-01",
      |    "qualifyingAmountExpenditure": 3000.40
      |  },
      |  "building": {
      |    "name": "house name",
      |    "number": "house number",
      |    "postcode": "GF49JH"
      |  }
      |}
      |""".stripMargin)

  val structuredBuildingAllowanceDownstreamJson: JsValue = Json.parse(
    """
      |{
      |  "amount": 3000.30,
      |  "firstYear": {
      |    "qualifyingDate": "2020-01-01",
      |    "qualifyingAmountExpenditure": 3000.40
      |  },
      |  "building": {
      |    "name": "house name",
      |    "number": "house number",
      |    "postCode": "GF49JH"
      |  }
      |}
      |""".stripMargin)

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
      goodsAndServicesOwnUse = Some(9.12))

  val adjustmentsMtdJson: JsValue = Json.parse(
    s"""{
       |  "includedNonTaxableProfits": 1.12,
       |  "basisAdjustment": 2.12,
       |  "overlapReliefUsed": 3.12,
       |  "accountingAdjustment": 4.12,
       |  "averagingAdjustment": 5.12,
       |  "outstandingBusinessIncome": 6.12,
       |  "balancingChargeBPRA": 7.12,
       |  "balancingChargeOther": 8.12,
       |  "goodsAndServicesOwnUse": 9.12
       |}
       |""".stripMargin)

  val adjustmentsDownstreamJson: JsValue = Json.parse(
    s"""{
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

  val allowances: Allowances = Allowances(
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
    structuredBuildingAllowance = Some(Seq(structuredBuildingAllowance)),
    enhancedStructuredBuildingAllowance = Some(Seq(structuredBuildingAllowance))
  )

  val allowancesMtdJson: JsValue = Json.parse(
    s"""{
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
       |  "structuredBuildingAllowance": [ $structuredBuildingAllowanceMtdJson ],
       |  "enhancedStructuredBuildingAllowance": [ $structuredBuildingAllowanceMtdJson ]
       |}
       |""".stripMargin)

  val allowancesDownstreamJson: JsValue = Json.parse(
    s"""{
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
       |  "structuredBuildingAllowance": [ $structuredBuildingAllowanceDownstreamJson ],
       |  "enhancedStructuredBuildingAllowance": [ $structuredBuildingAllowanceDownstreamJson]
       |}
       |""".stripMargin)

  val allowancesTradingIncomeAllowance: Allowances = Allowances(
    None, None, None, None, None, None, None, None, tradingIncomeAllowance = Some(10.12), None, None, None, None
  )

  val allowancesTradingIncomeAllowanceMtdJson: JsValue = Json.parse(
    s"""{
       |  "tradingIncomeAllowance": 10.12
       |}
       |""".stripMargin)

  val allowancesTradingIncomeAllowanceDownstreamJson: JsValue = Json.parse(
    s"""{
       |  "tradingIncomeAllowance": 10.12
       |}
       |""".stripMargin)

  val nonFinancials: NonFinancials = NonFinancials(
    businessDetailsChangedRecently = true,
    class4NicsExemptionReason = Some(MtdNicExemption.`non-resident`))

  val nonFinancialsMtdJson: JsValue = Json.parse(
    s"""
       |{
       |    "businessDetailsChangedRecently": true,
       |    "class4NicsExemptionReason": "non-resident"
       |  }
       |""".stripMargin)

  val nonFinancialsDownstreamJson: JsValue = Json.parse(
    s"""
       |{
       |  "businessDetailsChangedRecently": true,
       |  "exemptFromPayingClass4Nics": true,
       |  "class4NicsExemptionReason": "001"
       |}
       |""".stripMargin)

  val retrieveResponseModel: RetrieveAnnualSubmissionResponse = RetrieveAnnualSubmissionResponse(
    Some(adjustments),
    Some(allowances),
    Some(nonFinancials)
  )

  def retrieveAnnualSubmissionBody(adjustmentsModel: Option[Adjustments] = Some(adjustments),
                                allowancesModel: Option[Allowances] = Some(allowances),
                                nonFinancialsModel: Option[NonFinancials] = Some(nonFinancials)): RetrieveAnnualSubmissionResponse =
    RetrieveAnnualSubmissionResponse(adjustmentsModel, allowancesModel, nonFinancialsModel)

  def retrieveAnnualSubmissionBodyMtdJson(adjustments: Option[JsValue] = Some(adjustmentsMtdJson),
                                       allowances: Option[JsValue] = Some(allowancesMtdJson),
                                       nonFinancials: Option[JsValue] = Some(nonFinancialsMtdJson)): JsValue =
    JsObject(Seq(
      adjustments.map("adjustments" -> _),
      allowances.map("allowances" -> _),
      nonFinancials.map("nonFinancials" -> _)
    ).collect { case Some((a, b)) => a -> b })

  val downstreamRetrieveResponseJson: JsValue = Json.parse(
    s"""
       |{
       |  "adjustments": $adjustmentsDownstreamJson,
       |  "allowances": $allowancesDownstreamJson,
       |  "nonFinancials": $nonFinancialsDownstreamJson
       |}
       |""".stripMargin)

  def retrieveAnnualSubmissionBodyDownstreamJson(adjustments: Option[JsValue] = Some(adjustmentsDownstreamJson),
                                              allowances: Option[JsValue] = Some(allowancesDownstreamJson),
                                              nonFinancials: Option[JsValue] = Some(nonFinancialsDownstreamJson)): JsValue =
    JsObject(Seq(
      adjustments.map("annualAdjustments" -> _),
      allowances.map("annualAllowances" -> _),
      nonFinancials.map("annualNonFinancials" -> _)
    ).collect { case Some((a, b)) => a -> b })

}
