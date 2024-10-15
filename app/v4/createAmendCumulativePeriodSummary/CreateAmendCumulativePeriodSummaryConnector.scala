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

package v4.createAmendCumulativePeriodSummary

import play.api.http.Status.NO_CONTENT
import shared.config.SharedAppConfig
import shared.connectors.DownstreamUri.TaxYearSpecificIfsUri
import shared.connectors.httpparsers.StandardDownstreamHttpParser._
import shared.connectors.{BaseDownstreamConnector, DownstreamOutcome}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v4.createAmendCumulativePeriodSummary.model.request.{
  CreateAmendCumulativePeriodSummaryRequestData,
  Def1_CreateAmendCumulativePeriodSummaryRequestData
}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CreateAmendCumulativePeriodSummaryConnector @Inject() (val http: HttpClient, val appConfig: SharedAppConfig) extends BaseDownstreamConnector {

  def amendCumulativePeriodSummary(request: CreateAmendCumulativePeriodSummaryRequestData)(implicit
      hc: HeaderCarrier,
      ec: ExecutionContext,
      correlationId: String): Future[DownstreamOutcome[Unit]] = {

    implicit val successCode: SuccessCode = SuccessCode(NO_CONTENT)

    request match {
      case def1: Def1_CreateAmendCumulativePeriodSummaryRequestData =>
        import def1._
        val downstreamUri =
          uriFactory(nino, businessId, taxYear)
        put(body, downstreamUri)
    }

  }

  private def uriFactory(nino: Nino, businessId: BusinessId, taxYear: TaxYear) = {
    TaxYearSpecificIfsUri[Unit](s"income-tax/${taxYear.asTysDownstream}/self-employments/periodic/$nino/$businessId")
  }

}
