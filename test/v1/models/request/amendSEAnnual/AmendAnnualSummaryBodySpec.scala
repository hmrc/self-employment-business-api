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

package v1.models.request.amendSEAnnual

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.models.domain.ex.MtdEx

class AmendAnnualSummaryBodySpec extends UnitSpec {

  val fullMtdModel: AmendAnnualSummaryBody =
    AmendAnnualSummaryBody(
      Some(Adjustments(Some(1.11), Some(2.22), Some(3.33), Some(4.44), Some(5.55), Some(6.66), Some(7.77), Some(8.88), Some(9.99), Some(10.10))),
      Some(Allowances(Some(1.11), Some(2.22), Some(3.33), Some(4.44), Some(5.55), Some(6.66), Some(7.77), Some(8.88), Some(9.99))),
      Some(NonFinancials(Some(Class4NicInfo(Some(MtdEx.`001 - Non Resident`))))))
  val partialMtdModel: AmendAnnualSummaryBody =
    AmendAnnualSummaryBody(
      Some(Adjustments(Some(1.11), Some(2.22), Some(3.33), Some(4.44), Some(5.55), Some(6.66), Some(7.77), Some(8.88), Some(9.99), Some(10.10))),
      Some(Allowances(Some(1.11), Some(2.22), Some(3.33), Some(4.44), Some(5.55), Some(6.66), Some(7.77), Some(8.88), Some(9.99))),
      None)
  val emptyMtdModel: AmendAnnualSummaryBody =
    AmendAnnualSummaryBody(None, None, None)

  "reads" should {

    "read from JSON" when {

      val fullRequestJson: JsValue = Json.parse(
        s"""
           |{
           |   "adjustments": {
           |        "includedNonTaxableProfits": 1.11,
           |        "basisAdjustment": 2.22,
           |        "overlapReliefUsed": 3.33,
           |        "accountingAdjustment": 4.44,
           |        "averagingAdjustment": 5.55,
           |        "lossBroughtForward": 6.66,
           |        "outstandingBusinessIncome": 7.77,
           |        "balancingChargeBPRA": 8.88,
           |        "balancingChargeOther": 9.99,
           |        "goodsAndServicesOwnUse": 10.10
           |    },
           |    "allowances": {
           |        "annualInvestmentAllowance": 1.11,
           |        "businessPremisesRenovationAllowance": 2.22,
           |        "capitalAllowanceMainPool": 3.33,
           |        "capitalAllowanceSpecialRatePool": 4.44,
           |        "zeroEmissionGoodsVehicleAllowance": 5.55,
           |        "enhancedCapitalAllowance": 6.66,
           |        "allowanceOnSales": 7.77,
           |        "capitalAllowanceSingleAssetPool": 8.88,
           |        "tradingAllowance": 9.99
           |    },
           |    "nonFinancials": {
           |        "class4NicInfo":{
           |            "exemptionCode": "001 - Non Resident"
           |        }
           |    }
           |}
           |""".stripMargin)

      val partialRequestJson: JsValue = Json.parse(
        s"""
           |{
           |   "adjustments": {
           |        "includedNonTaxableProfits": 1.11,
           |        "basisAdjustment": 2.22,
           |        "overlapReliefUsed": 3.33,
           |        "accountingAdjustment": 4.44,
           |        "averagingAdjustment": 5.55,
           |        "lossBroughtForward": 6.66,
           |        "outstandingBusinessIncome": 7.77,
           |        "balancingChargeBPRA": 8.88,
           |        "balancingChargeOther": 9.99,
           |        "goodsAndServicesOwnUse": 10.10
           |    },
           |    "allowances": {
           |        "annualInvestmentAllowance": 1.11,
           |        "businessPremisesRenovationAllowance": 2.22,
           |        "capitalAllowanceMainPool": 3.33,
           |        "capitalAllowanceSpecialRatePool": 4.44,
           |        "zeroEmissionGoodsVehicleAllowance": 5.55,
           |        "enhancedCapitalAllowance": 6.66,
           |        "allowanceOnSales": 7.77,
           |        "capitalAllowanceSingleAssetPool": 8.88,
           |        "tradingAllowance": 9.99
           |    }
           |}
           |""".stripMargin)

      val emptyRequestJson: JsValue = Json.parse(
        s"""
           |{
           |
           |}
           |""".stripMargin)

      "a valid request with all data is made" in {
        fullRequestJson.as[AmendAnnualSummaryBody] shouldBe fullMtdModel
      }

      "a valid request with some data is made" in {
        partialRequestJson.as[AmendAnnualSummaryBody] shouldBe partialMtdModel
      }

      "a valid request with no data is made" in {
        emptyRequestJson.as[AmendAnnualSummaryBody] shouldBe emptyMtdModel
      }

    }
  }

