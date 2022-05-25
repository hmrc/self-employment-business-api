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

case class PeriodAllowableExpenses(
    consolidatedExpenses: Option[BigDecimal],
    costOfGoodsAllowable: Option[BigDecimal],
    paymentsToSubcontractorsAllowable: Option[BigDecimal],
    wagesAndStaffCostsAllowable: Option[BigDecimal],
    carVanTravelExpensesAllowable: Option[BigDecimal],
    premisesRunningCostsAllowable: Option[BigDecimal],
    maintenanceCostsAllowable: Option[BigDecimal],
    adminCostsAllowable: Option[BigDecimal],
    businessEntertainmentCostsAllowable: Option[BigDecimal],
    advertisingCostsAllowable: Option[BigDecimal],
    interestOnBankOtherLoansAllowable: Option[BigDecimal],
    financeChargesAllowable: Option[BigDecimal],
    irrecoverableDebtsAllowable: Option[BigDecimal],
    professionalFeesAllowable: Option[BigDecimal],
    depreciationAllowable: Option[BigDecimal],
    otherExpensesAllowable: Option[BigDecimal]
) {

  def isEmptyObject: Boolean =
    consolidatedExpenses.isEmpty &&
      costOfGoodsAllowable.isEmpty &&
      paymentsToSubcontractorsAllowable.isEmpty &&
      wagesAndStaffCostsAllowable.isEmpty &&
      carVanTravelExpensesAllowable.isEmpty &&
      premisesRunningCostsAllowable.isEmpty &&
      maintenanceCostsAllowable.isEmpty &&
      adminCostsAllowable.isEmpty &&
      businessEntertainmentCostsAllowable.isEmpty &&
      advertisingCostsAllowable.isEmpty &&
      interestOnBankOtherLoansAllowable.isEmpty &&
      financeChargesAllowable.isEmpty &&
      irrecoverableDebtsAllowable.isEmpty &&
      professionalFeesAllowable.isEmpty &&
      depreciationAllowable.isEmpty &&
      otherExpensesAllowable.isEmpty

}

object PeriodAllowableExpenses {

  implicit val reads: Reads[PeriodAllowableExpenses] = (
    (JsPath \ "deductions" \ "simplifiedExpenses").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "costOfGoods" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "constructionIndustryScheme" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "staffCosts" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "travelCosts" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "premisesRunningCosts" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "maintenanceCosts" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "adminCosts" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "businessEntertainmentCosts" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "advertisingCosts" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "interest" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "financialCharges" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "badDebt" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "professionalFees" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "depreciation" \ "amount").readNullable[BigDecimal] and
      (JsPath \ "deductions" \ "other" \ "amount").readNullable[BigDecimal]
  )(PeriodAllowableExpenses.apply _)

  implicit val writes: OWrites[PeriodAllowableExpenses] = Json.writes
}
