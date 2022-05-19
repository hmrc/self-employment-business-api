/*
 * Copyright 2022 HM Revenue & Customs
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

package v1.models.request.createPeriodic

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class CreatePeriodicBody(periodDates: PeriodDates,
                              periodIncome: Option[PeriodIncome],
                              periodAllowableExpenses: Option[PeriodAllowableExpenses],
                              periodDisallowableExpenses: Option[PeriodDisallowableExpenses])

object CreatePeriodicBody {
  implicit val reads: Reads[CreatePeriodicBody] = Json.reads[CreatePeriodicBody]

  implicit val writes: OWrites[CreatePeriodicBody] = (
    JsPath.write[PeriodDates] and
      (JsPath \ "financials" \ "incomes").writeNullable[PeriodIncome] and
      (JsPath \ "financials" \ "deductions").writeNullable[PeriodAllowableExpenses] and
      (JsPath \ "financials" \ "deductions").writeNullable[PeriodDisallowableExpenses]
    )(unlift(CreatePeriodicBody.unapply))
}
