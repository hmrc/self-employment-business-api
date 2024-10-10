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

package v4.amendPeriodSummary.model.request

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v4.amendPeriodSummary.def1.model.request.{Amend_PeriodDisallowableExpenses, Amend_PeriodExpenses, Amend_PeriodIncome}

sealed trait AmendPeriodSummaryRequestBody

case class Def1_AmendPeriodSummaryRequestBody(periodIncome: Option[Amend_PeriodIncome],
                                              periodExpenses: Option[Amend_PeriodExpenses],
                                              periodDisallowableExpenses: Option[Amend_PeriodDisallowableExpenses])
    extends AmendPeriodSummaryRequestBody

object Def1_AmendPeriodSummaryRequestBody {

  implicit val reads: Reads[Def1_AmendPeriodSummaryRequestBody] = Json.reads[Def1_AmendPeriodSummaryRequestBody]

  implicit val writes: OWrites[Def1_AmendPeriodSummaryRequestBody] = (
    (JsPath \ "incomes").writeNullable[Amend_PeriodIncome] and
      (JsPath \ "deductions").writeNullable[Amend_PeriodExpenses] and
      (JsPath \ "deductions").writeNullable[Amend_PeriodDisallowableExpenses]
  )(unlift(Def1_AmendPeriodSummaryRequestBody.unapply))

}
