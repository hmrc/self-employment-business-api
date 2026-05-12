/*
 * Copyright 2026 HM Revenue & Customs
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

package v5.retrieveAnnualSubmission

import shared.config.SharedAppConfig
import shared.config.ConfigFeatureSwitches
import shared.connectors.DownstreamUri.{HipUri, IfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser.*
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v5.retrieveAnnualSubmission.model.request.RetrieveAnnualSubmissionRequestData
import v5.retrieveAnnualSubmission.model.response.RetrieveAnnualSubmissionResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveAnnualSubmissionConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def retrieveAnnualSubmission(request: RetrieveAnnualSubmissionRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String
  ): Future[DownstreamOutcome[RetrieveAnnualSubmissionResponse]] = {

    import request.*
    import schema.*

    lazy val downstream1803Uri = if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1803")) {
      HipUri(
        s"itsa/income-tax/v1/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries"
      )
    } else {
      IfsUri(
        s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries"
      )
    }

    lazy val downstream1403Uri = IfsUri(s"income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}")

    val downstreamUri: DownstreamUri[DownstreamResp] =
      if (taxYear.useTaxYearSpecificApi) {
        downstream1803Uri
      } else {
        downstream1403Uri
      }

    get(downstreamUri)
  }

}
