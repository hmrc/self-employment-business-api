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

package v5.amendPeriodSummary.def1

import cats.data.Validated
import cats.implicits.catsSyntaxTuple5Semigroupal
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers._
import shared.models.errors.MtdError
import v5.amendPeriodSummary.def1.Def1_AmendPeriodSummaryValidator.resolveTaxYear
import v5.amendPeriodSummary.def1.model.request.{Def1_AmendPeriodSummaryRequestBody, Def1_AmendPeriodSummaryRequestData}
import v5.amendPeriodSummary.model.request.AmendPeriodSummaryRequestData
import v5.validators.resolvers.ResolvePeriodId

object Def1_AmendPeriodSummaryValidator extends ResolverSupport {
  private val resolveTaxYear = ResolveTaxYear.resolver
}

class Def1_AmendPeriodSummaryValidator(nino: String, businessId: String, periodId: String, taxYear: String, body: JsValue, includeNegatives: Boolean)
    extends Validator[AmendPeriodSummaryRequestData] {

  private val resolveJson = new ResolveNonEmptyJsonObject[Def1_AmendPeriodSummaryRequestBody]()

  private val rulesValidator = new Def1_AmendPeriodSummaryRulesValidator(includeNegatives)

  def validate: Validated[Seq[MtdError], AmendPeriodSummaryRequestData] =
    (
      ResolveNino(nino),
      ResolveBusinessId(businessId),
      ResolvePeriodId(periodId),
      resolveTaxYear(taxYear),
      resolveJson(body)
    ).mapN(Def1_AmendPeriodSummaryRequestData.apply) andThen rulesValidator.validateBusinessRules

}
