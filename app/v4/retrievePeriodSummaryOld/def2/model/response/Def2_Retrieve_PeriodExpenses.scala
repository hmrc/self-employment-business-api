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

package v4.retrievePeriodSummaryOld.def2.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Def2_Retrieve_PeriodExpenses(
    consolidatedExpenses: Option[BigDecimal],
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
    otherExpenses: Option[BigDecimal]
) {

  def isEmptyObject: Boolean =
    consolidatedExpenses.isEmpty &&
      costOfGoods.isEmpty &&
      paymentsToSubcontractors.isEmpty &&
      wagesAndStaffCosts.isEmpty &&
      carVanTravelExpenses.isEmpty &&
      premisesRunningCosts.isEmpty &&
      maintenanceCosts.isEmpty &&
      adminCosts.isEmpty &&
      businessEntertainmentCosts.isEmpty &&
      advertisingCosts.isEmpty &&
      interestOnBankOtherLoans.isEmpty &&
      financeCharges.isEmpty &&
      irrecoverableDebts.isEmpty &&
      professionalFees.isEmpty &&
      depreciation.isEmpty &&
      otherExpenses.isEmpty

}

object Def2_Retrieve_PeriodExpenses {

  implicit val reads: Reads[Def2_Retrieve_PeriodExpenses] = (
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
  )(Def2_Retrieve_PeriodExpenses.apply _)

  implicit val writes: OWrites[Def2_Retrieve_PeriodExpenses] = Json.writes
}
