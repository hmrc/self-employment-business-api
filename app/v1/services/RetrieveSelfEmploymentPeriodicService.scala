/*
 * Copyright 2020 HM Revenue & Customs
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
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.connectors.RetrieveSelfEmploymentPeriodicConnector
import v1.controllers.EndpointLogContext
import v1.models.errors.{BusinessIdFormatError, DownstreamError, NinoFormatError, NotFoundError, PeriodIdFormatError}
import v1.models.request.retrieveSEPeriodic.RetrieveSelfEmploymentPeriodicRequest
import v1.support.DesResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveSelfEmploymentPeriodicService @Inject()(retrieveSelfEmploymentPeriodicUpdateConnector: RetrieveSelfEmploymentPeriodicConnector)
  extends DesResponseMappingSupport with Logging {

  def retrieveSelfEmploymentPeriodicUpdate(request: RetrieveSelfEmploymentPeriodicRequest)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    logContext: EndpointLogContext): Future[RetrieveSelfEmploymentPeriodicUpdateServiceOutcome] = {

    val result = for {
      desResponseWrapper <- EitherT(retrieveSelfEmploymentPeriodicUpdateConnector.retrieveSEAnnual(request)).leftMap(mapDesErrors(desErrorMap))
    } yield desResponseWrapper

    result.value
  }

  private def desErrorMap =
    Map(
      "INVALID_NINO" -> NinoFormatError,
      "INVALID_INCOMESOURCEID" -> BusinessIdFormatError,
      "INVALID_DATE_FROM" -> PeriodIdFormatError,
      "INVALID_DATE_TO" -> PeriodIdFormatError,
      "NOT_FOUND_INCOME_SOURCE" -> NotFoundError,
      "NOT_FOUND_PERIOD" -> NotFoundError,
      "SERVER_ERROR" -> DownstreamError,
      "SERVICE_UNAVAILABLE" -> DownstreamError
    )
}

