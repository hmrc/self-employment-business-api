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

package v5.retrieveAnnualSubmission

import cats.implicits._
import shared.controllers.RequestContext
import shared.models.errors._
import shared.services.{BaseService, ServiceOutcome}
import v5.retrieveAnnualSubmission.model.request.RetrieveAnnualSubmissionRequestData
import v5.retrieveAnnualSubmission.model.response.RetrieveAnnualSubmissionResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveAnnualSubmissionService @Inject() (connector: RetrieveAnnualSubmissionConnector) extends BaseService {

  def retrieveAnnualSubmission(request: RetrieveAnnualSubmissionRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[RetrieveAnnualSubmissionResponse]] = {

    connector
      .retrieveAnnualSubmission(request)
      .map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))
  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_NINO"            -> NinoFormatError,
      "INVALID_INCOMESOURCEID"  -> BusinessIdFormatError,
      "INVALID_TAX_YEAR"        -> TaxYearFormatError,
      "INCOME_SOURCE_NOT_FOUND" -> NotFoundError,
      "NOT_FOUND_PERIOD"        -> NotFoundError,
      "INVALID_CORRELATIONID"   -> InternalError,
      "SERVER_ERROR"            -> InternalError,
      "SERVICE_UNAVAILABLE"     -> InternalError
    )

    val extraTysErrors = Map(
      "INVALID_INCOMESOURCE_ID"       -> BusinessIdFormatError,
      "INVALID_CORRELATION_ID"        -> InternalError,
      "INVALID_DELETED_RETURN_PERIOD" -> InternalError,
      "SUBMISSION_DATA_NOT_FOUND"     -> NotFoundError,
      "INCOME_DATA_SOURCE_NOT_FOUND"  -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED"        -> RuleTaxYearNotSupportedError
    )

    errors ++ extraTysErrors
  }

}
