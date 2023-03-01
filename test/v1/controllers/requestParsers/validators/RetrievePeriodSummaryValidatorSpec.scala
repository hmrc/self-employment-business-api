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

import anyVersion.controllers.requestParsers.validators.RetrievePeriodSummaryValidator
import anyVersion.models.request.retrievePeriodSummary.RetrievePeriodSummaryRawData
import api.models.errors._
import support.UnitSpec

class RetrievePeriodSummaryValidatorSpec extends UnitSpec {

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678910"
  private val validPeriodId   = "2017-01-25_2017-02-31"
  private val validTaxYear    = Some("2023-24")

  val validator = new RetrievePeriodSummaryValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request with no tax year supplied" in {
        validator.validate(RetrievePeriodSummaryRawData(validNino, validBusinessId, validPeriodId, None)) shouldBe Nil
      }

      "a valid TYS request is supplied" in {
        validator.validate(RetrievePeriodSummaryRawData(validNino, validBusinessId, validPeriodId, validTaxYear)) shouldBe Nil
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(RetrievePeriodSummaryRawData("A12344A", validBusinessId, validPeriodId, None)) shouldBe
          List(NinoFormatError)
      }
    }

    "return BusinessIdFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(RetrievePeriodSummaryRawData(validNino, "Walruses", validPeriodId, None)) shouldBe
          List(BusinessIdFormatError)
      }
    }

    "return PeriodIdFormatError error" when {
      "an invalid period id is supplied" in {
        validator.validate(RetrievePeriodSummaryRawData(validNino, validBusinessId, "2647667456", None)) shouldBe
          List(PeriodIdFormatError)
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year format is supplied" in {
        validator.validate(RetrievePeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Some("202324"))) shouldBe
          List(TaxYearFormatError)
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year range is supplied" in {
        validator.validate(RetrievePeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Some("2023-26"))) shouldBe
          List(RuleTaxYearRangeInvalidError)
      }
    }

    "return InvalidTaxYearParameterError" when {
      "an invalid tax year is supplied" in {
        validator.validate(RetrievePeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Some("2021-22"))) shouldBe
          List(InvalidTaxYearParameterError)
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors" in {
        validator.validate(RetrievePeriodSummaryRawData("A12344A", "Baked Beans", "21742624", None)) shouldBe
          List(NinoFormatError, BusinessIdFormatError, PeriodIdFormatError)
      }
    }
  }

}
