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

package v1.models.request.amendSEAnnual

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, Reads, Writes}
import v1.models.domain.ex.{DownstreamNicExemption, MtdNicExemption}

case class NonFinancials(businessDetailsChangedRecently: Boolean, class4NicsExemptionReason: Option[MtdNicExemption])

object NonFinancials {
  implicit val reads: Reads[NonFinancials] = Json.reads[NonFinancials]

  implicit val writes: Writes[NonFinancials] = (
    (JsPath \ "businessDetailsChangedRecently").write[Boolean] and
      (JsPath \ "exemptFromPayingClass4Nics").write[Boolean] and
      (JsPath \ "class4NicsExemptionReason").writeNullable[DownstreamNicExemption]
  )(unlift(NonFinancials.unapply(_: NonFinancials).map { case (changed, exemption) =>
    (changed, exemption.isDefined, exemption.map(_.toDownstream))
  }))

}
