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

package v4.createAmendAnnualSubmission.def3.request

import config.SeBusinessFeatureSwitches
import play.api.libs.json.{JsObject, Json, Reads, Writes}

case class Def3_CreateAmend_Adjustments(
    includedNonTaxableProfits: Option[BigDecimal],
    basisAdjustment: Option[BigDecimal],
    overlapReliefUsed: Option[BigDecimal],
    accountingAdjustment: Option[BigDecimal],
    averagingAdjustment: Option[BigDecimal],
    outstandingBusinessIncome: Option[BigDecimal],
    balancingChargeBpra: Option[BigDecimal],
    balancingChargeOther: Option[BigDecimal],
    goodsAndServicesOwnUse: Option[BigDecimal],
    transitionProfitAmount: Option[BigDecimal],
    transitionProfitAccelerationAmount: Option[BigDecimal]
)

object Def3_CreateAmend_Adjustments {

  private val standardReads: Reads[Def3_CreateAmend_Adjustments] = Json.reads

  implicit def reads(implicit featureSwitches: SeBusinessFeatureSwitches): Reads[Def3_CreateAmend_Adjustments] =
    implicitly[Reads[JsObject]]
      .map { json =>
        if (featureSwitches.isAdjustmentsAdditionalFieldsEnabled)
          json
        else
          json - "transitionProfitAmount" - "transitionProfitAccelerationAmount"
      }
      .andThen(standardReads)

  implicit val writes: Writes[Def3_CreateAmend_Adjustments] = Json.writes
}
