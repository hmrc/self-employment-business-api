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

package v2.controllers.validators

import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import mocks.MockAppConfig
import support.UnitSpec
import v2.models.request.deleteAnnual.DeleteAnnualSubmissionRequestData

class DeleteAnnualSubmissionValidatorFactorySpec extends UnitSpec with MockAppConfig {

  private implicit val correlationId: String = "1234"

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678910"
  private val validTaxYear    = "2017-18"

  private val parsedNino       = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)
  private val parsedTaxYear    = TaxYear.fromMtd(validTaxYear)

  private val validatorFactory = new DeleteAnnualSubmissionValidatorFactory
  private def validator(nino: String, businessId: String, taxYear: String) = validatorFactory.validator(nino, businessId, taxYear)

  "validator" should {
    "return the parsed domain object" when {
      "a valid request is made" in {
        val result: Either[ErrorWrapper, DeleteAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, validTaxYear).validateAndWrapResult()
        result shouldBe Right(
          DeleteAnnualSubmissionRequestData(parsedNino, parsedBusinessId, parsedTaxYear)
        )
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        val result: Either[ErrorWrapper, DeleteAnnualSubmissionRequestData] =
          validator("A12344A", validBusinessId, validTaxYear).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return BusinessIdFormatError error" when {
      "an invalid businessId is supplied" in {
        val result: Either[ErrorWrapper, DeleteAnnualSubmissionRequestData] = validator(validNino, "Walruses", validTaxYear).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, BusinessIdFormatError)
        )
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year is supplied" in {
        val result: Either[ErrorWrapper, DeleteAnnualSubmissionRequestData] = validator(validNino, validBusinessId, "20178").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError" when {
      "an invalid tax year is supplied" in {
        val result: Either[ErrorWrapper, DeleteAnnualSubmissionRequestData] = validator(validNino, validBusinessId, "2017-19").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return RuleTaxYearNotSupportedError" when {
      "an invalid tax year is supplied" in {
        val result: Either[ErrorWrapper, DeleteAnnualSubmissionRequestData] = validator(validNino, validBusinessId, "2016-17").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearNotSupportedError)
        )
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors" in {
        val result: Either[ErrorWrapper, DeleteAnnualSubmissionRequestData] = validator("A12344A", "Baked Beans", "20178").validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, BadRequestError, Some(List(BusinessIdFormatError, NinoFormatError, TaxYearFormatError)))
        )
      }
    }
  }

}
