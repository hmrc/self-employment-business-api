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

package v3.amendPeriodSummary

import play.api.libs.json.JsValue
import shared.config.SharedAppConfig
import shared.controllers.validators.Validator
import shared.models.errors.NotFoundError
import v3.amendPeriodSummary.def2.Def2_AmendPeriodSummaryValidator
import v3.amendPeriodSummary.model.request.AmendPeriodSummaryRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class AmendPeriodSummaryValidatorFactory @Inject() (implicit appConfig: SharedAppConfig) {

  def validator(nino: String,
                businessId: String,
                periodId: String,
                maybeTaxYear: Option[String],
                body: JsValue,
                includeNegatives: Boolean): Validator[AmendPeriodSummaryRequestData] = {

    maybeTaxYear match {
      case None             => Validator.returningErrors(Seq(NotFoundError))
      case Some(taxYearStr) => new Def2_AmendPeriodSummaryValidator(nino, businessId, periodId, taxYearStr, body, includeNegatives)
    }
  }

}
