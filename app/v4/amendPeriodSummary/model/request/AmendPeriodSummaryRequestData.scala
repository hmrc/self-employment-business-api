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

package v4.amendPeriodSummary.model.request

import api.models.domain.PeriodId
import shared.models.domain.{BusinessId, Nino, TaxYear}

sealed trait AmendPeriodSummaryRequestData {
  val nino: Nino
  val businessId: BusinessId
  val periodId: PeriodId
  val maybeTaxYear: Option[TaxYear]
  val body: AmendPeriodSummaryRequestBody
}

/** Applicable from minimumTaxYear to 2022-23 (pre-TYS).
  */
case class Def1_AmendPeriodSummaryRequestData(
    nino: Nino,
    businessId: BusinessId,
    periodId: PeriodId,
    body: Def1_AmendPeriodSummaryRequestBody
) extends AmendPeriodSummaryRequestData {

  val maybeTaxYear: Option[TaxYear] = None
}

/** Applicable from 2023-24 onwards.
  */
case class Def2_AmendPeriodSummaryRequestData(
    nino: Nino,
    businessId: BusinessId,
    periodId: PeriodId,
    taxYear: TaxYear,
    body: Def2_AmendPeriodSummaryRequestBody
) extends AmendPeriodSummaryRequestData {

  val maybeTaxYear: Option[TaxYear] = Some(taxYear)
}
