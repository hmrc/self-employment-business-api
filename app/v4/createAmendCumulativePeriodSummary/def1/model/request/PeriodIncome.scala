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

package v4.createAmendCumulativePeriodSummary.def1.model.request

import play.api.libs.json.{Json, OWrites, Reads}
import v4.createAmendCumulativePeriodSummary.model.request.Create_PeriodIncome

case class PeriodIncome(turnover: Option[BigDecimal], other: Option[BigDecimal], taxTakenOffTradingIncome: Option[BigDecimal])
    extends Create_PeriodIncome

object PeriodIncome {
  implicit val reads: Reads[PeriodIncome] = Json.reads[PeriodIncome]

  implicit val writes: OWrites[PeriodIncome] = Json.writes[PeriodIncome]
}