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

package v1.services

import api.controllers.RequestContext
import api.models.errors._
import api.services.{BaseService, RetrievePeriodSummaryServiceOutcome}
import cats.implicits._
import v1.connectors.RetrievePeriodSummaryConnector
import v1.models.request.retrievePeriodSummary.RetrievePeriodSummaryRequest

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrievePeriodSummaryService @Inject() (connector: RetrievePeriodSummaryConnector) extends BaseService {

  def retrievePeriodSummary(
      request: RetrievePeriodSummaryRequest)(implicit ctx: RequestContext, ec: ExecutionContext): Future[RetrievePeriodSummaryServiceOutcome] = {

    connector.retrievePeriodSummary(request).map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))

  }

  private def downstreamErrorMap: Map[String, MtdError] = {
    val downstreamErrors = Map(
      "INVALID_NINO"            -> NinoFormatError,
      "INVALID_INCOMESOURCEID"  -> BusinessIdFormatError,
      "INVALID_DATE_FROM"       -> PeriodIdFormatError,
      "INVALID_DATE_TO"         -> PeriodIdFormatError,
      "NOT_FOUND_INCOME_SOURCE" -> NotFoundError,
      "NOT_FOUND_PERIOD"        -> NotFoundError,
      "SERVER_ERROR"            -> InternalError,
      "SERVICE_UNAVAILABLE"     -> InternalError
    )
    val extraTysErrors = Map(
      "INVALID_TAX_YEAR"             -> TaxYearFormatError,
      "INVALID_INCOMESOURCE_ID"      -> BusinessIdFormatError,
      "INCOME_DATA_SOURCE_NOT_FOUND" -> NotFoundError,
      "INVALID_CORRELATION_ID"       -> InternalError,
      "SUBMISSION_DATA_NOT_FOUND"    -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED"       -> RuleTaxYearNotSupportedError
    )
    downstreamErrors ++ extraTysErrors
  }

}
