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

package v3.models.request.createPeriodSummary.def2

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v3.models.request.createPeriodSummary.CreatePeriodSummaryRequestBody

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
