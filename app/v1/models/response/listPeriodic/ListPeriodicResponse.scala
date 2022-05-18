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

package v1.models.response.listPeriodic

import cats.Functor
import config.AppConfig
import play.api.libs.json.{Json, OWrites, Reads, Writes}
import v1.hateoas.{HateoasLinks, HateoasListLinksFactory}
import v1.models.hateoas.{HateoasData, Link}

case class ListPeriodicResponse[I](periods: Seq[I])

object ListPeriodicResponse extends HateoasLinks {
  implicit def reads: Reads[ListPeriodicResponse[PeriodDetails]]   = Json.format[ListPeriodicResponse[PeriodDetails]]
  implicit def writes[I: Writes]: OWrites[ListPeriodicResponse[I]] = Json.writes[ListPeriodicResponse[I]]

  implicit object LinksFactory extends HateoasListLinksFactory[ListPeriodicResponse, PeriodDetails, ListPeriodicHateoasData] {

    override def links(appConfig: AppConfig, data: ListPeriodicHateoasData): Seq[Link] = {
      Seq(
        listPeriodicSummarySelf(appConfig, data.nino, data.businessId),
        createPeriodicSummary(appConfig, data.nino, data.businessId)
      )
    }

    override def itemLinks(appConfig: AppConfig, data: ListPeriodicHateoasData, item: PeriodDetails): Seq[Link] =
      Seq(
        retrievePeriodicSummary(appConfig, data.nino, data.businessId, item.periodId)
      )

  }

  implicit object ResponseFunctor extends Functor[ListPeriodicResponse] {

    override def map[A, B](fa: ListPeriodicResponse[A])(f: A => B): ListPeriodicResponse[B] =
      ListPeriodicResponse(fa.periods.map(f))

  }

}

case class ListPeriodicHateoasData(nino: String, businessId: String) extends HateoasData
