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

package v3.retrievePeriodSummary

import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import org.scalatest.TestSuite
import v3.retrievePeriodSummary.model.request.RetrievePeriodSummaryRequestData

trait MockRetrievePeriodSummaryValidatorFactory extends TestSuite with MockFactory {

  val mockRetrievePeriodSummaryValidatorFactory: RetrievePeriodSummaryValidatorFactory =
    mock[RetrievePeriodSummaryValidatorFactory]

  object MockedRetrievePeriodSummaryValidatorFactory {

    def validator(): CallHandler[Validator[RetrievePeriodSummaryRequestData]] =
      (mockRetrievePeriodSummaryValidatorFactory.validator(_: String, _: String, _: String, _: Option[String])).expects(*, *, *, *)

  }

  def willUseValidator(use: Validator[RetrievePeriodSummaryRequestData]): CallHandler[Validator[RetrievePeriodSummaryRequestData]] = {
    MockedRetrievePeriodSummaryValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: RetrievePeriodSummaryRequestData): Validator[RetrievePeriodSummaryRequestData] =
    new Validator[RetrievePeriodSummaryRequestData] {
      def validate: Validated[Seq[MtdError], RetrievePeriodSummaryRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[RetrievePeriodSummaryRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[RetrievePeriodSummaryRequestData] =
    new Validator[RetrievePeriodSummaryRequestData] {
      def validate: Validated[Seq[MtdError], RetrievePeriodSummaryRequestData] = Invalid(result)
    }

}
