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

package v2.controllers.requestParsers.validators.validations

import anyVersion.models.request.createPeriodSummary.PeriodDisallowableExpenses
import api.controllers.requestParsers.validators.validations.NoValidationErrors
import api.models.errors.{MtdError, RuleBothExpensesSuppliedError}
import v2.models.request.createPeriodSummary.PeriodExpenses

object ConsolidatedExpensesValidation {

  def validate(Expenses: Option[PeriodExpenses], disallowableExpenses: Option[PeriodDisallowableExpenses]): List[MtdError] = {
    (Expenses, disallowableExpenses) match {
      case (Some(allowable), Some(_)) => if (allowable.consolidatedExpenses.isDefined) List(RuleBothExpensesSuppliedError) else NoValidationErrors
      case (Some(allowable), None) =>
        allowable.consolidatedExpenses match {
          case None => NoValidationErrors
          case Some(_) =>
            Expenses match {
              case Some(PeriodExpenses(Some(_), None, None, None, None, None, None, None, None, None, None, None, None, None, None, None)) =>
                NoValidationErrors
              case _ => List(RuleBothExpensesSuppliedError)
            }
        }

      case (_, _) => NoValidationErrors
    }
  }

}