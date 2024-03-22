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

package v3.createAmendAnnualSubmission.def1.model.request

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v3.createAmendAnnualSubmission.model.request.CreateAmendAnnualSubmissionRequestBody

case class Def1_CreateAmendAnnualSubmissionRequestBody(
                                                        adjustments: Option[Def1_CreateAmend_Adjustments],
                                                        allowances: Option[Def1_CreateAmend_Allowances],
                                                        nonFinancials: Option[Def1_CreateAmend_NonFinancials]
                                                      ) extends CreateAmendAnnualSubmissionRequestBody

object Def1_CreateAmendAnnualSubmissionRequestBody {

  implicit val reads: Reads[Def1_CreateAmendAnnualSubmissionRequestBody] = Json.reads[Def1_CreateAmendAnnualSubmissionRequestBody]

  implicit val writes: OWrites[Def1_CreateAmendAnnualSubmissionRequestBody] = (
    (JsPath \ "annualAdjustments").writeNullable[Def1_CreateAmend_Adjustments] and
      (JsPath \ "annualAllowances").writeNullable[Def1_CreateAmend_Allowances] and
      (JsPath \ "annualNonFinancials").writeNullable[Def1_CreateAmend_NonFinancials]
  )(unlift(Def1_CreateAmendAnnualSubmissionRequestBody.unapply))

}
