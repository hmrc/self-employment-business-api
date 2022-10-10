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

package v1.controllers.requestParsers

import javax.inject.Inject
import v1.controllers.requestParsers.validators.ListPeriodSummariesValidator
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.request.listPeriodSummaries.{ListPeriodSummariesRawData, ListPeriodSummariesRequest}

class ListPeriodSummariesRequestParser @Inject() (val validator: ListPeriodSummariesValidator)
    extends RequestParser[ListPeriodSummariesRawData, ListPeriodSummariesRequest] {

  override protected def requestFor(data: ListPeriodSummariesRawData): ListPeriodSummariesRequest = {
    val taxYear: Option[TaxYear] = if (data.taxYear.isEmpty) None else Some(TaxYear.fromMtd(data.taxYear.get))
    ListPeriodSummariesRequest(Nino(data.nino), BusinessId(data.businessId), taxYear)
  }

}
