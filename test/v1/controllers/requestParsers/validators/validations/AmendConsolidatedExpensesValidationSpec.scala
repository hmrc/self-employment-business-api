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
import v1.models.request.amendPeriodic.{PeriodAllowableExpenses, PeriodDisallowableExpenses}
import v1.models.utils.JsonErrorValidators

class AmendConsolidatedExpensesValidationSpec extends UnitSpec with JsonErrorValidators {



  val allowableExpenses: Option[PeriodAllowableExpenses] = Some(PeriodAllowableExpenses(
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

  val allowableExpensesConsolidated: Option[PeriodAllowableExpenses] = Some(PeriodAllowableExpenses(
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

  val allowableExpensesBothSupplied: Option[PeriodAllowableExpenses] = Some(PeriodAllowableExpenses(
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



  val disallowableExpenses: Option[PeriodDisallowableExpenses] = Some(PeriodDisallowableExpenses(
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
        AmendConsolidatedExpensesValidation.validate(allowableExpenses, None).isEmpty shouldBe true
      }
      "when expenses is only supplied with disallowable expenses" in {
        AmendConsolidatedExpensesValidation.validate(allowableExpenses, disallowableExpenses).isEmpty shouldBe true
      }
      "when consolidatedExpenses is only supplied" in {
        AmendConsolidatedExpensesValidation.validate(allowableExpensesConsolidated, None).isEmpty shouldBe true
      }
    }

    "return an error" when {
      "when both expenses and consolidatedExpenses is supplied" in {
        val validationResult = AmendConsolidatedExpensesValidation.validate(allowableExpensesBothSupplied, None)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBothExpensesSuppliedError
      }
      "when both expenses and consolidatedExpenses is supplied with disallowable expenses" in {
        val validationResult = AmendConsolidatedExpensesValidation.validate(allowableExpensesBothSupplied, disallowableExpenses)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBothExpensesSuppliedError
      }
      "when both disallowable expenses and consolidatedExpenses is supplied" in {
        val validationResult = AmendConsolidatedExpensesValidation.validate(allowableExpensesConsolidated, disallowableExpenses)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBothExpensesSuppliedError
      }
    }
  }

}
