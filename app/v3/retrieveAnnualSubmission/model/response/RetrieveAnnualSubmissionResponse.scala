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

package v3.retrieveAnnualSubmission.model.response

import play.api.libs.json.{Json, OWrites}
import shared.config.SharedAppConfig
import shared.hateoas.{HateoasData, HateoasLinksFactory, Link}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.utils.JsonWritesUtil
import v3.amendPeriodSummary.model.response.AmendPeriodSummaryResponse.{amendAnnualSubmission, deleteAnnualSubmission, retrieveAnnualSubmission}
import v3.retrieveAnnualSubmission.def1.model.response.Def1_RetrieveAnnualSubmissionResponse
import v3.retrieveAnnualSubmission.def2.model.response.Def2_RetrieveAnnualSubmissionResponse

trait RetrieveAnnualSubmissionResponse

object RetrieveAnnualSubmissionResponse extends JsonWritesUtil {

  implicit val writes: OWrites[RetrieveAnnualSubmissionResponse] = writesFrom {
    case response: Def1_RetrieveAnnualSubmissionResponse => Json.toJsObject(response)
    case response: Def2_RetrieveAnnualSubmissionResponse => Json.toJsObject(response)
  }

  implicit object RetrieveAnnualSubmissionLinksFactory
      extends HateoasLinksFactory[RetrieveAnnualSubmissionResponse, RetrieveAnnualSubmissionHateoasData] {

    override def links(appConfig: SharedAppConfig, data: RetrieveAnnualSubmissionHateoasData): Seq[Link] = {
      import data.*
      List(
        amendAnnualSubmission(appConfig, nino, businessId, TaxYear.fromMtd(taxYear)),
        retrieveAnnualSubmission(appConfig, nino, businessId, TaxYear.fromMtd(taxYear)),
        deleteAnnualSubmission(appConfig, nino, businessId, TaxYear.fromMtd(taxYear))
      )
    }

  }

}

case class RetrieveAnnualSubmissionHateoasData(nino: Nino, businessId: BusinessId, taxYear: String) extends HateoasData
