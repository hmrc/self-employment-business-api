/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.response.retrieveSEPeriodic

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class RetrieveSelfEmploymentPeriodicResponseBody(periodFromDate: String,
                                                      periodToDate: String,
                                                      incomes: Option[Incomes],
                                                      consolidatedExpenses: Option[ConsolidatedExpenses],
                                                      expenses: Option[Expenses])

object RetrieveSelfEmploymentPeriodicResponseBody {
  implicit val reads: Reads[RetrieveSelfEmploymentPeriodicResponseBody] = (
    (JsPath \ "from").read[String] and
      (JsPath \ "to").read[String] and
      (JsPath \ "financials" \ "incomes").readNullable[Incomes] and
      (JsPath \ "financials" \ "deductions" \ "simplifiedExpenses").readNullable[BigDecimal].map(_.map(ConsolidatedExpenses(_))) and
      (JsPath \ "financials" \ "deductions").readNullable[Expenses]
    ) (RetrieveSelfEmploymentPeriodicResponseBody.apply _)


  implicit val writes: OWrites[RetrieveSelfEmploymentPeriodicResponseBody] = Json.writes[RetrieveSelfEmploymentPeriodicResponseBody]

}