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

package v1.models.response.amendSEAnnual

import config.AppConfig
import play.api.libs.json.{Json, OWrites, Reads}
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, Link}

case class AmendAnnualSummaryResponse(
                                       transactionReference: String
                                     )

object AmendAnnualSummaryResponse extends HateoasLinks {

  implicit val reads: Reads[AmendAnnualSummaryResponse] = Json.reads[AmendAnnualSummaryResponse]

  implicit val writes: OWrites[AmendAnnualSummaryResponse] = OWrites[AmendAnnualSummaryResponse] { _ =>
    Json.obj()
  }

  implicit object AmendSelfEmploymentAnnualSummaryLinksFactory extends HateoasLinksFactory[AmendAnnualSummaryResponse, AmendAnnualSummaryHateoasData] {
    override def links(appConfig: AppConfig, data: AmendAnnualSummaryHateoasData): Seq[Link] = {
      import data._
      Seq(
        retrieveAnnualSummary(appConfig, nino, businessId, taxYear),
        amendAnnualSummary(appConfig, nino, businessId, taxYear),
        deleteAnnualSummary(appConfig, nino, businessId, taxYear)
      )
    }
  }
}

case class AmendAnnualSummaryHateoasData(nino: String, businessId: String, taxYear: String) extends HateoasData
