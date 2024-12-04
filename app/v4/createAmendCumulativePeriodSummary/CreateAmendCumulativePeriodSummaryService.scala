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

package v4.createAmendCumulativePeriodSummary

import api.models.errors._
import cats.implicits._
import shared.controllers.RequestContext
import shared.models.errors._
import shared.services.{BaseService, ServiceOutcome}
import v4.createAmendCumulativePeriodSummary.model.request.CreateAmendCumulativePeriodSummaryRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateAmendCumulativePeriodSummaryService @Inject() (connector: CreateAmendCumulativePeriodSummaryConnector) extends BaseService {

  def createAmendCumulativePeriodSummary(
      request: CreateAmendCumulativePeriodSummaryRequestData
  )(implicit ctx: RequestContext, ec: ExecutionContext): Future[ServiceOutcome[Unit]] = {

    connector
      .amendCumulativePeriodSummary(request)
      .map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))
  }

  private val downstreamErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_NINO"                       -> NinoFormatError,
      "INVALID_INCOME_SOURCE_ID"           -> BusinessIdFormatError,
      "INVALID_CORRELATION_ID"             -> InternalError,
      "INVALID_TAX_YEAR"                   -> TaxYearFormatError,
      "UNMATCHED_STUB_ERROR"               -> RuleIncorrectGovTestScenarioError,
      "INVALID_PAYLOAD"                    -> InternalError,
      "INCOME_SOURCE_NOT_FOUND"            -> NotFoundError,
      "BOTH_EXPENSES_SUPPLIED"             -> RuleBothExpensesSuppliedError,
      "TAX_YEAR_NOT_SUPPORTED"             -> RuleTaxYearNotSupportedError,
      "EARLY_DATA_SUBMISSION_NOT_ACCEPTED" -> RuleEarlyDataPeriodSummaryNotAcceptedError,
      "INVALID_SUBMISSION_END_DATE"        -> RuleAdvancePeriodSummaryRequiresPeriodEndDateError,
      "SUBMISSION_END_DATE_VALUE"          -> RulePeriodSummaryEndDateCannotMoveBackwardsError,
      "INVALID_START_DATE"                 -> RuleStartDateNotAlignedWithReportingTypeError,
      "START_DATE_NOT_ALIGNED"             -> RuleStartDateNotAlignedToCommencementDateError,
      "END_DATE_NOT_ALIGNED"               -> RuleEndDateNotAlignedWithReportingTypeError,
      "START_END_DATE_NOT_ACCEPTED"        -> RuleStartAndEndDateNotAllowedError,
      "OUTSIDE_AMENDMENT_WINDOW"           -> RuleOutsideAmendmentWindowError,
      "MISSING_SUBMISSION_DATES"           -> InternalError,
      "SERVER_ERROR"                       -> InternalError,
      "SERVICE_UNAVAILABLE"                -> InternalError
    )

}
