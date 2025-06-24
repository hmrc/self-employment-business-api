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

package v4.retrieveAnnualSubmission

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import config.MockSeBusinessConfig
import org.scalatest.TestSuite
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import v4.retrieveAnnualSubmission.model.request.RetrieveAnnualSubmissionRequestData

trait MockRetrieveAnnualSubmissionValidatorFactory extends TestSuite with MockFactory with MockSeBusinessConfig {

  val mockRetrieveAnnualSubmissionValidatorFactory: RetrieveAnnualSubmissionValidatorFactory = mock[RetrieveAnnualSubmissionValidatorFactory]

  object MockedRetrieveAnnualSubmissionValidatorFactory {

    def validator(): CallHandler[Validator[RetrieveAnnualSubmissionRequestData]] =
      (mockRetrieveAnnualSubmissionValidatorFactory.validator(_: String, _: String, _: String)).expects(*, *, *)

  }

  def willUseValidator(use: Validator[RetrieveAnnualSubmissionRequestData]): CallHandler[Validator[RetrieveAnnualSubmissionRequestData]] = {
    MockedRetrieveAnnualSubmissionValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: RetrieveAnnualSubmissionRequestData): Validator[RetrieveAnnualSubmissionRequestData] =
    new Validator[RetrieveAnnualSubmissionRequestData] {
      def validate: Validated[Seq[MtdError], RetrieveAnnualSubmissionRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[RetrieveAnnualSubmissionRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[RetrieveAnnualSubmissionRequestData] =
    new Validator[RetrieveAnnualSubmissionRequestData] {
      def validate: Validated[Seq[MtdError], RetrieveAnnualSubmissionRequestData] = Invalid(result)
    }

}
