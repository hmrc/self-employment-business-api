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

package v4.createAmendCumulativeSubmission

import shared.connectors.DownstreamUri.{IfsUri, TaxYearSpecificIfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.config.SharedAppConfig
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v4.createAmendCumulativeSubmission.model.request.{CreateAmendCumulativeSubmissionRequestData, Def1_CreateAmendCumulativeSubmissionRequestData}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateAmendCumulativeSubmissionConnector @Inject() (val http: HttpClient, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def amendCumulativeSubmission(request: CreateAmendCumulativeSubmissionRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    implicit val successCode: SuccessCode = SuccessCode(OK)

    request match {
      case def1: Def1_CreateAmendCumulativeSubmissionRequestData =>
        import def1._
        val downstreamUri =
          uriFactory(nino, businessId, taxYear)
        put(body, downstreamUri)
    }

  }

  private def uriFactory(nino: Nino, businessId: BusinessId, taxYear: TaxYear) = {
    if (taxYear.useTaxYearSpecificApi) {
      TaxYearSpecificIfsUri[Unit](s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/cumulative-summaries")
    } else {
      IfsUri[Unit](s"income-tax/nino/$nino/self-employments/$businessId/cumulative-summaries/${taxYear.asDownstream}")
    }
  }

}
