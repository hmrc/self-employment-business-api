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

import support.UnitSpec
import v1.models.errors.ValueFormatError

class NumberValidationSpec extends UnitSpec {

  val validNumber: Option[BigDecimal] = Some(9000.42)
  val lowestAllowedNumber: Option[BigDecimal] = Some(0)
  val highestAllowedNumber: Option[BigDecimal] = Some(99999999999.99)
  val invalidNumber: Option[BigDecimal] = Some(-9000.42)

  "validate" should {
    "return no errors" when {
      "a valid number is supplied" in {
        val validationResult = NumberValidation.validateOptional(validNumber, "/vctSubscription/1/amountInvested")
        validationResult.isEmpty shouldBe true
      }
      "no number is supplied" in {
        val validationResult = NumberValidation.validateOptional(None, "/vctSubscription/1/amountInvested")
        validationResult.isEmpty shouldBe true
      }
      "the lowest allowed number (0) is supplied" in {
        val validationResult = NumberValidation.validateOptional(lowestAllowedNumber, "/vctSubscription/1/amountInvested")
        validationResult.isEmpty shouldBe true
      }
      "the highest allowed number (99999999999.99) is supplied" in {
        val validationResult = NumberValidation.validateOptional(highestAllowedNumber, "/vctSubscription/1/amountInvested")
        validationResult.isEmpty shouldBe true
      }
    }

    "return an error" when {
      "an invalid number is supplied" in {
        val validationResult = NumberValidation.validateOptional(invalidNumber, "/vctSubscription/1/amountInvested")
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe ValueFormatError.copy(paths = Some(Seq("/vctSubscription/1/amountInvested")))
      }
    }
  }
}