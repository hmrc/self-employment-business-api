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

package v3.controllers.createPeriodSummary.validators.def1

import api.controllers.validators.Validator
import api.controllers.validators.resolvers._
import api.models.errors.{EndDateFormatError, MtdError, StartDateFormatError}
import cats.data.Validated
import cats.data.Validated.Valid
import cats.implicits.{catsSyntaxTuple3Semigroupal, toFoldableOps}
import play.api.libs.json.JsValue
import v3.models.request.createPeriodSummary.def1.Def1_CreatePeriodSummaryRequestBody
import v3.models.request.createPeriodSummary.{CreatePeriodSummaryRequestData, Def1_CreatePeriodSummaryRequestData}

import scala.annotation.nowarn

class Def1_CreatePeriodSummaryValidator(nino: String, businessId: String, body: JsValue, includeNegatives: Boolean)
    extends Validator[CreatePeriodSummaryRequestData] {

  @nowarn("cat=lint-byname-implicit")
  private val resolveJson = new ResolveNonEmptyJsonObject[Def1_CreatePeriodSummaryRequestBody]()

  private val rulesValidator = Def1_CreatePeriodSummaryRulesValidator(includeNegatives)

  def validate: Validated[Seq[MtdError], CreatePeriodSummaryRequestData] = {
    validateJsonFields(body) andThen { parsedBody =>
      (
        ResolveNino(nino),
        ResolveBusinessId(businessId),
        Valid(parsedBody)
      ).mapN(Def1_CreatePeriodSummaryRequestData)
    } andThen rulesValidator.validateBusinessRules
  }

  private def validateJsonFields(body: JsValue): Validated[Seq[MtdError], Def1_CreatePeriodSummaryRequestBody] =
    resolveJson(body) andThen (parsedBody =>
      List(
        ResolveIsoDate(parsedBody.periodDates.periodStartDate, StartDateFormatError),
        ResolveIsoDate(parsedBody.periodDates.periodEndDate, EndDateFormatError)
      ).traverse_(identity).map(_ => parsedBody))

}
