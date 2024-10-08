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

package v3.amendPeriodSummary.model.response

import shared.config.MockSharedAppConfig
import shared.hateoas.{Link, Method}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.utils.UnitSpec

class AmendPeriodSummaryResponseSpec extends UnitSpec with MockSharedAppConfig {

  "LinksFactory" should {

    "produce the correct non-TYS links" when {
      "called" in {
        val nino       = "AA111111A"
        val businessId = "id"
        val periodId   = "periodId"

        val data: AmendPeriodSummaryHateoasData = AmendPeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, None)

        MockedSharedAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        AmendPeriodSummaryResponse.LinksFactory.links(mockSharedAppConfig, data) shouldBe List(
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.PUT, rel = "amend-self-employment-period-summary"),
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.GET, rel = "self"),
          Link(href = s"/my/context/$nino/$businessId/period", method = Method.GET, rel = "list-self-employment-period-summaries")
        )
      }
    }

    "produce the correct TYS links" when {
      "called" in {
        val nino       = "AA111111A"
        val businessId = "id"
        val periodId   = "periodId"
        val taxYear    = TaxYear.fromMtd("2023-24")

        val data = AmendPeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(taxYear))

        MockedSharedAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        AmendPeriodSummaryResponse.LinksFactory.links(mockSharedAppConfig, data) shouldBe List(
          Link(
            href = s"/my/context/$nino/$businessId/period/$periodId?taxYear=2023-24",
            method = Method.PUT,
            rel = "amend-self-employment-period-summary"),
          Link(href = s"/my/context/$nino/$businessId/period/$periodId?taxYear=2023-24", method = Method.GET, rel = "self"),
          Link(href = s"/my/context/$nino/$businessId/period?taxYear=2023-24", method = Method.GET, rel = "list-self-employment-period-summaries")
        )
      }
    }
  }

}
