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

package v3.createPeriodSummary

import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import play.api.libs.json.JsValue
import v3.createPeriodSummary.model.request.CreatePeriodSummaryRequestData

trait MockCreatePeriodSummaryValidatorFactory extends MockFactory {

  val mockCreatePeriodSummaryValidatorFactory: CreatePeriodSummaryValidatorFactory =
    mock[CreatePeriodSummaryValidatorFactory]

  object MockedCreatePeriodSummaryValidatorFactory {

    def validator(): CallHandler[Validator[CreatePeriodSummaryRequestData]] =
      (mockCreatePeriodSummaryValidatorFactory.validator(_: String, _: String, _: JsValue, _: Boolean)).expects(*, *, *, *)

  }

  def willUseValidator(use: Validator[CreatePeriodSummaryRequestData]): CallHandler[Validator[CreatePeriodSummaryRequestData]] = {
    MockedCreatePeriodSummaryValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: CreatePeriodSummaryRequestData): Validator[CreatePeriodSummaryRequestData] =
    new Validator[CreatePeriodSummaryRequestData] {
      def validate: Validated[Seq[MtdError], CreatePeriodSummaryRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[CreatePeriodSummaryRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[CreatePeriodSummaryRequestData] =
    new Validator[CreatePeriodSummaryRequestData] {
      def validate: Validated[Seq[MtdError], CreatePeriodSummaryRequestData] = Invalid(result)
    }

}
