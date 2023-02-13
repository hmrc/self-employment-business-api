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

import api.models.errors.StringFormatError
import support.UnitSpec

class StringValidationSpec extends UnitSpec {

  val validString                     = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789- _&():.'^aaaaaaaaaaaaaaaaaa"
  val stringThatExceedsCharacterLimit = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789- _&():.'^aaaaaaaaaaaaaaaaaab"
  val stringWithInvalidChar           = "#"

  "validate" should {
    "return no errors" when {
      "a valid string is submitted" in {
        val validationResult = StringValidation.validate(validString, "/testPath")

        validationResult.isEmpty shouldBe true
      }
      "no string is submitted" in {
        val validationResult = StringValidation.validateOptional(None, "/testPath")

        validationResult.isEmpty shouldBe true
      }
    }
    "return an error" when {
      "a string that exceeds the character limit is submitted" in {
        val validationResult = StringValidation.validate(stringThatExceedsCharacterLimit, "/testPath")

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe StringFormatError.copy(paths = Some(Seq("/testPath")))
      }
      "a string with an invalid character is submitted" in {
        val validationResult = StringValidation.validate(stringWithInvalidChar, "/testPath")

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe StringFormatError.copy(paths = Some(Seq("/testPath")))
      }
    }
  }

}
