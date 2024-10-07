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

package v4.createPeriodSummary

import api.controllers.validators.common.InvalidResultValidator
import play.api.libs.json.{JsObject, JsValue}
import shared.config.SharedAppConfig
import shared.controllers.validators.Validator
import shared.models.domain.TaxYear
import shared.models.domain.TaxYear.fromIso
import shared.models.errors.{EndDateFormatError, RuleIncorrectOrEmptyBodyError}
import v4.createPeriodSummary.def1.Def1_CreatePeriodSummaryValidator
import v4.createPeriodSummary.def2.Def2_CreatePeriodSummaryValidator
import v4.createPeriodSummary.model.request.CreatePeriodSummaryRequestData

import javax.inject.{Inject, Singleton}
import scala.math.Ordering.Implicits.infixOrderingOps
import scala.util.Try

@Singleton
class CreatePeriodSummaryValidatorFactory @Inject() (implicit appConfig: SharedAppConfig) {

  private val def2TaxYearApplicableFrom = TaxYear.fromMtd("2023-24")

  private val emptyBodyValidator = InvalidResultValidator[CreatePeriodSummaryRequestData](RuleIncorrectOrEmptyBodyError)

  private val invalidEndDateValidator =
    InvalidResultValidator[CreatePeriodSummaryRequestData](EndDateFormatError.withPath("periodDates/periodEndDate"))

  def validator(nino: String, businessId: String, body: JsValue, includeNegatives: Boolean): Validator[CreatePeriodSummaryRequestData] = {

    if (body == JsObject.empty) emptyBodyValidator
    else {
      maybeTaxYear(body) match {
        case Some(taxYear) if taxYear < def2TaxYearApplicableFrom =>
          new Def1_CreatePeriodSummaryValidator(nino, businessId, body, includeNegatives)

        case Some(_) =>
          new Def2_CreatePeriodSummaryValidator(nino, businessId, body, includeNegatives)

        case None =>
          invalidEndDateValidator
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
