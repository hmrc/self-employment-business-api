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

package v1.models.response.listPeriodic

import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import play.api.libs.functional.syntax._

case class PeriodDetails(periodId: String, from: String, to: String)

object PeriodDetails {
    implicit val reads: Reads[PeriodDetails] = (
        (JsPath \ "from").read[String] and
        (JsPath \ "to").read[String]
    ) ((from, to) => PeriodDetails(periodId = s"${from}_$to", from = from, to = to))

  implicit val writes: OWrites[PeriodDetails] = Json.writes[PeriodDetails]
}