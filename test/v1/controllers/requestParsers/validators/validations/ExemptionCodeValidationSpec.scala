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
import v1.models.domain.ex.MtdEx
import v1.models.domain.ex.MtdEx._
import v1.models.errors._
import v1.models.request.amendAnnualSummary.Class4NicInfo
import v1.models.utils.JsonErrorValidators

class ExemptionCodeValidationSpec extends UnitSpec with JsonErrorValidators {

  val validExemptionCode: Option[MtdEx] = Some(`001 - Non Resident`)

  "validate" should {
    "return no errors" when {
      "when exemption code is supplied where the is exempt indicator is set to true" in {

        val validationResult = IsExemptValidation.validate(Class4NicInfo(true, validExemptionCode))
        validationResult.isEmpty shouldBe true

      }
    }

    "return an error" when {
      "when an exemption code is supplied where the is exempt indicator is set to false" in {

        val validationResult = IsExemptValidation.validate(Class4NicInfo(false, validExemptionCode))
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleExemptionCodeError

      }
    }
    "return an error" when {
      "when no exemption code is supplied where the is exempt indicator is set to true" in {

        val validationResult = IsExemptValidation.validate(Class4NicInfo(true, None))
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleExemptionCodeError

      }
    }
  }
}