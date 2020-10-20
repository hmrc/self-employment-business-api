/*
 * Copyright 2020 HM Revenue & Customs
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

import mocks.MockAppConfig
import support.UnitSpec
import v1.models.errors.RuleTaxYearNotSupportedError
import v1.models.utils.JsonErrorValidators

class MtdTaxYearValidationSpec extends UnitSpec with JsonErrorValidators with MockAppConfig {
  
  val minimumTaxYear = 2018

  "validate" should {

    class Test {
      (mockAppConfig.minimumTaxYear _: () => Int).expects().returns(minimumTaxYear)
    }

    "return no errors" when {
      "a tax year greater than the minimum allowed tax year is supplied" in new Test {
        val taxYear = "2020-21"
        val validationResult = MtdTaxYearValidation.validate(taxYear, mockAppConfig.minimumTaxYear)
        validationResult.isEmpty shouldBe true

      }

      "the minimum allowed tax year is supplied" in new Test {
        val taxYear = "2019-20"
        val validationResult = MtdTaxYearValidation.validate(taxYear, mockAppConfig.minimumTaxYear)
        validationResult.isEmpty shouldBe true
      }

    }

    "return the given error" when {
      "a tax year below the minimum allowed tax year is supplied" in new Test {
        val taxYear = "2018-19"
        val validationResult = MtdTaxYearValidation.validate(taxYear, mockAppConfig.minimumTaxYear)
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleTaxYearNotSupportedError
      }
    }
  }
}
