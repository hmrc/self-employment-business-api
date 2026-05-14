/*
 * Copyright 2026 HM Revenue & Customs
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

package v5.createAmendAnnualSubmission.def4.request

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v5.createAmendAnnualSubmission.model.request.CreateAmendAnnualSubmissionRequestBody

case class Def4_CreateAmendAnnualSubmissionRequestBody(
    adjustments: Option[Def4_CreateAmend_Adjustments],
    allowances: Option[Def4_CreateAmend_Allowances],
    nonFinancials: Option[Def4_CreateAmend_NonFinancials]
) extends CreateAmendAnnualSubmissionRequestBody

object Def4_CreateAmendAnnualSubmissionRequestBody {

  implicit val reads: Reads[Def4_CreateAmendAnnualSubmissionRequestBody] =
    Json.reads[Def4_CreateAmendAnnualSubmissionRequestBody]

  implicit val writes: OWrites[Def4_CreateAmendAnnualSubmissionRequestBody] = (
    (JsPath \ "annualAdjustments").writeNullable[Def4_CreateAmend_Adjustments] and
      (JsPath \ "annualAllowances").writeNullable[Def4_CreateAmend_Allowances] and
      (JsPath \ "annualNonFinancials").writeNullable[Def4_CreateAmend_NonFinancials]
  )(w => Tuple.fromProductTyped(w))

}
