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

import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v1.connectors.DownstreamUri.DesUri
import v1.models.request.createPeriodSummary.CreatePeriodSummaryRequest

import scala.concurrent.{ExecutionContext, Future}
import v1.connectors.httpparsers.StandardDesHttpParser._

@Singleton
class CreatePeriodicConnector @Inject() (val http: HttpClient, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def createPeriodicSummary(request: CreatePeriodSummaryRequest)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    val nino       = request.nino.nino
    val businessId = request.businessId

    implicit val successCode: SuccessCode = SuccessCode(OK)

    post(
      body = request.body,
      DesUri[Unit](s"income-tax/nino/$nino/self-employments/$businessId/periodic-summaries")
    )
  }

}
