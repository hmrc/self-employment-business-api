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

package api.controllers.validators.resolvers

import api.models.errors.IdFormatError
import cats.data.Validated.{Invalid, Valid}
import support.UnitSpec

class ResolveParsedNumericIdSpec extends UnitSpec {

  private val validId       = BigDecimal(2000)
  private val path          = "path"
  private val expectedError = Invalid(List(IdFormatError.withPath(path)))

  "apply()" should {
    "return the parsed value" when {
      "given a valid ID" in {
        val result = ResolveParsedNumericId(validId, path)
        result shouldBe Valid(validId)
      }
    }

    "return IdFormatError" when {
      "given zero" in {
        val result = ResolveParsedNumericId(0, path)
        result shouldBe expectedError
      }

      "given a negative number" in {
        val result = ResolveParsedNumericId(-2000, path)
        result shouldBe expectedError
      }

      "given a non-integer" in {
        val result = ResolveParsedNumericId(2000.9, path)
        result shouldBe expectedError
      }

      "given a number that's too large" in {
        val result = ResolveParsedNumericId(BigDecimal("1000000000000000"), path)
        result shouldBe expectedError
      }
    }
  }

}
