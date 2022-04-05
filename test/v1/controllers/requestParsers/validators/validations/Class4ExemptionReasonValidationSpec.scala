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
import v1.models.errors.Class4ExemptionReasonFormatError

class Class4ExemptionReasonValidationSpec extends UnitSpec {

  "validate" should {
    "return no errors" when {
      Seq("non-resident", "trustee", "diver", "ITTOIA-2005", "over-state-pension-age", "under-16").foreach { nic =>
        s"return an empty list for valid class4 $nic" in {
          Class4ExemptionReasonValidation.validate(nic) shouldBe NoValidationErrors
        }
      }
    }
    "return an error" when {
      "a value that isn't one of the class4 fields is submitted" in {
        val validationResult = Class4ExemptionReasonValidation.validate("notValid")

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe Class4ExemptionReasonFormatError
      }
    }
  }

}
