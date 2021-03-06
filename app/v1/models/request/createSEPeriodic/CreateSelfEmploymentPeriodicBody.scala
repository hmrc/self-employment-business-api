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

package v1.models.request.createSEPeriodic

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class CreateSelfEmploymentPeriodicBody(periodFromDate: String,
                                            periodToDate: String,
                                            incomes: Option[Incomes],
                                            consolidatedExpenses: Option[ConsolidatedExpenses],
                                            expenses: Option[Expenses]) {
  def isEmpty: Boolean = {
    incomes.exists(_.isEmpty) || expenses.exists(_.isEmpty)
  }
}

object CreateSelfEmploymentPeriodicBody {
  implicit val reads: Reads[CreateSelfEmploymentPeriodicBody] = Json.reads[CreateSelfEmploymentPeriodicBody]
  implicit val writes: OWrites[CreateSelfEmploymentPeriodicBody] = (
    (JsPath \ "from").write[String] and
      (JsPath \ "to").write[String] and
      (JsPath \ "financials" \ "incomes").writeNullable[Incomes] and
      (JsPath \ "financials" \ "deductions" \ "simplifiedExpenses").writeNullable[BigDecimal] and
      (JsPath \ "financials" \ "deductions").writeNullable[Expenses]
    ) (unlift(CreateSelfEmploymentPeriodicBody.unapply(_: CreateSelfEmploymentPeriodicBody).map {
    case (periodFromDate, periodToDate, incomesO, consolidatedExpensesO, expensesO) =>
      (periodFromDate, periodToDate, incomesO, consolidatedExpensesO.map(_.consolidatedExpenses), expensesO)
  }))
}