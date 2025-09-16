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

package v5.retrieveAnnualSubmission.def3.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class RetrieveAllowances(
    annualInvestmentAllowance: Option[BigDecimal],
    businessPremisesRenovationAllowance: Option[BigDecimal],
    capitalAllowanceMainPool: Option[BigDecimal],
    capitalAllowanceSpecialRatePool: Option[BigDecimal],
    enhancedCapitalAllowance: Option[BigDecimal],
    allowanceOnSales: Option[BigDecimal],
    capitalAllowanceSingleAssetPool: Option[BigDecimal],
    tradingIncomeAllowance: Option[BigDecimal],
    zeroEmissionsCarAllowance: Option[BigDecimal],
    structuredBuildingAllowance: Option[Seq[RetrieveStructuredBuildingAllowance]],
    enhancedStructuredBuildingAllowance: Option[Seq[RetrieveStructuredBuildingAllowance]]
)

object RetrieveAllowances {

  implicit val reads: Reads[RetrieveAllowances] = (
    (JsPath \ "annualInvestmentAllowance").readNullable[BigDecimal] and
      (JsPath \ "businessPremisesRenovationAllowance").readNullable[BigDecimal] and
      (JsPath \ "capitalAllowanceMainPool").readNullable[BigDecimal] and
      (JsPath \ "capitalAllowanceSpecialRatePool").readNullable[BigDecimal] and
      (JsPath \ "enhanceCapitalAllowance").readNullable[BigDecimal] and
      (JsPath \ "allowanceOnSales").readNullable[BigDecimal] and
      (JsPath \ "capitalAllowanceSingleAssetPool").readNullable[BigDecimal] and
      (JsPath \ "tradingIncomeAllowance").readNullable[BigDecimal] and
      (JsPath \ "zeroEmissionsCarAllowance").readNullable[BigDecimal] and
      (JsPath \ "structuredBuildingAllowance").readNullable[Seq[RetrieveStructuredBuildingAllowance]] and
      (JsPath \ "enhancedStructuredBuildingAllowance").readNullable[Seq[RetrieveStructuredBuildingAllowance]]
  )(RetrieveAllowances.apply)

  implicit val writes: OWrites[RetrieveAllowances] = Json.writes[RetrieveAllowances]

}
