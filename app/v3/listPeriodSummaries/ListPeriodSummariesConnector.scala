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

package v3.listPeriodSummaries

import config.SeBusinessFeatureSwitches
import shared.config.AppConfig
import shared.connectors.DownstreamUri.{DesUri, IfsUri, TaxYearSpecificIfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v3.listPeriodSummaries.model.listPeriodSummaries.Def1_ListPeriodSummariesRequestData
import v3.listPeriodSummaries.model.response.listPeriodSummaries.{ListPeriodSummariesResponse, PeriodDetails}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListPeriodSummariesConnector @Inject() (val http: HttpClient, val appConfig: AppConfig)(implicit featureSwitches: SeBusinessFeatureSwitches)
    extends BaseDownstreamConnector {

  def listPeriodSummaries(request: Def1_ListPeriodSummariesRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[ListPeriodSummariesResponse[PeriodDetails]]] = {

    import request._

    val path = s"income-tax/nino/$nino/self-employments/$businessId/periodic-summaries"

    val downstreamUri =
      taxYear match {
        case Some(taxYear) if taxYear.useTaxYearSpecificApi =>
          TaxYearSpecificIfsUri[ListPeriodSummariesResponse[PeriodDetails]](
            s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/periodic-summaries")
        case _ if featureSwitches.isDesIf_MigrationEnabled =>
          IfsUri[ListPeriodSummariesResponse[PeriodDetails]](path)
        case _ =>
          DesUri[ListPeriodSummariesResponse[PeriodDetails]](path)
      }

    get(downstreamUri)
  }

}
