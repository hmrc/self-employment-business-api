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

package v2.controllers.requestParsers.validators.validations

import anyVersion.models.request.createPeriodSummary.PeriodDisallowableExpenses
import api.models.errors.RuleBothExpensesSuppliedError
import support.UnitSpec
import v2.models.request.createPeriodSummary._

class ConsolidatedExpensesValidationSpec extends UnitSpec {

  val expensesWithoutConsolidated: Option[PeriodExpenses] = Some(
    PeriodExpenses(
      consolidatedExpenses = None,
      costOfGoods = Some(100.50),
      paymentsToSubcontractors = Some(100.50),
      wagesAndStaffCosts = Some(100.50),
      carVanTravelExpenses = Some(100.50),
      premisesRunningCosts = Some(100.50),
      maintenanceCosts = Some(100.50),
      adminCosts = Some(100.50),
      businessEntertainmentCosts = Some(100.50),
      advertisingCosts = Some(100.50),
      interestOnBankOtherLoans = Some(100.50),
      financeCharges = Some(100.50),
      irrecoverableDebts = Some(100.50),
      professionalFees = Some(100.50),
      depreciation = Some(100.50),
      otherExpenses = Some(100.50)
    ))

  val expensesWithConsolidated: Option[PeriodExpenses] = Some(
    PeriodExpenses(
      consolidatedExpenses = Some(100.50),
      costOfGoods = Some(100.50),
      paymentsToSubcontractors = Some(100.50),
      wagesAndStaffCosts = Some(100.50),
      carVanTravelExpenses = Some(100.50),
      premisesRunningCosts = Some(100.50),
      maintenanceCosts = Some(100.50),
      adminCosts = None,
      businessEntertainmentCosts = Some(100.50),
      advertisingCosts = Some(100.50),
      interestOnBankOtherLoans = Some(100.50),
      financeCharges = Some(100.50),
      irrecoverableDebts = Some(100.50),
      professionalFees = Some(100.50),
      depreciation = Some(100.50),
      otherExpenses = Some(100.50)
    ))

  val expensesWithConsolidatedOnly: Option[PeriodExpenses] = Some(
    PeriodExpenses(
      consolidatedExpenses = Some(100.50),
      costOfGoods = None,
      paymentsToSubcontractors = None,
      wagesAndStaffCosts = None,
      carVanTravelExpenses = None,
      premisesRunningCosts = None,
      maintenanceCosts = None,
      adminCosts = None,
      businessEntertainmentCosts = None,
      advertisingCosts = None,
      interestOnBankOtherLoans = None,
      financeCharges = None,
      irrecoverableDebts = None,
      professionalFees = None,
      depreciation = None,
      otherExpenses = None
    ))

  val disallowableExpenses: Option[PeriodDisallowableExpenses] = Some(
    PeriodDisallowableExpenses(
      costOfGoodsDisallowable = Some(100.50),
      paymentsToSubcontractorsDisallowable = Some(100.50),
      wagesAndStaffCostsDisallowable = Some(100.50),
      carVanTravelExpensesDisallowable = Some(100.50),
      premisesRunningCostsDisallowable = Some(100.50),
      maintenanceCostsDisallowable = Some(100.50),
      adminCostsDisallowable = Some(100.50),
      businessEntertainmentCostsDisallowable = Some(100.50),
      advertisingCostsDisallowable = Some(100.50),
      interestOnBankOtherLoansDisallowable = Some(100.50),
      financeChargesDisallowable = Some(100.50),
      irrecoverableDebtsDisallowable = Some(100.50),
      professionalFeesDisallowable = Some(100.50),
      depreciationDisallowable = Some(100.50),
      otherExpensesDisallowable = Some(100.50)
    ))

  "validate" should {
    "return no errors" when {
      "no expenses is supplied" in {
        ConsolidatedExpensesValidation.validate(None, None).isEmpty shouldBe true
      }
      "other expenses without consolidated expenses are supplied" in {
        ConsolidatedExpensesValidation.validate(expensesWithoutConsolidated, None).isEmpty shouldBe true
      }
      "disallowable expenses without expenses are supplied" in {
        ConsolidatedExpensesValidation.validate(None, disallowableExpenses).isEmpty shouldBe true
      }
      "other expenses without consolidated expenses and disallowable expenses are supplied" in {
        ConsolidatedExpensesValidation.validate(expensesWithoutConsolidated, disallowableExpenses).isEmpty shouldBe true
      }
      "consolidatedExpenses is only supplied" in {
        ConsolidatedExpensesValidation.validate(expensesWithConsolidatedOnly, None).isEmpty shouldBe true
      }
    }

    "return RuleBothExpensesSuppliedError" when {
      "all expenses (consolidated expenses included) are supplied" in {
        val validationResult = ConsolidatedExpensesValidation.validate(expensesWithConsolidated, None)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBothExpensesSuppliedError
      }
      "all expenses (consolidated expenses included) and disallowable expenses are supplied" in {
        val validationResult = ConsolidatedExpensesValidation.validate(expensesWithConsolidated, disallowableExpenses)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBothExpensesSuppliedError
      }
      "only consolidated expenses (excluding all other expenses) and disallowable expenses are supplied" in {
        val validationResult = ConsolidatedExpensesValidation.validate(expensesWithConsolidatedOnly, disallowableExpenses)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBothExpensesSuppliedError
      }
    }
  }

}
