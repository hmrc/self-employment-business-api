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

package v1.controllers.requestParsers.validators

import api.models.errors.{BusinessIdFormatError, InvalidTaxYearParameterError, NinoFormatError, RuleTaxYearRangeInvalidError, TaxYearFormatError}
import support.UnitSpec
import v1.models.request.listPeriodSummaries.ListPeriodSummariesRawData

class ListPeriodSummariesValidatorSpec extends UnitSpec {

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678910"
  private val taxYear         = Some("2024-25")

  val validator = new ListPeriodSummariesValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(ListPeriodSummariesRawData(validNino, validBusinessId, taxYear)) shouldBe Nil
      }
      "a valid request with no tax year supplied" in {
        validator.validate(ListPeriodSummariesRawData(validNino, validBusinessId, None)) shouldBe Nil
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(ListPeriodSummariesRawData("A12344A", validBusinessId, taxYear)) shouldBe List(NinoFormatError)
      }
    }

    "return BusinessIdFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(ListPeriodSummariesRawData(validNino, "Walruses", taxYear)) shouldBe List(BusinessIdFormatError)
      }
    }

    "return TaxYearFormat" when {
      "an invalid tax year is supplied" in {
        validator.validate(ListPeriodSummariesRawData(validNino, validBusinessId, Some("1234A"))) shouldBe List(TaxYearFormatError)
      }
    }

    "return RuleTaxYearRangeInvalidError" when {
      "an invalid tax year is supplied" in {
        validator.validate(ListPeriodSummariesRawData(validNino, validBusinessId, Some("2023-25"))) shouldBe List(RuleTaxYearRangeInvalidError)
      }
    }

    "return InvalidTaxYearParameterError" when {
      "an invalid tax year is supplied" in {
        validator.validate(ListPeriodSummariesRawData(validNino, validBusinessId, Some("2021-22"))) shouldBe List(InvalidTaxYearParameterError)
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors" in {
        validator.validate(ListPeriodSummariesRawData("A12344A", "Baked Beans", taxYear)) shouldBe List(NinoFormatError, BusinessIdFormatError)
      }
    }
  }

}
