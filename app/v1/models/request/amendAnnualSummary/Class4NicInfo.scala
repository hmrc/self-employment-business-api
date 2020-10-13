/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.request.amendAnnualSummary

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v1.models.domain.exemptionCode.{DesExemptionCode, MtdExemptionCode}

case class Class4NicInfo(isExempt: Boolean, exemptionCode: Option[MtdExemptionCode])

object Class4NicInfo {

  implicit val reads: Reads[Class4NicInfo] = Json.reads[Class4NicInfo]
  implicit val writes: OWrites[Class4NicInfo] = (
    (JsPath \ "exemptFromPayingClass4Nics").write[Boolean] and
      (JsPath \ "class4NicsExemptionReason").writeNullable[DesExemptionCode]
    ) (unlift(Class4NicInfo.unapply(_: Class4NicInfo).map {
    case (bool, exemptionCode) => (bool, exemptionCode.map(_.toDes))
  }))
}