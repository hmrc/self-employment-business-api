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

package v1.controllers.validators

import api.controllers.validators.Validator
import api.controllers.validators.resolvers.{DetailedResolveTaxYear, ResolveBusinessId, ResolveNino, ResolveNonEmptyJsonObject}
import api.models.domain.TaxYear
import api.models.errors.MtdError
import cats.data.Validated
import cats.implicits._
import play.api.libs.json.JsValue
import v1.controllers.validators.AmendAnnualSubmissionRulesValidator.validateBusinessRules
import v1.models.request.amendSEAnnual.{AmendAnnualSubmissionBody, AmendAnnualSubmissionRequestData}

import scala.annotation.nowarn

class AmendAnnualSubmissionValidatorFactory {

  @nowarn("cat=lint-byname-implicit")
  private val resolveJson = new ResolveNonEmptyJsonObject[AmendAnnualSubmissionBody]()

  private val resolveTaxYear =
    DetailedResolveTaxYear(maybeMinimumTaxYear = Some(TaxYear.minimumTaxYear.year))

  def validator(nino: String, businessId: String, taxYear: String, body: JsValue): Validator[AmendAnnualSubmissionRequestData] =
    new Validator[AmendAnnualSubmissionRequestData] {

      def validate: Validated[Seq[MtdError], AmendAnnualSubmissionRequestData] =
        (
          ResolveNino(nino),
          ResolveBusinessId(businessId),
          resolveTaxYear(taxYear),
          resolveJson(body)
        ).mapN(AmendAnnualSubmissionRequestData) andThen validateBusinessRules

    }

}
