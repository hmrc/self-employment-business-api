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

package v1.models.response.retrievePeriodic

import config.AppConfig
import play.api.libs.json._
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.domain.{BusinessId, Nino}
import v1.models.hateoas.{HateoasData, Link}

case class RetrievePeriodicResponse(periodDates: PeriodDates,
                                    periodIncome: Option[PeriodIncome],
                                    periodAllowableExpenses: Option[PeriodAllowableExpenses],
                                    periodDisallowableExpenses: Option[PeriodDisallowableExpenses])

object RetrievePeriodicResponse extends HateoasLinks {

  implicit val reads: Reads[RetrievePeriodicResponse] = for {
    periodStartDate <- (JsPath \ "from").read[String]
    periodEndDate   <- (JsPath \ "to").read[String]

    periodDates = PeriodDates(periodStartDate = periodStartDate, periodEndDate = periodEndDate)

    periodIncome               <- (JsPath \ "financials" \ "incomes").readNullable[PeriodIncome]
    periodAllowableExpenses    <- (JsPath \ "financials").readNullable[PeriodAllowableExpenses]
    periodDisallowableExpenses <- (JsPath \ "financials").readNullable[PeriodDisallowableExpenses]
  } yield {
    RetrievePeriodicResponse(
      periodDates = periodDates,
      periodIncome = if (periodIncome.exists(_.isEmptyObject)) None else periodIncome,
      periodAllowableExpenses = if (periodAllowableExpenses.exists(_.isEmptyObject)) None else periodAllowableExpenses,
      periodDisallowableExpenses = if (periodDisallowableExpenses.exists(_.isEmptyObject)) None else periodDisallowableExpenses
    )
  }

  implicit val writes: OWrites[RetrievePeriodicResponse] = Json.writes[RetrievePeriodicResponse]

  implicit object RetrieveAnnualSubmissionLinksFactory extends HateoasLinksFactory[RetrievePeriodicResponse, RetrievePeriodicHateoasData] {

    override def links(appConfig: AppConfig, data: RetrievePeriodicHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendPeriodicSummary(appConfig, nino, businessId, periodId),
        retrievePeriodicSummary(appConfig, nino, businessId, periodId),
        listPeriodicSummary(appConfig, nino, businessId, isSelf = false)
      )
    }

  }

}

case class RetrievePeriodicHateoasData(nino: Nino, businessId: BusinessId, periodId: String) extends HateoasData
