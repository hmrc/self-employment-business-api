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

package v1.models.request.createPeriodic

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Expenses(costOfGoodsBought: Option[ExpensesAmountObject],
                    cisPaymentsTo: Option[ExpensesAmountObject],
                    staffCosts: Option[ExpensesAmountObject],
                    travelCosts: Option[ExpensesAmountObject],
                    premisesRunningCosts: Option[ExpensesAmountObject],
                    maintenanceCosts: Option[ExpensesAmountObject],
                    adminCosts: Option[ExpensesAmountObject],
                    advertisingCosts: Option[ExpensesAmountObject],
                    businessEntertainmentCosts: Option[ExpensesAmountObject],
                    interestOnLoans: Option[ExpensesAmountObject],
                    financialCharges: Option[ExpensesAmountObject],
                    badDebt: Option[ExpensesAmountObject],
                    professionalFees: Option[ExpensesAmountObject],
                    depreciation: Option[ExpensesAmountObject],
                    other: Option[ExpensesAmountObject]) {

  def isEmpty: Boolean = {
    costOfGoodsBought.isEmpty &&
    cisPaymentsTo.isEmpty &&
    staffCosts.isEmpty &&
    travelCosts.isEmpty &&
    premisesRunningCosts.isEmpty &&
    maintenanceCosts.isEmpty &&
    adminCosts.isEmpty &&
    advertisingCosts.isEmpty &&
    businessEntertainmentCosts.isEmpty &&
    interestOnLoans.isEmpty &&
    financialCharges.isEmpty &&
    badDebt.isEmpty &&
    professionalFees.isEmpty &&
    depreciation.isEmpty &&
    other.isEmpty
  }

}

object Expenses {
  implicit val reads: Reads[Expenses] = Json.reads[Expenses]

  implicit val writes: OWrites[Expenses] = (
    (JsPath \ "costOfGoods").writeNullable[ExpensesAmountObject] and
      (JsPath \ "constructionIndustryScheme").writeNullable[ExpensesAmountObject] and
      (JsPath \ "staffCosts").writeNullable[ExpensesAmountObject] and
      (JsPath \ "travelCosts").writeNullable[ExpensesAmountObject] and
      (JsPath \ "premisesRunningCosts").writeNullable[ExpensesAmountObject] and
      (JsPath \ "maintenanceCosts").writeNullable[ExpensesAmountObject] and
      (JsPath \ "adminCosts").writeNullable[ExpensesAmountObject] and
      (JsPath \ "advertisingCosts").writeNullable[ExpensesAmountObject] and
      (JsPath \ "businessEntertainmentCosts").writeNullable[ExpensesAmountObject] and
      (JsPath \ "interest").writeNullable[ExpensesAmountObject] and
      (JsPath \ "financialCharges").writeNullable[ExpensesAmountObject] and
      (JsPath \ "badDebt").writeNullable[ExpensesAmountObject] and
      (JsPath \ "professionalFees").writeNullable[ExpensesAmountObject] and
      (JsPath \ "depreciation").writeNullable[ExpensesAmountObject] and
      (JsPath \ "other").writeNullable[ExpensesAmountObject]
  )(unlift(Expenses.unapply))

}
