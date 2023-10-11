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

import api.models.errors.PeriodIdFormatError
import support.UnitSpec

class PeriodIdValidationSpec extends UnitSpec {

  "validate" should {
    "return no errors" when {
      "a valid period id is supplied" in {
        PeriodIdValidation.validate("2017-01-25_2017-02-25") shouldBe empty
      }

      "a period id where the start date is the earliest allowed" in {
        PeriodIdValidation.validate("1900-01-01_2000-01-01") shouldBe empty
      }

      "a period id where the start date is the latest allowed" in {
        PeriodIdValidation.validate("2000-01-01-2099-12-31") shouldBe empty
      }
    }

    "return an error" when {
      "an empty periodId is supplied" in {
        PeriodIdValidation.validate("") shouldBe Seq(PeriodIdFormatError)
      }

      "an invalid period id is supplied" in {
        PeriodIdValidation.validate("201839127") shouldBe Seq(PeriodIdFormatError)
      }

      "a period id where only the start date has valid format is supplied" in {
        PeriodIdValidation.validate("2017-01-25_XXX") shouldBe Seq(PeriodIdFormatError)
      }

      "a period id where only the end date has valid format is supplied" in {
        PeriodIdValidation.validate("XXX_2017-01-25") shouldBe Seq(PeriodIdFormatError)
      }

      "an invalid period id (but with the correct length) is supplied" in {
        PeriodIdValidation.validate("201839127125364718273") shouldBe Seq(PeriodIdFormatError)
      }

      "a period id is supplied that is too long" in {
        PeriodIdValidation.validate("2017-01-25_2017-02-2512") shouldBe Seq(PeriodIdFormatError)
      }

      "a period id where the start date is earlier than allowed" in {
        PeriodIdValidation.validate("1899-12-31_2000-01-01") shouldBe Seq(PeriodIdFormatError)
      }

      "a period id where the start date is later than allowed" in {
        PeriodIdValidation.validate("2000-01-01-2100-01-01") shouldBe Seq(PeriodIdFormatError)
      }
    }

  }

}
