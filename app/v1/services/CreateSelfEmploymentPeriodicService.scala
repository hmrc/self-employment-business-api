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

import cats.data.EitherT
import cats.implicits._
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.connectors.CreateSelfEmploymentPeriodicConnector
import v1.controllers.EndpointLogContext
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.createSEPeriodic.CreateSelfEmploymentPeriodicRequest
import v1.models.response.createSEPeriodic.CreateSelfEmploymentPeriodicResponse
import v1.support.DesResponseMappingSupport

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

@Singleton
class CreateSelfEmploymentPeriodicService @Inject()(connector: CreateSelfEmploymentPeriodicConnector)
    extends DesResponseMappingSupport
    with BaseService[CreateSelfEmploymentPeriodicRequest, CreateSelfEmploymentPeriodicResponse]
    with Logging {

  override def doService(request: CreateSelfEmploymentPeriodicRequest)(
      implicit hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[CreateSelfEmploymentPeriodicResponse]]] = {
    val result = for {
      desResponseWrapper <- EitherT(connector.createPeriodic(request)).leftMap(mapDesErrors(desErrorMap))
      mtdResponseWrapper <- EitherT.fromEither[Future](createPeriodId(desResponseWrapper, request.body.periodFromDate, request.body.periodToDate))
    } yield mtdResponseWrapper

    result.value
  }

  private def desErrorMap: Map[String, MtdError] = Map(
    "INVALID_NINO"                    -> NinoFormatError,
    "INVALID_INCOME_SOURCE"           -> BusinessIdFormatError,
    "INVALID_PERIOD"                  -> RuleToDateBeforeFromDateError,
    "OVERLAPS_IN_PERIOD"              -> RuleOverlappingPeriod,
    "NOT_ALIGN_PERIOD"                -> RuleMisalignedPeriod,
    "BOTH_EXPENSES_SUPPLIED"          -> RuleBothExpensesSuppliedError,
    "NOT_CONTIGUOUS_PERIOD"           -> RuleNotContiguousPeriod,
    "NOT_ALLOWED_SIMPLIFIED_EXPENSES" -> RuleNotAllowedConsolidatedExpenses,
    "NOT_FOUND_INCOME_SOURCE"         -> NotFoundError,
    "SERVER_ERROR"                    -> DownstreamError,
    "SERVICE_UNAVAILABLE"             -> DownstreamError
  )
}
