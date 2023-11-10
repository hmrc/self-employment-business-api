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
import config.{AppConfig, FeatureSwitches}
import play.api.libs.json.JsObject
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v1.models.request.deleteAnnual.DeleteAnnualSubmissionRequestData

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteAnnualSubmissionConnector @Inject() (val http: HttpClient, val appConfig: AppConfig)(implicit featureSwitches: FeatureSwitches)
    extends BaseDownstreamConnector {

  def deleteAnnualSubmission(request: DeleteAnnualSubmissionRequestData)(implicit
                                                                         hc: HeaderCarrier,
                                                                         ec: ExecutionContext,
                                                                         correlationId: String): Future[DownstreamOutcome[Unit]] = {

    import request._

    val intent = if (featureSwitches.isPassDeleteIntentEnabled) Some("DELETE") else None

    if (taxYear.useTaxYearSpecificApi) {
      delete(TaxYearSpecificIfsUri[Unit](s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries"))
    } else {
      put(
        JsObject.empty,
        DesUri[Unit](s"income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}"),
        intent
      )
    }
  }

}
