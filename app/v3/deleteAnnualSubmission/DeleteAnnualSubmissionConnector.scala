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

package v3.deleteAnnualSubmission

import config.SeBusinessFeatureSwitches
import play.api.libs.json.JsObject
import shared.config.SharedAppConfig
import shared.connectors.DownstreamUri.{DesUri, IfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser.*
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v3.deleteAnnualSubmission.model.{Def1_DeleteAnnualSubmissionRequestData, DeleteAnnualSubmissionRequestData}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteAnnualSubmissionConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig)(implicit
    featureSwitches: SeBusinessFeatureSwitches)
    extends BaseDownstreamConnector {

  lazy private val intent = if (featureSwitches.isPassDeleteIntentEnabled) Some("DELETE") else None

  def deleteAnnualSubmission(request: DeleteAnnualSubmissionRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    request match {
      case def1: Def1_DeleteAnnualSubmissionRequestData =>
        import def1.*

        if (taxYear.useTaxYearSpecificApi) {
          delete(IfsUri[Unit](s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries"))
        } else {
          put(
            JsObject.empty,
            DesUri[Unit](s"income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}"),
            intent
          )
        }

    }

  }

}
