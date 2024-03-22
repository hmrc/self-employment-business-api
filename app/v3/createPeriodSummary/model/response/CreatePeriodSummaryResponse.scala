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

package v3.createPeriodSummary.model.response

import api.hateoas.{HateoasData, HateoasLinks, HateoasLinksFactory, Link}
import api.models.domain.{BusinessId, Nino, TaxYear}
import config.AppConfig
import play.api.libs.json.{Json, OFormat}

case class CreatePeriodSummaryResponse(periodId: String)

object CreatePeriodSummaryResponse extends HateoasLinks {
  implicit val format: OFormat[CreatePeriodSummaryResponse] = Json.format[CreatePeriodSummaryResponse]

  implicit object LinksFactory extends HateoasLinksFactory[CreatePeriodSummaryResponse, CreatePeriodSummaryHateoasData] {

    override def links(appConfig: AppConfig, data: CreatePeriodSummaryHateoasData): Seq[Link] = {
      import data._
      List(
        amendPeriodSummary(appConfig, nino, businessId, periodId, taxYear),
        retrievePeriodSummary(appConfig, nino, businessId, periodId, taxYear),
        listPeriodSummaries(appConfig, nino, businessId, taxYear, isSelf = false)
      )
    }

  }

}

case class CreatePeriodSummaryHateoasData(nino: Nino, businessId: BusinessId, periodId: String, taxYear: Option[TaxYear]) extends HateoasData
