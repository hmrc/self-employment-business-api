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
import cats.implicits._
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino, ResolveTaxYear, ResolverSupport}
import shared.models.domain.TaxYear
import shared.models.errors.{InvalidTaxYearParameterError, MtdError, RuleTaxYearNotSupportedError}
import v2.models.request.listPeriodSummaries.ListPeriodSummariesRequestData

object ListPeriodSummariesValidatorFactory {
  import ResolverSupport._

  private val resolveTaxYear =
    (ResolveTaxYear.resolver thenValidate
      satisfiesMin(TaxYear.tysTaxYear, InvalidTaxYearParameterError) thenValidate
      satisfiesMax(TaxYear.fromMtd("2024-25"), RuleTaxYearNotSupportedError)).resolveOptionally

}

class ListPeriodSummariesValidatorFactory {
  import ListPeriodSummariesValidatorFactory._

  def validator(nino: String, businessId: String, taxYear: Option[String]): Validator[ListPeriodSummariesRequestData] =
    new Validator[ListPeriodSummariesRequestData] {

      def validate: Validated[Seq[MtdError], ListPeriodSummariesRequestData] =
        (
          ResolveNino(nino),
          ResolveBusinessId(businessId),
          resolveTaxYear(taxYear)
        ).mapN(ListPeriodSummariesRequestData)

    }

}
