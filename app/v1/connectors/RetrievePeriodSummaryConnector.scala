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

package v1.connectors

import api.connectors.DownstreamUri.{DesUri, TaxYearSpecificIfsUri}
import api.connectors.httpparsers.StandardDownstreamHttpParser._
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import api.models.domain.TaxYear
import config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v1.models.request.retrievePeriodSummary.RetrievePeriodSummaryRequest
import v1.models.response.retrievePeriodSummary.RetrievePeriodSummaryResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrievePeriodSummaryConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def retrievePeriodSummary(request: RetrievePeriodSummaryRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrievePeriodSummaryResponse]] = {

    val fromDate = request.periodId.from
    val toDate   = request.periodId.to
    val taxYear  = request.taxYear

    val nino       = request.nino.nino
    val businessId = request.businessId.value

    val downstreamUri =
      if (TaxYear.isTys(taxYear)) {
        get(
          TaxYearSpecificIfsUri[RetrievePeriodSummaryResponse](
            s"income-tax/${taxYear.get.asTysDownstream}/$nino/self-employments/$businessId/periodic-summary-detail?from=$fromDate&to=$toDate"))
      } else {
        get(
          DesUri[RetrievePeriodSummaryResponse](
            s"income-tax/nino/$nino/self-employments/$businessId/periodic-summary-detail?from=$fromDate&to=$toDate"))
      }
    downstreamUri
  }

}
