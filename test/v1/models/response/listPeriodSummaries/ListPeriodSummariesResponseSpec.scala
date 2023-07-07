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

package v1.models.response.listPeriodSummaries

import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.hateoas.Link
import api.models.hateoas.Method._
import mocks.MockAppConfig
import play.api.libs.json.Json
import support.UnitSpec

class ListPeriodSummariesResponseSpec extends UnitSpec with MockAppConfig {

  private val model = ListPeriodSummariesResponse(
    Seq(
      PeriodDetails(
        periodId = "2019-01-01_2020-01-01",
        periodStartDate = "2019-01-01",
        periodEndDate = "2020-01-01"
        //        periodCreationDate = Some("2020-01-02") // To be reinstated, see MTDSA-15595
      ),
      PeriodDetails(
        periodId = "2019-01-01_2020-01-01",
        periodStartDate = "2019-01-01",
        periodEndDate = "2020-01-01"
        //        periodCreationDate = Some("2020-01-02") // To be reinstated, see MTDSA-15595
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
            |           "to": "2020-01-01",
            |           "periodCreationDate": "2020-01-02"
            |      },
            |      {
            |           "transactionReference": "3123123123121",
            |           "from": "2019-01-01",
            |           "to": "2020-01-01",
            |           "periodCreationDate": "2020-01-02"
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

  "LinksFactory" when {
    val nino           = "AA111111A"
    val businessId     = "id"
    val periodId       = "periodId"
    val hateoasData    = ListPeriodSummariesHateoasData(Nino(nino), BusinessId(businessId), None)
    val hateoasDataTys = hateoasData.copy(taxYear = Some(TaxYear.fromMtd("2023-24")))

    "TYS feature switch is disabled" should {
      "return the correct top-level links" in {
        MockAppConfig.apiGatewayContext.returns("test/context").anyNumberOfTimes()

        ListPeriodSummariesResponse.LinksFactory.links(mockAppConfig, hateoasData) shouldBe Seq(
          Link(href = s"/test/context/$nino/$businessId/period", method = POST, rel = "create-self-employment-period-summary"),
          Link(href = s"/test/context/$nino/$businessId/period", method = GET, rel = "self")
        )
      }

      "return the correct item-level links" in {
        MockAppConfig.apiGatewayContext.returns("test/context").anyNumberOfTimes()

        val periodDetails = PeriodDetails(
          periodId,
          "",
          ""
          //          Some("2020-01-02") // To be reinstated, see MTDSA-15595
        )

        ListPeriodSummariesResponse.LinksFactory.itemLinks(mockAppConfig, hateoasData, periodDetails) shouldBe Seq(
          Link(href = s"/test/context/$nino/$businessId/period/$periodId", method = GET, rel = "self")
        )
      }
    }

    "TYS feature switch is enabled and tax year is TYS" should {
      "return the correct top-level links" in {
        MockAppConfig.apiGatewayContext.returns("test/context").anyNumberOfTimes()

        ListPeriodSummariesResponse.LinksFactory.links(mockAppConfig, hateoasDataTys) shouldBe Seq(
          Link(href = s"/test/context/$nino/$businessId/period", method = POST, rel = "create-self-employment-period-summary"),
          Link(href = s"/test/context/$nino/$businessId/period?taxYear=2023-24", method = GET, rel = "self")
        )
      }

      "return the correct item-level links" in {
        MockAppConfig.apiGatewayContext.returns("test/context").anyNumberOfTimes()

        val periodDetails = PeriodDetails(
          periodId,
          "",
          ""
          //          Some("2020-01-02") // To be reinstated, see MTDSA-15595
        )

        ListPeriodSummariesResponse.LinksFactory.itemLinks(mockAppConfig, hateoasDataTys, periodDetails) shouldBe Seq(
          Link(href = s"/test/context/$nino/$businessId/period/$periodId?taxYear=2023-24", method = GET, rel = "self")
        )
      }
    }
  }

}
