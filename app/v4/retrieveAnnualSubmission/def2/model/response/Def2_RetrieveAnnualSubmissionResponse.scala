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

package v4.retrieveAnnualSubmission.def2.model.response

import play.api.libs.functional.syntax.*
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v4.retrieveAnnualSubmission.model.response.RetrieveAnnualSubmissionResponse

case class Def2_RetrieveAnnualSubmissionResponse(
    adjustments: Option[RetrieveAdjustments],
    allowances: Option[RetrieveAllowances],
    nonFinancials: Option[RetrieveNonFinancials]
) extends RetrieveAnnualSubmissionResponse

object Def2_RetrieveAnnualSubmissionResponse {

  implicit val reads: Reads[Def2_RetrieveAnnualSubmissionResponse] = (
    (JsPath \ "annualAdjustments").readNullable[RetrieveAdjustments] and
      (JsPath \ "annualAllowances").readNullable[RetrieveAllowances] and
      (JsPath \ "annualNonFinancials").readNullable[RetrieveNonFinancials]
  )(Def2_RetrieveAnnualSubmissionResponse.apply)

  implicit val writes: OWrites[Def2_RetrieveAnnualSubmissionResponse] =
    Json.writes[Def2_RetrieveAnnualSubmissionResponse]

}
