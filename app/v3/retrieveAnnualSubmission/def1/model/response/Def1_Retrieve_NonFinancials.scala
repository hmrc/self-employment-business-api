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

package v3.retrieveAnnualSubmission.def1.model.response

import api.models.domain.ex.{DownstreamNicExemption, MtdNicExemption}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Def1_Retrieve_NonFinancials(businessDetailsChangedRecently: Boolean, class4NicsExemptionReason: Option[MtdNicExemption])

object Def1_Retrieve_NonFinancials {
  implicit val writes: OWrites[Def1_Retrieve_NonFinancials] = Json.writes[Def1_Retrieve_NonFinancials]

  implicit val reads: Reads[Def1_Retrieve_NonFinancials] = (
    (JsPath \ "businessDetailsChangedRecently").read[Boolean] and
      (JsPath \ "class4NicsExemptionReason").readNullable[DownstreamNicExemption].map(_.map(_.toMtd))
  )(Def1_Retrieve_NonFinancials.apply _)

}