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
import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import shared.models.errors.{InvalidTaxYearParameterError, MtdError}
import shared.schema.DownstreamReadable
import v4.listPeriodSummaries.def2.model.response.{Def2_ListPeriodSummariesResponse, Def2_PeriodDetails}
import v4.listPeriodSummaries.model.response.{ListPeriodSummariesResponse, PeriodDetails}

import scala.math.Ordered.orderingToOrdered

sealed trait ListPeriodSummariesSchema extends DownstreamReadable[ListPeriodSummariesResponse[PeriodDetails]]

object ListPeriodSummariesSchema {

  case object Def1 extends ListPeriodSummariesSchema {
    type DownstreamResp = Def2_ListPeriodSummariesResponse[Def2_PeriodDetails]
    val connectorReads: Reads[DownstreamResp] = Def2_ListPeriodSummariesResponse.reads
  }

  case object Def2 extends ListPeriodSummariesSchema {
    type DownstreamResp = Def2_ListPeriodSummariesResponse[Def2_PeriodDetails]
    val connectorReads: Reads[DownstreamResp] = Def2_ListPeriodSummariesResponse.reads
  }

  def schemaFor(taxYearString: String): Validated[Seq[MtdError], ListPeriodSummariesSchema] = {
    ResolveTaxYear(taxYearString) andThen schemaFor
  }

  def schemaFor(taxYear: TaxYear): Validated[Seq[MtdError], ListPeriodSummariesSchema] = {
    if (taxYear <= TaxYear.fromMtd("2022-23")) Valid(Def1)
    else if (taxYear == TaxYear.fromMtd("2023-24")) {
      Valid(Def2)
    } else {
      Invalid(Seq(InvalidTaxYearParameterError))
    }
  }

}
