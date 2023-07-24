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

package v2.models.response.amendPeriodSummary

import api.hateoas.{HateoasLinks, HateoasLinksFactory}
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.hateoas.{HateoasData, Link}
import config.AppConfig

object AmendPeriodSummaryResponse extends HateoasLinks {

  implicit object LinksFactory extends HateoasLinksFactory[Unit, AmendPeriodSummaryHateoasData] {

    override def links(appConfig: AppConfig, data: AmendPeriodSummaryHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendPeriodSummary(appConfig, nino, businessId, periodId, taxYear),
        retrievePeriodSummary(appConfig, nino, businessId, periodId, taxYear),
        listPeriodSummaries(appConfig, nino, businessId, taxYear, isSelf = false)
      )
    }

  }

}

case class AmendPeriodSummaryHateoasData(nino: Nino, businessId: BusinessId, periodId: String, taxYear: Option[TaxYear]) extends HateoasData
