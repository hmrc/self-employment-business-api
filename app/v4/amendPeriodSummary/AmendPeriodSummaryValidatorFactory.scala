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

package v4.amendPeriodSummary

import play.api.libs.json.JsValue
import shared.config.AppConfig
import shared.controllers.validators.Validator
import v4.amendPeriodSummary.def1.Def1_AmendPeriodSummaryValidator
import v4.amendPeriodSummary.model.request.AmendPeriodSummaryRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class AmendPeriodSummaryValidatorFactory @Inject() (implicit appConfig: AppConfig) {

  def validator(nino: String,
                businessId: String,
                periodId: String,
                taxYear: String,
                body: JsValue,
                includeNegatives: Boolean): Validator[AmendPeriodSummaryRequestData] = {

    taxYear match {
      case taxYearStr => new Def1_AmendPeriodSummaryValidator(nino, businessId, periodId, taxYearStr, body, includeNegatives)
    }
  }

}
