/*
 * Copyright 2022 HM Revenue & Customs
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

package v1.models.request.createPeriodic

import play.api.libs.json.{Json, OWrites, Reads}

case class PeriodDates(periodStartDate: String,
                       periodEndDate: String)

object PeriodDates {
  implicit val reads: Reads[PeriodDates] = Json.reads[PeriodDates]
  implicit val writes: OWrites[PeriodDates] = Json.writes[PeriodDates]
}

//to flatten from a periodDates object to just from and to fields.
case class OuterObjectPeriodDates(periodDates: PeriodDates)

object OuterObjectPeriodDates {

  implicit val readsOuter: Reads[OuterObjectPeriodDates] = Json.reads[OuterObjectPeriodDates]

  implicit val writesOuter: OWrites[OuterObjectPeriodDates] = (o: OuterObjectPeriodDates) => Json.obj(
    "from" -> o.periodDates.periodStartDate,
    "to" -> o.periodDates.periodEndDate
  )
}