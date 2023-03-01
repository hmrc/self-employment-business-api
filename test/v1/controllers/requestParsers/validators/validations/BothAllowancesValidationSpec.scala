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

package v1.controllers.requestParsers.validators.validations

import api.models.errors.RuleBothAllowancesSuppliedError
import support.UnitSpec
import v1.models.request.amendSEAnnual.{Allowances, Building, StructuredBuildingAllowance}

class BothAllowancesValidationSpec extends UnitSpec {
  val building: Building                                       = Building(None, None, "postcode")
  val structuredBuildingAllowance: StructuredBuildingAllowance = StructuredBuildingAllowance(1.00, None, building)

  val allowancesWithoutTradingIncome: Allowances =
    Allowances(
      Some(1.00),
      Some(1.00),
      Some(1.00),
      Some(1.00),
      Some(1.00),
      Some(1.00),
      Some(1.00),
      Some(1.00),
      None,
      Some(1.00),
      Some(1.00),
      Some(Seq(structuredBuildingAllowance)),
      Some(Seq(structuredBuildingAllowance))
    )

  val allowancesWithOnlyTradingIncome: Allowances =
    Allowances(None, None, None, None, None, None, None, None, tradingIncomeAllowance = Some(1.00), None, None, None, None)

  val invalidAllowance: Allowances = Allowances(
    Some(1.00),
    Some(1.00),
    Some(1.00),
    Some(1.00),
    Some(1.00),
    Some(1.00),
    Some(1.00),
    Some(1.00),
    Some(1.00),
    Some(1.00),
    Some(1.00),
    Some(Seq(structuredBuildingAllowance)),
    Some(Seq(structuredBuildingAllowance))
  )

  "validate" should {
    "return no errors" when {
      "allowance has every field except tradingIncomeAllowance" in {
        val validationResult = BothAllowancesValidation.validate(allowancesWithoutTradingIncome)
        validationResult.isEmpty shouldBe true
      }
      "allowance has only tradingIncomeAllowance" in {
        val validationResult = BothAllowancesValidation.validate(allowancesWithOnlyTradingIncome)
        validationResult.isEmpty shouldBe true
      }
    }
  }

  "return an error" when {
    "allowance contains both tradingIncomeAllowance and other allowances" in {
      val validationResult = BothAllowancesValidation.validate(invalidAllowance)
      validationResult.isEmpty shouldBe false
      validationResult.length shouldBe 1
      validationResult.head shouldBe RuleBothAllowancesSuppliedError
    }
    "annualInvestmentAllowance is provided with tradingIncomeAllowance" in {
      val allowances = allowancesWithOnlyTradingIncome.copy(annualInvestmentAllowance = Some(123.12))
      BothAllowancesValidation.validate(allowances) shouldBe List(RuleBothAllowancesSuppliedError)
    }
    "businessPremisesRenovationAllowance is provided with tradingIncomeAllowance" in {
      val allowances = allowancesWithOnlyTradingIncome.copy(businessPremisesRenovationAllowance = Some(123.12))
      BothAllowancesValidation.validate(allowances) shouldBe List(RuleBothAllowancesSuppliedError)
    }
    "capitalAllowanceMainPool is provided with tradingIncomeAllowance" in {
      val allowances = allowancesWithOnlyTradingIncome.copy(capitalAllowanceMainPool = Some(123.12))
      BothAllowancesValidation.validate(allowances) shouldBe List(RuleBothAllowancesSuppliedError)
    }
    "capitalAllowanceSpecialRatePool is provided with tradingIncomeAllowance" in {
      val allowances = allowancesWithOnlyTradingIncome.copy(capitalAllowanceSpecialRatePool = Some(123.12))
      BothAllowancesValidation.validate(allowances) shouldBe List(RuleBothAllowancesSuppliedError)
    }
    "zeroEmissionsGoodsVehicleAllowance is provided with tradingIncomeAllowance" in {
      val allowances = allowancesWithOnlyTradingIncome.copy(zeroEmissionsGoodsVehicleAllowance = Some(123.12))
      BothAllowancesValidation.validate(allowances) shouldBe List(RuleBothAllowancesSuppliedError)
    }
    "enhancedCapitalAllowance is provided with tradingIncomeAllowance" in {
      val allowances = allowancesWithOnlyTradingIncome.copy(enhancedCapitalAllowance = Some(123.12))
      BothAllowancesValidation.validate(allowances) shouldBe List(RuleBothAllowancesSuppliedError)
    }
    "allowanceOnSales is provided with tradingIncomeAllowance" in {
      val allowances = allowancesWithOnlyTradingIncome.copy(allowanceOnSales = Some(123.12))
      BothAllowancesValidation.validate(allowances) shouldBe List(RuleBothAllowancesSuppliedError)
    }
    "capitalAllowanceSingleAssetPool is provided with tradingIncomeAllowance" in {
      val allowances = allowancesWithOnlyTradingIncome.copy(capitalAllowanceSingleAssetPool = Some(123.12))
      BothAllowancesValidation.validate(allowances) shouldBe List(RuleBothAllowancesSuppliedError)
    }
    "electricChargePointAllowance is provided with tradingIncomeAllowance" in {
      val allowances = allowancesWithOnlyTradingIncome.copy(electricChargePointAllowance = Some(123.12))
      BothAllowancesValidation.validate(allowances) shouldBe List(RuleBothAllowancesSuppliedError)
    }
    "zeroEmissionsCarAllowance is provided with tradingIncomeAllowance" in {
      val allowances = allowancesWithOnlyTradingIncome.copy(zeroEmissionsCarAllowance = Some(123.12))
      BothAllowancesValidation.validate(allowances) shouldBe List(RuleBothAllowancesSuppliedError)
    }
    "structuredBuildingAllowance is provided with tradingIncomeAllowance" in {
      val allowances = allowancesWithOnlyTradingIncome.copy(structuredBuildingAllowance = Some(Seq(structuredBuildingAllowance)))
      BothAllowancesValidation.validate(allowances) shouldBe List(RuleBothAllowancesSuppliedError)
    }
    "enhancedStructuredBuildingAllowance is provided with tradingIncomeAllowance" in {
      val allowances = allowancesWithOnlyTradingIncome.copy(enhancedStructuredBuildingAllowance = Some(Seq(structuredBuildingAllowance)))
      BothAllowancesValidation.validate(allowances) shouldBe List(RuleBothAllowancesSuppliedError)
    }
  }

}
