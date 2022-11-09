/*
 * Copyright 2022 HM Revenue & Customs
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

import config.AppConfig
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v1.connectors.DownstreamUri.{DesUri, TaxYearSpecificIfsUri}
import v1.connectors.httpparsers.StandardDownstreamHttpParser._
import v1.models.request.amendSEAnnual.AmendAnnualSubmissionRequest

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AmendAnnualSubmissionConnector @Inject()(val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def amendAnnualSubmission(request: AmendAnnualSubmissionRequest)(implicit
                                                                   hc: HeaderCarrier,
                                                                   ec: ExecutionContext,
                                                                   correlationId: String): Future[DownstreamOutcome[Unit]] = {

    val nino = request.nino.nino
    val taxYear = request.taxYear
    val businessId = request.businessId.value

    implicit val successCode: SuccessCode = SuccessCode(OK)

    val downstreamUri =
      if (taxYear.useTaxYearSpecificApi) {
        TaxYearSpecificIfsUri[Unit](s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/annual-summaries")
      } else {
        DesUri[Unit](s"income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${taxYear.asDownstream}")
      }

    put(
      uri = downstreamUri,
      body = request.body
    )
  }

}
