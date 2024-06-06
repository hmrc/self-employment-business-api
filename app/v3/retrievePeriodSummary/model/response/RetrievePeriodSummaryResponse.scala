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

package v3.retrievePeriodSummary.model.response

import api.hateoas.HateoasLinks
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import shared.config.AppConfig
import shared.hateoas.{HateoasData, HateoasLinksFactory, Link}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import v3.retrievePeriodSummary.def1.model.response.{Def1_Retrieve_PeriodDates, Def1_Retrieve_PeriodDisallowableExpenses, Def1_Retrieve_PeriodExpenses, Def1_Retrieve_PeriodIncome}
import v3.retrievePeriodSummary.def2.model.response.{Def2_Retrieve_PeriodDates, Def2_Retrieve_PeriodDisallowableExpenses, Def2_Retrieve_PeriodExpenses, Def2_Retrieve_PeriodIncome}
import v3.retrievePeriodSummary.model.response.Def1_RetrievePeriodSummaryResponse.Def1_RetrieveAnnualSubmissionLinksFactory
import v3.retrievePeriodSummary.model.response.Def2_RetrievePeriodSummaryResponse.Def2_RetrieveAnnualSubmissionLinksFactory

sealed trait RetrievePeriodSummaryResponse {
  def withoutTaxTakenOffTradingIncome: RetrievePeriodSummaryResponse
}

object RetrievePeriodSummaryResponse extends HateoasLinks {

  implicit val writes: OWrites[RetrievePeriodSummaryResponse] = {
    case def1: Def1_RetrievePeriodSummaryResponse => Json.toJsObject(def1)
    case def2: Def2_RetrievePeriodSummaryResponse => Json.toJsObject(def2)
  }

  implicit object RetrieveAnnualSubmissionLinksFactory extends HateoasLinksFactory[RetrievePeriodSummaryResponse, RetrievePeriodSummaryHateoasData] {

    override def links(appConfig: AppConfig, data: RetrievePeriodSummaryHateoasData): Seq[Link] =
      data.taxYear match {
        case None =>
          Def1_RetrieveAnnualSubmissionLinksFactory.links(appConfig, data)
        case Some(_) =>
          Def2_RetrieveAnnualSubmissionLinksFactory.links(appConfig, data)
      }

  }

}

case class Def1_RetrievePeriodSummaryResponse(
    periodDates: Def1_Retrieve_PeriodDates,
    periodIncome: Option[Def1_Retrieve_PeriodIncome],
    periodExpenses: Option[Def1_Retrieve_PeriodExpenses],
    periodDisallowableExpenses: Option[Def1_Retrieve_PeriodDisallowableExpenses]
) extends RetrievePeriodSummaryResponse {

  def withoutTaxTakenOffTradingIncome: Def1_RetrievePeriodSummaryResponse = this
}

object Def1_RetrievePeriodSummaryResponse extends HateoasLinks {

  implicit val reads: Reads[Def1_RetrievePeriodSummaryResponse] = for {
    periodStartDate <- (JsPath \ "from").read[String]
    periodEndDate   <- (JsPath \ "to").read[String]

    periodDates = Def1_Retrieve_PeriodDates(periodStartDate = periodStartDate, periodEndDate = periodEndDate)

    periodIncome               <- (JsPath \ "financials" \ "incomes").readNullable[Def1_Retrieve_PeriodIncome]
    periodExpenses             <- (JsPath \ "financials").readNullable[Def1_Retrieve_PeriodExpenses]
    periodDisallowableExpenses <- (JsPath \ "financials").readNullable[Def1_Retrieve_PeriodDisallowableExpenses]
  } yield {
    Def1_RetrievePeriodSummaryResponse(
      periodDates = periodDates,
      periodIncome = if (periodIncome.exists(_.isEmptyObject)) None else periodIncome,
      periodExpenses = if (periodExpenses.exists(_.isEmptyObject)) None else periodExpenses,
      periodDisallowableExpenses = if (periodDisallowableExpenses.exists(_.isEmptyObject)) None else periodDisallowableExpenses
    )
  }

  implicit val writes: OWrites[Def1_RetrievePeriodSummaryResponse] = Json.writes[Def1_RetrievePeriodSummaryResponse]

  implicit object Def1_RetrieveAnnualSubmissionLinksFactory
      extends HateoasLinksFactory[Def1_RetrievePeriodSummaryResponse, RetrievePeriodSummaryHateoasData] {

    override def links(appConfig: AppConfig, data: RetrievePeriodSummaryHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendPeriodSummary(appConfig, nino, businessId, periodId, None),
        retrievePeriodSummary(appConfig, nino, businessId, periodId, None),
        listPeriodSummaries(appConfig, nino, businessId, None, isSelf = false)
      )
    }

  }

}

/** def2 response adds periodIncome.taxTakenOffTradingIncome for TYS tax years.
  */
case class Def2_RetrievePeriodSummaryResponse(periodDates: Def2_Retrieve_PeriodDates,
                                              periodIncome: Option[Def2_Retrieve_PeriodIncome],
                                              periodExpenses: Option[Def2_Retrieve_PeriodExpenses],
                                              periodDisallowableExpenses: Option[Def2_Retrieve_PeriodDisallowableExpenses])
    extends RetrievePeriodSummaryResponse {

  def withoutTaxTakenOffTradingIncome: Def2_RetrievePeriodSummaryResponse =
    periodIncome
      .map(pi => copy(periodIncome = Some(pi.copy(taxTakenOffTradingIncome = None))))
      .getOrElse(this)

}

object Def2_RetrievePeriodSummaryResponse extends HateoasLinks {

  implicit val reads: Reads[Def2_RetrievePeriodSummaryResponse] = for {
    periodStartDate <- (JsPath \ "from").read[String]
    periodEndDate   <- (JsPath \ "to").read[String]

    periodDates = Def2_Retrieve_PeriodDates(periodStartDate = periodStartDate, periodEndDate = periodEndDate)

    periodIncome               <- (JsPath \ "financials" \ "incomes").readNullable[Def2_Retrieve_PeriodIncome]
    periodExpenses             <- (JsPath \ "financials").readNullable[Def2_Retrieve_PeriodExpenses]
    periodDisallowableExpenses <- (JsPath \ "financials").readNullable[Def2_Retrieve_PeriodDisallowableExpenses]
  } yield {
    Def2_RetrievePeriodSummaryResponse(
      periodDates = periodDates,
      periodIncome = if (periodIncome.exists(_.isEmptyObject)) None else periodIncome,
      periodExpenses = if (periodExpenses.exists(_.isEmptyObject)) None else periodExpenses,
      periodDisallowableExpenses = if (periodDisallowableExpenses.exists(_.isEmptyObject)) None else periodDisallowableExpenses
    )
  }

  implicit val writes: OWrites[Def2_RetrievePeriodSummaryResponse] = Json.writes[Def2_RetrievePeriodSummaryResponse]

  implicit object Def2_RetrieveAnnualSubmissionLinksFactory
      extends HateoasLinksFactory[Def2_RetrievePeriodSummaryResponse, RetrievePeriodSummaryHateoasData] {

    override def links(appConfig: AppConfig, data: RetrievePeriodSummaryHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendPeriodSummary(appConfig, nino, businessId, periodId, taxYear),
        retrievePeriodSummary(appConfig, nino, businessId, periodId, taxYear),
        listPeriodSummaries(appConfig, nino, businessId, taxYear, isSelf = false)
      )
    }

  }

}

case class RetrievePeriodSummaryHateoasData(nino: Nino, businessId: BusinessId, periodId: String, taxYear: Option[TaxYear]) extends HateoasData
