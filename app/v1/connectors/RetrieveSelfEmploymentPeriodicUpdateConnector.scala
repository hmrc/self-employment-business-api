/*
 * Copyright 2020 HM Revenue & Customs
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
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient}
import v1.models.request.retrieveSEPeriodic.RetrieveSelfEmploymentPeriodicRequest
import v1.models.response.retrieveSEPeriodic.RetrieveSelfEmploymentPeriodicResponse
import v1.connectors.httpparsers.StandardDesHttpParser._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class RetrieveSelfEmploymentPeriodicUpdateConnector @Inject()(val http: HttpClient,
                                                              val appConfig: AppConfig) extends BaseDesConnector {
  def retrieveSEAnnual(request: RetrieveSelfEmploymentPeriodicRequest)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext): Future[DesOutcome[RetrieveSelfEmploymentPeriodicResponse]] = {

    val fromDate = request.periodId.substring(0, 10)
    val toDate = request.periodId.substring(11, 21)

    get(
      uri = DesUri[RetrieveSelfEmploymentPeriodicResponse](s"income-store/nino/${request.nino}/self-employments/${request.businessId}/periodic-summary-detail?from=$fromDate&to=$toDate")
    )
  }
}
