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

package v3.retrieveAnnualSubmission

import api.connectors.DownstreamUri.{IfsUri, TaxYearSpecificIfsUri}
import api.connectors.httpparsers.StandardDownstreamHttpParser._
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import config.AppConfig
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v3.retrieveAnnualSubmission.model.request.{Def1_RetrieveAnnualSubmissionRequestData, RetrieveAnnualSubmissionRequestData}
import v3.retrieveAnnualSubmission.model.response.{Def1_RetrieveAnnualSubmissionResponse, RetrieveAnnualSubmissionResponse}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveAnnualSubmissionConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def retrieveAnnualSubmission(request: RetrieveAnnualSubmissionRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[RetrieveAnnualSubmissionResponse]] = {

    request match {
      case tysDef1: Def1_RetrieveAnnualSubmissionRequestData if tysDef1.taxYear.isTys =>
        import tysDef1._

        val downstreamUri = TaxYearSpecificIfsUri[Def1_RetrieveAnnualSubmissionResponse](
          s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries"
        )
        val result = get(downstreamUri)
        result

      case nonTysDef1: Def1_RetrieveAnnualSubmissionRequestData =>
        import nonTysDef1._

        val downstreamUri = IfsUri[Def1_RetrieveAnnualSubmissionResponse](
          s"income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}"
        )
        val result = get(downstreamUri)
        result
    }
  }

}
