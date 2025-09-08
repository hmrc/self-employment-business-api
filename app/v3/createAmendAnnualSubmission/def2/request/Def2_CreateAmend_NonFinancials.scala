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

package v3.createAmendAnnualSubmission.def2.request

import api.models.domain.ex.MtdNicExemption
import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.*

case class Def2_CreateAmend_NonFinancials(businessDetailsChangedRecently: Boolean, class4NicsExemptionReason: Option[MtdNicExemption])

object Def2_CreateAmend_NonFinancials {
  implicit val reads: Reads[Def2_CreateAmend_NonFinancials] = Json.reads[Def2_CreateAmend_NonFinancials]

  implicit val writes: Writes[Def2_CreateAmend_NonFinancials] = (
    (JsPath \ "businessDetailsChangedRecently").write[Boolean] and
      (JsPath \ "exemptFromPayingClass4Nics").write[Boolean] and
      (JsPath \ "class4NicsExemptionReason").writeNullable[String]
  ) { nonFinancials =>
    val exemption: Option[MtdNicExemption] = nonFinancials.class4NicsExemptionReason
    (nonFinancials.businessDetailsChangedRecently, exemption.isDefined, exemption.map(_.toDownstream))
  }

}
