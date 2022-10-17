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

import v1.controllers.requestParsers.validators.validations._
import v1.models.errors.MtdError
import v1.models.request.listPeriodSummaries.ListPeriodSummariesRawData

class ListPeriodSummariesValidator extends Validator[ListPeriodSummariesRawData] {

  private val validationSet = List(parameterFormatValidation, parameterRuleValidation)

  override def validate(data: ListPeriodSummariesRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }

  private def parameterFormatValidation: ListPeriodSummariesRawData => List[List[MtdError]] = (data: ListPeriodSummariesRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.businessId),
      data.taxYear.map(TaxYearValidation.validate) getOrElse (Nil)
    )
  }

  private def parameterRuleValidation: ListPeriodSummariesRawData => List[List[MtdError]] = (data: ListPeriodSummariesRawData) => {

    val year = data.taxYear match {
      case Some(value) => value.dropRight(3).toInt
      case None        => 0
    }

    if (year < 2023) {
      List(
        data.taxYear.map(TaxYearNotSupportedValidation.validate).getOrElse(Nil),
      )
    } else {
      List(
        data.taxYear.map(TaxYearNotSupportedValidation.validate).getOrElse(Nil),
        data.taxYear.map(TaxYearTYSParameterValidation.validate).getOrElse(Nil)
      )
    }
  }
}
