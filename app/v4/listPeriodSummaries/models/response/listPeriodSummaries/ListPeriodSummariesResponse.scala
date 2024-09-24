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

package v4.listPeriodSummaries.models.response.listPeriodSummaries

import api.hateoas.HateoasLinks
import cats.Functor
import play.api.libs.json.{Json, OWrites, Reads, Writes}
import shared.config.AppConfig
import shared.hateoas.{HateoasData, HateoasListLinksFactory, Link}
import shared.models.domain.{BusinessId, Nino, TaxYear}

import scala.collection.immutable

case class ListPeriodSummariesResponse[I](periods: Seq[I])

object ListPeriodSummariesResponse extends HateoasLinks {

  implicit def reads: Reads[ListPeriodSummariesResponse[PeriodDetails]] = Json.reads[ListPeriodSummariesResponse[PeriodDetails]]

  implicit def writes[I: Writes]: OWrites[ListPeriodSummariesResponse[I]] = Json.writes[ListPeriodSummariesResponse[I]]

  implicit object LinksFactory extends HateoasListLinksFactory[ListPeriodSummariesResponse, PeriodDetails, ListPeriodSummariesHateoasData] {

    override def itemLinks(appConfig: AppConfig, data: ListPeriodSummariesHateoasData, item: PeriodDetails): immutable.Seq[Link] =
      immutable.Seq(
        retrievePeriodSummary(appConfig, data.nino, data.businessId, item.periodId, data.taxYear)
      )

    override def links(appConfig: AppConfig, data: ListPeriodSummariesHateoasData): immutable.Seq[Link] = {

      immutable.Seq(
        createPeriodSummary(appConfig, data.nino, data.businessId),
        listPeriodSummaries(appConfig, data.nino, data.businessId, data.taxYear, isSelf = true)
      )
    }

  }

  implicit object ResponseFunctor extends Functor[ListPeriodSummariesResponse] {

    override def map[A, B](fa: ListPeriodSummariesResponse[A])(f: A => B): ListPeriodSummariesResponse[B] =
      ListPeriodSummariesResponse(fa.periods.map(f))

  }

}

case class ListPeriodSummariesHateoasData(nino: Nino, businessId: BusinessId, taxYear: Option[TaxYear]) extends HateoasData
