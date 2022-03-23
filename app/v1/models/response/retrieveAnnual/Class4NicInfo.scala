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

package v1.models.response.retrieveAnnual

import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v1.models.domain.ex.{DownstreamNicExemption, MtdNicExemption}

case class Class4NicInfo(exemptionCode: Option[MtdNicExemption])

object Class4NicInfo {
  implicit val reads: Reads[Class4NicInfo] =
      (JsPath \ "class4NicsExemptionReason").readNullable[DownstreamNicExemption].map(_.map(_.toMtd)).map {
        exemptionReason => Class4NicInfo(exemptionReason)
      }
  implicit val writes: OWrites[Class4NicInfo] = Json.writes[Class4NicInfo]
}
