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

package v3.createAmendAnnualSubmission.def2.request

import play.api.libs.json.Json
import support.UnitSpec

class Def2_CreateAmend_AllowancesSpec extends UnitSpec {

  val model: Def2_CreateAmend_Allowances = Def2_CreateAmend_Allowances(
    annualInvestmentAllowance = Some(1.12),
    capitalAllowanceMainPool = Some(2.12),
    capitalAllowanceSpecialRatePool = Some(3.12),
    zeroEmissionsGoodsVehicleAllowance = Some(4.12),
    businessPremisesRenovationAllowance = Some(5.12),
    enhancedCapitalAllowance = Some(6.12),
    allowanceOnSales = Some(7.12),
    capitalAllowanceSingleAssetPool = Some(8.12),
    electricChargePointAllowance = Some(9.12),
    tradingIncomeAllowance = Some(10.12),
    zeroEmissionsCarAllowance = Some(11.12),
    structuredBuildingAllowance = Some(Nil),
    enhancedStructuredBuildingAllowance = Some(Nil)
  )

  "reads" when {
    "passed valid mtd JSON" should {
      "return the model" in {
        Json
          .parse(s"""{
             |  "annualInvestmentAllowance": 1.12,
             |  "capitalAllowanceMainPool": 2.12,
             |  "capitalAllowanceSpecialRatePool": 3.12,
             |  "zeroEmissionsGoodsVehicleAllowance": 4.12,
             |  "businessPremisesRenovationAllowance": 5.12,
             |  "enhancedCapitalAllowance": 6.12,
             |  "allowanceOnSales": 7.12,
             |  "capitalAllowanceSingleAssetPool": 8.12,
             |  "electricChargePointAllowance": 9.12,
             |  "tradingIncomeAllowance": 10.12,
             |  "zeroEmissionsCarAllowance": 11.12,
             |  "structuredBuildingAllowance": [],
             |  "enhancedStructuredBuildingAllowance": []
             |}
             |""".stripMargin)
          .as[Def2_CreateAmend_Allowances] shouldBe model
      }
    }
  }

  "writes" when {
    "passed a model" should {
      "return downstream JSON" in {
        Json.toJson(model) shouldBe Json.parse(s"""{
             |  "annualInvestmentAllowance": 1.12,
             |  "capitalAllowanceMainPool": 2.12,
             |  "capitalAllowanceSpecialRatePool": 3.12,
             |  "zeroEmissionGoodsVehicleAllowance": 4.12,
             |  "businessPremisesRenovationAllowance": 5.12,
             |  "enhanceCapitalAllowance": 6.12,
             |  "allowanceOnSales": 7.12,
             |  "capitalAllowanceSingleAssetPool": 8.12,
             |  "electricChargePointAllowance": 9.12,
             |  "tradingIncomeAllowance": 10.12,
             |  "zeroEmissionsCarAllowance": 11.12,
             |  "structuredBuildingAllowance": [],
             |  "enhancedStructuredBuildingAllowance": []
             |}
             |""".stripMargin)
      }
    }
  }

}
