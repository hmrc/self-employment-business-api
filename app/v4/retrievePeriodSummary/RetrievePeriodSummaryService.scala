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

package v4.retrievePeriodSummary

import api.models.errors.PeriodIdFormatError
import cats.data.EitherT
import cats.implicits._
import config.SeBusinessFeatureSwitches
import shared.config.AppConfig
import shared.controllers.RequestContext
import shared.models.errors._
import shared.services.{BaseService, ServiceOutcome}
import v4.retrievePeriodSummary.model.request.RetrievePeriodSummaryRequestData
import v4.retrievePeriodSummary.model.response.RetrievePeriodSummaryResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrievePeriodSummaryService @Inject() (
    connector: RetrievePeriodSummaryConnector
)(implicit appConfig: AppConfig)
    extends BaseService {

  lazy private val featureSwitches = SeBusinessFeatureSwitches()

  def retrievePeriodSummary(request: RetrievePeriodSummaryRequestData)(implicit
      ctx: RequestContext,
      ec: ExecutionContext
  ): Future[ServiceOutcome[RetrievePeriodSummaryResponse]] = {

    EitherT(connector.retrievePeriodSummary(request))
      .map(_.map(maybeWithoutTaxTakenOffTradingIncome))
      .leftMap(mapDownstreamErrors(downstreamErrorMap))
      .value
  }

  private def maybeWithoutTaxTakenOffTradingIncome(response: RetrievePeriodSummaryResponse): RetrievePeriodSummaryResponse =
    if (featureSwitches.isCl290Enabled)
      response
    else
      response.withoutTaxTakenOffTradingIncome

  private val downstreamErrorMap: Map[String, MtdError] = {
    val errors = Map(
      "INVALID_NINO"             -> NinoFormatError,
      "INVALID_INCOMESOURCEID"   -> BusinessIdFormatError,
      "INVALID_INCOME_SOURCE_ID" -> BusinessIdFormatError,
      "INVALID_DATE_FROM"        -> PeriodIdFormatError,
      "INVALID_DATE_TO"          -> PeriodIdFormatError,
      "NOT_FOUND_INCOME_SOURCE"  -> NotFoundError,
      "NOT_FOUND_PERIOD"         -> NotFoundError,
      "SERVER_ERROR"             -> InternalError,
      "SERVICE_UNAVAILABLE"      -> InternalError
    )
    val extraTysErrors = Map(
      "INVALID_TAX_YEAR"             -> TaxYearFormatError,
      "INVALID_INCOMESOURCE_ID"      -> BusinessIdFormatError,
      "INCOME_DATA_SOURCE_NOT_FOUND" -> NotFoundError,
      "SUBMISSION_DATA_NOT_FOUND"    -> NotFoundError,
      "TAX_YEAR_NOT_SUPPORTED"       -> RuleTaxYearNotSupportedError
    )
    errors ++ extraTysErrors

  }

}
