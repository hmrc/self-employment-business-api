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

package v4.listPeriodSummaries

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import v4.listPeriodSummaries.model.request.ListPeriodSummariesRequestData

trait MockListPeriodSummariesValidatorFactory extends MockFactory {

  val mockListPeriodSummariesValidatorFactory: ListPeriodSummariesValidatorFactory = mock[ListPeriodSummariesValidatorFactory]

  object MockListPeriodSummariesValidatorFactory {

    def validator(): CallHandler[Validator[ListPeriodSummariesRequestData]] =
      (mockListPeriodSummariesValidatorFactory.validator(_: String, _: String, _: Option[String])).expects(*, *, *)

  }

  def willUseValidator(use: Validator[ListPeriodSummariesRequestData]): CallHandler[Validator[ListPeriodSummariesRequestData]] = {
    MockListPeriodSummariesValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: ListPeriodSummariesRequestData): Validator[ListPeriodSummariesRequestData] =
    new Validator[ListPeriodSummariesRequestData] {
      def validate: Validated[Seq[MtdError], ListPeriodSummariesRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[ListPeriodSummariesRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[ListPeriodSummariesRequestData] =
    new Validator[ListPeriodSummariesRequestData] {
      def validate: Validated[Seq[MtdError], ListPeriodSummariesRequestData] = Invalid(result)
    }

}
