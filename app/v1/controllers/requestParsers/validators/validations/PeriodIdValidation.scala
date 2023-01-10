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

import java.time.LocalDate

import v1.models.errors.{MtdError, PeriodIdFormatError}

import scala.util.{Failure, Success, Try}

object PeriodIdValidation {

  def validate(periodId: String): List[MtdError] = {
    if (periodId.length == 21) {
      Try {
        LocalDate.parse(periodId.substring(0, 10), dateFormat)
        LocalDate.parse(periodId.substring(11, 21), dateFormat)
      } match {
        case Success(_) => Nil
        case Failure(_) => List(PeriodIdFormatError)
      }
    } else {
      List(PeriodIdFormatError)
    }
  }

}
