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

package v4.amendPeriodSummary.def1.model.request

import play.api.libs.functional.syntax._
import play.api.libs.json._

case class Def1_Amend_PeriodExpenses(consolidatedExpenses: Option[BigDecimal],
                                     costOfGoods: Option[BigDecimal],
                                     paymentsToSubcontractors: Option[BigDecimal],
                                     wagesAndStaffCosts: Option[BigDecimal],
                                     carVanTravelExpenses: Option[BigDecimal],
                                     premisesRunningCosts: Option[BigDecimal],
                                     maintenanceCosts: Option[BigDecimal],
                                     adminCosts: Option[BigDecimal],
                                     businessEntertainmentCosts: Option[BigDecimal],
                                     advertisingCosts: Option[BigDecimal],
                                     interestOnBankOtherLoans: Option[BigDecimal],
                                     financeCharges: Option[BigDecimal],
                                     irrecoverableDebts: Option[BigDecimal],
                                     professionalFees: Option[BigDecimal],
                                     depreciation: Option[BigDecimal],
                                     otherExpenses: Option[BigDecimal])

object Def1_Amend_PeriodExpenses {

  implicit val reads: Reads[Def1_Amend_PeriodExpenses] = Json.reads[Def1_Amend_PeriodExpenses]

  implicit val writes: OWrites[Def1_Amend_PeriodExpenses] = (
    (JsPath \ "simplifiedExpenses").writeNullable[BigDecimal] and
      (JsPath \ "costOfGoods" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "constructionIndustryScheme" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "staffCosts" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "travelCosts" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "premisesRunningCosts" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "maintenanceCosts" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "adminCosts" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "businessEntertainmentCosts" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "advertisingCosts" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "interest" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "financialCharges" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "badDebt" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "professionalFees" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "depreciation" \ "amount").writeNullable[BigDecimal] and
      (JsPath \ "other" \ "amount").writeNullable[BigDecimal]
  )(unlift(Def1_Amend_PeriodExpenses.unapply))

}