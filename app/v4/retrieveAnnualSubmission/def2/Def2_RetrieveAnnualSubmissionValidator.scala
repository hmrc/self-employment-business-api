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

package v4.retrieveAnnualSubmission.def2

import cats.data.Validated
import cats.implicits._
import config.SeBusinessConfig
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers.{ResolveBusinessId, ResolveNino, ResolveTaxYearMinimum}
import shared.models.errors.MtdError
import v4.retrieveAnnualSubmission.def2.model.request.Def2_RetrieveAnnualSubmissionRequestData
import v4.retrieveAnnualSubmission.model.request.RetrieveAnnualSubmissionRequestData

class Def2_RetrieveAnnualSubmissionValidator(nino: String, businessId: String, taxYear: String)
    extends Validator[RetrieveAnnualSubmissionRequestData] {

  private val resolveTaxYear =
    ResolveTaxYearMinimum(minimumTaxYear = SeBusinessConfig.minimumTaxYear)

  def validate: Validated[Seq[MtdError], RetrieveAnnualSubmissionRequestData] =
    (
      ResolveNino(nino),
      ResolveBusinessId(businessId),
      resolveTaxYear(taxYear)
    ).mapN(Def2_RetrieveAnnualSubmissionRequestData)

}
