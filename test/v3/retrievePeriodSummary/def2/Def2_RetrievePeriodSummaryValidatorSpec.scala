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

package v3.retrievePeriodSummary.def2

import api.models.domain.PeriodId
import api.models.errors.PeriodIdFormatError
import shared.utils.UnitSpec
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import v3.retrievePeriodSummary.model.request.{Def2_RetrievePeriodSummaryRequestData, RetrievePeriodSummaryRequestData}

class Def2_RetrievePeriodSummaryValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validPeriodId   = "2017-01-25_2017-02-28"
  private val validTaxYear    = "2023-24"

  private val parsedNino       = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)
  private val parsedPeriodId   = PeriodId(validPeriodId)
  private val parsedTaxYear    = TaxYear.fromMtd(validTaxYear)

  private def validator(nino: String, businessId: String, periodId: String, taxYear: String) =
    new Def2_RetrievePeriodSummaryValidator(nino, businessId, periodId, taxYear)

  "validator()" should {
    "return the parsed domain object" when {

      "passed a valid request with a TYS tax year" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear).validateAndWrapResult()

        result shouldBe Right(Def2_RetrievePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedPeriodId, parsedTaxYear))
      }
    }

    "return a single error" when {
      "given an invalid nino" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator("invalid nino", validBusinessId, validPeriodId, validTaxYear).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "given an invalid business id" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, "invalid business id", validPeriodId, validTaxYear).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BusinessIdFormatError))
      }

      "given an invalid period id" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "invalid period id", validTaxYear).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, PeriodIdFormatError))
      }

      "given a period id outside of range" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "0010-01-01_2017-02-31", validTaxYear).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, PeriodIdFormatError))
      }

      "given an invalid tax year" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, "invalid tax year").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, TaxYearFormatError))
      }

      "given an invalid tax year range" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, "2023-25").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError))
      }

      "a tax year 2025 or over is passed" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, "2025-26").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))
      }

      "given a non-TYS tax year" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, "2021-22").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, InvalidTaxYearParameterError))
      }
    }

    "return multiple errors" when {
      "given invalid parameters" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator("invalid", "invalid", "invalid", "invalid").validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(BusinessIdFormatError, NinoFormatError, PeriodIdFormatError, TaxYearFormatError))
          )
        )
      }
    }
  }

}
