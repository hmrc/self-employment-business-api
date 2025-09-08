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

package v4.createPeriodSummary.def2

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.{catsSyntaxTuple3Semigroupal, toFoldableOps}
import config.SeBusinessFeatureSwitches
import play.api.libs.json.JsValue
import shared.config.SharedAppConfig
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.*
import shared.models.errors.{EndDateFormatError, MtdError, RuleIncorrectOrEmptyBodyError, StartDateFormatError}
import v4.createPeriodSummary.model.request.{CreatePeriodSummaryRequestData, Def2_CreatePeriodSummaryRequestBody, Def2_CreatePeriodSummaryRequestData}

class Def2_CreatePeriodSummaryValidator(
    nino: String,
    businessId: String,
    body: JsValue,
    includeNegatives: Boolean
)(implicit appConfig: SharedAppConfig)
    extends Validator[CreatePeriodSummaryRequestData] {

  lazy private val featureSwitches = SeBusinessFeatureSwitches()

  private val resolveJson = new ResolveNonEmptyJsonObject[Def2_CreatePeriodSummaryRequestBody]()

  private val rulesValidator = Def2_CreatePeriodSummaryRulesValidator(includeNegatives)

  def validate: Validated[Seq[MtdError], CreatePeriodSummaryRequestData] =
    validateJsonFields(body) andThen { parsedBody =>
      (
        ResolveNino(nino),
        ResolveBusinessId(businessId),
        Valid(parsedBody)
      ).mapN(Def2_CreatePeriodSummaryRequestData.apply)
    } andThen rulesValidator.validateBusinessRules andThen validateTaxTakenOffTradingIncome

  /** Can be removed when CL290 is released.
    */
  private def validateTaxTakenOffTradingIncome(
      parsed: Def2_CreatePeriodSummaryRequestData): Validated[Seq[MtdError], Def2_CreatePeriodSummaryRequestData] =
    (for {
      income <- parsed.body.periodIncome if !featureSwitches.isCl290Enabled
      _      <- income.taxTakenOffTradingIncome
    } yield {
      Invalid(
        List(
          RuleIncorrectOrEmptyBodyError.withPath("/periodIncome/taxTakenOffTradingIncome")
        ))
    }).getOrElse(Valid(parsed))

  private def validateJsonFields(body: JsValue): Validated[Seq[MtdError], Def2_CreatePeriodSummaryRequestBody] =
    resolveJson(body) andThen (parsedBody =>
      List(
        ResolveIsoDate(parsedBody.periodDates.periodStartDate, StartDateFormatError),
        ResolveIsoDate(parsedBody.periodDates.periodEndDate, EndDateFormatError)
      ).traverse_(identity).map(_ => parsedBody))

}
