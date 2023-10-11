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

package api.controllers.requestParsers.validators.validations

import api.models.errors.{MtdError, PeriodIdFormatError}

import java.time.LocalDate
import scala.util.{Failure, Success, Try}
import DateValidation.isWithinAllowedRange

object PeriodIdValidation {

  private val errs = List(PeriodIdFormatError)

  def validate(periodId: String): List[MtdError] = {
    if (periodId.length == 21) {
      Try {
        (LocalDate.parse(periodId.substring(0, 10), dateFormat), LocalDate.parse(periodId.substring(11, 21), dateFormat))
      } match {
        case Success((date1, date2)) => if (isWithinAllowedRange(date1) && isWithinAllowedRange(date2)) Nil else errs
        case Failure(_)              => errs
      }
    } else {
      errs
    }
  }

}
