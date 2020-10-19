/*
 * Copyright 2020 HM Revenue & Customs
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

import v1.controllers.requestParsers.validators.validations.{BusinessIdValidation, JsonFormatValidation, NinoValidation, NoValidationErrors}
import v1.models.errors.{MtdError, RuleIncorrectOrEmptyBodyError}

class CreateSelfEmploymentPeriodicUpdateValidator extends Validator[AmendSelfEmploymentPeriodicUpdateRawData] {

  private val validationSet = List(parameterFormatValidation, incorrectOrEmptyBodySubmittedValidation, bodyFieldValidation)

  private def parameterFormatValidation: AmendSelfEmploymentPeriodicUpdateRawData => List[List[MtdError]] = (data: AmendSelfEmploymentPeriodicUpdateRawData) => {
    List(
      NinoValidation.validate(data.nino),
      BusinessIdValidation.validate(data.taxYear),
      JsonFormatValidation.validate[AmendSelfEmploymentPeriodicUpdateRequestBody](data.body)
    )
  }

  private def incorrectOrEmptyBodySubmittedValidation: AmendSelfEmploymentPeriodicUpdateRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendEmploymentExpensesBody]
    if (body.isIncorrectOrEmptyBody) List(List(RuleIncorrectOrEmptyBodyError)) else NoValidationErrors
  }


  private def bodyFieldValidation: AmendSelfEmploymentPeriodicUpdateRawData => List[List[MtdError]] = { data =>
    val body = data.body.as[AmendOtherExpensesBody]

    List(flattenErrors(
      List(
        body.paymentsToTradeUnionsForDeathBenefits.map(validatePaymentsToTradeUnionsForDeathBenefits).getOrElse(NoValidationErrors),
        body.patentRoyaltiesPayments.map(validatePatentRoyaltiesPayments).getOrElse(NoValidationErrors)
      )
    ))
  }

  private def validatePaymentsToTradeUnionsForDeathBenefits(paymentsToTradeUnionsForDeathBenefits: PaymentsToTradeUnionsForDeathBenefits): List[MtdError] = {
    List(
      CustomerReferenceValidation.validateOptional(
        field = paymentsToTradeUnionsForDeathBenefits.customerReference,
        path = s"/paymentsToTradeUnionsForDeathBenefits/customerReference"
      ),
      NumberValidation.validateOptional(
        field = Some(paymentsToTradeUnionsForDeathBenefits.expenseAmount),
        path = s"/paymentsToTradeUnionsForDeathBenefits/expenseAmount"
      )
    ).flatten
  }

  private def validatePatentRoyaltiesPayments(patentRoyaltiesPayments: PatentRoyaltiesPayments): List[MtdError] = {
    List(
      CustomerReferenceValidation.validateOptional(
        field = patentRoyaltiesPayments.customerReference,
        path = s"/patentRoyaltiesPayments/customerReference"
      ),
      NumberValidation.validateOptional(
        field = Some(patentRoyaltiesPayments.expenseAmount),
        path = s"/patentRoyaltiesPayments/expenseAmount"
      )
    ).flatten
  }

  override def validate(data: AmendSelfEmploymentPeriodicUpdateRawData): List[MtdError] = {
    run(validationSet, data).distinct
  }
}
