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

package v2.models.response.retrieveAnnual

import api.hateoas.HateoasLinks
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import shared.config.AppConfig
import shared.hateoas.{HateoasData, HateoasLinksFactory, Link}
import shared.models.domain.{BusinessId, Nino, TaxYear}

case class RetrieveAnnualSubmissionResponse(adjustments: Option[Adjustments], allowances: Option[Allowances], nonFinancials: Option[NonFinancials])

object RetrieveAnnualSubmissionResponse extends HateoasLinks {

  implicit val reads: Reads[RetrieveAnnualSubmissionResponse] = (
    (JsPath \ "annualAdjustments").readNullable[Adjustments] and
      (JsPath \ "annualAllowances").readNullable[Allowances] and
      (JsPath \ "annualNonFinancials").readNullable[NonFinancials]
  )(RetrieveAnnualSubmissionResponse.apply _)

  implicit val writes: OWrites[RetrieveAnnualSubmissionResponse] = Json.writes[RetrieveAnnualSubmissionResponse]

  implicit object RetrieveAnnualSubmissionLinksFactory
      extends HateoasLinksFactory[RetrieveAnnualSubmissionResponse, RetrieveAnnualSubmissionHateoasData] {

    override def links(appConfig: AppConfig, data: RetrieveAnnualSubmissionHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendAnnualSubmission(appConfig, nino, businessId, TaxYear.fromMtd(taxYear)),
        retrieveAnnualSubmission(appConfig, nino, businessId, TaxYear.fromMtd(taxYear)),
        deleteAnnualSubmission(appConfig, nino, businessId, TaxYear.fromMtd(taxYear))
      )
    }

  }

}

case class RetrieveAnnualSubmissionHateoasData(nino: Nino, businessId: BusinessId, taxYear: String) extends HateoasData
