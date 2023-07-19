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

package v3.models.request.createPeriodSummary

import anyVersion.models.request.createPeriodSummary.{PeriodDates, PeriodDisallowableExpenses, PeriodIncome}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class CreatePeriodSummaryBody(periodDates: PeriodDates,
                                   periodIncome: Option[PeriodIncome],
                                   periodExpenses: Option[PeriodExpenses],
                                   periodDisallowableExpenses: Option[PeriodDisallowableExpenses])

object CreatePeriodSummaryBody {
  implicit val reads: Reads[CreatePeriodSummaryBody] = Json.reads[CreatePeriodSummaryBody]

  implicit val writes: OWrites[CreatePeriodSummaryBody] = (
    JsPath.write[PeriodDates] and
      (JsPath \ "financials" \ "incomes").writeNullable[PeriodIncome] and
      (JsPath \ "financials" \ "deductions").writeNullable[PeriodExpenses] and
      (JsPath \ "financials" \ "deductions").writeNullable[PeriodDisallowableExpenses]
  )(unlift(CreatePeriodSummaryBody.unapply))

}
