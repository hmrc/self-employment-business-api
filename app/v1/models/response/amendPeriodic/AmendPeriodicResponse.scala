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

package v1.models.response.amendPeriodic

import config.AppConfig
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.domain.{BusinessId, Nino}
import v1.models.hateoas.{HateoasData, Link}

object AmendPeriodicResponse extends HateoasLinks {

  implicit object LinksFactory extends HateoasLinksFactory[Unit, AmendPeriodicHateoasData] {

    override def links(appConfig: AppConfig, data: AmendPeriodicHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendPeriodicSummary(appConfig, nino, businessId, periodId),
        retrievePeriodicSummary(appConfig, nino, businessId, periodId),
        listPeriodicSummary(appConfig, nino, businessId, isSelf = false)
      )
    }

  }

}

case class AmendPeriodicHateoasData(nino: Nino, businessId: BusinessId, periodId: String) extends HateoasData
