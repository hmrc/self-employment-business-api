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

package v3.createAmendAnnualSubmission

import api.controllers.validators.Validator
import api.controllers.validators.common.InvalidResultValidator
import api.models.domain.TaxYear
import api.models.errors.TaxYearFormatError
import config.FeatureSwitches
import play.api.libs.json._
import v3.createAmendAnnualSubmission.def1.Def1_CreateAmendAnnualSubmissionValidator
import v3.createAmendAnnualSubmission.def2.Def2_CreateAmendAnnualSubmissionValidator
import v3.createAmendAnnualSubmission.model.request.CreateAmendAnnualSubmissionRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class CreateAmendAnnualSubmissionValidatorFactory @Inject()(implicit featureSwitches: FeatureSwitches) {

  private val def2TaxYearApplicableFrom = TaxYear.fromMtd("2024-25")


  private val invalidTaxYearValidator =
    InvalidResultValidator[CreateAmendAnnualSubmissionRequestData](TaxYearFormatError)

  def validator(nino: String, businessId: String, taxYear: String, body: JsValue): Validator[CreateAmendAnnualSubmissionRequestData] = {

    TaxYear.maybeFromMtd(taxYear) match {

      case Some(ty) if ty.isBefore(def2TaxYearApplicableFrom) =>
        new Def1_CreateAmendAnnualSubmissionValidator(nino, businessId, taxYear, body)

      case Some(_) =>
        new Def2_CreateAmendAnnualSubmissionValidator(nino, businessId, taxYear, body)

      case None =>
        invalidTaxYearValidator
    }

  }

}
