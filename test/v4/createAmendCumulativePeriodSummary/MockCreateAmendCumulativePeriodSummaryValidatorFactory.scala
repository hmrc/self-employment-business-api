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

package v4.createAmendCumulativePeriodSummary

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import v4.createAmendCumulativePeriodSummary.model.request.CreateAmendCumulativePeriodSummaryRequestData

trait MockCreateAmendCumulativePeriodSummaryValidatorFactory extends MockFactory {

  val mockAmendCumulativePeriodSummaryValidatorFactory: CreateAmendCumulativePeriodSummaryValidatorFactory =
    mock[CreateAmendCumulativePeriodSummaryValidatorFactory]

  object MockedCreateAmendCumulativePeriodSummaryValidatorFactory {

    def validator(): CallHandler[Validator[CreateAmendCumulativePeriodSummaryRequestData]] =
      (mockAmendCumulativePeriodSummaryValidatorFactory.validator(_: String, _: String, _: String, _: JsValue)).expects(*, *, *, *)

  }

  def willUseValidator(
      use: Validator[CreateAmendCumulativePeriodSummaryRequestData]): CallHandler[Validator[CreateAmendCumulativePeriodSummaryRequestData]] = {
    MockedCreateAmendCumulativePeriodSummaryValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: CreateAmendCumulativePeriodSummaryRequestData): Validator[CreateAmendCumulativePeriodSummaryRequestData] =
    new Validator[CreateAmendCumulativePeriodSummaryRequestData] {
      def validate: Validated[Seq[MtdError], CreateAmendCumulativePeriodSummaryRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[CreateAmendCumulativePeriodSummaryRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[CreateAmendCumulativePeriodSummaryRequestData] =
    new Validator[CreateAmendCumulativePeriodSummaryRequestData] {
      def validate: Validated[Seq[MtdError], CreateAmendCumulativePeriodSummaryRequestData] = Invalid(result)
    }

}