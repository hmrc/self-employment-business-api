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

package v4.createAmendCumulativeSubmission

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import v4.createAmendCumulativeSubmission.model.request.CreateAmendCumulativeSubmissionRequestData

trait MockCreateAmendCumulativeSubmissionValidatorFactory extends MockFactory {

  val mockAmendCumulativeSubmissionValidatorFactory: CreateAmendCumulativeSubmissionValidatorFactory =
    mock[CreateAmendCumulativeSubmissionValidatorFactory]

  object MockedCreateAmendCumulativeSubmissionValidatorFactory {

    def validator(): CallHandler[Validator[CreateAmendCumulativeSubmissionRequestData]] =
      (mockAmendCumulativeSubmissionValidatorFactory.validator(_: String, _: String, _: String, _: JsValue)).expects(*, *, *, *)

  }

  def willUseValidator(
      use: Validator[CreateAmendCumulativeSubmissionRequestData]): CallHandler[Validator[CreateAmendCumulativeSubmissionRequestData]] = {
    MockedCreateAmendCumulativeSubmissionValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: CreateAmendCumulativeSubmissionRequestData): Validator[CreateAmendCumulativeSubmissionRequestData] =
    new Validator[CreateAmendCumulativeSubmissionRequestData] {
      def validate: Validated[Seq[MtdError], CreateAmendCumulativeSubmissionRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[CreateAmendCumulativeSubmissionRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[CreateAmendCumulativeSubmissionRequestData] =
    new Validator[CreateAmendCumulativeSubmissionRequestData] {
      def validate: Validated[Seq[MtdError], CreateAmendCumulativeSubmissionRequestData] = Invalid(result)
    }

}
