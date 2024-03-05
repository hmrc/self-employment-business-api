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

package v3.controllers.retrieveAnnualSubmission.model.response

import api.hateoas.{HateoasData, HateoasLinks, HateoasLinksFactory, Link}
import api.models.domain.{BusinessId, Nino, TaxYear}
import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v3.controllers.retrieveAnnualSubmission.def1.model.response.{Def1_Retrieve_Adjustments, Def1_Retrieve_Allowances, Def1_Retrieve_NonFinancials}
import v3.controllers.retrieveAnnualSubmission.model.response.Def1_RetrieveAnnualSubmissionResponse.Def1_RetrieveAnnualSubmissionLinksFactory

sealed trait RetrieveAnnualSubmissionResponse

object RetrieveAnnualSubmissionResponse extends HateoasLinks {

  implicit val writes: OWrites[RetrieveAnnualSubmissionResponse] = { case def1: Def1_RetrieveAnnualSubmissionResponse =>
    Json.toJsObject(def1)
  }

  implicit object RetrieveAnnualSubmissionLinksFactory
      extends HateoasLinksFactory[RetrieveAnnualSubmissionResponse, RetrieveAnnualSubmissionHateoasData] {

    override def links(appConfig: AppConfig, data: RetrieveAnnualSubmissionHateoasData): Seq[Link] =
      Def1_RetrieveAnnualSubmissionLinksFactory.links(appConfig, data)

  }

}

case class Def1_RetrieveAnnualSubmissionResponse(
    adjustments: Option[Def1_Retrieve_Adjustments],
    allowances: Option[Def1_Retrieve_Allowances],
    nonFinancials: Option[Def1_Retrieve_NonFinancials]
) extends RetrieveAnnualSubmissionResponse

object Def1_RetrieveAnnualSubmissionResponse extends HateoasLinks {

  implicit val reads: Reads[Def1_RetrieveAnnualSubmissionResponse] = (
    (JsPath \ "annualAdjustments").readNullable[Def1_Retrieve_Adjustments] and
      (JsPath \ "annualAllowances").readNullable[Def1_Retrieve_Allowances] and
      (JsPath \ "annualNonFinancials").readNullable[Def1_Retrieve_NonFinancials]
  )(Def1_RetrieveAnnualSubmissionResponse.apply _)

  implicit val writes: OWrites[Def1_RetrieveAnnualSubmissionResponse] = Json.writes[Def1_RetrieveAnnualSubmissionResponse]

  implicit object Def1_RetrieveAnnualSubmissionLinksFactory
      extends HateoasLinksFactory[Def1_RetrieveAnnualSubmissionResponse, RetrieveAnnualSubmissionHateoasData] {

    override def links(appConfig: AppConfig, data: RetrieveAnnualSubmissionHateoasData): Seq[Link] = {
      import data._
      List(
        amendAnnualSubmission(appConfig, nino, businessId, TaxYear.fromMtd(taxYear)),
        retrieveAnnualSubmission(appConfig, nino, businessId, TaxYear.fromMtd(taxYear)),
        deleteAnnualSubmission(appConfig, nino, businessId, TaxYear.fromMtd(taxYear))
      )
    }

  }

}

case class RetrieveAnnualSubmissionHateoasData(nino: Nino, businessId: BusinessId, taxYear: String) extends HateoasData
