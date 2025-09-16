/*
 * Copyright 2024 HM Revenue & Customs
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

package v4.retrieveCumulativePeriodSummary.def1

import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors.*
import shared.utils.UnitSpec
import v4.retrieveCumulativePeriodSummary.def1.model.request.Def1_RetrieveCumulativePeriodSummaryRequestData
import v4.retrieveCumulativePeriodSummary.model.request.RetrieveCumulativePeriodSummaryRequestData

class Def1_RetrieveCumulativePeriodSummaryValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validTaxYear    = "2025-26"

  private val parsedNino       = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)
  private val parsedTaxYear    = TaxYear.fromMtd(validTaxYear)

  private def validator(nino: String, businessId: String, taxYear: String) =
    new Def1_RetrieveCumulativePeriodSummaryValidator(nino, businessId, taxYear)

  "validator()" should {
    "return the parsed domain object" when {
      "given a valid request" in {
        val result: Either[ErrorWrapper, RetrieveCumulativePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validTaxYear).validateAndWrapResult()

        result shouldBe Right(Def1_RetrieveCumulativePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedTaxYear))
      }
    }

    "return a single error" when {
      "given an invalid nino" in {
        val result: Either[ErrorWrapper, RetrieveCumulativePeriodSummaryRequestData] =
          validator("invalid nino", validBusinessId, validTaxYear).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "given an invalid business id" in {
        val result: Either[ErrorWrapper, RetrieveCumulativePeriodSummaryRequestData] =
          validator(validNino, "invalid business id", validTaxYear).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BusinessIdFormatError))
      }

      "given an invalid tax year" in {
        val result: Either[ErrorWrapper, RetrieveCumulativePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "invalid tax year").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, TaxYearFormatError))
      }

      "given an invalid tax year range" in {
        val result: Either[ErrorWrapper, RetrieveCumulativePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "2025-27").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError))
      }

      "a tax year below 2025-26 is passed" in {
        val result: Either[ErrorWrapper, RetrieveCumulativePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "2024-25").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))
      }

    }

    "return multiple errors" when {
      "given invalid parameters" in {
        val result: Either[ErrorWrapper, RetrieveCumulativePeriodSummaryRequestData] =
          validator("invalid", "invalid", "invalid").validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(BusinessIdFormatError, NinoFormatError, TaxYearFormatError))
          )
        )
      }
    }
  }

}
