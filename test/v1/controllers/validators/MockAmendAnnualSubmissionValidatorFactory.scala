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

package v1.controllers.validators

import api.controllers.validators.Validator
import api.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.JsValue
import v1.models.request.amendSEAnnual.AmendAnnualSubmissionRequestData

trait MockAmendAnnualSubmissionValidatorFactory extends MockFactory {

  val mockAmendAnnualSubmissionValidatorFactory: AmendAnnualSubmissionValidatorFactory = mock[AmendAnnualSubmissionValidatorFactory]

  object MockAmendAnnualSubmissionValidatorFactory {

    def validator(): CallHandler[Validator[AmendAnnualSubmissionRequestData]] =
      (mockAmendAnnualSubmissionValidatorFactory.validator(_: String, _: String, _: String, _: JsValue)).expects(*, *, *, *)

  }

  def willUseValidator(use: Validator[AmendAnnualSubmissionRequestData]): CallHandler[Validator[AmendAnnualSubmissionRequestData]] = {
    MockAmendAnnualSubmissionValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: AmendAnnualSubmissionRequestData): Validator[AmendAnnualSubmissionRequestData] =
    new Validator[AmendAnnualSubmissionRequestData] {
      def validate: Validated[Seq[MtdError], AmendAnnualSubmissionRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[AmendAnnualSubmissionRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[AmendAnnualSubmissionRequestData] =
    new Validator[AmendAnnualSubmissionRequestData] {
      def validate: Validated[Seq[MtdError], AmendAnnualSubmissionRequestData] = Invalid(result)
    }

}
