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

package v4.retrieveAnnualSubmission

import cats.data.Validated
import cats.data.Validated.Valid
import config.SeBusinessConfig
import play.api.libs.json.Reads
import shared.controllers.validators.resolvers.ResolveTaxYearMinimum
import shared.models.domain.TaxYear
import shared.models.errors.MtdError
import shared.schema.DownstreamReadable
import v4.retrieveAnnualSubmission.def1.model.response.Def1_RetrieveAnnualSubmissionResponse
import v4.retrieveAnnualSubmission.def2.model.response.Def2_RetrieveAnnualSubmissionResponse
import v4.retrieveAnnualSubmission.def3.model.response.Def3_RetrieveAnnualSubmissionResponse
import v4.retrieveAnnualSubmission.model.response.RetrieveAnnualSubmissionResponse

import scala.math.Ordered.orderingToOrdered

sealed trait RetrieveAnnualSubmissionSchema extends DownstreamReadable[RetrieveAnnualSubmissionResponse]

object RetrieveAnnualSubmissionSchema {

  case object Def1 extends RetrieveAnnualSubmissionSchema {
    type DownstreamResp = Def1_RetrieveAnnualSubmissionResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveAnnualSubmissionResponse.reads
  }

  case object Def2 extends RetrieveAnnualSubmissionSchema {
    type DownstreamResp = Def2_RetrieveAnnualSubmissionResponse
    val connectorReads: Reads[DownstreamResp] = Def2_RetrieveAnnualSubmissionResponse.reads
  }

  case object Def3 extends RetrieveAnnualSubmissionSchema {
    type DownstreamResp = Def3_RetrieveAnnualSubmissionResponse
    val connectorReads: Reads[DownstreamResp] = Def3_RetrieveAnnualSubmissionResponse.reads
  }

  private val resolveTaxYear =
    ResolveTaxYearMinimum(minimumTaxYear = SeBusinessConfig.minimumTaxYear)

  def schemaFor(taxYear: String): Validated[Seq[MtdError], RetrieveAnnualSubmissionSchema] =
    resolveTaxYear(taxYear) andThen schemaFor

  def schemaFor(taxYear: TaxYear): Validated[Seq[MtdError], RetrieveAnnualSubmissionSchema] =
    if (taxYear >= TaxYear.fromMtd("2025-26")) Valid(Def3)
    else if (taxYear == TaxYear.fromMtd("2024-25")) Valid(Def2)
    else Valid(Def1)

}
