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

import api.models.errors.InvalidTaxYearParameterError
import support.UnitSpec

class TaxYearTYSParameterValidationSpec extends UnitSpec {

  "validate" should {
    "return no errors" when {
      "a tax year that is 2023-24 is supplied" in {

        val validTaxYear     = "2023-24"
        val validationResult = TaxYearTYSParameterValidation.validate(validTaxYear)
        validationResult shouldBe empty
      }
    }

    "return the given error" when {
      "a tax year below 2023-24 is supplied" in {

        val invalidTaxYear   = "2022-23"
        val validationResult = TaxYearTYSParameterValidation.validate(invalidTaxYear)
        validationResult shouldBe List(InvalidTaxYearParameterError)
      }
    }
  }

}
