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

package v1.models.response.listPeriodSummaries

import cats.Functor
import config.AppConfig
import play.api.libs.json.{Json, OWrites, Reads, Writes}
import v1.hateoas.{HateoasLinks, HateoasListLinksFactory}
import v1.models.domain.{BusinessId, Nino}
import v1.models.hateoas.{HateoasData, Link}

case class ListPeriodSummariesResponse[I](periods: Seq[I])

object ListPeriodSummariesResponse extends HateoasLinks {

  implicit val reads: Reads[ListPeriodSummariesResponse[PeriodDetails]] = Json.reads

  implicit def writes[I: Writes]: OWrites[ListPeriodSummariesResponse[I]] = Json.writes

  implicit object LinksFactory extends HateoasListLinksFactory[ListPeriodSummariesResponse, PeriodDetails, ListPeriodSummariesHateoasData] {

    override def links(appConfig: AppConfig, data: ListPeriodSummariesHateoasData): Seq[Link] = {
      Seq(
        createPeriodSummary(appConfig, data.nino, data.businessId),
        listPeriodSummaries(appConfig, data.nino, data.businessId, isSelf = true)
      )
    }

    override def itemLinks(appConfig: AppConfig, data: ListPeriodSummariesHateoasData, item: PeriodDetails): Seq[Link] =
      Seq(
        retrievePeriodSummary(appConfig, data.nino, data.businessId, item.periodId)
      )

  }

  implicit object ResponseFunctor extends Functor[ListPeriodSummariesResponse] {

    override def map[A, B](fa: ListPeriodSummariesResponse[A])(f: A => B): ListPeriodSummariesResponse[B] =
      ListPeriodSummariesResponse(fa.periods.map(f))

  }

}

case class ListPeriodSummariesHateoasData(nino: Nino, businessId: BusinessId) extends HateoasData