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

package v5.retrieveAnnualSubmission.def3.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v5.retrieveAnnualSubmission.model.response.RetrieveAnnualSubmissionResponse

case class Def3_RetrieveAnnualSubmissionResponse(
    adjustments: Option[RetrieveAdjustments],
    allowances: Option[RetrieveAllowances],
    nonFinancials: Option[RetrieveNonFinancials]
) extends RetrieveAnnualSubmissionResponse

object Def3_RetrieveAnnualSubmissionResponse {

  implicit val reads: Reads[Def3_RetrieveAnnualSubmissionResponse] = (
    (JsPath \ "annualAdjustments").readNullable[RetrieveAdjustments] and
      (JsPath \ "annualAllowances").readNullable[RetrieveAllowances] and
      (JsPath \ "annualNonFinancials").readNullable[RetrieveNonFinancials]
  )(Def3_RetrieveAnnualSubmissionResponse.apply _)

  implicit val writes: OWrites[Def3_RetrieveAnnualSubmissionResponse] =
    Json.writes[Def3_RetrieveAnnualSubmissionResponse]

}
