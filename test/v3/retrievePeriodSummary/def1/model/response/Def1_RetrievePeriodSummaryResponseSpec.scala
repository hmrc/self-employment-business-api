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

package v3.retrievePeriodSummary.def1.model.response

import shared.hateoas.{Link, Method}
import shared.models.domain.{BusinessId, Nino}
import play.api.libs.json.Json
import shared.UnitSpec
import shared.config.MockAppConfig
import v3.retrievePeriodSummary.def1.model.Def1_RetrievePeriodSummaryFixture
import v3.retrievePeriodSummary.model.response.{Def1_RetrievePeriodSummaryResponse, RetrievePeriodSummaryHateoasData}

class Def1_RetrievePeriodSummaryResponseSpec extends UnitSpec with MockAppConfig with Def1_RetrievePeriodSummaryFixture {

  "round trip" should {
    "return mtd json" when {
      "passed valid full downstream json" in {
        Json.toJson(def1_DownstreamFullJson.as[Def1_RetrievePeriodSummaryResponse]) shouldBe def1_MtdFullJson
      }
      "passed valid consolidated downstream json" in {
        Json.toJson(def1_DownstreamConsolidatedJson.as[Def1_RetrievePeriodSummaryResponse]) shouldBe def1_MtdConsolidatedJson
      }
      "passed valid minimal downstream json" in {
        Json.toJson(def1_DownstreamMinimalJson.as[Def1_RetrievePeriodSummaryResponse]) shouldBe def1_MtdMinimalJson
      }
    }
  }

  "LinksFactory" should {

    "produce the correct links" when {
      "called" in {
        val data: RetrievePeriodSummaryHateoasData = RetrievePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, None)

        MockAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        Def1_RetrievePeriodSummaryResponse.Def1_RetrieveAnnualSubmissionLinksFactory.links(mockAppConfig, data) shouldBe List(
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.PUT, rel = "amend-self-employment-period-summary"),
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.GET, rel = "self"),
          Link(href = s"/my/context/$nino/$businessId/period", method = Method.GET, rel = "list-self-employment-period-summaries")
        )
      }
    }
  }

}
