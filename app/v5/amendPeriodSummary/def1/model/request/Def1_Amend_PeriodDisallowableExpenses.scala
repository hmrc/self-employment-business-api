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

package v5.amendPeriodSummary.def1.model.request

import play.api.libs.functional.syntax.*
import play.api.libs.json.*

case class Def1_Amend_PeriodDisallowableExpenses(
    costOfGoodsDisallowable: Option[BigDecimal],
    paymentsToSubcontractorsDisallowable: Option[BigDecimal],
    wagesAndStaffCostsDisallowable: Option[BigDecimal],
    carVanTravelExpensesDisallowable: Option[BigDecimal],
    premisesRunningCostsDisallowable: Option[BigDecimal],
    maintenanceCostsDisallowable: Option[BigDecimal],
    adminCostsDisallowable: Option[BigDecimal],
    businessEntertainmentCostsDisallowable: Option[BigDecimal],
    advertisingCostsDisallowable: Option[BigDecimal],
    interestOnBankOtherLoansDisallowable: Option[BigDecimal],
    financeChargesDisallowable: Option[BigDecimal],
    irrecoverableDebtsDisallowable: Option[BigDecimal],
    professionalFeesDisallowable: Option[BigDecimal],
    depreciationDisallowable: Option[BigDecimal],
    otherExpensesDisallowable: Option[BigDecimal]
)

object Def1_Amend_PeriodDisallowableExpenses {

  implicit val reads: Reads[Def1_Amend_PeriodDisallowableExpenses] = Json.reads[Def1_Amend_PeriodDisallowableExpenses]

  implicit val writes: OWrites[Def1_Amend_PeriodDisallowableExpenses] = (
    (JsPath \ "costOfGoods" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "constructionIndustryScheme" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "staffCosts" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "travelCosts" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "premisesRunningCosts" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "maintenanceCosts" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "adminCosts" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "businessEntertainmentCosts" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "advertisingCosts" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "interest" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "financialCharges" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "badDebt" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "professionalFees" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "depreciation" \ "disallowableAmount").writeNullable[BigDecimal] and
      (JsPath \ "other" \ "disallowableAmount").writeNullable[BigDecimal]
  )(w => Tuple.fromProductTyped(w))

}
