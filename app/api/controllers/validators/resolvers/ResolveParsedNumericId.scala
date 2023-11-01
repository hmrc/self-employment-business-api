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

import api.models.errors.{IdFormatError, MtdError}
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}

object ResolveParsedNumericId extends Resolver[BigDecimal, BigDecimal] {

  def apply(value: BigDecimal, maybeError: Option[MtdError], maybePath: Option[String]): Validated[Seq[MtdError], BigDecimal] = {
    if (value > 0 && value < 1000000000000000.00 && value.scale <= 0)
      Valid(value)
    else
      Invalid(List(withError(maybeError, IdFormatError, maybePath)))
  }

}
