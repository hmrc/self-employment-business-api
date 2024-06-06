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

import api.controllers.validators.common.InvalidResultValidator
import play.api.libs.json.{JsObject, JsValue}
import shared.config.AppConfig
import shared.controllers.validators.Validator
import shared.models.domain.TaxYear
import shared.models.domain.TaxYear.fromIso
import shared.models.errors.{RuleIncorrectOrEmptyBodyError, TaxYearFormatError}
import v3.createAmendAnnualSubmission.def1.Def1_CreateAmendAnnualSubmissionValidator
import v3.createAmendAnnualSubmission.def2.Def2_CreateAmendAnnualSubmissionValidator
import v3.createAmendAnnualSubmission.model.request.CreateAmendAnnualSubmissionRequestData
import v3.createPeriodSummary.model.request.CreatePeriodSummaryRequestData

import javax.inject.{Inject, Singleton}
import scala.math.Ordering.Implicits.infixOrderingOps
import scala.util.Try

@Singleton
class CreateAmendAnnualSubmissionValidatorFactory @Inject() (implicit appConfig: AppConfig) {

  private val def2TaxYearApplicableFrom = TaxYear.fromMtd("2024-25")

  private val emptyBodyValidator = InvalidResultValidator[CreateAmendAnnualSubmissionRequestData](RuleIncorrectOrEmptyBodyError)

  private val invalidTaxYearValidator =
    InvalidResultValidator[CreateAmendAnnualSubmissionRequestData](TaxYearFormatError)

  def validator(nino: String, businessId: String, taxYear: String, body: JsValue): Validator[CreateAmendAnnualSubmissionRequestData] = {

    if (body == JsObject.empty) emptyBodyValidator
    else {
      maybeTaxYear(body) match {
        case Some(taxYear) if taxYear < def2TaxYearApplicableFrom =>
          new Def1_CreateAmendAnnualSubmissionValidator(nino, businessId, taxYear, body)

        case Some(_) =>
          new Def2_CreateAmendAnnualSubmissionValidator(nino, businessId, taxYear, body)

        case None =>
          invalidTaxYearValidator
      }
    }
  }

  private def maybeTaxYear(body: JsValue): Option[TaxYear] = {
    for {
      isoDateStr <- CreatePeriodSummaryRequestData.rawTaxYear(body)
      taxYear    <- Try(fromIso(isoDateStr)).toOption
    } yield taxYear
  }

}
