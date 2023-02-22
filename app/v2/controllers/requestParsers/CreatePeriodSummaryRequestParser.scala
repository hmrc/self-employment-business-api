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

package v2.controllers.requestParsers

import api.controllers.requestParsers.RequestParser
import api.models.domain.{BusinessId, Nino}
import api.models.request.createPeriodSummary.CreatePeriodSummaryRawData
import v2.models.request.createPeriodSummary.CreatePeriodSummaryRequest
import v2.controllers.requestParsers.validators.CreatePeriodSummaryValidator
import v2.models.request.createPeriodSummary.CreatePeriodSummaryBody

import javax.inject.Inject

class CreatePeriodSummaryRequestParser @Inject() (val validator: CreatePeriodSummaryValidator)
    extends RequestParser[CreatePeriodSummaryRawData, CreatePeriodSummaryRequest] {

  override protected def requestFor(data: CreatePeriodSummaryRawData): CreatePeriodSummaryRequest =
    CreatePeriodSummaryRequest(Nino(data.nino), BusinessId(data.businessId), data.body.as[CreatePeriodSummaryBody])

}
