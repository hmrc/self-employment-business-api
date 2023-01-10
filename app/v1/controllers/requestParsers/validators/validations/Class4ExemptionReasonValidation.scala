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

package v1.controllers.requestParsers.validators.validations

import v1.models.domain.ex.MtdNicExemption
import v1.models.errors.{Class4ExemptionReasonFormatError, MtdError}

import scala.util.{Failure, Success, Try}

object Class4ExemptionReasonValidation {

  def validateOptional(field: Option[String]): List[MtdError] = {
    field match {
      case None        => NoValidationErrors
      case Some(value) => validate(value)
    }
  }

  def validate(field: String): List[MtdError] =
    Try {
      MtdNicExemption.parser(field)
    } match {
      case Failure(_) => List(Class4ExemptionReasonFormatError)
      case Success(_) => NoValidationErrors
    }

}
