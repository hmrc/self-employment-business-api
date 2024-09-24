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

package v4.createAmendAnnualSubmission.def1.model.request

import api.models.domain.ex.{DownstreamNicExemption, MtdNicExemption}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}

case class Def1_CreateAmend_NonFinancials(businessDetailsChangedRecently: Boolean, class4NicsExemptionReason: Option[MtdNicExemption])

object Def1_CreateAmend_NonFinancials {
  implicit val reads: Reads[Def1_CreateAmend_NonFinancials] = Json.reads[Def1_CreateAmend_NonFinancials]

  implicit val writes: Writes[Def1_CreateAmend_NonFinancials] = (
    (JsPath \ "businessDetailsChangedRecently").write[Boolean] and
      (JsPath \ "exemptFromPayingClass4Nics").write[Boolean] and
      (JsPath \ "class4NicsExemptionReason").writeNullable[DownstreamNicExemption]
  )(unlift(Def1_CreateAmend_NonFinancials.unapply(_: Def1_CreateAmend_NonFinancials).map { case (changed, exemption) =>
    (changed, exemption.isDefined, exemption.map(_.toDownstream))
  }))

}
