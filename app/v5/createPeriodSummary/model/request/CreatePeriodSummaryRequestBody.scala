/*
 * Copyright 2024 HM Revenue & Customs
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

package v5.createPeriodSummary.model.request

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v5.createPeriodSummary.def1.model.request.{Def1_Create_PeriodDates, Def1_Create_PeriodDisallowableExpenses, Def1_Create_PeriodExpenses, Def1_Create_PeriodIncome}
import v5.createPeriodSummary.def2.model.request.{Def2_Create_PeriodDates, Def2_Create_PeriodDisallowableExpenses, Def2_Create_PeriodExpenses, Def2_Create_PeriodIncome}

sealed trait CreatePeriodSummaryRequestBody {
  val periodDates: Create_PeriodDates
  val periodIncome: Option[Create_PeriodIncome]
  val periodExpenses: Option[Create_PeriodExpenses]
  val periodDisallowableExpenses: Option[Create_PeriodDisallowableExpenses]
}

trait Create_PeriodDates {
  val periodStartDate: String
  val periodEndDate: String
}

trait Create_PeriodExpenses

trait Create_PeriodIncome {
  val turnover: Option[BigDecimal]
  val other: Option[BigDecimal]
}

trait Create_PeriodDisallowableExpenses

case class Def1_CreatePeriodSummaryRequestBody(periodDates: Def1_Create_PeriodDates,
                                               periodIncome: Option[Def1_Create_PeriodIncome],
                                               periodExpenses: Option[Def1_Create_PeriodExpenses],
                                               periodDisallowableExpenses: Option[Def1_Create_PeriodDisallowableExpenses])
    extends CreatePeriodSummaryRequestBody

object Def1_CreatePeriodSummaryRequestBody {
  implicit val reads: Reads[Def1_CreatePeriodSummaryRequestBody] = Json.reads[Def1_CreatePeriodSummaryRequestBody]

  implicit val writes: OWrites[Def1_CreatePeriodSummaryRequestBody] = (
    JsPath.write[Def1_Create_PeriodDates] and
      (JsPath \ "financials" \ "incomes").writeNullable[Def1_Create_PeriodIncome] and
      (JsPath \ "financials" \ "deductions").writeNullable[Def1_Create_PeriodExpenses] and
      (JsPath \ "financials" \ "deductions").writeNullable[Def1_Create_PeriodDisallowableExpenses]
  )(unlift(Def1_CreatePeriodSummaryRequestBody.unapply))

}

case class Def2_CreatePeriodSummaryRequestBody(periodDates: Def2_Create_PeriodDates,
                                               periodIncome: Option[Def2_Create_PeriodIncome],
                                               periodExpenses: Option[Def2_Create_PeriodExpenses],
                                               periodDisallowableExpenses: Option[Def2_Create_PeriodDisallowableExpenses])
    extends CreatePeriodSummaryRequestBody

object Def2_CreatePeriodSummaryRequestBody {
  implicit val reads: Reads[Def2_CreatePeriodSummaryRequestBody] = Json.reads[Def2_CreatePeriodSummaryRequestBody]

  implicit val writes: OWrites[Def2_CreatePeriodSummaryRequestBody] = (
    JsPath.write[Def2_Create_PeriodDates] and
      (JsPath \ "financials" \ "incomes").writeNullable[Def2_Create_PeriodIncome] and
      (JsPath \ "financials" \ "deductions").writeNullable[Def2_Create_PeriodExpenses] and
      (JsPath \ "financials" \ "deductions").writeNullable[Def2_Create_PeriodDisallowableExpenses]
  )(unlift(Def2_CreatePeriodSummaryRequestBody.unapply))

}
