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

package v1.models.response.retrievePeriodic

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
                    other: Option[ExpensesAmountObject])

object Expenses {
  implicit val reads: Reads[Expenses] = (
    (JsPath \ "costOfGoods").readNullable[ExpensesAmountObject] and
      (JsPath \ "constructionIndustryScheme").readNullable[ExpensesAmountObject] and
      (JsPath \ "staffCosts").readNullable[ExpensesAmountObject] and
      (JsPath \ "travelCosts").readNullable[ExpensesAmountObject] and
      (JsPath \ "premisesRunningCosts").readNullable[ExpensesAmountObject] and
      (JsPath \ "maintenanceCosts").readNullable[ExpensesAmountObject] and
      (JsPath \ "adminCosts").readNullable[ExpensesAmountObject] and
      (JsPath \ "advertisingCosts").readNullable[ExpensesAmountObject] and
      (JsPath \ "businessEntertainmentCosts").readNullable[ExpensesAmountObject] and
      (JsPath \ "interest").readNullable[ExpensesAmountObject] and
      (JsPath \ "financialCharges").readNullable[ExpensesAmountObject] and
      (JsPath \ "badDebt").readNullable[ExpensesAmountObject] and
      (JsPath \ "professionalFees").readNullable[ExpensesAmountObject] and
      (JsPath \ "depreciation").readNullable[ExpensesAmountObject] and
      (JsPath \ "other").readNullable[ExpensesAmountObject]
    ) (Expenses.apply _)

  implicit val writes: OWrites[Expenses] = Json.writes[Expenses]

}