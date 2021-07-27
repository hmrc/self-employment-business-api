/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.models.response.listSEPeriodic

import mocks.MockAppConfig
import play.api.libs.json.Json
import support.UnitSpec
import v1.models.hateoas.Link
import v1.models.hateoas.Method._

class ListSelfEmploymentPeriodicResponseSpec extends UnitSpec with MockAppConfig {

  val generateModelEmpty: ListSelfEmploymentPeriodicResponse[PeriodDetails] = ListSelfEmploymentPeriodicResponse(Seq())

  private val model = ListSelfEmploymentPeriodicResponse(
    Seq(PeriodDetails(
      "2019-01-01_2020-01-01",
      "2019-01-01",
      "2020-01-01"
    ))
  )

  private val modelMultiple = ListSelfEmploymentPeriodicResponse(
    Seq(
      PeriodDetails(
        "2019-01-01_2020-01-01",
        "2019-01-01",
        "2020-01-01"
      ),
      PeriodDetails(
        "2019-01-01_2020-01-01",
        "2019-01-01",
        "2020-01-01"
      )
    )
  )

  private val jsonEmptyArray = Json.parse(
    """
      |{
      |   "periods": [ ]
      |}
    """.stripMargin
  )

  private val json = Json.parse(
    """
      |{
      |   "periods": [
      |       {
      |           "periodId": "2019-01-01_2020-01-01",
      |           "from": "2019-01-01",
      |           "to": "2020-01-01"
      |       }
      |    ]
      |}
    """.stripMargin
  )

  private val jsonMultiple = Json.parse(
    """
      |{
      |   "periods": [
      |     {
      |         "periodId": "2019-01-01_2020-01-01",
      |         "from": "2019-01-01",
      |         "to": "2020-01-01"
      |     },
      |     {
      |         "periodId": "2019-01-01_2020-01-01",
      |         "from": "2019-01-01",
      |         "to": "2020-01-01"
      |     }
      |   ]
      |}
    """.stripMargin
  )

  private val desJson = Json.parse(
    """
      |{
      |   "periods": [
      |      {
      |           "transactionReference": "3123123123121",
      |           "from": "2019-01-01",
      |           "to": "2020-01-01"
      |      }
      |   ]
      |}
    """.stripMargin
  )

  private val desJsonMultiple = Json.parse(
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

  "reads" should {
    "read from a json" when {
      "a valid request is made" in {
        desJson.as[ListSelfEmploymentPeriodicResponse[PeriodDetails]] shouldBe model
      }

      "a valid empty request is made" in {
        jsonEmptyArray.as[ListSelfEmploymentPeriodicResponse[PeriodDetails]] shouldBe generateModelEmpty
      }

      "a valid request with multiple fields is made" in {
        desJsonMultiple.as[ListSelfEmploymentPeriodicResponse[PeriodDetails]] shouldBe modelMultiple
      }
    }
  }

  "writes" should {
    "write to a model" when {
      "a valid request is made" in {
        Json.toJson(model) shouldBe json
      }

      "a valid empty request is made" in {
        Json.toJson(generateModelEmpty) shouldBe jsonEmptyArray
      }

      "a valid request with multiple fields is made" in {
        Json.toJson(modelMultiple) shouldBe jsonMultiple
      }
    }
  }

  "LinksFactory" should {
    "return the correct top-level links" in {
      MockAppConfig.apiGatewayContext returns "test/context" anyNumberOfTimes()

      ListSelfEmploymentPeriodicResponse.LinksFactory.links(mockAppConfig, ListSelfEmploymentPeriodicHateoasData("nino", "id")) shouldBe Seq(
        Link(href = "/test/context/nino/id/period", method = GET, rel = "self"),
        Link(href = "/test/context/nino/id/period", method = POST, rel = "create-periodic-update"),
      )
    }
    "return the correct item-level links" in {
      MockAppConfig.apiGatewayContext returns "test/context" anyNumberOfTimes()

      ListSelfEmploymentPeriodicResponse.LinksFactory.itemLinks(mockAppConfig, ListSelfEmploymentPeriodicHateoasData("nino", "id"), PeriodDetails("periodId", "", "")) shouldBe Seq(
        Link(href = "/test/context/nino/id/period/periodId", method = GET, rel = "self"),
      )
    }
  }
}