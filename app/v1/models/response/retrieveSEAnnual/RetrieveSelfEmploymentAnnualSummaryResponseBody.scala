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

package v1.models.response.retrieveSEAnnual

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, Link}

case class RetrieveSelfEmploymentAnnualSummaryResponseBody(
                                                            adjustments: Option[Adjustments],
                                                            allowances: Option[Allowances],
                                                            nonFinancials: Option[NonFinancials]
                                                          )

object RetrieveSelfEmploymentAnnualSummaryResponseBody extends HateoasLinks {
  implicit val reads: Reads[RetrieveSelfEmploymentAnnualSummaryResponseBody] = (
    (JsPath \ "annualAdjustments").readNullable[Adjustments] and
      (JsPath \ "annualAllowances").readNullable[Allowances] and
      ((JsPath \ "annualNonFinancials").readNullable[Class4NicInfo].map(_.map(class4NicInfo => NonFinancials(Some(class4NicInfo)))))
    ) (RetrieveSelfEmploymentAnnualSummaryResponseBody.apply _)

  implicit val writes: OWrites[RetrieveSelfEmploymentAnnualSummaryResponseBody] = Json.writes[RetrieveSelfEmploymentAnnualSummaryResponseBody]


  implicit object RetrieveSelfEmploymentAnnualSummaryLinksFactory extends
    HateoasLinksFactory[RetrieveSelfEmploymentAnnualSummaryResponseBody, RetrieveSelfEmploymentAnnualSummaryHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveSelfEmploymentAnnualSummaryHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendAnnualSummary(appConfig, nino, businessId, taxYear),
        retrieveAnnualSummary(appConfig, nino, businessId, taxYear),
        deleteAnnualSummary(appConfig, nino, businessId, taxYear)
      )
    }
  }

}

case class RetrieveSelfEmploymentAnnualSummaryHateoasData(nino: String, businessId: String, taxYear: String) extends HateoasData
