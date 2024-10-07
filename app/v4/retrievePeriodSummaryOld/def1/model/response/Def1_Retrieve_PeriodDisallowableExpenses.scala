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

package v4.retrievePeriodSummaryOld.def1.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Def1_Retrieve_PeriodDisallowableExpenses(
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
) {

  def isEmptyObject: Boolean =
    costOfGoodsDisallowable.isEmpty &&
      paymentsToSubcontractorsDisallowable.isEmpty &&
      wagesAndStaffCostsDisallowable.isEmpty &&
      carVanTravelExpensesDisallowable.isEmpty &&
      premisesRunningCostsDisallowable.isEmpty &&
      maintenanceCostsDisallowable.isEmpty &&
      adminCostsDisallowable.isEmpty &&
      businessEntertainmentCostsDisallowable.isEmpty &&
      advertisingCostsDisallowable.isEmpty &&
      interestOnBankOtherLoansDisallowable.isEmpty &&
      financeChargesDisallowable.isEmpty &&
      irrecoverableDebtsDisallowable.isEmpty &&
      professionalFeesDisallowable.isEmpty &&
      depreciationDisallowable.isEmpty &&
      otherExpensesDisallowable.isEmpty

}

object Def1_Retrieve_PeriodDisallowableExpenses {

  implicit val reads: Reads[Def1_Retrieve_PeriodDisallowableExpenses] = (
    (JsPath \ "deductions" \ "costOfGoods" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "constructionIndustryScheme" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "staffCosts" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "travelCosts" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "premisesRunningCosts" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "maintenanceCosts" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "adminCosts" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "businessEntertainmentCosts" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "advertisingCosts" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "interest" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "financialCharges" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "badDebt" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "professionalFees" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "depreciation" \ "disallowableAmount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "other" \ "disallowableAmount").readNullable[BigDecimal]
  )(Def1_Retrieve_PeriodDisallowableExpenses.apply _)

  implicit val writes: OWrites[Def1_Retrieve_PeriodDisallowableExpenses] = Json.writes
}
