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

import utils.Logging
import cats.implicits._
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import v1.connectors.DeleteAnnualSubmissionConnector
import v1.controllers.EndpointLogContext
import v1.models.errors._
import v1.models.request.deleteAnnual.DeleteAnnualSubmissionRequest
import v1.support.DownstreamResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteAnnualSubmissionService @Inject() (connector: DeleteAnnualSubmissionConnector) extends DownstreamResponseMappingSupport with Logging {

  def deleteAnnualSubmission(request: DeleteAnnualSubmissionRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      logContext: EndpointLogContext,
      correlationId: String): Future[ServiceOutcome[Unit]] = {

    connector.deleteAnnualSubmission(request).map(_.leftMap(mapDownstreamErrors(desErrorMap)))
  }

  private val desErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_NINO"                -> NinoFormatError,
      "INVALID_TAX_YEAR"            -> TaxYearFormatError,
      "INVALID_INCOME_SOURCE"       -> BusinessIdFormatError,
      "INVALID_CORRELATIONID"       -> InternalError,
      "INVALID_PAYLOAD"             -> InternalError,
      "MISSING_EXEMPTION_REASON"    -> InternalError,
      "MISSING_EXEMPTION_INDICATOR" -> InternalError,
      "ALLOWANCE_NOT_SUPPORTED"     -> InternalError,
      "NOT_FOUND"                   -> NotFoundError,
      "NOT_FOUND_INCOME_SOURCE"     -> NotFoundError,
      "GONE"                        -> NotFoundError,
      "SERVER_ERROR"                -> InternalError,
      "BAD_GATEWAY"                 -> InternalError,
      "SERVICE_UNAVAILABLE"         -> InternalError
    )

}
