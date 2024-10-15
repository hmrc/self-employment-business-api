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

package v4.listPeriodSummaries.def1

import cats.data.Validated
import cats.implicits._
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino, ResolveTaxYearMinMax}
import shared.models.domain.TaxYear
import shared.models.errors.{InvalidTaxYearParameterError, MtdError, RuleTaxYearNotSupportedError}
import v4.listPeriodSummaries.def1.model.request.Def1_ListPeriodSummariesRequestData
import v4.listPeriodSummaries.model.request.ListPeriodSummariesRequestData

class Def1_ListPeriodSummariesValidator(
    nino: String,
    businessId: String,
    taxYear: String
) extends Validator[ListPeriodSummariesRequestData] {

  private val minMaxTaxYears: (TaxYear, TaxYear) = (TaxYear.ending(2018), TaxYear.ending(2025))

  private val resolveTaxYear = ResolveTaxYearMinMax(
    minMaxTaxYears,
    minError = InvalidTaxYearParameterError,
    maxError = RuleTaxYearNotSupportedError
  ).resolver

  def validate: Validated[Seq[MtdError], ListPeriodSummariesRequestData] =
    (
      ResolveNino(nino),
      ResolveBusinessId(businessId),
      resolveTaxYear(taxYear)
    ).mapN(Def1_ListPeriodSummariesRequestData)

}
