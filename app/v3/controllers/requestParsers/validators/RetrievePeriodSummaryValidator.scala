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

package v3.controllers.requestParsers.validators

import api.controllers.requestParsers.validators.Validator
import api.controllers.requestParsers.validators.validations._
import api.models.errors.MtdError
import v3.models.request.retrievePeriodSummary.RetrievePeriodSummaryRawData

class RetrievePeriodSummaryValidator extends Validator[RetrievePeriodSummaryRawData] {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation)

  override def validate(data: RetrievePeriodSummaryRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

  private def parameterFormatValidation: RetrievePeriodSummaryRawData => List[List[MtdError]] = (data: RetrievePeriodSummaryRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.businessId),
      PeriodIdValidation.validate(data.periodId),
      data.taxYear.map(TaxYearValidation.validate).getOrElse(Nil)
    )
  }

  private def parameterRuleValidation: RetrievePeriodSummaryRawData => List[List[MtdError]] = (data: RetrievePeriodSummaryRawData) => {
    List(
      data.taxYear.map(TaxYearTYSParameterValidation.validate).getOrElse(Nil)
    )
  }

}
