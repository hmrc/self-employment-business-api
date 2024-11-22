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

package v4.createAmendAnnualSubmission

import api.controllers.validators.common.InvalidResultValidator
import play.api.libs.json.{JsObject, JsValue}
import shared.controllers.validators.Validator
import shared.models.domain.TaxYear
import shared.models.errors.{RuleIncorrectOrEmptyBodyError, TaxYearFormatError}
import v4.createAmendAnnualSubmission.def1.Def1_CreateAmendAnnualSubmissionValidator
import v4.createAmendAnnualSubmission.def2.Def2_CreateAmendAnnualSubmissionValidator
import v4.createAmendAnnualSubmission.def3.Def3_CreateAmendAnnualSubmissionValidator
import v4.createAmendAnnualSubmission.model.request.CreateAmendAnnualSubmissionRequestData

import javax.inject.Singleton
import scala.math.Ordering.Implicits.infixOrderingOps

@Singleton
class CreateAmendAnnualSubmissionValidatorFactory {

  private val def2TaxYearApplicableFrom = TaxYear.fromMtd("2024-25")

  private val emptyBodyValidator = InvalidResultValidator[CreateAmendAnnualSubmissionRequestData](RuleIncorrectOrEmptyBodyError)

  private val invalidTaxYearValidator =
    InvalidResultValidator[CreateAmendAnnualSubmissionRequestData](TaxYearFormatError)

  def validator(nino: String, businessId: String, taxYear: String, body: JsValue): Validator[CreateAmendAnnualSubmissionRequestData] = {

    if (body == JsObject.empty) emptyBodyValidator
    else {
      TaxYear.maybeFromMtd(taxYear) match {
        case Some(ty) if ty < def2TaxYearApplicableFrom =>
          new Def1_CreateAmendAnnualSubmissionValidator(nino, businessId, taxYear, body)
        case Some(ty) if ty > def2TaxYearApplicableFrom =>
          new Def3_CreateAmendAnnualSubmissionValidator(nino, businessId, taxYear, body)
        case Some(_) =>
          new Def2_CreateAmendAnnualSubmissionValidator(nino, businessId, taxYear, body)
        case None =>
          invalidTaxYearValidator
      }
    }
  }

}
