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

package v1.models.request.createSEPeriodic

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Expenses(costOfGoodsBought: Option[AmountObject],
                    cisPaymentsTo: Option[AmountObject],
                    staffCosts: Option[AmountObject],
                    travelCosts: Option[AmountObject],
                    premisesRunningCosts: Option[AmountObject],
                    maintenanceCosts: Option[AmountObject],
                    adminCosts: Option[AmountObject],
                    advertisingCosts: Option[AmountObject],
                    businessEntertainmentCosts: Option[AmountObject],
                    interestOnLoans: Option[AmountObject],
                    financialCharges: Option[AmountObject],
                    badDebt: Option[AmountObject],
                    professionalFees: Option[AmountObject],
                    depreciation: Option[AmountObject],
                    other: Option[AmountObject])

object Expenses {
  implicit val reads: Reads[Expenses] = Json.reads[Expenses]
  implicit val writes: OWrites[Expenses] = (
    (JsPath \ "costOfGoods").writeNullable[AmountObject] and
      (JsPath \ "constructionIndustryScheme").writeNullable[AmountObject] and
      (JsPath \ "staffCosts").writeNullable[AmountObject] and
      (JsPath \ "travelCosts").writeNullable[AmountObject] and
      (JsPath \ "premisesRunningCosts").writeNullable[AmountObject] and
      (JsPath \ "maintenanceCosts").writeNullable[AmountObject] and
      (JsPath \ "adminCosts").writeNullable[AmountObject] and
      (JsPath \ "advertisingCosts").writeNullable[AmountObject] and
      (JsPath \ "businessEntertainmentCosts").writeNullable[AmountObject] and
      (JsPath \ "interest").writeNullable[AmountObject] and
      (JsPath \ "financialCharges").writeNullable[AmountObject] and
      (JsPath \ "badDebt").writeNullable[AmountObject] and
      (JsPath \ "professionalFees").writeNullable[AmountObject] and
      (JsPath \ "deprecation").writeNullable[AmountObject] and
      (JsPath \ "other").writeNullable[AmountObject]
    ) (unlift(Expenses.unapply))
}