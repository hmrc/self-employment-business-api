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

package v2.services

import api.models.errors._
import cats.implicits._
import shared.controllers.RequestContext
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.{BaseService, ServiceOutcome}
import v2.connectors.CreatePeriodSummaryConnector
import v2.models.request.createPeriodSummary.CreatePeriodSummaryRequestData
import v2.models.response.createPeriodSummary.CreatePeriodSummaryResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreatePeriodSummaryService @Inject() (connector: CreatePeriodSummaryConnector) extends BaseService {

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_NINO"                    -> NinoFormatError,
      "INVALID_INCOME_SOURCE"           -> BusinessIdFormatError,
      "INVALID_PAYLOAD"                 -> InternalError,
      "INVALID_PERIOD"                  -> RuleEndBeforeStartDateError,
      "OVERLAPS_IN_PERIOD"              -> RuleOverlappingPeriod,
      "NOT_ALIGN_PERIOD"                -> RuleMisalignedPeriod,
      "BOTH_EXPENSES_SUPPLIED"          -> RuleBothExpensesSuppliedError,
      "NOT_CONTIGUOUS_PERIOD"           -> RuleNotContiguousPeriod,
      "NOT_ALLOWED_SIMPLIFIED_EXPENSES" -> RuleNotAllowedConsolidatedExpenses,
      "NOT_FOUND_INCOME_SOURCE"         -> NotFoundError,
      "SERVER_ERROR"                    -> InternalError,
      "SERVICE_UNAVAILABLE"             -> InternalError
    )
    val extraTysErrors = Map(
      "INVALID_TAX_YEAR"         -> InternalError,
      "TAX_YEAR_NOT_SUPPORTED"   -> RuleTaxYearNotSupportedError,
      "INVALID_CORRELATIONID"    -> InternalError,
      "INVALID_INCOME_SOURCE_ID" -> BusinessIdFormatError,
      "INCOME_SOURCE_NOT_FOUND"  -> NotFoundError,
      "PERIOD_EXISTS"            -> RuleDuplicateSubmissionError,
      "END_BEFORE_START"         -> RuleEndBeforeStartDateError,
      "PERIOD_HAS_GAPS"          -> RuleNotContiguousPeriod,
      "PERIOD_OVERLAP"           -> RuleOverlappingPeriod,
      "PERIOD_ALIGNMENT"         -> RuleMisalignedPeriod
//      "INVALID_SUBMISSION_PERIOD"   -> RuleInvalidSubmissionPeriodError, // To be reinstated, see MTDSA-15595
//      "INVALID_SUBMISSION_END_DATE" -> RuleInvalidSubmissionEndDateError // To be reinstated, see MTDSA-15595
    )

    errors ++ extraTysErrors
  }

  def createPeriodSummary(request: CreatePeriodSummaryRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext): Future[ServiceOutcome[CreatePeriodSummaryResponse]] = {

    def createSummaryResponse(wrapper: ResponseWrapper[Unit]): ResponseWrapper[CreatePeriodSummaryResponse] = {
      import request.body.periodDates._
      wrapper.copy(responseData = CreatePeriodSummaryResponse(s"${periodStartDate}_$periodEndDate"))
    }

    connector
      .createPeriodSummary(request)
      .map(_.map(createSummaryResponse).leftMap(mapDownstreamErrors(downstreamErrorMap)))
  }

}
