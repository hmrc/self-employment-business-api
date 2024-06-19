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

package v3.createPeriodSummary

import shared.connectors.DownstreamUri.{DesUri, TaxYearSpecificIfsUri}
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import shared.config.AppConfig
import play.api.http.Status.{CREATED, OK}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v3.createPeriodSummary.model.request.{CreatePeriodSummaryRequestData, Def1_CreatePeriodSummaryRequestData, Def2_CreatePeriodSummaryRequestData}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreatePeriodSummaryConnector @Inject()(val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def createPeriodSummary(request: CreatePeriodSummaryRequestData)(implicit
                                                                   hc: HeaderCarrier,
                                                                   ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    import request._

    request match {
      case def1: Def1_CreatePeriodSummaryRequestData =>
        val desUri                            = DesUri[Unit](s"income-tax/nino/$nino/self-employments/$businessId/periodic-summaries")
        implicit val successCode: SuccessCode = SuccessCode(OK)
        post(def1.body, desUri)

      case def2: Def2_CreatePeriodSummaryRequestData =>
        val ifsUri = TaxYearSpecificIfsUri[Unit](s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/periodic-summaries")
        implicit val successCode: SuccessCode = SuccessCode(CREATED)
        post(def2.body, ifsUri)
    }
  }

}