  "Writes" should {

    "write to des" when{

      val fullDesJson: JsValue = Json.parse(
        s"""
           |{
           |   "annualAdjustments": {
           |        "includedNonTaxableProfits": 1.11,
           |        "basisAdjustment": 2.22,
           |        "overlapReliefUsed": 3.33,
           |        "accountingAdjustment": 4.44,
           |        "averagingAdjustment": 5.55,
           |        "lossBroughtForward": 6.66,
           |        "outstandingBusinessIncome": 7.77,
           |        "balancingChargeBPRA": 8.88,
           |        "balancingChargeOther": 9.99,
           |        "goodsAndServicesOwnUse": 10.10
           |    },
           |    "annualAllowances": {
           |        "annualInvestmentAllowance": 1.11,
           |        "businessPremisesRenovationAllowance": 2.22,
           |        "capitalAllowanceMainPool": 3.33,
           |        "capitalAllowanceSpecialRatePool": 4.44,
           |        "zeroEmissionGoodsVehicleAllowance": 5.55,
           |        "enhancedCapitalAllowance": 6.66,
           |        "allowanceOnSales": 7.77,
           |        "capitalAllowanceSingleAssetPool": 8.88,
           |        "tradingIncomeAllowance": 9.99
           |    },
           |    "annualNonFinancials": {
           |        "exemptFromPayingClass4Nics": true,
           |        "class4NicsExemptionReason": "001"
           |    }
           |}
           |""".stripMargin)

      val partialDesJson: JsValue = Json.parse(
        s"""
           |{
           |   "annualAdjustments": {
           |        "includedNonTaxableProfits": 1.11,
           |        "basisAdjustment": 2.22,
           |        "overlapReliefUsed": 3.33,
           |        "accountingAdjustment": 4.44,
           |        "averagingAdjustment": 5.55,
           |        "lossBroughtForward": 6.66,
           |        "outstandingBusinessIncome": 7.77,
           |        "balancingChargeBPRA": 8.88,
           |        "balancingChargeOther": 9.99,
           |        "goodsAndServicesOwnUse": 10.10
           |    },
           |    "annualAllowances": {
           |        "annualInvestmentAllowance": 1.11,
           |        "businessPremisesRenovationAllowance": 2.22,
           |        "capitalAllowanceMainPool": 3.33,
           |        "capitalAllowanceSpecialRatePool": 4.44,
           |        "zeroEmissionGoodsVehicleAllowance": 5.55,
           |        "enhancedCapitalAllowance": 6.66,
           |        "allowanceOnSales": 7.77,
           |        "capitalAllowanceSingleAssetPool": 8.88,
           |        "tradingIncomeAllowance": 9.99
           |    }
           |}
           |""".stripMargin)

      val emptyDesJson: JsValue = Json.parse(
        s"""
           |{
           |
           |}
           |""".stripMargin)

      "a valid request is made with full body" in {
        Json.toJson(fullMtdModel) shouldBe fullDesJson
      }

      "a valid request is made with partial body" in {
        Json.toJson(partialMtdModel) shouldBe partialDesJson
      }

      "a valid request is made with empty body" in {
        Json.toJson(emptyMtdModel) shouldBe emptyDesJson
      }

    }
  }
}

