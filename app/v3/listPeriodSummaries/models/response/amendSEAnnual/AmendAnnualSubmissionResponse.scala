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

package v3.listPeriodSummaries.models.response.amendSEAnnual

import api.hateoas.HateoasLinks
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.config.AppConfig
import shared.hateoas.{HateoasData, HateoasLinksFactory, Link}

object AmendAnnualSubmissionResponse extends HateoasLinks {

  implicit object AmendAnnualSubmissionLinksFactory extends HateoasLinksFactory[Unit, AmendAnnualSubmissionHateoasData] {

    override def links(appConfig: AppConfig, data: AmendAnnualSubmissionHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendAnnualSubmission(appConfig, nino, businessId, TaxYear.fromMtd(taxYear)),
        retrieveAnnualSubmission(appConfig, nino, businessId, TaxYear.fromMtd(taxYear)),
        deleteAnnualSubmission(appConfig, nino, businessId, TaxYear.fromMtd(taxYear))
      )
    }

  }

}

case class AmendAnnualSubmissionHateoasData(nino: Nino, businessId: BusinessId, taxYear: String) extends HateoasData
