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

package v2.models.request.amendSEAnnual

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}

case class AmendAnnualSubmissionBody(adjustments: Option[Adjustments], allowances: Option[Allowances], nonFinancials: Option[NonFinancials])

object AmendAnnualSubmissionBody {

  implicit val reads: Reads[AmendAnnualSubmissionBody] = Json.reads[AmendAnnualSubmissionBody]

  implicit val writes: OWrites[AmendAnnualSubmissionBody] = (
    (JsPath \ "annualAdjustments").writeNullable[Adjustments] and
      (JsPath \ "annualAllowances").writeNullable[Allowances] and
      (JsPath \ "annualNonFinancials").writeNullable[NonFinancials]
  )(unlift(AmendAnnualSubmissionBody.unapply))

}
