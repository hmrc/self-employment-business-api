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

package v3.listPeriodSummaries

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import shared.controllers.validators.Validator
import shared.models.errors.MtdError
import v3.listPeriodSummaries.model.listPeriodSummaries.Def1_ListPeriodSummariesRequestData

trait MockListPeriodSummariesValidatorFactory extends MockFactory {

  val mockListPeriodSummariesValidatorFactory: ListPeriodSummariesValidatorFactory = mock[ListPeriodSummariesValidatorFactory]

  object MockListPeriodSummariesValidatorFactory {

    def validator(): CallHandler[Validator[Def1_ListPeriodSummariesRequestData]] =
      (mockListPeriodSummariesValidatorFactory.validator(_: String, _: String, _: Option[String])).expects(*, *, *)

  }

  def willUseValidator(use: Validator[Def1_ListPeriodSummariesRequestData]): CallHandler[Validator[Def1_ListPeriodSummariesRequestData]] = {
    MockListPeriodSummariesValidatorFactory
      .validator()
      .anyNumberOfTimes()
      .returns(use)
  }

  def returningSuccess(result: Def1_ListPeriodSummariesRequestData): Validator[Def1_ListPeriodSummariesRequestData] =
    new Validator[Def1_ListPeriodSummariesRequestData] {
      def validate: Validated[Seq[MtdError], Def1_ListPeriodSummariesRequestData] = Valid(result)
    }

  def returning(result: MtdError*): Validator[Def1_ListPeriodSummariesRequestData] = returningErrors(result)

  def returningErrors(result: Seq[MtdError]): Validator[Def1_ListPeriodSummariesRequestData] =
    new Validator[Def1_ListPeriodSummariesRequestData] {
      def validate: Validated[Seq[MtdError], Def1_ListPeriodSummariesRequestData] = Invalid(result)
    }

}
