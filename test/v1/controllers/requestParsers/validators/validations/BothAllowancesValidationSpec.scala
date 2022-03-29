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

package v1.controllers.requestParsers.validators.validations

import support.UnitSpec
import v1.models.errors.RuleBothAllowancesSuppliedError
import v1.models.request.amendSEAnnual.{Allowances, Building, StructuredBuildingAllowance}

class BothAllowancesValidationSpec extends UnitSpec {
  val building: Building = Building(None, None, "postcode")
  val structuredBuildingAllowance: StructuredBuildingAllowance = StructuredBuildingAllowance(1.00, None, building)

  val allowancesWithoutTradingIncome: Allowances =
    Allowances(Some(1.00), Some(1.00), Some(1.00), Some(1.00), Some(1.00), Some(1.00), Some(1.00), Some(1.00), None,
      Some(1.00), Some(1.00), Some(Seq(structuredBuildingAllowance)), Some(Seq(structuredBuildingAllowance)))
  val allowancesWithOnlyTradingIncome: Allowances =
    Allowances(None, None, None, None, None, None, None, None,tradingIncomeAllowance = Some(1.00), None, None, None, None)
  val invalidAllowance: Allowances = Allowances(
    Some(1.00), Some(1.00), Some(1.00), Some(1.00), Some(1.00), Some(1.00), Some(1.00), Some(1.00), Some(1.00),
    Some(1.00), Some(1.00), Some(Seq(structuredBuildingAllowance)), Some(Seq(structuredBuildingAllowance))
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
  }
}
