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
import play.api.libs.json.{JsPath, OWrites, Reads}

case class Incomes(turnover: Option[IncomesAmountObject], other: Option[IncomesAmountObject]) {
  def isEmpty: Boolean = turnover.isEmpty && other.isEmpty
}

object Incomes {
  implicit val reads: Reads[Incomes] = (
    (JsPath \ "turnover" \ "amount").readNullable[BigDecimal].map(_.map(IncomesAmountObject(_))) and
      (JsPath \ "other" \ "amount").readNullable[BigDecimal].map(_.map(IncomesAmountObject(_)))
    ) (Incomes.apply _)

  implicit val writes: OWrites[Incomes] = (
      (JsPath \ "turnover").writeNullable[BigDecimal] and
        (JsPath \ "other").writeNullable[BigDecimal]
      ) (unlift(Incomes.unapply(_: Incomes).map {
    case (turnoverO, otherO) => (turnoverO.map(_.amount), otherO.map(_.amount))
  }))
}