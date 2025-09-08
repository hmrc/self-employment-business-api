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

package v5.amendPeriodSummary.def2.model.request

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v5.amendPeriodSummary.model.request.AmendPeriodSummaryRequestBody

case class Def2_AmendPeriodSummaryRequestBody(periodIncome: Option[Def2_Amend_PeriodIncome],
                                              periodExpenses: Option[Def2_Amend_PeriodExpenses],
                                              periodDisallowableExpenses: Option[Def2_Amend_PeriodDisallowableExpenses])
    extends AmendPeriodSummaryRequestBody

object Def2_AmendPeriodSummaryRequestBody {

  implicit val reads: Reads[Def2_AmendPeriodSummaryRequestBody] = Json.reads[Def2_AmendPeriodSummaryRequestBody]

  implicit val writes: OWrites[Def2_AmendPeriodSummaryRequestBody] = (
    (JsPath \ "incomes").writeNullable[Def2_Amend_PeriodIncome] and
      (JsPath \ "deductions").writeNullable[Def2_Amend_PeriodExpenses] and
      (JsPath \ "deductions").writeNullable[Def2_Amend_PeriodDisallowableExpenses]
  )(w => Tuple.fromProductTyped(w))

}
