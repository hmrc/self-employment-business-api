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

package v1.controllers.validators

import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import support.UnitSpec
import v1.models.request.listPeriodSummaries.ListPeriodSummariesRequestData

class ListPeriodSummariesValidatorFactorySpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678910"
  private val validTaxYear    = "2024-25"

  private val parsedNino       = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)
  private val parsedTaxYear    = Some(TaxYear.fromMtd(validTaxYear))

  private val validatorFactory = new ListPeriodSummariesValidatorFactory

  private def validator(nino: String, businessId: String, taxYear: Option[String]) = validatorFactory.validator(nino, businessId, taxYear)

  "validator" should {
    "return the parsed domain object" when {
      "a valid request is made" in {
        val result: Either[ErrorWrapper, ListPeriodSummariesRequestData] =
          validator(validNino, validBusinessId, Some(validTaxYear)).validateAndWrapResult()
        result shouldBe Right(
          ListPeriodSummariesRequestData(parsedNino, parsedBusinessId, parsedTaxYear)
        )
      }
      "a valid request with no tax year supplied" in {
        val result: Either[ErrorWrapper, ListPeriodSummariesRequestData] =
          validator(validNino, validBusinessId, None).validateAndWrapResult()
        result shouldBe Right(
          ListPeriodSummariesRequestData(parsedNino, parsedBusinessId, None)
        )
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        val result: Either[ErrorWrapper, ListPeriodSummariesRequestData] =
          validator("A12344A", validBusinessId, Some(validTaxYear)).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
    }

    "return BusinessIdFormatError error" when {
      "an invalid nino is supplied" in {
        val result: Either[ErrorWrapper, ListPeriodSummariesRequestData] =
          validator(validNino, "Walruses", Some(validTaxYear)).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, BusinessIdFormatError)
        )
      }
    }

    "return TaxYearFormat" when {
      "an invalid tax year is supplied" in {
        val result: Either[ErrorWrapper, ListPeriodSummariesRequestData] =
          validator(validNino, validBusinessId, Some("1234A")).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
    }

    "return RuleTaxYearRangeInvalidError" when {
      "an invalid tax year is supplied" in {
        val result: Either[ErrorWrapper, ListPeriodSummariesRequestData] =
          validator(validNino, validBusinessId, Some("2023-25")).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
    }

    "return InvalidTaxYearParameterError" when {
      "an invalid tax year is supplied" in {
        val result: Either[ErrorWrapper, ListPeriodSummariesRequestData] =
          validator(validNino, validBusinessId, Some("2021-22")).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, InvalidTaxYearParameterError)
        )
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors" in {
        val result: Either[ErrorWrapper, ListPeriodSummariesRequestData] = validator("A12344A", "Baked Beans", Some("20178")).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, BadRequestError, Some(List(BusinessIdFormatError, NinoFormatError, TaxYearFormatError)))
        )
      }
    }
  }

}
