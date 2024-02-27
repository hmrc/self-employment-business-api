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

package v3.controllers.createPeriodSummary.def2.model.request

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v3.controllers.createPeriodSummary.model.request.Create_PeriodDates

object Def2_Create_PeriodDates {
  implicit val reads: Reads[Def2_Create_PeriodDates] = Json.reads[Def2_Create_PeriodDates]

  implicit val writes: OWrites[Def2_Create_PeriodDates] = (
    (JsPath \ "from").write[String] and
      (JsPath \ "to").write[String]
  )(unlift(Def2_Create_PeriodDates.unapply))

}

case class Def2_Create_PeriodDates(periodStartDate: String, periodEndDate: String) extends Create_PeriodDates
