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

package v4.retrieveCumulativePeriodSummary.def1.model.response

import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v4.retrieveCumulativePeriodSummary.model.response.RetrieveCumulativePeriodSummaryResponse

case class Def1_RetrieveCumulativePeriodSummaryResponse(
    periodDates: Def1_Retrieve_PeriodDates,
    periodIncome: Option[Def1_Retrieve_PeriodIncome],
    periodExpenses: Option[Def1_Retrieve_PeriodExpenses],
    periodDisallowableExpenses: Option[Def1_Retrieve_PeriodDisallowableExpenses]
) extends RetrieveCumulativePeriodSummaryResponse

object Def1_RetrieveCumulativePeriodSummaryResponse {

  implicit val reads: Reads[Def1_RetrieveCumulativePeriodSummaryResponse] = for {
    periodDates                <- (JsPath \ "selfEmploymentPeriodDates").read[Def1_Retrieve_PeriodDates]
    periodIncome               <- (JsPath \ "selfEmploymentPeriodIncome").readNullable[Def1_Retrieve_PeriodIncome]
    periodExpenses             <- (JsPath \ "selfEmploymentPeriodDeductions").readNullable[Def1_Retrieve_PeriodExpenses]
    periodDisallowableExpenses <- (JsPath \ "selfEmploymentPeriodDeductions").readNullable[Def1_Retrieve_PeriodDisallowableExpenses]
  } yield {
    Def1_RetrieveCumulativePeriodSummaryResponse(
      periodDates = periodDates,
      periodIncome = if (periodIncome.exists(_.isEmptyObject)) None else periodIncome,
      periodExpenses = if (periodExpenses.exists(_.isEmptyObject)) None else periodExpenses,
      periodDisallowableExpenses = if (periodDisallowableExpenses.exists(_.isEmptyObject)) None else periodDisallowableExpenses
    )
  }

  implicit val writes: OWrites[Def1_RetrieveCumulativePeriodSummaryResponse] = Json.writes[Def1_RetrieveCumulativePeriodSummaryResponse]

}
