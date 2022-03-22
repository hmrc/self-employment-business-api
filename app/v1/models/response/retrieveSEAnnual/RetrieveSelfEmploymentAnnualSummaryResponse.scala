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

package v1.models.response.retrieveSEAnnual

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.hateoas.{HateoasData, Link}

case class RetrieveSelfEmploymentAnnualSummaryResponse(
                                                            adjustments: Option[Adjustments],
                                                            allowances: Option[Allowances],
                                                            nonFinancials: Option[NonFinancials]
                                                          )

object RetrieveSelfEmploymentAnnualSummaryResponse extends HateoasLinks {
  implicit val reads: Reads[RetrieveSelfEmploymentAnnualSummaryResponse] = (
    (JsPath \ "annualAdjustments").readNullable[Adjustments] and
      (JsPath \ "annualAllowances").readNullable[Allowances] and
      (JsPath \ "annualNonFinancials").readNullable[Class4NicInfo].map(_.map(class4NicInfo => NonFinancials(Some(class4NicInfo))))
    ) (RetrieveSelfEmploymentAnnualSummaryResponse.apply _)

  implicit val writes: OWrites[RetrieveSelfEmploymentAnnualSummaryResponse] = Json.writes[RetrieveSelfEmploymentAnnualSummaryResponse]


  implicit object RetrieveSelfEmploymentAnnualSummaryLinksFactory extends
    HateoasLinksFactory[RetrieveSelfEmploymentAnnualSummaryResponse, RetrieveSelfEmploymentAnnualSummaryHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveSelfEmploymentAnnualSummaryHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendAnnualSubmission(appConfig, nino, businessId, taxYear),
        retrieveAnnualSubmission(appConfig, nino, businessId, taxYear),
        deleteAnnualSubmission(appConfig, nino, businessId, taxYear)
      )
    }
  }

}

case class RetrieveSelfEmploymentAnnualSummaryHateoasData(nino: Nino, businessId: BusinessId, taxYear: TaxYear) extends HateoasData
