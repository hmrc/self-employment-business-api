/*
 * Copyright 2024 HM Revenue & Customs
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

package v4.amendPeriodSummary.def2.model.request

import api.models.domain.PeriodId
import shared.models.domain.{BusinessId, Nino, TaxYear}
import v4.amendPeriodSummary.AmendPeriodSummarySchema
import v4.amendPeriodSummary.model.request.{AmendPeriodSummaryRequestData, Def2_AmendPeriodSummaryRequestBody}

case class Def2_AmendPeriodSummaryRequestData(
    nino: Nino,
    businessId: BusinessId,
    periodId: PeriodId,
    taxYear: TaxYear,
    body: Def2_AmendPeriodSummaryRequestBody
) extends AmendPeriodSummaryRequestData {

  override val schema: AmendPeriodSummarySchema = AmendPeriodSummarySchema.Def2

}
