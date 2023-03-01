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
import config.AppConfig
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v1.models.request.amendPeriodSummary.AmendPeriodSummaryRequest

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AmendPeriodSummaryConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def amendPeriodSummary(request: AmendPeriodSummaryRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    val nino       = request.nino.nino
    val businessId = request.businessId.value
    val fromDate   = request.periodId.substring(0, 10)
    val toDate     = request.periodId.substring(11, 21)
    val taxYear    = request.taxYear

    implicit val successCode: SuccessCode = SuccessCode(OK)

    val downstreamUri =
      taxYear match {
        case Some(taxYear) if taxYear.useTaxYearSpecificApi =>
          TaxYearSpecificIfsUri[Unit](
            s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/periodic-summaries?from=$fromDate&to=$toDate")

        case _ =>
          DesUri[Unit](s"income-tax/nino/$nino/self-employments/$businessId/periodic-summaries?from=$fromDate&to=$toDate")

      }

    put(
      body = request.body,
      uri = downstreamUri
    )
  }

}
