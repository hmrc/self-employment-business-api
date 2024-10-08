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

package v4.retrievePeriodSummary.def1.model.response

import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v4.retrievePeriodSummary.model.response.RetrievePeriodSummaryResponse

case class Def1_RetrievePeriodSummaryResponse(periodDates: Retrieve_PeriodDates,
                                              periodIncome: Option[Retrieve_PeriodIncome],
                                              periodExpenses: Option[Retrieve_PeriodExpenses],
                                              periodDisallowableExpenses: Option[Retrieve_PeriodDisallowableExpenses])
    extends RetrievePeriodSummaryResponse {

  def withoutTaxTakenOffTradingIncome: Def1_RetrievePeriodSummaryResponse =
    periodIncome
      .map(pi => copy(periodIncome = Some(pi.copy(taxTakenOffTradingIncome = None))))
      .getOrElse(this)

}

object Def1_RetrievePeriodSummaryResponse {

  implicit val reads: Reads[Def1_RetrievePeriodSummaryResponse] = for {
    periodStartDate <- (JsPath \ "from").read[String]
    periodEndDate   <- (JsPath \ "to").read[String]

    periodDates = Retrieve_PeriodDates(periodStartDate = periodStartDate, periodEndDate = periodEndDate)

    periodIncome               <- (JsPath \ "financials" \ "incomes").readNullable[Retrieve_PeriodIncome]
    periodExpenses             <- (JsPath \ "financials").readNullable[Retrieve_PeriodExpenses]
    periodDisallowableExpenses <- (JsPath \ "financials").readNullable[Retrieve_PeriodDisallowableExpenses]
  } yield {
    Def1_RetrievePeriodSummaryResponse(
      periodDates = periodDates,
      periodIncome = if (periodIncome.exists(_.isEmptyObject)) None else periodIncome,
      periodExpenses = if (periodExpenses.exists(_.isEmptyObject)) None else periodExpenses,
      periodDisallowableExpenses = if (periodDisallowableExpenses.exists(_.isEmptyObject)) None else periodDisallowableExpenses
    )
  }

  implicit val writes: OWrites[Def1_RetrievePeriodSummaryResponse] = Json.writes[Def1_RetrievePeriodSummaryResponse]

}
