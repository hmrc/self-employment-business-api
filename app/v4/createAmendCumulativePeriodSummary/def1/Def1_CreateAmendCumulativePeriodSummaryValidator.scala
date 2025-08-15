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

package v4.createAmendCumulativePeriodSummary.def1

import cats.data.Validated
import cats.data.Validated.{Invalid, Valid}
import cats.implicits.catsSyntaxTuple4Semigroupal
import play.api.libs.json.JsValue
import shared.controllers.validators.Validator
import shared.controllers.validators.resolvers._
import shared.models.domain.TaxYear
import shared.models.errors.{MtdError, RuleIncorrectOrEmptyBodyError}
import v4.createAmendCumulativePeriodSummary.model.request._

class Def1_CreateAmendCumulativePeriodSummaryValidator(
    nino: String,
    businessId: String,
    taxYear: TaxYear,
    body: JsValue
) extends Validator[CreateAmendCumulativePeriodSummaryRequestData] {

  private val resolveJson = new ResolveNonEmptyJsonObject[Def1_CreateAmendCumulativePeriodSummaryRequestBody]()

  private def validateMinimumFields(body: CreateAmendCumulativePeriodSummaryRequestBody): Validated[Seq[MtdError], Unit] = {
    val hasDates: Boolean = body.periodDates.isDefined
    val hasIncome: Boolean = body.periodIncome.isDefined
    val hasExpenses: Boolean = body.periodExpenses.isDefined || body.periodDisallowableExpenses.isDefined

    val nonEmptyFieldsCount: Int = List(hasDates, hasIncome, hasExpenses).count(identity)

    if (nonEmptyFieldsCount >= 2) Valid(()) else Invalid(List(RuleIncorrectOrEmptyBodyError.copy(
      message = "At least two of periodDates, periodIncome, and expenses (periodExpenses and periodDisallowableExpenses) must be supplied." +
        "Zero values can be submitted when there is no income or expenses"
    )))
  }

  private val rulesValidator = Def1_CreateAmendCumulativePeriodSummaryRulesValidator(taxYear)

  def validate: Validated[Seq[MtdError], CreateAmendCumulativePeriodSummaryRequestData] =
    (
      ResolveNino(nino),
      ResolveBusinessId(businessId),
      Valid(taxYear),
      resolveJson(body)
    ).mapN(Def1_CreateAmendCumulativePeriodSummaryRequestData).andThen { parsedRequestData =>
      validateMinimumFields(parsedRequestData.body).andThen(_ => rulesValidator.validateBusinessRules(parsedRequestData))
    }

}
