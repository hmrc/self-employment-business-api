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

import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino, ResolveNonEmptyJsonObject}
import shared.models.errors.MtdError
import cats.data.Validated
import cats.implicits.catsSyntaxTuple4Semigroupal
import play.api.libs.json.JsValue
import v4.amendPeriodSummary.model.request.{AmendPeriodSummaryRequestData, Def1_AmendPeriodSummaryRequestBody, Def1_AmendPeriodSummaryRequestData}
import v4.validators.resolvers.ResolvePeriodId

class Def1_AmendPeriodSummaryValidator(nino: String, businessId: String, periodId: String, body: JsValue, includeNegatives: Boolean)
    extends Validator[AmendPeriodSummaryRequestData] {

  private val resolveJson = new ResolveNonEmptyJsonObject[Def1_AmendPeriodSummaryRequestBody]()

  private val rulesValidator = new Def1_AmendPeriodSummaryRulesValidator(includeNegatives)

  def validate: Validated[Seq[MtdError], AmendPeriodSummaryRequestData] =
    (
      ResolveNino(nino),
      ResolveBusinessId(businessId),
      ResolvePeriodId(periodId),
      resolveJson(body)
    ).mapN(Def1_AmendPeriodSummaryRequestData) andThen rulesValidator.validateBusinessRules

}
