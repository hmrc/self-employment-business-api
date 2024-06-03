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

package v3.amendPeriodSummary.def2

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino, ResolveNonEmptyJsonObject, ResolveTysTaxYear}
import api.models.errors.{MtdError, RuleIncorrectOrEmptyBodyError}
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.catsSyntaxTuple5Semigroupal
import config.{AppConfig, FeatureSwitches}
import play.api.libs.json.JsValue
import v3.amendPeriodSummary.model.request.{AmendPeriodSummaryRequestData, Def2_AmendPeriodSummaryRequestBody, Def2_AmendPeriodSummaryRequestData}
import v3.validators.resolvers.ResolvePeriodId

class Def2_AmendPeriodSummaryValidator(nino: String, businessId: String, periodId: String, taxYear: String, body: JsValue, includeNegatives: Boolean)(
    implicit appConfig: AppConfig)
    extends Validator[AmendPeriodSummaryRequestData] {

  lazy private val featureSwitches = FeatureSwitches(appConfig)

  private val resolveJson = new ResolveNonEmptyJsonObject[Def2_AmendPeriodSummaryRequestBody]()

  private val rulesValidator = new Def2_AmendPeriodSummaryRulesValidator(includeNegatives)

  def validate: Validated[Seq[MtdError], AmendPeriodSummaryRequestData] =
    (
      ResolveNino(nino),
      ResolveBusinessId(businessId),
      ResolvePeriodId(periodId),
      ResolveTysTaxYear(taxYear),
      resolveJson(body)
    ).mapN(Def2_AmendPeriodSummaryRequestData) andThen rulesValidator.validateBusinessRules andThen validateTaxTakenOffTradingIncome

  /** Can be removed when CL290 is released.
    */
  private def validateTaxTakenOffTradingIncome(
      parsed: Def2_AmendPeriodSummaryRequestData): Validated[Seq[MtdError], Def2_AmendPeriodSummaryRequestData] =
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
