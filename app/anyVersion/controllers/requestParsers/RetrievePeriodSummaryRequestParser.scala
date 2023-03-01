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

package anyVersion.controllers.requestParsers

import anyVersion.controllers.requestParsers.validators.RetrievePeriodSummaryValidator
import anyVersion.models.request.retrievePeriodSummary
import anyVersion.models.request.retrievePeriodSummary.{RetrievePeriodSummaryRawData, RetrievePeriodSummaryRequest}
import api.controllers.requestParsers.RequestParser
import api.models.domain.{BusinessId, Nino, PeriodId, TaxYear}

import javax.inject.Inject

class RetrievePeriodSummaryRequestParser @Inject() (val validator: RetrievePeriodSummaryValidator)
    extends RequestParser[RetrievePeriodSummaryRawData, RetrievePeriodSummaryRequest] {

  override protected def requestFor(data: RetrievePeriodSummaryRawData): RetrievePeriodSummaryRequest = {
    val taxYear = data.taxYear.map(TaxYear.fromMtd)
    retrievePeriodSummary.RetrievePeriodSummaryRequest(Nino(data.nino), BusinessId(data.businessId), PeriodId(data.periodId), taxYear)
  }

}
