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

import api.models.domain.PeriodId
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import support.UnitSpec
import v2.models.request.retrievePeriodSummary.RetrievePeriodSummaryRequestData

class RetrievePeriodSummaryValidatorFactorySpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validPeriodId   = "2017-01-25_2017-02-28"
  private val validTaxYear    = Some("2023-24")

  private val parsedNino       = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)
  private val parsedPeriodId   = PeriodId(validPeriodId)
  private val parsedTaxYear    = validTaxYear.map(TaxYear.fromMtd)

  private val validatorFactory = new RetrievePeriodSummaryValidatorFactory

  private def validator(nino: String, businessId: String, periodId: String, taxYear: Option[String]) =
    validatorFactory.validator(nino, businessId, periodId, taxYear)

  "validator" should {
    "return the parsed domain object" when {
      "passed a valid request without a tax year" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, None).validateAndWrapResult()

        result shouldBe Right(RetrievePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedPeriodId, None))
      }

      "passed a valid request with a TYS tax year" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear).validateAndWrapResult()

        result shouldBe Right(RetrievePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedPeriodId, parsedTaxYear))
      }
    }

    "return a single error" when {
      "an invalid nino is supplied" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator("invalid nino", validBusinessId, validPeriodId, validTaxYear).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "an invalid business id is supplied" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, "invalid business id", validPeriodId, validTaxYear).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BusinessIdFormatError))
      }

      "an invalid period id is supplied" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "invalid period id", validTaxYear).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, PeriodIdFormatError))
      }

      "a period id outside of range is supplied" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "0010-01-01_2017-02-31", validTaxYear).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, PeriodIdFormatError))
      }

      "an invalid tax year is supplied" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, Some("invalid tax year")).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, TaxYearFormatError))
      }

      "an invalid tax year range is supplied" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, Some("2023-25")).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError))
      }

      "a non-TYS tax year is supplied" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, Some("2021-22")).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, InvalidTaxYearParameterError))
      }
    }

    "return multiple errors" when {
      "invalid parameters are supplied" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator("invalid", "invalid", "invalid", Some("invalid")).validateAndWrapResult()

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
