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

package v4.retrieveCumulativePeriodSummary.def1

import cats.data.Validated
import cats.implicits.catsSyntaxTuple3Semigroupal
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino, ResolveTaxYearMinimum}
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import v4.retrieveCumulativePeriodSummary.def1.model.request.Def1_RetrieveCumulativePeriodSummaryRequestData
import v4.retrieveCumulativePeriodSummary.model.request.RetrieveCumulativePeriodSummaryRequestData

class Def1_RetrieveCumulativePeriodSummaryValidator(nino: String, businessId: String, taxYear: String)
    extends Validator[RetrieveCumulativePeriodSummaryRequestData] {

  private lazy val resolveTaxYear = ResolveTaxYearMinimum(TaxYear.ending(2025))

  def validate: Validated[Seq[MtdError], RetrieveCumulativePeriodSummaryRequestData] = {
    (
      ResolveNino(nino),
      ResolveBusinessId(businessId),
      resolveTaxYear(taxYear)
    ).mapN(Def1_RetrieveCumulativePeriodSummaryRequestData)
  }

}
