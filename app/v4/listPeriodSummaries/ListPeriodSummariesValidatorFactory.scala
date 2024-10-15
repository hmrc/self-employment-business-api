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

import cats.data.Validated.{Invalid, Valid}
import shared.controllers.validators.Validator
import v4.listPeriodSummaries.ListPeriodSummariesSchema.{Def1, Def2}
import v4.listPeriodSummaries.def1.Def1_ListPeriodSummariesValidator
import v4.listPeriodSummaries.def2.Def2_ListPeriodSummariesValidator
import v4.listPeriodSummaries.model.request.ListPeriodSummariesRequestData

class ListPeriodSummariesValidatorFactory {

  def validator(nino: String, businessId: String, taxYear: String): Validator[ListPeriodSummariesRequestData] = {

    ListPeriodSummariesSchema.schemaFor(taxYear) match {
      case Valid(Def1)     => new Def1_ListPeriodSummariesValidator(nino, businessId, taxYear)
      case Valid(Def2)     => new Def2_ListPeriodSummariesValidator(nino, businessId, taxYear)
      case Invalid(errors) => Validator.returningErrors(errors)
    }

  }

}
