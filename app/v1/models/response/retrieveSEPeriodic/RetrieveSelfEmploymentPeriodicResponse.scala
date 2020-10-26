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

package v1.models.response.retrieveSEPeriodic

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.Link

case class RetrieveSelfEmploymentPeriodicResponse(periodFromDate: String,
                                                  periodToDate: String,
                                                  incomes: Option[Incomes],
                                                  consolidatedExpenses: Option[ConsolidatedExpenses],
                                                  expenses: Option[Expenses])

object RetrieveSelfEmploymentPeriodicResponse extends HateoasLinks {
  implicit val reads: Reads[RetrieveSelfEmploymentPeriodicResponse] = (
    (JsPath \ "from").read[String] and
      (JsPath \ "to").read[String] and
      (JsPath \ "financials" \ "incomes").readNullable[Incomes] and
      (JsPath \ "financials" \ "deductions" \ "simplifiedExpenses").readNullable[BigDecimal].map(_.map(ConsolidatedExpenses(_))) and
      (JsPath \ "financials" \ "deductions").readNullable[Expenses]
    ) (RetrieveSelfEmploymentPeriodicResponse.apply _)


  implicit val writes: OWrites[RetrieveSelfEmploymentPeriodicResponse] = Json.writes[RetrieveSelfEmploymentPeriodicResponse]

  implicit object RetrieveSelfEmploymentAnnualSummaryLinksFactory extends
    HateoasLinksFactory[RetrieveSelfEmploymentPeriodicResponse, RetrieveSelfEmploymentPeriodicHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveSelfEmploymentPeriodicHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendPeriodicUpdate(appConfig, nino, businessId, periodId),
        retrievePeriodicUpdate(appConfig, nino, businessId, periodId)
      )
    }
  }

}

case class RetrieveSelfEmploymentPeriodicHateoasData(nino: String, businessId: String, periodId: String)