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

package v4.retrievePeriodSummary

import shared.controllers.validators.Validator
import v4.retrievePeriodSummary.def1.Def1_RetrievePeriodSummaryValidator
import v4.retrievePeriodSummary.model.request.RetrievePeriodSummaryRequestData

import javax.inject.Singleton

@Singleton
class RetrievePeriodSummaryValidatorFactory {

  def validator(nino: String, businessId: String, periodId: String, taxYear: String): Validator[RetrievePeriodSummaryRequestData] = {

    taxYear match {
      case taxYearStr => new Def1_RetrievePeriodSummaryValidator(nino, businessId, periodId, taxYearStr)
    }

  }

}