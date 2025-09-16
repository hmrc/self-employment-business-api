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

package v4.retrieveCumulativePeriodSummary

import cats.implicits.*
import shared.controllers.RequestContext
import shared.models.errors.*
import shared.services.{BaseService, ServiceOutcome}
import v4.retrieveCumulativePeriodSummary.model.request.RetrieveCumulativePeriodSummaryRequestData
import v4.retrieveCumulativePeriodSummary.model.response.RetrieveCumulativePeriodSummaryResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveCumulativePeriodSummaryService @Inject() (
    connector: RetrieveCumulativePeriodSummaryConnector
) extends BaseService {

  def retrieveCumulativePeriodSummary(request: RetrieveCumulativePeriodSummaryRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext
  ): Future[ServiceOutcome[RetrieveCumulativePeriodSummaryResponse]] = {

    connector.retrieveCumulativePeriodSummary(request).map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))
  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_NINO"             -> NinoFormatError,
      "INVALID_TAX_YEAR"         -> TaxYearFormatError,
      "INVALID_INCOME_SOURCE_ID" -> BusinessIdFormatError,
      "INVALID_CORRELATION_ID"   -> InternalError,
      "NOT_FOUND"                -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED"   -> RuleTaxYearNotSupportedError,
      "SERVER_ERROR"             -> InternalError,
      "SERVICE_UNAVAILABLE"      -> InternalError
    )
    errors

  }

}
