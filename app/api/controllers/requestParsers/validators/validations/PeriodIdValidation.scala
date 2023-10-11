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

  private val regex = "(.{10})_(.{10})".r

  def validate(periodId: String): List[MtdError] =
    periodId match {
      case regex(part1, part2) =>
        Try((parseDate(part1), parseDate(part2))) match {
          case Success((date1, date2)) => if (isWithinAllowedRange(date1) && isWithinAllowedRange(date2)) Nil else errs
          case Failure(_)              => errs
        }

      case _ => errs
    }

  private def parseDate(start: String) = LocalDate.parse(start, dateFormat)

}
