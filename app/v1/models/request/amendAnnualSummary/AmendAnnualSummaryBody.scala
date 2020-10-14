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

package v1.models.request.amendAnnualSummary

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class AmendAnnualSummaryBody(adjustments: Option[Adjustments], allowances: Option[Allowances], nonFinancials: Option[NonFinancials])

object AmendAnnualSummaryBody {

  implicit val reads: Reads[AmendAnnualSummaryBody] = Json.reads[AmendAnnualSummaryBody]
  implicit val writes: OWrites[AmendAnnualSummaryBody] = (
    (JsPath \ "annualAdjustments").writeNullable[Adjustments] and
      (JsPath \ "annualAllowances").writeNullable[Allowances] and
      (JsPath \ "annualNonFinancials").writeNullable[NonFinancials]
    ) (unlift(AmendAnnualSummaryBody.unapply))
//  an empty nonFinancials should be errored out as IncorrectOrEmptyBody so it ever has to write from NonFinancials(None) which will return a Null
}