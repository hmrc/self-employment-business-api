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

package v4.createAmendCumulativePeriodSummary.model.request

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v4.createAmendCumulativePeriodSummary.def1.model.request.{PeriodDates, PeriodDisallowableExpenses, PeriodExpenses, PeriodIncome}

sealed trait CreateAmendCumulativePeriodSummaryRequestBody {
  val periodDates: Option[Create_PeriodDates]
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
  val taxTakenOffTradingIncome: Option[BigDecimal]
}

trait Create_PeriodDisallowableExpenses

case class Def1_CreateAmendCumulativePeriodSummaryRequestBody(periodDates: Option[PeriodDates],
                                                              periodIncome: Option[PeriodIncome],
                                                              periodExpenses: Option[PeriodExpenses],
                                                              periodDisallowableExpenses: Option[PeriodDisallowableExpenses])
    extends CreateAmendCumulativePeriodSummaryRequestBody

object Def1_CreateAmendCumulativePeriodSummaryRequestBody {
  implicit val reads: Reads[Def1_CreateAmendCumulativePeriodSummaryRequestBody] = Json.reads[Def1_CreateAmendCumulativePeriodSummaryRequestBody]

  implicit val writes: OWrites[Def1_CreateAmendCumulativePeriodSummaryRequestBody] = (
    (JsPath \ "selfEmploymentPeriodDates").writeNullable[PeriodDates] and
      (JsPath \ "selfEmploymentPeriodIncome").writeNullable[PeriodIncome] and
      (JsPath \ "selfEmploymentPeriodDeductions").writeNullable[PeriodExpenses] and
      (JsPath \ "selfEmploymentPeriodDeductions").writeNullable[PeriodDisallowableExpenses]
  )(w => Tuple.fromProductTyped(w))

}
