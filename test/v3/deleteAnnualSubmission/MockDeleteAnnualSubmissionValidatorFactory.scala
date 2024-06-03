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

package v3.deleteAnnualSubmission

import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import v3.deleteAnnualSubmission.model.DeleteAnnualSubmissionRequestData

trait MockDeleteAnnualSubmissionValidatorFactory extends MockFactory {

  val mockDeleteAnnualSubmissionValidatorFactory: DeleteAnnualSubmissionValidatorFactory = mock[DeleteAnnualSubmissionValidatorFactory]

  object MockedDeleteAnnualSubmissionValidatorFactory {

    def validator(): CallHandler[Validator[DeleteAnnualSubmissionRequestData]] =
      (mockDeleteAnnualSubmissionValidatorFactory.validator(_: String, _: String, _: String)).expects(*, *, *)

  }

  def willUseValidator(use: Validator[DeleteAnnualSubmissionRequestData]): CallHandler[Validator[DeleteAnnualSubmissionRequestData]] = {
    MockedDeleteAnnualSubmissionValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: DeleteAnnualSubmissionRequestData): Validator[DeleteAnnualSubmissionRequestData] =
    new Validator[DeleteAnnualSubmissionRequestData] {
      def validate: Validated[Seq[MtdError], DeleteAnnualSubmissionRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[DeleteAnnualSubmissionRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[DeleteAnnualSubmissionRequestData] =
    new Validator[DeleteAnnualSubmissionRequestData] {
      def validate: Validated[Seq[MtdError], DeleteAnnualSubmissionRequestData] = Invalid(result)
    }

}
