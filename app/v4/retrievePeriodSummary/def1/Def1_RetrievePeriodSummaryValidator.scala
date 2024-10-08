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

package v4.retrievePeriodSummary.def1

import cats.data.Validated
import cats.implicits.catsSyntaxTuple4Semigroupal
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino, ResolveTaxYearMinMax}
import shared.models.domain.TaxYear
import shared.models.errors.{InvalidTaxYearParameterError, MtdError, RuleTaxYearNotSupportedError}
import v4.retrievePeriodSummary.model.request.{Def1_RetrievePeriodSummaryRequestData, RetrievePeriodSummaryRequestData}
import v4.validators.resolvers.ResolvePeriodId

class Def1_RetrievePeriodSummaryValidator(nino: String, businessId: String, periodId: String, taxYear: String)
    extends Validator[RetrievePeriodSummaryRequestData] {

  private val minMaxTaxYears: (TaxYear, TaxYear) = (TaxYear.ending(2024), TaxYear.ending(2025))

  private val resolveTaxYear = ResolveTaxYearMinMax(
    minMaxTaxYears,
    minError = InvalidTaxYearParameterError,
    maxError = RuleTaxYearNotSupportedError
  ).resolver

  def validate: Validated[Seq[MtdError], RetrievePeriodSummaryRequestData] = {
    (
      ResolveNino(nino),
      ResolveBusinessId(businessId),
      ResolvePeriodId(periodId, 1900, 2100),
      resolveTaxYear(taxYear)
    ).mapN(Def1_RetrievePeriodSummaryRequestData)
  }

}
