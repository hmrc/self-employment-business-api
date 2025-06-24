/*
 * Copyright 2024 HM Revenue & Customs
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

package v5.retrieveCumulativePeriodSummary

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import v5.retrieveCumulativePeriodSummary.model.request.RetrieveCumulativePeriodSummaryRequestData

trait MockRetrieveCumulativePeriodSummaryValidatorFactory extends TestSuite with MockFactory {

  val mockRetrieveCumulativePeriodSummaryValidatorFactory: RetrieveCumulativePeriodSummaryValidatorFactory =
    mock[RetrieveCumulativePeriodSummaryValidatorFactory]

  object MockedRetrieveCumulativePeriodSummaryValidatorFactory {

    def validator(): CallHandler[Validator[RetrieveCumulativePeriodSummaryRequestData]] =
      (mockRetrieveCumulativePeriodSummaryValidatorFactory.validator(_: String, _: String, _: String)).expects(*, *, *)

  }

  def willUseValidator(
      use: Validator[RetrieveCumulativePeriodSummaryRequestData]): CallHandler[Validator[RetrieveCumulativePeriodSummaryRequestData]] = {
    MockedRetrieveCumulativePeriodSummaryValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: RetrieveCumulativePeriodSummaryRequestData): Validator[RetrieveCumulativePeriodSummaryRequestData] =
    new Validator[RetrieveCumulativePeriodSummaryRequestData] {
      def validate: Validated[Seq[MtdError], RetrieveCumulativePeriodSummaryRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[RetrieveCumulativePeriodSummaryRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[RetrieveCumulativePeriodSummaryRequestData] =
    new Validator[RetrieveCumulativePeriodSummaryRequestData] {
      def validate: Validated[Seq[MtdError], RetrieveCumulativePeriodSummaryRequestData] = Invalid(result)
    }

}
