/*
 * Copyright 2024 HM Revenue & Customs
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

package v4.amendPeriodSummary.def1

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.catsSyntaxTuple5Semigroupal
import config.SeBusinessFeatureSwitches
import play.api.libs.json.JsValue
import shared.config.AppConfig
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino, ResolveNonEmptyJsonObject, ResolveTaxYearMinMax}
import shared.models.domain.TaxYear
import shared.models.errors.{InvalidTaxYearParameterError, MtdError, RuleIncorrectOrEmptyBodyError, RuleTaxYearNotSupportedError}
import v4.amendPeriodSummary.model.request.{AmendPeriodSummaryRequestData, Def1_AmendPeriodSummaryRequestBody, Def1_AmendPeriodSummaryRequestData}
import v4.validators.resolvers.ResolvePeriodId

class Def1_AmendPeriodSummaryValidator(nino: String, businessId: String, periodId: String, taxYear: String, body: JsValue, includeNegatives: Boolean)(
    implicit appConfig: AppConfig)
    extends Validator[AmendPeriodSummaryRequestData] {

  private val minMaxTaxYears: (TaxYear, TaxYear) = (TaxYear.ending(2024), TaxYear.ending(2025))

  private val resolveTaxYear = ResolveTaxYearMinMax(
    minMaxTaxYears,
    minError = InvalidTaxYearParameterError,
    maxError = RuleTaxYearNotSupportedError
  ).resolver

  lazy private val featureSwitches = SeBusinessFeatureSwitches()

  private val resolveJson = new ResolveNonEmptyJsonObject[Def1_AmendPeriodSummaryRequestBody]()

  private val rulesValidator = new Def1_AmendPeriodSummaryRulesValidator(includeNegatives)

  def validate: Validated[Seq[MtdError], AmendPeriodSummaryRequestData] =
    (
      ResolveNino(nino),
      ResolveBusinessId(businessId),
      ResolvePeriodId(periodId),
      resolveTaxYear(taxYear),
      resolveJson(body)
    ).mapN(Def1_AmendPeriodSummaryRequestData) andThen rulesValidator.validateBusinessRules andThen validateTaxTakenOffTradingIncome

  /** Can be removed when CL290 is released.
    */
  private def validateTaxTakenOffTradingIncome(
      parsed: Def1_AmendPeriodSummaryRequestData): Validated[Seq[MtdError], Def1_AmendPeriodSummaryRequestData] =
    (for {
      income <- parsed.body.periodIncome if !featureSwitches.isCl290Enabled
      _      <- income.taxTakenOffTradingIncome
    } yield {
      Invalid(
        List(
          RuleIncorrectOrEmptyBodyError.withPath("/periodIncome/taxTakenOffTradingIncome")
        ))
    }).getOrElse(Valid(parsed))

}