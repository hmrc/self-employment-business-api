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

import cats.implicits.*
import shared.controllers.RequestContext
import shared.models.errors.*
import shared.services.{BaseService, ServiceOutcome}
import v4.listPeriodSummaries.model.request.ListPeriodSummariesRequestData
import v4.listPeriodSummaries.model.response.{ListPeriodSummariesResponse, PeriodDetails}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListPeriodSummariesService @Inject() (connector: ListPeriodSummariesConnector) extends BaseService {

  def listPeriodSummaries(request: ListPeriodSummariesRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[ListPeriodSummariesResponse[PeriodDetails]]] = {
    connector.listPeriodSummaries(request).map(_.leftMap(mapDownstreamErrors(errorMap)))
  }

  private val errorMap: Map[String, MtdError] = {
    val errors =
      Map(
        "INVALID_NINO"             -> NinoFormatError,
        "INVALID_INCOME_SOURCEID"  -> BusinessIdFormatError,
        "INVALID_INCOME_SOURCE_ID" -> BusinessIdFormatError,
        "NOT_FOUND"                -> NotFoundError,
        "NOT_FOUND_INCOME_SOURCE"  -> NotFoundError,
        "SERVER_ERROR"             -> InternalError,
        "SERVICE_UNAVAILABLE"      -> InternalError
      )
    val extraTysErrors = Map(
      "INVALID_TAX_YEAR"        -> TaxYearFormatError,
      "INVALID_INCOMESOURCE_ID" -> BusinessIdFormatError,
      "TAX_YEAR_NOT_SUPPORTED"  -> RuleTaxYearNotSupportedError
    )
    errors ++ extraTysErrors
  }

}
