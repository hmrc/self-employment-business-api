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

class AllowancesSpec extends UnitSpec {

  val fullMtdModel: Allowances =
    Allowances(Some(1.11), Some(2.22), Some(3.33), Some(4.44), Some(5.55), Some(6.66), Some(7.77), Some(8.88), Some(9.99), Some(10.10), Some(11.11))
  val partialMtdModel: Allowances = Allowances(Some(1.11), None, Some(3.33), None, None, Some(6.66), Some(7.77), None, Some(9.99), Some(10.10), Some(11.11))
  val emptyMtdModel: Allowances = Allowances(None, None, None, None, None, None, None, None, None, None, None)

  "reads" should {

    "read from JSON" when {

      val fullRequestJson: JsValue = Json.parse(
        s"""
           |{
           |  "annualInvestmentAllowance": 1.11,
           |  "businessPremisesRenovationAllowance": 2.22,
           |  "capitalAllowanceMainPool": 3.33,
           |  "capitalAllowanceSpecialRatePool": 4.44,
           |  "zeroEmissionGoodsVehicleAllowance": 5.55,
           |  "enhancedCapitalAllowance": 6.66,
           |  "allowanceOnSales": 7.77,
           |  "capitalAllowanceSingleAssetPool": 8.88,
           |  "tradingAllowance": 9.99,
           |  "structureAndBuildingAllowance": 10.10,
           |  "electricChargePointAllowance": 11.11
           |}
           |""".stripMargin)

      val partialRequestJson: JsValue = Json.parse(
        s"""
           |{
           |  "annualInvestmentAllowance": 1.11,
           |  "capitalAllowanceMainPool": 3.33,
           |  "enhancedCapitalAllowance": 6.66,
           |  "allowanceOnSales": 7.77,
           |  "tradingAllowance": "9.99",
           |  "structureAndBuildingAllowance": 10.10,
           |  "electricChargePointAllowance": 11.11
           |}
           |""".stripMargin)

      val emptyRequestJson: JsValue = Json.parse(
        s"""
           |{
           |
           |}
           |""".stripMargin)

      "a valid request with all data is made" in {
        fullRequestJson.as[Allowances] shouldBe fullMtdModel
      }

      "a valid request with some data is made" in {
        partialRequestJson.as[Allowances] shouldBe partialMtdModel
      }

      "a valid request with no data is made" in {
        emptyRequestJson.as[Allowances] shouldBe emptyMtdModel
      }

    }
  }

  "Writes" should {

    "write to des" when{

      val fullDesJson: JsValue = Json.parse(
        s"""
           |{
           |  "annualInvestmentAllowance": 1.11,
           |  "businessPremisesRenovationAllowance": 2.22,
           |  "capitalAllowanceMainPool": 3.33,
           |  "capitalAllowanceSpecialRatePool": 4.44,
           |  "zeroEmissionGoodsVehicleAllowance": 5.55,
           |  "enhanceCapitalAllowance": 6.66,
           |  "allowanceOnSales": 7.77,
           |  "capitalAllowanceSingleAssetPool": 8.88,
           |  "tradingIncomeAllowance": 9.99,
           |  "structureAndBuildingAllowance": 10.10,
           |  "electricChargePointAllowance": 11.11
           |}
           |""".stripMargin)

      val partialDesJson: JsValue = Json.parse(
        s"""
           |{
           |  "annualInvestmentAllowance": 1.11,
           |  "capitalAllowanceMainPool": 3.33,
           |  "enhanceCapitalAllowance": 6.66,
           |  "allowanceOnSales": 7.77,
           |  "tradingIncomeAllowance": 9.99,
           |  "structureAndBuildingAllowance": 10.10,
           |  "electricChargePointAllowance": 11.11
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
