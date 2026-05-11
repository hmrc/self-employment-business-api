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

package v5.createAmendAnnualSubmission

import play.api.http.Status.OK
import shared.config.{ConfigFeatureSwitches, SharedAppConfig}
import shared.connectors.DownstreamUri.{HipUri, IfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser.*
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome, DownstreamUri}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v5.createAmendAnnualSubmission.model.request.*

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateAmendAnnualSubmissionConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def amendAnnualSubmission(request: CreateAmendAnnualSubmissionRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    implicit val successCode: SuccessCode = SuccessCode(OK)

    import request.*

    lazy val downstreamUri1802: DownstreamUri[Unit] =
      if (taxYear.year >= 2026 && ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1802")) {
        HipUri[Unit](s"itsa/income-tax/v1/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries")
      } else {
        IfsUri[Unit](s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries")
      }

    lazy val downstreamUri1403: DownstreamUri[Unit] =
      IfsUri[Unit](s"income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}")

    val downstreamUri: DownstreamUri[Unit] = if (taxYear.useTaxYearSpecificApi) downstreamUri1802 else downstreamUri1403

    request match {
      case def1: Def1_CreateAmendAnnualSubmissionRequestData => put(def1.body, downstreamUri)
      case def2: Def2_CreateAmendAnnualSubmissionRequestData => put(def2.body, downstreamUri)
      case def3: Def3_CreateAmendAnnualSubmissionRequestData => put(def3.body, downstreamUri)
      case def4: Def4_CreateAmendAnnualSubmissionRequestData => put(def4.body, downstreamUri)
    }
  }

}
