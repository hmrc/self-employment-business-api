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

package v3.models.response.retrievePeriodSummary

import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.hateoas.{Link, Method}
import mocks.MockAppConfig
import play.api.libs.json.Json
import support.UnitSpec
import v3.fixtures.RetrievePeriodSummaryFixture
class RetrievePeriodSummaryResponseSpec extends UnitSpec with MockAppConfig with RetrievePeriodSummaryFixture {

  "round trip" should {
    "return mtd json" when {
      "passed valid full downstream json" in {
        Json.toJson(downstreamFullJson.as[RetrievePeriodSummaryResponse]) shouldBe mtdFullJson
      }
      "passed valid consolidated downstream json" in {
        Json.toJson(downstreamConsolidatedJson.as[RetrievePeriodSummaryResponse]) shouldBe mtdConsolidatedJson
      }
      "passed valid minimal downstream json" in {
        Json.toJson(downstreamMinimalJson.as[RetrievePeriodSummaryResponse]) shouldBe mtdMinimalJson
      }
    }
  }

  "LinksFactory" should {

    "produce the correct links with TYS disabled" when {
      "called" in {
        val data: RetrievePeriodSummaryHateoasData = RetrievePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, None)

        MockAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        RetrievePeriodSummaryResponse.RetrieveAnnualSubmissionLinksFactory.links(mockAppConfig, data) shouldBe Seq(
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.PUT, rel = "amend-self-employment-period-summary"),
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.GET, rel = "self"),
          Link(href = s"/my/context/$nino/$businessId/period", method = Method.GET, rel = "list-self-employment-period-summaries")
        )
      }
    }

    "produce the correct links with TYS enabled and the tax year is TYS" when {
      "called" in {
        val data: RetrievePeriodSummaryHateoasData =
          RetrievePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(TaxYear.fromMtd("2023-24")))

        MockAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        RetrievePeriodSummaryResponse.RetrieveAnnualSubmissionLinksFactory.links(mockAppConfig, data) shouldBe Seq(
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
