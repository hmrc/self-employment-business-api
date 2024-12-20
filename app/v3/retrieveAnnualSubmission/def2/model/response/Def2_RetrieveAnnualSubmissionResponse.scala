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

package v3.retrieveAnnualSubmission.def2.model.response

import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v3.retrieveAnnualSubmission.model.response.RetrieveAnnualSubmissionResponse

case class Def2_RetrieveAnnualSubmissionResponse(
    adjustments: Option[Retrieve_Adjustments],
    allowances: Option[Retrieve_Allowances],
    nonFinancials: Option[Retrieve_NonFinancials]
) extends RetrieveAnnualSubmissionResponse

object Def2_RetrieveAnnualSubmissionResponse {

  implicit val reads: Reads[Def2_RetrieveAnnualSubmissionResponse] = (
    (JsPath \ "annualAdjustments").readNullable[Retrieve_Adjustments] and
      (JsPath \ "annualAllowances").readNullable[Retrieve_Allowances] and
      (JsPath \ "annualNonFinancials").readNullable[Retrieve_NonFinancials]
  )(Def2_RetrieveAnnualSubmissionResponse.apply _)

  implicit val writes: OWrites[Def2_RetrieveAnnualSubmissionResponse] =
    Json.writes[Def2_RetrieveAnnualSubmissionResponse]

}
