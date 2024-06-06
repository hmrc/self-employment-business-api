/*
 * Copyright 2024 HM Revenue & Customs
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

package v3.retrieveAnnualSubmission.def1.model

import api.models.domain.ex.MtdNicExemption
import play.api.libs.json.{JsObject, JsValue, Json}
import v3.retrieveAnnualSubmission.def1.model.response._

trait Def1_RetrieveAnnualSubmissionFixture {

  val structuredBuildingAllowance: Def1_Retrieve_StructuredBuildingAllowance =
    Def1_Retrieve_StructuredBuildingAllowance(
      3000.30,
      Some(
        Def1_Retrieve_FirstYear(
          "2020-01-01",
          3000.40
        )),
      Def1_Retrieve_Building(
        Some("house name"),
        Some("house number"),
        "GF49JH"
      )
    )

  val buildingMtdModel: Def1_Retrieve_Building =
    Def1_Retrieve_Building(
      Some("house name"),
      Some("house number"),
      "GF4 9JH"
    )

  val buildingAllowanceMtdJson: JsValue = Json.parse("""
      |{
      |    "name": "house name",
      |    "number": "house number",
      |    "postcode": "GF4 9JH"
      |}
      |""".stripMargin)

  val buildingAllowanceDownstreamJson: JsValue = Json.parse("""
      |{
      |    "name": "house name",
      |    "number": "house number",
      |    "postCode": "GF4 9JH"
      |}
      |""".stripMargin)

  val structuredBuildingAllowanceMtdJson: JsValue = Json.parse("""
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

  val structuredBuildingAllowanceDownstreamJson: JsValue = Json.parse("""
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

  val adjustments: Retrieve_Adjustments =
    Retrieve_Adjustments(
      includedNonTaxableProfits = Some(1.12),
      basisAdjustment = Some(2.12),
      overlapReliefUsed = Some(3.12),
      accountingAdjustment = Some(4.12),
      averagingAdjustment = Some(5.12),
      outstandingBusinessIncome = Some(6.12),
      balancingChargeBpra = Some(7.12),
      balancingChargeOther = Some(8.12),
      goodsAndServicesOwnUse = Some(9.12),
      transitionProfitAmount = Some(9.12),
      transitionProfitAccelerationAmount = Some(9.12)
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
       |  "goodsAndServicesOwnUse": 9.12,
       |  "transitionProfitAmount": 9.12,
       |  "transitionProfitAccelerationAmount": 9.12
       |}
       |""".stripMargin)

  val adjustmentsWithoutAdditionalFieldsMtdJson: JsValue = Json.parse(s"""{
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
       |  "goodsAndServicesOwnUse": 9.12,
       |  "transitionProfitAmount": 9.12,
       |  "transitionProfitAccelerationAmount": 9.12
       |}
       |""".stripMargin)

  val allowances: Def1_Retrieve_Allowances = Def1_Retrieve_Allowances(
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
    structuredBuildingAllowance = Some(List(structuredBuildingAllowance)),
    enhancedStructuredBuildingAllowance = Some(List(structuredBuildingAllowance))
  )

  val allowancesMtdJson: JsValue = Json.parse(s"""{
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

  val allowancesDownstreamJson: JsValue = Json.parse(s"""{
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

  val allowancesTradingIncomeAllowance: Def1_Retrieve_Allowances = Def1_Retrieve_Allowances(
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

  val nonFinancials: Def1_Retrieve_NonFinancials =
    Def1_Retrieve_NonFinancials(
      businessDetailsChangedRecently = true,
      class4NicsExemptionReason = Some(MtdNicExemption.`non-resident`)
    )

  val nonFinancialsMtdJson: JsValue = Json.parse(s"""
       |{
       |    "businessDetailsChangedRecently": true,
       |    "class4NicsExemptionReason": "non-resident"
       |  }
       |""".stripMargin)

  val nonFinancialsDownstreamJson: JsValue = Json.parse(s"""
       |{
       |  "businessDetailsChangedRecently": true,
       |  "class4NicsExemptionReason": "001"
       |}
       |""".stripMargin)

  val retrieveResponseModel: Def1_RetrieveAnnualSubmissionResponse = Def1_RetrieveAnnualSubmissionResponse(
    Some(adjustments),
    Some(allowances),
    Some(nonFinancials)
  )

  val mtdRetrieveResponseJson: JsValue = Json.parse(s"""
       |{
       |  "adjustments": $adjustmentsMtdJson,
       |  "allowances": $allowancesMtdJson,
       |  "nonFinancials": $nonFinancialsMtdJson
       |}
       |""".stripMargin)

  val mtdRetrieveResponseWithNoAdditionalFieldsJson: JsValue = Json.parse(s"""
       |{
       |  "adjustments": $adjustmentsWithoutAdditionalFieldsMtdJson,
       |  "allowances": $allowancesMtdJson,
       |  "nonFinancials": $nonFinancialsMtdJson
       |}
       |""".stripMargin)

  val downstreamRetrieveResponseJson: JsValue = Json.parse(s"""
       |{
       |  "annualAdjustments": $adjustmentsDownstreamJson,
       |  "annualAllowances": $allowancesDownstreamJson,
       |  "annualNonFinancials": $nonFinancialsDownstreamJson
       |}
       |""".stripMargin)

  def mtdRetrieveAnnualSubmissionJsonWithHateoas(nino: String,
                                                 businessId: String,
                                                 taxYear: String,
                                                 responseBody: JsValue = mtdRetrieveResponseJson): JsValue =
    responseBody.as[JsObject] ++ Json
      .parse(s"""
         |{
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
         |      "method": "PUT",
         |      "rel": "create-and-amend-self-employment-annual-submission"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
         |      "method": "GET",
         |      "rel": "self"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
         |      "method": "DELETE",
         |      "rel": "delete-self-employment-annual-submission"
         |    }
         |  ]
         |}
         |""".stripMargin)
      .as[JsObject]

  def retrieveAnnualSubmissionBody(
                                    adjustmentsModel: Option[Retrieve_Adjustments] = Some(adjustments),
                                    allowancesModel: Option[Def1_Retrieve_Allowances] = Some(allowances),
                                    nonFinancialsModel: Option[Def1_Retrieve_NonFinancials] = Some(nonFinancials)): Def1_RetrieveAnnualSubmissionResponse =
    Def1_RetrieveAnnualSubmissionResponse(adjustmentsModel, allowancesModel, nonFinancialsModel)

  def retrieveAnnualSubmissionBodyMtdJson(adjustments: Option[JsValue] = Some(adjustmentsMtdJson),
                                          allowances: Option[JsValue] = Some(allowancesMtdJson),
                                          nonFinancials: Option[JsValue] = Some(nonFinancialsMtdJson)): JsValue =
    JsObject(
      Seq(
        adjustments.map("adjustments"     -> _),
        allowances.map("allowances"       -> _),
        nonFinancials.map("nonFinancials" -> _)
      ).collect { case Some((a, b)) => a -> b })

  def retrieveAnnualSubmissionBodyDownstreamJson(adjustments: Option[JsValue] = Some(adjustmentsDownstreamJson),
                                                 allowances: Option[JsValue] = Some(allowancesDownstreamJson),
                                                 nonFinancials: Option[JsValue] = Some(nonFinancialsDownstreamJson)): JsValue =
    JsObject(
      Seq(
        adjustments.map("annualAdjustments"     -> _),
        allowances.map("annualAllowances"       -> _),
        nonFinancials.map("annualNonFinancials" -> _)
      ).collect { case Some((a, b)) => a -> b })

}
