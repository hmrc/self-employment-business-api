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

package v1.models.response.listPeriodSummaries

import mocks.MockAppConfig
import play.api.libs.json.Json
import support.UnitSpec
import v1.models.domain.{BusinessId, Nino}
import v1.models.hateoas.Link
import v1.models.hateoas.Method._

class ListPeriodSummariesResponseSpec extends UnitSpec with MockAppConfig {

  private val model = ListPeriodSummariesResponse(
    Seq(
      PeriodDetails(
        periodId = "2019-01-01_2020-01-01",
        periodStartDate = "2019-01-01",
        periodEndDate = "2020-01-01"
      ),
      PeriodDetails(
        periodId = "2019-01-01_2020-01-01",
        periodStartDate = "2019-01-01",
        periodEndDate = "2020-01-01"
      )
    )
  )

  "reads" should {
    "read from downstream json" in {
      Json
        .parse(
          """
          |{
          |   "periods": [
          |      {
          |           "transactionReference": "32131123131",
          |           "from": "2019-01-01",
          |           "to": "2020-01-01"
          |      },
          |      {
          |           "transactionReference": "3123123123121",
          |           "from": "2019-01-01",
          |           "to": "2020-01-01"
          |      }
          |   ]
          |}
    """.stripMargin
        )
        .as[ListPeriodSummariesResponse[PeriodDetails]] shouldBe model
    }
  }

  "writes" should {
    "write to mtd json" in {
      Json.toJson(model) shouldBe Json.parse(
        """
            |{
            |   "periods": [
            |     {
            |         "periodId": "2019-01-01_2020-01-01",
            |         "periodStartDate": "2019-01-01",
            |         "periodEndDate": "2020-01-01"
            |     },
            |     {
            |         "periodId": "2019-01-01_2020-01-01",
            |         "periodStartDate": "2019-01-01",
            |         "periodEndDate": "2020-01-01"
            |     }
            |   ]
            |}
    """.stripMargin
      )
    }
  }

  "LinksFactory" should {
    val nino        = "AA111111A"
    val businessId  = "id"
    val periodId    = "periodId"
    val hateoasData = ListPeriodSummariesHateoasData(Nino(nino), BusinessId(businessId))

    "return the correct top-level links" in {
      MockAppConfig.apiGatewayContext returns "test/context" anyNumberOfTimes ()

      ListPeriodSummariesResponse.LinksFactory.links(mockAppConfig, hateoasData) shouldBe Seq(
        Link(href = s"/test/context/$nino/$businessId/period", method = POST, rel = "create-self-employment-period-summary"),
        Link(href = s"/test/context/$nino/$businessId/period", method = GET, rel = "self")
      )
    }

    "return the correct item-level links" in {
      MockAppConfig.apiGatewayContext returns "test/context" anyNumberOfTimes ()

      ListPeriodSummariesResponse.LinksFactory.itemLinks(mockAppConfig, hateoasData, PeriodDetails(periodId, "", "")) shouldBe Seq(
        Link(href = s"/test/context/$nino/$businessId/period/$periodId", method = GET, rel = "self")
      )
    }
  }

}
