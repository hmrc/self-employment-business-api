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

import api.models.errors.{DateFormatError, MtdError, RuleEndDateBeforeStartDateError}

import java.time.LocalDate
import scala.util.{Failure, Success, Try}

object DateValidation {

  def validate(field: String, error: MtdError): List[MtdError] = {
    Try {
      LocalDate.parse(field, dateFormat)
    } match {
      case Success(_) => Nil
      case Failure(_) => List(error)
    }
  }

  def validateOptional(field: Option[String], path: String): List[MtdError] = {
    field match {
      case None        => NoValidationErrors
      case Some(value) => validateWithPaths(value, path)
    }
  }

  def validateWithPaths(field: String, path: String): List[MtdError] = {
    Try {
      LocalDate.parse(field, dateFormat)
    } match {
      case Success(_) => Nil
      case Failure(_) => List(DateFormatError.copy(paths = Some(Seq(path))))
    }
  }

  def validateEndDateBeforeStartDate(startDate: String, endDate: String): List[MtdError] = {
    val convertedFromDate = LocalDate.parse(startDate, dateFormat)
    val convertedToDate   = LocalDate.parse(endDate, dateFormat)

    if (convertedToDate.isBefore(convertedFromDate)) List(RuleEndDateBeforeStartDateError) else Nil
  }

}