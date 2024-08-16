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

package v2.controllers.validators

import cats.data.Validated
import cats.implicits.catsSyntaxTuple4Semigroupal
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino}
import shared.models.errors.MtdError
import v2.controllers.validators.resolvers.{ResolvePeriodId, ResolveTysTaxYearWithMax}
import v2.models.request.retrievePeriodSummary.RetrievePeriodSummaryRequestData

import javax.inject.Singleton

@Singleton
class RetrievePeriodSummaryValidatorFactory {

  def validator(nino: String, businessId: String, periodId: String, taxYear: Option[String]): Validator[RetrievePeriodSummaryRequestData] =
    new Validator[RetrievePeriodSummaryRequestData] {

      def validate: Validated[Seq[MtdError], RetrievePeriodSummaryRequestData] =
        (
          ResolveNino(nino),
          ResolveBusinessId(businessId),
          ResolvePeriodId(periodId, 1900, 2100),
          ResolveTysTaxYearWithMax(taxYear)
        ).mapN(RetrievePeriodSummaryRequestData)

    }

}
