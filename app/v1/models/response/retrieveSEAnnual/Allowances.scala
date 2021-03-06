/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.models.response.retrieveSEAnnual

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class Allowances(
                       annualInvestmentAllowance: Option[BigDecimal],
                       businessPremisesRenovationAllowance: Option[BigDecimal],
                       capitalAllowanceMainPool: Option[BigDecimal],
                       capitalAllowanceSpecialRatePool: Option[BigDecimal],
                       zeroEmissionGoodsVehicleAllowance: Option[BigDecimal],
                       enhancedCapitalAllowance: Option[BigDecimal],
                       allowanceOnSales: Option[BigDecimal],
                       capitalAllowanceSingleAssetPool: Option[BigDecimal],
                       tradingAllowance: Option[BigDecimal],
                       structureAndBuildingAllowance: Option[BigDecimal],
                       electricChargePointAllowance: Option[BigDecimal]
                     )

object Allowances {
  implicit val reads: Reads[Allowances] = (
    (JsPath \ "annualInvestmentAllowance").readNullable[BigDecimal] and
    (JsPath \ "businessPremisesRenovationAllowance").readNullable[BigDecimal] and
    (JsPath \ "capitalAllowanceMainPool").readNullable[BigDecimal] and
    (JsPath \ "capitalAllowanceSpecialRatePool").readNullable[BigDecimal] and
    (JsPath \ "zeroEmissionGoodsVehicleAllowance").readNullable[BigDecimal] and
    (JsPath \ "enhanceCapitalAllowance").readNullable[BigDecimal] and
    (JsPath \ "allowanceOnSales").readNullable[BigDecimal] and
    (JsPath \ "capitalAllowanceSingleAssetPool").readNullable[BigDecimal] and
    (JsPath \ "tradingIncomeAllowance").readNullable[BigDecimal] and
    (JsPath \ "structureAndBuildingAllowance").readNullable[BigDecimal] and
    (JsPath \ "electricChargePointAllowance").readNullable[BigDecimal]
  )(Allowances.apply _)
  implicit val writes: OWrites[Allowances] = Json.writes[Allowances]
}
