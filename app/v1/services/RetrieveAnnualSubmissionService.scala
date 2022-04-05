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
import v1.connectors.RetrieveAnnualSubmissionConnector
import v1.controllers.EndpointLogContext
import v1.models.errors._
import v1.models.request.retrieveAnnual.RetrieveAnnualSubmissionRequest
import v1.models.response.retrieveAnnual.RetrieveAnnualSubmissionResponse
import v1.support.DesResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveAnnualSubmissionService @Inject() (connector: RetrieveAnnualSubmissionConnector) extends DesResponseMappingSupport with Logging {

  def retrieveAnnualSubmission(request: RetrieveAnnualSubmissionRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[ServiceOutcome[RetrieveAnnualSubmissionResponse]] = {

    connector.retrieveAnnualSubmission(request).map(_.leftMap(mapDesErrors(desErrorMap)))

  }

  private def desErrorMap =
    Map(
      "INVALID_NINO"            -> NinoFormatError,
      "INVALID_INCOMESOURCEID"  -> BusinessIdFormatError,
      "INVALID_TAX_YEAR"        -> TaxYearFormatError,
      "INCOME_SOURCE_NOT_FOUND" -> NotFoundError,
      "NOT_FOUND_PERIOD"        -> NotFoundError,
      "INVALID_CORRELATIONID"   -> DownstreamError,
      "SERVER_ERROR"            -> DownstreamError,
      "SERVICE_UNAVAILABLE"     -> DownstreamError
    )

}
