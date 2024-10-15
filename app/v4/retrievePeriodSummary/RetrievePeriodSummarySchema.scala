/*
 * Copyright 2024 HM Revenue & Customs
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

package v4.retrievePeriodSummary

import cats.data.Validated
import cats.data.Validated.Valid
import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYear
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import shared.schema.DownstreamReadable
import v4.retrievePeriodSummary.model.response.{Def1_RetrievePeriodSummaryResponse, Def2_RetrievePeriodSummaryResponse, RetrievePeriodSummaryResponse}

import scala.math.Ordered.orderingToOrdered

sealed trait RetrievePeriodSummarySchema extends DownstreamReadable[RetrievePeriodSummaryResponse]

object RetrievePeriodSummarySchema {

  case object Def1 extends RetrievePeriodSummarySchema {
    type DownstreamResp = Def1_RetrievePeriodSummaryResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrievePeriodSummaryResponse.reads
  }

  case object Def2 extends RetrievePeriodSummarySchema {
    type DownstreamResp = Def2_RetrievePeriodSummaryResponse
    val connectorReads: Reads[DownstreamResp] = Def2_RetrievePeriodSummaryResponse.reads
  }

  def schemaFor(taxYearString: String): Validated[Seq[MtdError], RetrievePeriodSummarySchema] = {
    ResolveTaxYear(taxYearString) andThen schemaFor
  }

  def schemaFor(taxYear: TaxYear): Validated[Seq[MtdError], RetrievePeriodSummarySchema] = {
    if (taxYear <= TaxYear.fromMtd("2022-23")) Valid(Def1)
    else Valid(Def2)
  }

}
