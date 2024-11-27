/*
 * Copyright 2024 HM Revenue & Customs
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

package v4.createAmendCumulativePeriodSummary.def1.model.request

import play.api.libs.json.{Json, OWrites, Reads}
import v4.createAmendCumulativePeriodSummary.model.request.Create_PeriodDates

object PeriodDates {
  implicit val reads: Reads[PeriodDates] = Json.reads[PeriodDates]

  implicit val writes: OWrites[PeriodDates] = Json.writes[PeriodDates]
}

case class PeriodDates(periodStartDate: Option[String], periodEndDate: Option[String]) extends Create_PeriodDates
