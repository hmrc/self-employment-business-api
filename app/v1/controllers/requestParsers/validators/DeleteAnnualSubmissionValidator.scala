/*
 * Copyright 2022 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators

import v1.controllers.requestParsers.validators.validations.{BusinessIdValidation, NinoValidation, TaxYearNotSupportedValidation, TaxYearValidation}
import v1.models.errors.MtdError
import v1.models.request.deleteAnnual.DeleteAnnualSubmissionRawData

class DeleteAnnualSubmissionValidator extends Validator[DeleteAnnualSubmissionRawData] {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation)

  override def validate(data: DeleteAnnualSubmissionRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

  private def parameterFormatValidation: DeleteAnnualSubmissionRawData => List[List[MtdError]] = (data: DeleteAnnualSubmissionRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.businessId),
      TaxYearValidation.validate(data.taxYear)
    )
  }

  private def parameterRuleValidation: DeleteAnnualSubmissionRawData => List[List[MtdError]] = (data: DeleteAnnualSubmissionRawData) => {
    List(
      TaxYearNotSupportedValidation.validate(data.taxYear)
    )
  }
}