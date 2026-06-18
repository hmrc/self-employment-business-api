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

package v5.createAmendCumulativePeriodSummary

import api.config.{AppConfig, ConfigFeatureSwitches}
import api.connectors.DownstreamUri.{HipUri, IfsUri}
import api.connectors.httpparsers.StandardDownstreamHttpParser.*
import api.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import api.models.domain.{BusinessId, Nino, TaxYear}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.client.HttpClientV2
import v5.createAmendCumulativePeriodSummary.model.request.{
  CreateAmendCumulativePeriodSummaryRequestData,
  Def1_CreateAmendCumulativePeriodSummaryRequestData
}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateAmendCumulativePeriodSummaryConnector @Inject() (val http: HttpClientV2, val appConfig: AppConfig) extends BaseDownstreamConnector {

  def amendCumulativePeriodSummary(request: CreateAmendCumulativePeriodSummaryRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    request match {
      case def1: Def1_CreateAmendCumulativePeriodSummaryRequestData =>
        import def1.*
        val downstreamUri =
          uriFactory(nino, businessId, taxYear)
        put(body, downstreamUri)
    }

  }

  private def uriFactory(nino: Nino, businessId: BusinessId, taxYear: TaxYear) = {
    if (ConfigFeatureSwitches().isEnabled("ifs_hip_migration_1959")) {
      HipUri[Unit](s"itsa/income-tax/v1/${taxYear.asTysDownstream}/self-employments/periodic/$nino/$businessId")
    } else {
      IfsUri[Unit](s"income-tax/${taxYear.asTysDownstream}/self-employments/periodic/$nino/$businessId")
    }
  }

}
