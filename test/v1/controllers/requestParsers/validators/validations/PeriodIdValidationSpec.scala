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
import v1.models.errors.PeriodIdFormatError

class PeriodIdValidationSpec extends UnitSpec {

  "validate" should {
    "return no errors" when {
      "a valid period id is supplied" in {

        val validPeriodId    = "2017-01-25_2017-02-25"
        val validationResult = PeriodIdValidation.validate(validPeriodId)
        validationResult.isEmpty shouldBe true
      }
    }

    "return an error" when {
      "an invalid period id is supplied" in {

        val invalidPeriodId  = "201839127"
        val validationResult = PeriodIdValidation.validate(invalidPeriodId)
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe PeriodIdFormatError
      }

      "an invalid period id with length of 21 is supplied" in {

        val invalidPeriodId  = "201839127125364718273"
        val validationResult = PeriodIdValidation.validate(invalidPeriodId)
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe PeriodIdFormatError
      }

      "a period id is supplied that is too long" in {

        val invalidPeriodId  = "2017-01-25_2017-02-2512"
        val validationResult = PeriodIdValidation.validate(invalidPeriodId)
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe PeriodIdFormatError
      }
    }

  }

}
