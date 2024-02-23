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

package v3.controllers.retrievePeriodSummary.def1

import api.models.domain.{BusinessId, Nino, PeriodId}
import api.models.errors._
import support.UnitSpec
import v3.controllers.retrievePeriodSummary.model.request.{Def1_RetrievePeriodSummaryRequestData, RetrievePeriodSummaryRequestData}

class Def1_RetrievePeriodSummaryValidatorSpec extends UnitSpec {

  private implicit val correlationId: String = "1234"

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validPeriodId   = "2017-01-25_2017-02-28"

  private val parsedNino       = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)
  private val parsedPeriodId   = PeriodId(validPeriodId)

  private def validator(nino: String, businessId: String, periodId: String) =
    new Def1_RetrievePeriodSummaryValidator(nino, businessId, periodId)

  "validator()" should {
    "return the parsed domain object" when {
      "given a valid request" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId).validateAndWrapResult()

        result shouldBe Right(Def1_RetrievePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedPeriodId))
      }
    }

    "return a single error" when {
      "given an invalid nino" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator("invalid nino", validBusinessId, validPeriodId).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "given an invalid business id" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, "invalid business id", validPeriodId).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BusinessIdFormatError))
      }

      "given an invalid period id" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "invalid period id").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, PeriodIdFormatError))
      }

      "given a period id outside of range" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "0010-01-01_2017-02-31").validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, PeriodIdFormatError))
      }
    }

    "return multiple errors" when {
      "given invalid parameters" in {
        val result: Either[ErrorWrapper, RetrievePeriodSummaryRequestData] =
          validator("invalid", "invalid", "invalid").validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(BusinessIdFormatError, NinoFormatError, PeriodIdFormatError))
          )
        )
      }
    }
  }

}
