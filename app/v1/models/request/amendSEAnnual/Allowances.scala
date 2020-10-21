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

package v1.models.request.amendSEAnnual

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Allowances(annualInvestmentAllowance: Option[BigDecimal],
                      businessPremisesRenovationAllowance: Option[BigDecimal],
                      capitalAllowanceMainPool: Option[BigDecimal],
                      capitalAllowanceSpecialRatePool: Option[BigDecimal],
                      zeroEmissionGoodsVehicleAllowance: Option[BigDecimal],
                      enhancedCapitalAllowance: Option[BigDecimal],
                      allowanceOnSales: Option[BigDecimal],
                      capitalAllowanceSingleAssetPool: Option[BigDecimal],
                      tradingAllowance: Option[BigDecimal]) {
  def isEmpty: Boolean = annualInvestmentAllowance.isEmpty &&
    businessPremisesRenovationAllowance.isEmpty &&
    capitalAllowanceMainPool.isEmpty &&
    capitalAllowanceSpecialRatePool.isEmpty &&
    zeroEmissionGoodsVehicleAllowance.isEmpty &&
    enhancedCapitalAllowance.isEmpty &&
    allowanceOnSales.isEmpty &&
    capitalAllowanceSingleAssetPool.isEmpty &&
    tradingAllowance.isEmpty

}

object Allowances {
  implicit val reads: Reads[Allowances] = Json.reads[Allowances]
  implicit val writes: OWrites[Allowances] = (
    (JsPath \ "annualInvestmentAllowance").writeNullable[BigDecimal] and
      (JsPath \ "businessPremisesRenovationAllowance").writeNullable[BigDecimal] and
      (JsPath \ "capitalAllowanceMainPool").writeNullable[BigDecimal] and
      (JsPath \ "capitalAllowanceSpecialRatePool").writeNullable[BigDecimal] and
      (JsPath \ "zeroEmissionGoodsVehicleAllowance").writeNullable[BigDecimal] and
      (JsPath \ "enhancedCapitalAllowance").writeNullable[BigDecimal] and
      (JsPath \ "allowanceOnSales").writeNullable[BigDecimal] and
      (JsPath \ "capitalAllowanceSingleAssetPool").writeNullable[BigDecimal] and
      (JsPath \ "tradingIncomeAllowance").writeNullable[BigDecimal]
    ) (unlift(Allowances.unapply))
}
