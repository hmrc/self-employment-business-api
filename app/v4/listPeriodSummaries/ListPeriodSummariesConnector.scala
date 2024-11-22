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

package v4.listPeriodSummaries

import config.SeBusinessFeatureSwitches
import shared.config.SharedAppConfig
import shared.connectors.DownstreamUri.IfsUri
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v4.listPeriodSummaries.model.request.ListPeriodSummariesRequestData
import v4.listPeriodSummaries.model.response.{ListPeriodSummariesResponse, PeriodDetails}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ListPeriodSummariesConnector @Inject() (val http: HttpClient, val appConfig: SharedAppConfig)(implicit
    featureSwitches: SeBusinessFeatureSwitches)
    extends BaseDownstreamConnector {

  def listPeriodSummaries(request: ListPeriodSummariesRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[ListPeriodSummariesResponse[PeriodDetails]]] = {

    import request._
    import schema._

    val downstreamUri =
      if (taxYear.useTaxYearSpecificApi) {
        IfsUri[DownstreamResp](
          s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/periodic-summaries"
        )
      } else {
        IfsUri[DownstreamResp](s"income-tax/nino/$nino/self-employments/$businessId/periodic-summaries")
      }

    get(downstreamUri)
  }

}
