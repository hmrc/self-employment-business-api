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
import v1.models.errors.RuleBothExpensesSuppliedError
import v1.models.request.createPeriodSummary._

class ConsolidatedExpensesValidationSpec extends UnitSpec {

  val allowableExpensesWithoutConsolidated: Option[PeriodAllowableExpenses] = Some(
    PeriodAllowableExpenses(
      consolidatedExpenses = None,
      costOfGoodsAllowable = Some(100.50),
      paymentsToSubcontractorsAllowable = Some(100.50),
      wagesAndStaffCostsAllowable = Some(100.50),
      carVanTravelExpensesAllowable = Some(100.50),
      premisesRunningCostsAllowable = Some(100.50),
      maintenanceCostsAllowable = Some(100.50),
      adminCostsAllowable = Some(100.50),
      businessEntertainmentCostsAllowable = Some(100.50),
      advertisingCostsAllowable = Some(100.50),
      interestOnBankOtherLoansAllowable = Some(100.50),
      financeChargesAllowable = Some(100.50),
      irrecoverableDebtsAllowable = Some(100.50),
      professionalFeesAllowable = Some(100.50),
      depreciationAllowable = Some(100.50),
      otherExpensesAllowable = Some(100.50)
    ))

  val allowableExpensesWithConsolidated: Option[PeriodAllowableExpenses] = Some(
    PeriodAllowableExpenses(
      consolidatedExpenses = Some(100.50),
      costOfGoodsAllowable = Some(100.50),
      paymentsToSubcontractorsAllowable = Some(100.50),
      wagesAndStaffCostsAllowable = Some(100.50),
      carVanTravelExpensesAllowable = Some(100.50),
      premisesRunningCostsAllowable = Some(100.50),
      maintenanceCostsAllowable = Some(100.50),
      adminCostsAllowable = None,
      businessEntertainmentCostsAllowable = Some(100.50),
      advertisingCostsAllowable = Some(100.50),
      interestOnBankOtherLoansAllowable = Some(100.50),
      financeChargesAllowable = Some(100.50),
      irrecoverableDebtsAllowable = Some(100.50),
      professionalFeesAllowable = Some(100.50),
      depreciationAllowable = Some(100.50),
      otherExpensesAllowable = Some(100.50)
    ))

  val allowableExpensesWithConsolidatedOnly: Option[PeriodAllowableExpenses] = Some(
    PeriodAllowableExpenses(
      consolidatedExpenses = Some(100.50),
      costOfGoodsAllowable = None,
      paymentsToSubcontractorsAllowable = None,
      wagesAndStaffCostsAllowable = None,
      carVanTravelExpensesAllowable = None,
      premisesRunningCostsAllowable = None,
      maintenanceCostsAllowable = None,
      adminCostsAllowable = None,
      businessEntertainmentCostsAllowable = None,
      advertisingCostsAllowable = None,
      interestOnBankOtherLoansAllowable = None,
      financeChargesAllowable = None,
      irrecoverableDebtsAllowable = None,
      professionalFeesAllowable = None,
      depreciationAllowable = None,
      otherExpensesAllowable = None
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
      "other allowable expenses without consolidated expenses are supplied" in {
        ConsolidatedExpensesValidation.validate(allowableExpensesWithoutConsolidated, None).isEmpty shouldBe true
      }
      "disallowable expenses without allowable expenses are supplied" in {
        ConsolidatedExpensesValidation.validate(None, disallowableExpenses).isEmpty shouldBe true
      }
      "other allowable expenses without consolidated expenses and disallowable expenses are supplied" in {
        ConsolidatedExpensesValidation.validate(allowableExpensesWithoutConsolidated, disallowableExpenses).isEmpty shouldBe true
      }
      "consolidatedExpenses is only supplied" in {
        ConsolidatedExpensesValidation.validate(allowableExpensesWithConsolidatedOnly, None).isEmpty shouldBe true
      }
    }

    "return RuleBothExpensesSuppliedError" when {
      "all allowable expenses (consolidated expenses included) are supplied" in {
        val validationResult = ConsolidatedExpensesValidation.validate(allowableExpensesWithConsolidated, None)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBothExpensesSuppliedError
      }
      "all allowable expenses (consolidated expenses included) and disallowable expenses are supplied" in {
        val validationResult = ConsolidatedExpensesValidation.validate(allowableExpensesWithConsolidated, disallowableExpenses)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBothExpensesSuppliedError
      }
      "only consolidated expenses (excluding all other allowable expenses) and disallowable expenses are supplied" in {
        val validationResult = ConsolidatedExpensesValidation.validate(allowableExpensesWithConsolidatedOnly, disallowableExpenses)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBothExpensesSuppliedError
      }
    }
  }

}
