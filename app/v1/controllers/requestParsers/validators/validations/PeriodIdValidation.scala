/*
 * Copyright 2020 HM Revenue & Customs
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

import v1.models.errors.{MtdError, PeriodIdFormatError}

object PeriodIdValidation {

  private val periodIdRegex = "20[1-9][0-9]\\-[0-9][0-2]\\-(0[1-9]|[12][0-9]|3[01])\\_20[1-9][0-9]\\-[0-9][0-2]\\-(0[1-9]|[12][0-9]|3[01])"

  def validate(periodId: String): List[MtdError] = {
    if (periodId.matches(periodIdRegex)) NoValidationErrors else List(PeriodIdFormatError)
  }

}
