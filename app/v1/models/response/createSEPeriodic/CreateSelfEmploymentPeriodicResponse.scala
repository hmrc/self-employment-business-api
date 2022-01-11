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

package v1.models.response.createSEPeriodic

import config.AppConfig
import play.api.libs.json.{Json, OFormat}
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, Link}


case class CreateSelfEmploymentPeriodicResponse(periodId: String)


object CreateSelfEmploymentPeriodicResponse extends HateoasLinks {
  implicit val format: OFormat[CreateSelfEmploymentPeriodicResponse] = Json.format[CreateSelfEmploymentPeriodicResponse]

  implicit object LinksFactory extends HateoasLinksFactory[CreateSelfEmploymentPeriodicResponse, CreateSelfEmploymentPeriodicHateoasData] {
    override def links(appConfig: AppConfig, data: CreateSelfEmploymentPeriodicHateoasData): Seq[Link] = {
      import data._
      Seq(
        retrievePeriodicUpdate(appConfig, nino, businessId, periodId)
      )
    }
  }
}

case class CreateSelfEmploymentPeriodicHateoasData(nino: String, businessId: String, periodId: String) extends HateoasData
