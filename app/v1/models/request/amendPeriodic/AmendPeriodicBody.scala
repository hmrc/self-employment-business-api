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

package v1.models.request.amendPeriodic

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class AmendPeriodicBody(incomes: Option[Incomes], consolidatedExpenses: Option[ConsolidatedExpenses], expenses: Option[Expenses]) {
  def isEmpty: Boolean =
    (incomes.isEmpty && consolidatedExpenses.isEmpty && expenses.isEmpty) ||
      incomes.exists(_.isEmpty) ||
      expenses.exists(_.isEmpty)
}

object AmendPeriodicBody {

  implicit val reads: Reads[AmendPeriodicBody] = Json.reads[AmendPeriodicBody]

  implicit val writes: OWrites[AmendPeriodicBody] = (
      (JsPath \ "incomes").writeNullable[Incomes] and
      (JsPath \ "deductions" \ "simplifiedExpenses").writeNullable[BigDecimal] and
      (JsPath \ "deductions").writeNullable[Expenses]
    )(unlift(AmendPeriodicBody.unapply(_: AmendPeriodicBody).map {
    case (incomesO, consolidatedExpensesO, expensesO) =>
      (incomesO, consolidatedExpensesO.map(_.consolidatedExpenses), expensesO)
  }))
}