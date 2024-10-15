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

import cats.data.Validated.{Invalid, Valid}
import play.api.libs.json.JsValue
import shared.config.SharedAppConfig
import shared.controllers.validators.Validator
import v4.amendPeriodSummary.AmendPeriodSummarySchema.{Def1, Def2}
import v4.amendPeriodSummary.def1.Def1_AmendPeriodSummaryValidator
import v4.amendPeriodSummary.def2.Def2_AmendPeriodSummaryValidator
import v4.amendPeriodSummary.model.request.AmendPeriodSummaryRequestData

import javax.inject.{Inject, Singleton}

@Singleton
class AmendPeriodSummaryValidatorFactory @Inject() (implicit appConfig: SharedAppConfig) {

  def validator(nino: String,
                businessId: String,
                periodId: String,
                taxYear: String,
                body: JsValue,
                includeNegatives: Boolean): Validator[AmendPeriodSummaryRequestData] = {

    AmendPeriodSummarySchema.schemaFor(taxYear) match {
      case Valid(Def1)     => new Def1_AmendPeriodSummaryValidator(nino, businessId, periodId, taxYear, body, includeNegatives)
      case Valid(Def2)     => new Def2_AmendPeriodSummaryValidator(nino, businessId, periodId, taxYear, body, includeNegatives)
      case Invalid(errors) => Validator.returningErrors(errors)
    }
  }

}
