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

package v3.retrievePeriodSummary.def2.model.response

import shared.hateoas.{Link, Method}
import shared.models.domain.{BusinessId, Nino}
import play.api.libs.json.{JsValue, Json}
import shared.utils.UnitSpec
import shared.config.MockAppConfig
import v3.retrievePeriodSummary.def2.model.Def2_RetrievePeriodSummaryFixture
import v3.retrievePeriodSummary.model.response.{Def2_RetrievePeriodSummaryResponse, RetrievePeriodSummaryHateoasData}

class Def2_RetrievePeriodSummaryResponseSpec extends UnitSpec with MockAppConfig with Def2_RetrievePeriodSummaryFixture {

  "round trip" should {
    "return mtd json" when {
      "given valid full downstream json" in {
        val result = Json.toJson(def2_DownstreamFullJson.as[Def2_RetrievePeriodSummaryResponse])
        result shouldBe def2_MtdFullJson
      }
      "given valid consolidated downstream json" in {
        val result: JsValue = Json.toJson(def2_DownstreamConsolidatedJson.as[Def2_RetrievePeriodSummaryResponse])
        result shouldBe def2_MtdConsolidatedJson
      }
      "given valid minimal downstream json" in {
        Json.toJson(def2_DownstreamMinimalJson.as[Def2_RetrievePeriodSummaryResponse]) shouldBe def2_MtdMinimalJson
      }
    }
  }

  "LinksFactory" should {

    "produce the correct links" when {
      "called" in {
        val data: RetrievePeriodSummaryHateoasData = RetrievePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(taxYear))

        MockedAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        Def2_RetrievePeriodSummaryResponse.Def2_RetrievePeriodSubmissionLinksFactory.links(mockAppConfig, data) shouldBe List(
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
