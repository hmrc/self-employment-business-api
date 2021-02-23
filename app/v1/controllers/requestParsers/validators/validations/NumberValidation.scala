/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators.validations

import v1.models.errors.{MtdError, ValueFormatError}

object NumberValidation {

  def validateOptional(field: Option[BigDecimal], path: String): List[MtdError] = {
    field match {
      case None => NoValidationErrors
      case Some(value) => validate(value, path)
    }
  }


  def validate(field: BigDecimal, path: String): List[MtdError] = {
    if (field >= 0 && field < 100000000000.00 && field.scale <= 2) {
      Nil
    } else {
      List(
        ValueFormatError.copy(paths = Some(Seq(path)))
      )
    }
  }

  def validateOptionalIncludeNegatives(field: Option[BigDecimal], path: String): List[MtdError] = {
    field match {
      case None => NoValidationErrors
      case Some(value) => validateIncludeNegatives(value, path)
    }
  }


  def validateIncludeNegatives(field: BigDecimal, path: String): List[MtdError] = {
    if (field > -100000000000.00 && field < 100000000000.00 && field.scale <= 2) {
      Nil
    } else {
      List(
        ValueFormatError.copy(paths = Some(Seq(path)))
      )
    }
  }
}
