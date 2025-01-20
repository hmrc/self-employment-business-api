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

package v5.amendPeriodSummary

import cats.data.Validated
import cats.data.Validated.Valid
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import shared.models.errors.MtdError

import scala.math.Ordered.orderingToOrdered

sealed trait AmendPeriodSummarySchema

object AmendPeriodSummarySchema {

  case object Def1 extends AmendPeriodSummarySchema
  case object Def2 extends AmendPeriodSummarySchema

  def schemaFor(taxYearString: String): Validated[Seq[MtdError], AmendPeriodSummarySchema] = {
    ResolveTaxYear(taxYearString) andThen schemaFor
  }

  def schemaFor(taxYear: TaxYear): Validated[Seq[MtdError], AmendPeriodSummarySchema] = {
    if (taxYear <= TaxYear.fromMtd("2022-23")) {
      Valid(Def1)
    } else {
      Valid(Def2)
    }
  }

}
