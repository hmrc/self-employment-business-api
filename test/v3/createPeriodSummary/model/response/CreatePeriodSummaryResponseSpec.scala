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

import shared.hateoas.{Link, Method}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import play.api.libs.json.{JsValue, Json}
import shared.config.MockSharedAppConfig
import shared.utils.UnitSpec

class CreatePeriodSummaryResponseSpec extends UnitSpec with MockSharedAppConfig {

  val json: JsValue = Json.parse(
    """
      |{
      |   "periodId": "2017090920170909"
      |}
    """.stripMargin
  )

  val model: CreatePeriodSummaryResponse = CreatePeriodSummaryResponse("2017090920170909")

  "reads" should {
    "return a model" when {
      "passed valid json" in {
        json.as[CreatePeriodSummaryResponse] shouldBe model
      }
    }
  }

  "writes" should {
    "return json" when {
      "passed a model" in {
        Json.toJson(model) shouldBe json
      }
    }
  }

  "LinksFactory" should {
    "produce the correct links with TYS disabled" when {
      "called" in {
        val nino       = "AA111111A"
        val businessId = "id"
        val periodId   = "periodId"
        val data       = CreatePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, None)

        MockedSharedAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        CreatePeriodSummaryResponse.LinksFactory.links(mockSharedAppConfig, data) shouldBe List(
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.PUT, rel = "amend-self-employment-period-summary"),
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.GET, rel = "self"),
          Link(href = s"/my/context/$nino/$businessId/period", method = Method.GET, rel = "list-self-employment-period-summaries")
        )
      }
    }

    "produce the correct links with TYS enabled and the tax year is TYS" when {
      "called" in {
        val nino       = "AA111111A"
        val businessId = "id"
        val periodId   = "periodId"
        val taxYear    = TaxYear.fromMtd("2023-24")
        val data       = CreatePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(taxYear))

        MockedSharedAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        val result: Seq[Link] = CreatePeriodSummaryResponse.LinksFactory.links(mockSharedAppConfig, data)

        result shouldBe List(
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
