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

package v1.models.response.retrieveSEAnnual

import play.api.libs.functional.syntax.toFunctionalBuilderOps
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Adjustments(includedNonTaxableProfits: Option[BigDecimal],
                        basisAdjustment: Option[BigDecimal],
                        overlapReliefUsed: Option[BigDecimal],
                        accountingAdjustment: Option[BigDecimal],
                        averagingAdjustment: Option[BigDecimal],
                        outstandingBusinessIncome: Option[BigDecimal],
                        balancingChargeBPRA: Option[BigDecimal],
                        balancingChargeOther: Option[BigDecimal],
                        goodsAndServicesOwnUse: Option[BigDecimal]
                      )

object Adjustments {
  implicit val reads: Reads[Adjustments] = (
    (JsPath \ "includedNonTaxableProfits").readNullable[BigDecimal] and
      (JsPath \ "basisAdjustment").readNullable[BigDecimal] and
      (JsPath \ "overlapReliefUsed").readNullable[BigDecimal] and
      (JsPath \ "accountingAdjustment").readNullable[BigDecimal] and
      (JsPath \ "averagingAdjustment").readNullable[BigDecimal] and
      (JsPath \ "outstandingBusinessIncome").readNullable[BigDecimal] and
      (JsPath \ "balancingChargeBpra").readNullable[BigDecimal] and
      (JsPath \ "balancingChargeOther").readNullable[BigDecimal] and
      (JsPath \ "goodsAndServicesOwnUse").readNullable[BigDecimal]
    )(Adjustments.apply _)
  implicit val writes: OWrites[Adjustments] = Json.writes[Adjustments]
}
