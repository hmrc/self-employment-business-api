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

package v4.createAmendCumulativePeriodSummary

import cats.data.Validated
import shared.controllers.validators.resolvers.ResolveTaxYearMinimum
import shared.models.domain.TaxYear
import shared.models.errors.MtdError

object CreateAmendCumulativePeriodSummarySchema {

  private val resolveTaxYearMinimum = ResolveTaxYearMinimum(TaxYear.fromMtd("2025-26"))

  def schemaFor(taxYear: String): Validated[Seq[MtdError], TaxYear] = resolveTaxYearMinimum(taxYear)

}