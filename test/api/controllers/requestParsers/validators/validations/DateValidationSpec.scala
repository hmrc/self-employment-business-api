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

package api.controllers.requestParsers.validators.validations

import api.models.errors.{MtdError, RuleEndBeforeStartDateError, ToDateFormatError}
import api.models.utils.JsonErrorValidators
import support.UnitSpec

class DateValidationSpec extends UnitSpec with JsonErrorValidators {

  val path            = "/testObject/testField"
  val error: MtdError = ToDateFormatError.copy(paths = Some(Seq(path)))

  "validate" should {

    "return no errors" when {
      "a valid date is supplied" in {
        DateValidation.validate("2020-03-20", error) shouldBe empty
      }

      "no date is supplied" in {
        DateValidation.validateOptional(None, error) shouldBe empty
      }

      "the date is the earliest allowed" in {
        DateValidation.validate("1900-01-01", error) shouldBe empty
      }

      "the date is the latest allowed" in {
        DateValidation.validate("2099-12-31", error) shouldBe empty
      }
    }

    "return an error" when {
      "an invalid (although correctly formatted) date is supplied" in {
        DateValidation.validate("2020-90-90", error) shouldBe Seq(error)
      }

      "an invalid date is supplied" in {
        DateValidation.validate("930-213", error) shouldBe Seq(error)
      }

      "the date is earlier than allowed" in {
        DateValidation.validate("1899-12-31", error) shouldBe Seq(error)
      }

      "the date is later than allowed" in {
        DateValidation.validate("2100-01-01", error) shouldBe Seq(error)
      }
    }
  }

  "validateEndDateBeforeStartDate" should {
    "return no errors" when {
      "the from date is before to date" in {
        DateValidation.validateEndDateBeforeStartDate("2019-01-01", "2020-01-01") shouldBe empty
      }

      "the from date is the same as the to date" in {
        DateValidation.validateEndDateBeforeStartDate("2019-01-01", "2019-01-01") shouldBe empty
      }
    }

    "return an error" when {
      "the to date is before from date" in {
        DateValidation.validateEndDateBeforeStartDate("2020-01-01", "2019-01-01") shouldBe Seq(RuleEndBeforeStartDateError)
      }
    }
  }

}
