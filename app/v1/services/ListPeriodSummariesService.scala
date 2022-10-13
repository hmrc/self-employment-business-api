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

package v1.services

import cats.implicits._
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.connectors.ListPeriodSummariesConnector
import v1.controllers.EndpointLogContext
import v1.models.errors._
import v1.models.request.listPeriodSummaries.ListPeriodSummariesRequest
import v1.models.response.listPeriodSummaries.{ListPeriodSummariesResponse, PeriodDetails}
import v1.support.DownstreamResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListPeriodSummariesService @Inject() (connector: ListPeriodSummariesConnector) extends DownstreamResponseMappingSupport with Logging {

  def listPeriodSummaries(request: ListPeriodSummariesRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[ServiceOutcome[ListPeriodSummariesResponse[PeriodDetails]]] = {

    connector.listPeriodSummaries(request).map(_.leftMap(mapDownstreamErrors(errorMap)))
  }

  private def errorMap = {
    val errors =
      Map(
        "INVALID_NINO"            -> NinoFormatError,
        "INVALID_INCOME_SOURCEID" -> BusinessIdFormatError,
        "NOT_FOUND_INCOME_SOURCE" -> NotFoundError,
        "SERVER_ERROR"            -> InternalError,
        "SERVICE_UNAVAILABLE"     -> InternalError
      )
    val extraTysErrors = Map(
      "INVALID_TAX_YEAR"            -> TaxYearFormatError,
      "INVALID_INCOMESOURCE_ID"     -> BusinessIdFormatError,
      "INVALID_CORRELATION_ID"      -> InternalError,
      "TAX_YEAR_NOT_SUPPORTED"      -> RuleTaxYearNotSupportedError
    )
    errors ++ extraTysErrors
  }

}
