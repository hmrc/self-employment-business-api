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

package v3.createAmendAnnualSubmission.model.request

import shared.models.domain.{BusinessId, Nino, TaxYear}
import v3.createAmendAnnualSubmission.def1.model.request.Def1_CreateAmendAnnualSubmissionRequestBody
import v3.createAmendAnnualSubmission.def2.request.Def2_CreateAmendAnnualSubmissionRequestBody

sealed trait CreateAmendAnnualSubmissionRequestData {
  val nino: Nino
  def businessId: BusinessId
  def taxYear: TaxYear
  def body: CreateAmendAnnualSubmissionRequestBody
}

case class Def1_CreateAmendAnnualSubmissionRequestData(
    nino: Nino,
    businessId: BusinessId,
    taxYear: TaxYear,
    body: Def1_CreateAmendAnnualSubmissionRequestBody
) extends CreateAmendAnnualSubmissionRequestData

case class Def2_CreateAmendAnnualSubmissionRequestData(
    nino: Nino,
    businessId: BusinessId,
    taxYear: TaxYear,
    body: Def2_CreateAmendAnnualSubmissionRequestBody
) extends CreateAmendAnnualSubmissionRequestData
