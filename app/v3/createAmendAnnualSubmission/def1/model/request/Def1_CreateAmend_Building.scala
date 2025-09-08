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

package v3.createAmendAnnualSubmission.def1.model.request

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, Reads, Writes}

case class Def1_CreateAmend_Building(name: Option[String], number: Option[String], postcode: String)

object Def1_CreateAmend_Building {
  implicit val reads: Reads[Def1_CreateAmend_Building] = Json.reads[Def1_CreateAmend_Building]

  implicit val writes: Writes[Def1_CreateAmend_Building] = (
    (JsPath \ "name").writeNullable[String] and
      (JsPath \ "number").writeNullable[String] and
      (JsPath \ "postCode").write[String]
  )(w => Tuple.fromProductTyped(w))

}
