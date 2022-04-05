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
import v1.models.errors.{DateFormatError, RuleToDateBeforeFromDateError, ToDateFormatError}
import v1.models.utils.JsonErrorValidators

class DateValidationSpec extends UnitSpec with JsonErrorValidators {

  "validate" should {
    "return no errors" when {
      "a valid date is supplied" in {
        val date           = "2020-03-20"
        val validateResult = DateValidation.validate(date, ToDateFormatError)

        validateResult.isEmpty shouldBe true
      }
      "a valid date is supplied with paths" in {
        val date             = "2020-03-20"
        val paths            = "/testObject/testField"
        val validationResult = DateValidation.validateWithPaths(date, paths)

        validationResult.isEmpty shouldBe true
      }
      "no date is supplied" in {
        val validationResult = DateValidation.validateOptional(None, "")

        validationResult.isEmpty shouldBe true
      }
    }
    "return an error" when {
      "an invalid date is supplied" in {
        val validationResult = DateValidation.validate("930-213", ToDateFormatError)

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe ToDateFormatError
      }
      "an invalid date is supplied with paths" in {
        val validationResult = DateValidation.validateWithPaths("930-213", "/testObject/testField")

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe DateFormatError.copy(paths = Some(Seq("/testObject/testField")))
      }
    }
  }

  "validateToDateBeforeFromDate" should {
    "return no errors" when {
      "the from date is before to date" in {
        val validationResult = DateValidation.validateToDateBeforeFromDate("2019-01-01", "2020-01-01")

        validationResult.isEmpty shouldBe true
      }
    }
    "return an error" when {
      "the to date is before from date" in {
        val validationResult = DateValidation.validateToDateBeforeFromDate("2020-01-01", "2019-01-01")

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleToDateBeforeFromDateError
      }
    }
  }

}
