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
import api.controllers.validators.resolvers.{ResolveBusinessId, ResolveDateRange, ResolveIsoDate, ResolveNino, ResolveNonEmptyJsonObject}
import api.models.errors.{EndDateFormatError, MtdError, RuleBothExpensesSuppliedError, StartDateFormatError}
import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.{catsSyntaxTuple3Semigroupal, toFoldableOps}
import play.api.libs.json.JsValue
import v1.models.request.createPeriodSummary.{
  CreatePeriodSummaryBody,
  CreatePeriodSummaryRequest,
  PeriodAllowableExpenses,
  PeriodDisallowableExpenses
}

import javax.inject.Singleton
import scala.annotation.nowarn

@Singleton
class CreatePeriodSummaryValidatorFactory {

  private val minYear = 1900
  private val maxYear = 2100

  @nowarn("cat=lint-byname-implicit")
  private val resolveJson = new ResolveNonEmptyJsonObject[CreatePeriodSummaryBody]()

  private val valid = Valid(())

  def validator(nino: String, businessId: String, body: JsValue): Validator[CreatePeriodSummaryRequest] =
    new Validator[CreatePeriodSummaryRequest] {

      def validate: Validated[Seq[MtdError], CreatePeriodSummaryRequest] =
        validateJsonFields(body) andThen
          (resolvedBody =>
            (
              ResolveNino(nino),
              ResolveBusinessId(businessId),
              Valid(resolvedBody)
            ).mapN(CreatePeriodSummaryRequest)) andThen validateBusinessRules

      private def validateJsonFields(body: JsValue): Validated[Seq[MtdError], CreatePeriodSummaryBody] =
        resolveJson(body) andThen (parsedBody =>
          List(
            ResolveIsoDate(parsedBody.periodDates.periodStartDate, StartDateFormatError),
            ResolveIsoDate(parsedBody.periodDates.periodEndDate, EndDateFormatError)
          ).traverse_(identity).map(_ => parsedBody))

      private def validateBusinessRules(parsed: CreatePeriodSummaryRequest): Validated[Seq[MtdError], CreatePeriodSummaryRequest] = {
        import parsed.body._

        val validatedConsolidatedExpenses = validateExpenses(periodAllowableExpenses, periodDisallowableExpenses)
        val validatedDates                = validateDates(periodDates.periodStartDate, periodDates.periodEndDate)

        List(
          validatedConsolidatedExpenses,
          validatedDates
        ).traverse_(identity).map(_ => parsed)
      }

    }

  private def validateExpenses(allowableExpenses: Option[PeriodAllowableExpenses],
                               disallowableExpenses: Option[PeriodDisallowableExpenses]): Validated[Seq[MtdError], Unit] =
    (allowableExpenses, disallowableExpenses) match {
      case (Some(allowable), Some(_)) if allowable.consolidatedExpenses.isDefined => Invalid(List(RuleBothExpensesSuppliedError))
      case (Some(allowable), None) if allowable.consolidatedExpenses.isDefined =>
        allowable match {
          case PeriodAllowableExpenses(Some(_), None, None, None, None, None, None, None, None, None, None, None, None, None, None, None) =>
            valid
          case _ => Invalid(List(RuleBothExpensesSuppliedError))
        }
      case _ => valid
    }

  private def validateDates(periodStartDate: String, periodEndDate: String): Validated[Seq[MtdError], Unit] =
    ResolveDateRange.withLimits(minYear, maxYear)(periodStartDate -> periodEndDate).map(_ => ())

}
