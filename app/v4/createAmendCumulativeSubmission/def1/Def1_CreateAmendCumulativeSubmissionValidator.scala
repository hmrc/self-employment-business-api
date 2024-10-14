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

package v4.createAmendCumulativeSubmission.def1

import cats.data.Validated
import cats.data.Validated.Valid
import cats.implicits.{catsSyntaxTuple4Semigroupal, toFoldableOps}
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers._
import shared.models.domain.TaxYear
import shared.models.errors.{EndDateFormatError, MtdError, StartDateFormatError}
import v4.createAmendCumulativeSubmission.model.request._

class Def1_CreateAmendCumulativeSubmissionValidator(
    nino: String,
    businessId: String,
    taxYear: TaxYear,
    body: JsValue
) extends Validator[CreateAmendCumulativeSubmissionRequestData] {

  private val resolveJson = new ResolveNonEmptyJsonObject[Def1_CreateAmendCumulativeSubmissionRequestBody]()

  private val rulesValidator = Def1_CreateAmendCumulativeSubmissionRulesValidator()

  def validate: Validated[Seq[MtdError], CreateAmendCumulativeSubmissionRequestData] =
    validateJsonFields(body) andThen { parsedBody =>
      (
        ResolveNino(nino),
        ResolveBusinessId(businessId),
        Valid(taxYear),
        Valid(parsedBody)
      ).mapN(Def1_CreateAmendCumulativeSubmissionRequestData)
    } andThen rulesValidator.validateBusinessRules

  private def validateJsonFields(body: JsValue): Validated[Seq[MtdError], Def1_CreateAmendCumulativeSubmissionRequestBody] =
    resolveJson(body) andThen (parsedBody =>
      List(
        ResolveIsoDate(parsedBody.periodDates.periodStartDate, StartDateFormatError),
        ResolveIsoDate(parsedBody.periodDates.periodEndDate, EndDateFormatError)
      ).traverse_(identity).map(_ => parsedBody))

}
