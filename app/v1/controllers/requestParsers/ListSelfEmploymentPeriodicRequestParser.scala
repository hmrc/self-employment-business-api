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

package v1.controllers.requestParsers

import javax.inject.Inject
import uk.gov.hmrc.domain.Nino
import v1.controllers.requestParsers.validators.ListSelfEmploymentPeriodicValidator
import v1.models.request.listSEPeriodic.{ListSelfEmploymentPeriodicRawData, ListSelfEmploymentPeriodicRequest}

class ListSelfEmploymentPeriodicRequestParser @Inject()(val validator: ListSelfEmploymentPeriodicValidator)
  extends RequestParser[ListSelfEmploymentPeriodicRawData, ListSelfEmploymentPeriodicRequest] {

  override protected def requestFor(data: ListSelfEmploymentPeriodicRawData): ListSelfEmploymentPeriodicRequest =
    ListSelfEmploymentPeriodicRequest(Nino(data.nino), data.businessId)
}
