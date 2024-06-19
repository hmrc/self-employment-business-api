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

package v3.amendPeriodSummary

import api.models.errors.{PeriodIdFormatError, RuleBothExpensesSuppliedError, RuleNotAllowedConsolidatedExpenses}
import cats.implicits._
import shared.controllers.RequestContext
import shared.models.errors._
import shared.services.{BaseService, ServiceOutcome}
import v3.amendPeriodSummary.model.request.AmendPeriodSummaryRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AmendPeriodSummaryService @Inject() (connector: AmendPeriodSummaryConnector) extends BaseService {

  def amendPeriodSummary(request: AmendPeriodSummaryRequestData)(implicit ctx: RequestContext, ec: ExecutionContext): Future[ServiceOutcome[Unit]] =
    connector.amendPeriodSummary(request).map(_.leftMap(mapDownstreamErrors(downstreamErrorMap)))

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_NINO"                    -> NinoFormatError,
      "INVALID_INCOME_SOURCE"           -> BusinessIdFormatError,
      "INVALID_DATE_FROM"               -> PeriodIdFormatError,
      "INVALID_DATE_TO"                 -> PeriodIdFormatError,
      "INVALID_PAYLOAD"                 -> InternalError,
      "NOT_FOUND_INCOME_SOURCE"         -> NotFoundError,
      "NOT_FOUND_PERIOD"                -> NotFoundError,
      "BOTH_EXPENSES_SUPPLIED"          -> RuleBothExpensesSuppliedError,
      "NOT_ALLOWED_SIMPLIFIED_EXPENSES" -> RuleNotAllowedConsolidatedExpenses,
      "SERVER_ERROR"                    -> InternalError,
      "SERVICE_UNAVAILABLE"             -> InternalError
    )
    val extraTysErrors = Map(
      "INVALID_TAX_YEAR"                      -> TaxYearFormatError,
      "TAX_YEAR_NOT_SUPPORTED"                -> RuleTaxYearNotSupportedError,
      "INVALID_CORRELATION_ID"                -> InternalError,
      "INVALID_INCOMESOURCE_ID"               -> BusinessIdFormatError,
      "PERIOD_NOT_FOUND"                      -> NotFoundError,
      "INCOME_SOURCE_NOT_FOUND"               -> NotFoundError,
      "INCOME_SOURCE_DATA_NOT_FOUND"          -> NotFoundError,
      "BOTH_CONS_BREAKDOWN_EXPENSES_SUPPLIED" -> RuleBothExpensesSuppliedError
    )

    errors ++ extraTysErrors
  }

}
