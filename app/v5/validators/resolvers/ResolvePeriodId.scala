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

package v5.validators.resolvers

import api.models.domain.PeriodId
import api.models.errors.PeriodIdFormatError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import shared.controllers.validators.resolvers.ResolveDateRange
import shared.models.domain.DateRange
import shared.models.errors.MtdError

object ResolvePeriodId {

  private val periodIdRegex = "(.{10})_(.{10})".r

  private val resolveDateRange: ResolveDateRange = ResolveDateRange()

  def apply(periodId: String): Validated[Seq[MtdError], PeriodId] =
    periodId match {
      case periodIdRegex(fromDate, toDate) => mapResult(resolveDateRange(fromDate -> toDate))
      case _                               => Invalid(List(PeriodIdFormatError))
    }

  def apply(periodId: String, minYear: Int, maxYear: Int): Validated[Seq[MtdError], PeriodId] =
    periodId match {
      case periodIdRegex(fromDate, toDate) => mapResult(resolveDateRange.withYearsLimitedTo(minYear, maxYear)(fromDate -> toDate))
      case _                               => Invalid(List(PeriodIdFormatError))
    }

  private def mapResult(result: Validated[Seq[MtdError], DateRange]): Validated[Seq[MtdError], PeriodId] =
    result match {
      case Valid(dateRange) => Valid(PeriodId(dateRange.startDate.toString, dateRange.endDate.toString))
      case _                => Invalid(List(PeriodIdFormatError))
    }

}
