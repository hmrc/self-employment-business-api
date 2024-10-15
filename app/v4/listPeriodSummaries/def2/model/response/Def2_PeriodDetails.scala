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

package v4.listPeriodSummaries.def2.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v4.listPeriodSummaries.model.response.PeriodDetails

case class Def2_PeriodDetails(
    periodId: String,
    periodStartDate: String,
    periodEndDate: String
//    periodCreationDate: Option[String] // To be reinstated, see MTDSA-15595
) extends PeriodDetails

object Def2_PeriodDetails {

  implicit val writes: OWrites[Def2_PeriodDetails] = Json.writes[Def2_PeriodDetails]

  implicit val reads: Reads[Def2_PeriodDetails] = (
    (JsPath \ "from").read[String] and
      (JsPath \ "to").read[String]
  )((from, to) =>
    Def2_PeriodDetails(
      periodId = s"${from}_$to",
      periodStartDate = from,
      periodEndDate = to
    ))

}
