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

package v1.controllers.requestParsers.validators

import support.UnitSpec
import v1.models.errors._
import v1.models.request.deleteAnnual.DeleteAnnualSubmissionRawData

class DeleteAnnualSubmissionValidatorSpec extends UnitSpec {

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678910"
  private val validTaxYear    = "2017-18"

  val validator = new DeleteAnnualSubmissionValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(DeleteAnnualSubmissionRawData(validNino, validBusinessId, validTaxYear)) shouldBe Nil
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(DeleteAnnualSubmissionRawData("A12344A", validBusinessId, validTaxYear)) shouldBe
          List(NinoFormatError)
      }
    }

    "return BusinessIdFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(DeleteAnnualSubmissionRawData(validNino, "Walruses", validTaxYear)) shouldBe
          List(BusinessIdFormatError)
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        validator.validate(DeleteAnnualSubmissionRawData(validNino, validBusinessId, "20178")) shouldBe
          List(TaxYearFormatError)
      }
    }

    "return RuleTaxYearRangeInvalidError" when {
      "an invalid tax year is supplied" in {
        validator.validate(DeleteAnnualSubmissionRawData(validNino, validBusinessId, "2017-19")) shouldBe
          List(RuleTaxYearRangeInvalidError)
      }
    }

    "return RuleTaxYearNotSupportedError" when {
      "an invalid tax year is supplied" in {
        validator.validate(DeleteAnnualSubmissionRawData(validNino, validBusinessId, "2016-17")) shouldBe
          List(RuleTaxYearNotSupportedError)
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors" in {
        validator.validate(DeleteAnnualSubmissionRawData("A12344A", "Baked Beans", "20178")) shouldBe
          List(NinoFormatError, BusinessIdFormatError, TaxYearFormatError)
      }
    }
  }

}
