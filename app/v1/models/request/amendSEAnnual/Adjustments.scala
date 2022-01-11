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

package v1.models.request.amendSEAnnual

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Adjustments(includedNonTaxableProfits: Option[BigDecimal],
                       basisAdjustment: Option[BigDecimal],
                       overlapReliefUsed: Option[BigDecimal],
                       accountingAdjustment: Option[BigDecimal],
                       averagingAdjustment: Option[BigDecimal],
                       lossBroughtForward: Option[BigDecimal],
                       outstandingBusinessIncome: Option[BigDecimal],
                       balancingChargeBPRA: Option[BigDecimal],
                       balancingChargeOther: Option[BigDecimal],
                       goodsAndServicesOwnUse: Option[BigDecimal]) {
  def isEmpty: Boolean = includedNonTaxableProfits.isEmpty &&
      basisAdjustment.isEmpty &&
      overlapReliefUsed.isEmpty &&
      accountingAdjustment.isEmpty &&
      averagingAdjustment.isEmpty &&
      lossBroughtForward.isEmpty &&
      outstandingBusinessIncome.isEmpty &&
      balancingChargeBPRA.isEmpty &&
      balancingChargeOther.isEmpty &&
      goodsAndServicesOwnUse.isEmpty
}

object Adjustments {
  implicit val reads: Reads[Adjustments] = Json.reads[Adjustments]
  implicit val writes: OWrites[Adjustments] = (
    (JsPath \ "includedNonTaxableProfits").writeNullable[BigDecimal] and
      (JsPath \ "basisAdjustment").writeNullable[BigDecimal] and
      (JsPath \ "overlapReliefUsed").writeNullable[BigDecimal] and
      (JsPath \ "accountingAdjustment").writeNullable[BigDecimal] and
      (JsPath \ "averagingAdjustment").writeNullable[BigDecimal] and
      (JsPath \ "lossBroughtForward").writeNullable[BigDecimal] and
      (JsPath \ "outstandingBusinessIncome").writeNullable[BigDecimal] and
      (JsPath \ "balancingChargeBpra").writeNullable[BigDecimal] and
      (JsPath \ "balancingChargeOther").writeNullable[BigDecimal] and
      (JsPath \ "goodsAndServicesOwnUse").writeNullable[BigDecimal]
    ) (unlift(Adjustments.unapply))
}
