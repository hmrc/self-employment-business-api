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

package v3.deleteAnnualSubmission

import shared.controllers.RequestContext
import shared.models.errors._
import shared.services.{BaseService, ServiceOutcome}
import cats.implicits._
import v3.deleteAnnualSubmission.model.DeleteAnnualSubmissionRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteAnnualSubmissionService @Inject() (connector: DeleteAnnualSubmissionConnector) extends BaseService {

  def deleteAnnualSubmission(
      request: DeleteAnnualSubmissionRequestData)(implicit ctx: RequestContext, ec: ExecutionContext): Future[ServiceOutcome[Unit]] = {

    connector.deleteAnnualSubmission(request).map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))
  }

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors: Map[String, MtdError] =
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

    val extraTysErrors: Map[String, MtdError] = Map(
      "INVALID_CORRELATION_ID"       -> InternalError,
      "INVALID_INCOME_SOURCE_ID"     -> BusinessIdFormatError,
      "PERIOD_NOT_FOUND"             -> NotFoundError,
      "INCOME_SOURCE_DATA_NOT_FOUND" -> NotFoundError,
      "PERIOD_ALREADY_DELETED"       -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED"       -> RuleTaxYearNotSupportedError
    )

    errors ++ extraTysErrors
  }

}
