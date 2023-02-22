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

import anyVersion.models.request.amendPeriodSummary.PeriodDisallowableExpenses
import api.models.errors.RuleBothExpensesSuppliedError
import api.models.utils.JsonErrorValidators
import support.UnitSpec
import v2.models.request.amendPeriodSummary.PeriodExpenses

class AmendConsolidatedExpensesValidationSpec extends UnitSpec with JsonErrorValidators {

  val expenses: Option[PeriodExpenses] = Some(
    PeriodExpenses(
      None,
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50)
    ))

  val expensesConsolidated: Option[PeriodExpenses] = Some(
    PeriodExpenses(
      Some(100.50),
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None,
      None
    ))

  val expensesBothSupplied: Option[PeriodExpenses] = Some(
    PeriodExpenses(
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50)
    ))

  val disallowableExpenses: Option[PeriodDisallowableExpenses] = Some(
    PeriodDisallowableExpenses(
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50),
      Some(100.50)
    ))

  "validate" should {
    "return no errors" when {
      "when expenses is only supplied" in {
        AmendConsolidatedExpensesValidation.validate(expenses, None).isEmpty shouldBe true
      }
      "when expenses is only supplied with disallowable expenses" in {
        AmendConsolidatedExpensesValidation.validate(expenses, disallowableExpenses).isEmpty shouldBe true
      }
      "when consolidatedExpenses is only supplied" in {
        AmendConsolidatedExpensesValidation.validate(expensesConsolidated, None).isEmpty shouldBe true
      }
      "only disallowable expenses is supplied" in {
        AmendConsolidatedExpensesValidation.validate(None, disallowableExpenses).isEmpty shouldBe true
      }
    }

    "return an error" when {
      "when both expenses and consolidatedExpenses is supplied" in {
        val validationResult = AmendConsolidatedExpensesValidation.validate(expensesBothSupplied, None)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBothExpensesSuppliedError
      }
      "when both expenses and consolidatedExpenses is supplied with disallowable expenses" in {
        val validationResult = AmendConsolidatedExpensesValidation.validate(expensesBothSupplied, disallowableExpenses)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBothExpensesSuppliedError
      }
      "when both disallowable expenses and consolidatedExpenses is supplied" in {
        val validationResult = AmendConsolidatedExpensesValidation.validate(expensesConsolidated, disallowableExpenses)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBothExpensesSuppliedError
      }
    }
  }

}
