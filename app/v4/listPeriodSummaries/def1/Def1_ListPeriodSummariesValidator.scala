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
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino}
import shared.models.errors.MtdError
import v4.listPeriodSummaries.def1.model.request.Def1_ListPeriodSummariesRequestData
import v4.listPeriodSummaries.model.request.ListPeriodSummariesRequestData
import v4.validators.resolvers.ResolveTysTaxYearWithMax

class Def1_ListPeriodSummariesValidator(
    nino: String,
    businessId: String,
    taxYear: Option[String]
) extends Validator[ListPeriodSummariesRequestData] {

  def validate: Validated[Seq[MtdError], ListPeriodSummariesRequestData] =
    (
      ResolveNino(nino),
      ResolveBusinessId(businessId),
      ResolveTysTaxYearWithMax(taxYear)
    ).mapN(Def1_ListPeriodSummariesRequestData)

}
