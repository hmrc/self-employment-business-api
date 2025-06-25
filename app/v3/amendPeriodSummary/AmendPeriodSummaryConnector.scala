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

package v3.amendPeriodSummary

import play.api.http.Status.OK
import shared.config.SharedAppConfig
import shared.connectors.DownstreamUri.IfsUri
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v3.amendPeriodSummary.model.request.{AmendPeriodSummaryRequestData, Def2_AmendPeriodSummaryRequestData}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AmendPeriodSummaryConnector @Inject() (val http: HttpClientV2, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def amendPeriodSummary(request: AmendPeriodSummaryRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {
    implicit val successCode: SuccessCode = SuccessCode(OK)

    val amendPeriodSummaryRequestData: Def2_AmendPeriodSummaryRequestData = request.asInstanceOf[Def2_AmendPeriodSummaryRequestData]
    import amendPeriodSummaryRequestData._

    val ifsUri = IfsUri[Unit](
      s"income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/periodic-summaries?from=${periodId.from}&to=${periodId.to}")
    put(amendPeriodSummaryRequestData.body, ifsUri)
  }

}
