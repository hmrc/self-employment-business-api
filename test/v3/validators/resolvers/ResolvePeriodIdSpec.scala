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

package v3.validators.resolvers

import shared.models.domain.PeriodId
import shared.models.errors.PeriodIdFormatError
import cats.data.Validated.{Invalid, Valid}
import support.UnitSpec

class ResolvePeriodIdSpec extends UnitSpec {

  private val validPeriodId  = "2017-01-25_2017-02-28"
  private val parsedPeriodId = PeriodId(validPeriodId)

  "ResolvePeriodId" should {
    "return no errors" when {
      "given a valid period id for an unlimited range" in {
        val result = ResolvePeriodId(validPeriodId)
        result shouldBe Valid(parsedPeriodId)
      }

      "given a valid period id for a limited range" in {
        val minYear = 1900
        val maxYear = 2100
        val result  = ResolvePeriodId(validPeriodId, minYear, maxYear)
        result shouldBe Valid(parsedPeriodId)
      }
    }

    "return an error" when {
      "given an invalidly format period ID" in {
        val invalidPeriodId = "2017-01-25__2017-02-31"
        val result          = ResolvePeriodId(invalidPeriodId)
        result shouldBe Invalid(List(PeriodIdFormatError))
      }

      "given a period ID where the toDate predates the fromDate" in {
        val invalidPeriodId = "2018-01-25_2017-02-31"
        val result          = ResolvePeriodId(invalidPeriodId)
        result shouldBe Invalid(List(PeriodIdFormatError))
      }

      "given a period ID before the minimum year supplied" in {
        val invalidPeriodId = "2017-01-25_2017-02-31"
        val minYear         = 2018
        val maxYear         = 2019
        val result          = ResolvePeriodId(invalidPeriodId, minYear, maxYear)
        result shouldBe Invalid(List(PeriodIdFormatError))
      }
    }
  }

}
