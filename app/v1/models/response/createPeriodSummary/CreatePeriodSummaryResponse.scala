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

package v1.models.response.createPeriodSummary

import config.AppConfig
import play.api.libs.json.{Json, OFormat}
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.domain.{BusinessId, Nino}
import v1.models.hateoas.{HateoasData, Link}

case class CreatePeriodSummaryResponse(periodId: String)

object CreatePeriodSummaryResponse extends HateoasLinks {
  implicit val format: OFormat[CreatePeriodSummaryResponse] = Json.format[CreatePeriodSummaryResponse]

  implicit object LinksFactory extends HateoasLinksFactory[CreatePeriodSummaryResponse, CreatePeriodSummaryHateoasData] {

    override def links(appConfig: AppConfig, data: CreatePeriodSummaryHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendPeriodSummary(appConfig, nino, businessId, periodId),
        retrievePeriodSummary(appConfig, nino, businessId, periodId),
        listPeriodSummaries(appConfig, nino, businessId, isSelf = false)
      )
    }

  }

}

case class CreatePeriodSummaryHateoasData(nino: Nino, businessId: BusinessId, periodId: String) extends HateoasData