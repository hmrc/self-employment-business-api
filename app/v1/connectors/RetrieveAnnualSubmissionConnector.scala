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

import api.connectors.DownstreamUri.{IfsUri, TaxYearSpecificIfsUri}
import api.connectors.httpparsers.StandardDownstreamHttpParser._
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v1.models.request.retrieveAnnual.RetrieveAnnualSubmissionRequest
import v1.models.response.retrieveAnnual.RetrieveAnnualSubmissionResponse

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveAnnualSubmissionConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def retrieveAnnualSubmission(request: RetrieveAnnualSubmissionRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrieveAnnualSubmissionResponse]] = {

    import request._

    val downstreamUri =
      if (taxYear.useTaxYearSpecificApi) {
        TaxYearSpecificIfsUri[RetrieveAnnualSubmissionResponse](
          s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries"
        )
      } else {
        IfsUri[RetrieveAnnualSubmissionResponse](
          s"income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}"
        )
      }

    get(downstreamUri)
  }

}
