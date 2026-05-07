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

package v5.deleteAnnualSubmission

import config.SeBusinessFeatureSwitches
import play.api.libs.json.JsObject
import shared.config.{ConfigFeatureSwitches, SharedAppConfig}
import shared.connectors.DownstreamUri.{DesUri, HipUri, IfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser.*
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import shared.models.domain.TaxYear
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v5.deleteAnnualSubmission.model.request.DeleteAnnualSubmissionRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}
import scala.math.Ordering.Implicits.infixOrderingOps

@Singleton
class DeleteAnnualSubmissionConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig)(implicit
    featureSwitches: SeBusinessFeatureSwitches)
    extends BaseDownstreamConnector {

  lazy private val intent = if (featureSwitches.isPassDeleteIntentEnabled) Some("DELETE") else None

  def deleteAnnualSubmission(request: DeleteAnnualSubmissionRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    import request.*

    lazy val downstream1787Uri = if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1787") && taxYear >= TaxYear.fromMtd("2025-26")) {
      HipUri[Unit](s"itsa/income-tax/v1/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries")
    } else {
      IfsUri[Unit](s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries")
    }

    lazy val downstream1403Uri = DesUri[Unit](s"income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}")

    if (taxYear.useTaxYearSpecificApi) {
      delete(downstream1787Uri)
    } else {
      put(JsObject.empty, downstream1403Uri, intent)
    }

  }

}
