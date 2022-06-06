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

package v1.models.response.listSEPeriodic

import cats.Functor
import config.AppConfig
import play.api.libs.json.{ Json, OWrites, Reads, Writes }
import v1.hateoas.{ HateoasLinks, HateoasListLinksFactory }
import v1.models.hateoas.{ HateoasData, HateoasDataBuilder, Link }
import v1.models.request.listSEPeriodic.ListSelfEmploymentPeriodicRawData

case class ListSelfEmploymentPeriodicResponse[I](periods: Seq[I])

object ListSelfEmploymentPeriodicResponse extends HateoasLinks {
  implicit def reads: Reads[ListSelfEmploymentPeriodicResponse[PeriodDetails]]   = Json.format[ListSelfEmploymentPeriodicResponse[PeriodDetails]]
  implicit def writes[I: Writes]: OWrites[ListSelfEmploymentPeriodicResponse[I]] = Json.writes[ListSelfEmploymentPeriodicResponse[I]]

  implicit object LinksFactory
      extends HateoasListLinksFactory[ListSelfEmploymentPeriodicResponse, PeriodDetails, ListSelfEmploymentPeriodicHateoasData] {
    override def links(appConfig: AppConfig, data: ListSelfEmploymentPeriodicHateoasData): Seq[Link] = {
      Seq(
        listPeriodicUpdate(appConfig, data.nino, data.businessId),
        createPeriodicUpdate(appConfig, data.nino, data.businessId),
      )
    }

    override def itemLinks(appConfig: AppConfig, data: ListSelfEmploymentPeriodicHateoasData, item: PeriodDetails): Seq[Link] =
      Seq(
        retrievePeriodicUpdate(appConfig, data.nino, data.businessId, item.periodId)
      )
  }

  implicit object ResponseFunctor extends Functor[ListSelfEmploymentPeriodicResponse] {
    override def map[A, B](fa: ListSelfEmploymentPeriodicResponse[A])(f: A => B): ListSelfEmploymentPeriodicResponse[B] =
      ListSelfEmploymentPeriodicResponse(fa.periods.map(f))
  }
}

case class ListSelfEmploymentPeriodicHateoasData(nino: String, businessId: String) extends HateoasData

object ListSelfEmploymentPeriodicHateoasData {
  implicit def dataBuilder[I]
    : HateoasDataBuilder[ListSelfEmploymentPeriodicRawData, ListSelfEmploymentPeriodicResponse[I], ListSelfEmploymentPeriodicHateoasData] =
    (raw: ListSelfEmploymentPeriodicRawData, _: ListSelfEmploymentPeriodicResponse[I]) => {
      import raw._
      ListSelfEmploymentPeriodicHateoasData(nino = nino, businessId = businessId)
    }
}
