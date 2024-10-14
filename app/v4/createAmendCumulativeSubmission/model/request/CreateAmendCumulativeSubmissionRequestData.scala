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

package v4.createAmendCumulativeSubmission.model.request

import play.api.libs.json.JsValue
import shared.models.domain.{BusinessId, Nino, TaxYear}

sealed trait CreateAmendCumulativeSubmissionRequestData {
  val nino: Nino
  def businessId: BusinessId
  def taxYear: TaxYear
  def body: CreateAmendCumulativeSubmissionRequestBody
}

object CreateAmendCumulativeSubmissionRequestData {

  def rawTaxYear(body: JsValue): Option[String] = (body \ "periodDates" \ "periodEndDate").asOpt[String]
}

case class Def1_CreateAmendCumulativeSubmissionRequestData(
    nino: Nino,
    businessId: BusinessId,
    taxYear: TaxYear,
    body: Def1_CreateAmendCumulativeSubmissionRequestBody
) extends CreateAmendCumulativeSubmissionRequestData
