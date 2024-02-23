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

package v3.connectors

import api.connectors.DownstreamUri.{DesUri, TaxYearSpecificIfsUri}
import api.connectors.httpparsers.StandardDownstreamHttpParser._
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v3.controllers.retrievePeriodSummary.model.request.{
  Def1_RetrievePeriodSummaryRequestData,
  Def2_RetrievePeriodSummaryRequestData,
  RetrievePeriodSummaryRequestData
}
import v3.controllers.retrievePeriodSummary.model.response.{
  Def1_RetrievePeriodSummaryResponse,
  Def2_RetrievePeriodSummaryResponse,
  RetrievePeriodSummaryResponse
}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrievePeriodSummaryConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def retrievePeriodSummary(request: RetrievePeriodSummaryRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrievePeriodSummaryResponse]] = {

    import request._

    val fromDate = periodId.from
    val toDate   = periodId.to

    request match {
      case _: Def1_RetrievePeriodSummaryRequestData =>
        val downstreamUri = DesUri[Def1_RetrievePeriodSummaryResponse](
          s"income-tax/nino/$nino/self-employments/$businessId/periodic-summary-detail?from=$fromDate&to=$toDate")
        val result = get(downstreamUri)
        result

      case def2: Def2_RetrievePeriodSummaryRequestData =>
        val downstreamUri = TaxYearSpecificIfsUri[Def2_RetrievePeriodSummaryResponse](
          s"income-tax/${def2.taxYear.asTysDownstream}/$nino/self-employments/$businessId/periodic-summary-detail?from=$fromDate&to=$toDate")
        val result = get(downstreamUri)
        result
    }
  }

}
