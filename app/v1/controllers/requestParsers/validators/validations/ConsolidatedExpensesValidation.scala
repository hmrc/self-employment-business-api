/*
 * Copyright 2022 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators.validations

import v1.models.errors.{MtdError, RuleBothExpensesSuppliedError}
import v1.models.request.amendPeriodic.{ConsolidatedExpenses, Expenses}

object ConsolidatedExpensesValidation {

  def validate(consolidatedExpenses: Option[ConsolidatedExpenses], expenses: Option[Expenses]): List[MtdError] = {
    (consolidatedExpenses, expenses) match {
      case (Some(_), Some(_)) => List(RuleBothExpensesSuppliedError)
      case _                  => NoValidationErrors
    }
  }

}
