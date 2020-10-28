/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.libs.json.Json
import support.UnitSpec

class ListSelfEmploymentPeriodicResponseSpec extends UnitSpec {

  val generateModelEmpty = ListSelfEmploymentPeriodicResponse(Seq())

  val beforeGenerateModel = ListSelfEmploymentPeriodicResponse(
    Seq(PeriodDetails(
        "",
        "2020-01-01",
        "2020-01-01"
      )))

  val afterGenerateModel = ListSelfEmploymentPeriodicResponse(
    Seq(PeriodDetails(
        "2020-01-01_2020-01-01",
        "2020-01-01",
        "2020-01-01"
      )))

  val beforeGenerateModelMultiple = ListSelfEmploymentPeriodicResponse(
    Seq(PeriodDetails(
        "",
        "2020-01-01",
        "2020-01-01"
        ),
        PeriodDetails(
          "",
          "2020-01-01",
          "2020-01-01"
        )))

  val afterGenerateModelMultiple = ListSelfEmploymentPeriodicResponse(
    Seq(PeriodDetails(
        "2020-01-01_2020-01-01",
        "2020-01-01",
        "2020-01-01"
      ),
        PeriodDetails(
          "2020-01-01_2020-01-01",
          "2020-01-01",
          "2020-01-01"
        )))

  val jsonEmpty = Json.parse(
    """
      |{
      |   "periods": [
      |    ]
      |}
      |""".stripMargin
  )

  val json = Json.parse(
    """
      |{
      |   "periods": [
      |       {
      |           "periodId": "2020-01-01_2020-01-01",
      |           "from": "2020-01-01",
      |           "to": "2020-01-01"
      |       }
      |    ]
      |}
      |""".stripMargin
  )

  val jsonMulitple = Json.parse(
    """
      |{
      |   "periods": [
      |    {
      |           "periodId": "2020-01-01_2020-01-01",
      |           "from": "2020-01-01",
      |           "to": "2020-01-01"
      |    },
      |    {
      |           "periodId": "2020-01-01_2020-01-01",
      |           "from": "2020-01-01",
      |           "to": "2020-01-01"
      |    }
      |  ]
      |}
      |""".stripMargin
  )

  val desJsonEmpty = Json.parse(
    """
      |{
      |   "periods": [
      |     ]
      |}
      |""".stripMargin
  )

  val desJson = Json.parse(
    """
      |{
      |   "periods": [
      |           {
      |               "transactionReference": "3123123123121",
      |               "from": "2020-01-01",
      |               "to": "2020-01-01"
      |           }
      |     ]
      |}
      |""".stripMargin
  )

  val desJsonMultiple = Json.parse(
    """
      |{
      |   "periods": [
      |           {
      |               "transactionReference": "32131123131",
      |               "from": "2020-01-01",
      |               "to": "2020-01-01"
      |           },
      |           {
      |               "transactionReference": "3123123123121",
      |               "from": "2020-01-01",
      |               "to": "2020-01-01"
      |           }
      |     ]
      |}
      |""".stripMargin
  )

  "reads" should {

    "read from a json" when {

      "a valid request is made" in {
        desJson.as[ListSelfEmploymentPeriodicResponse] shouldBe beforeGenerateModel
      }

      "a valid empty request is made" in {
        desJsonEmpty.as[ListSelfEmploymentPeriodicResponse] shouldBe generateModelEmpty
      }

      "a valid request with multiple fields is made" in {
        desJsonMultiple.as[ListSelfEmploymentPeriodicResponse] shouldBe beforeGenerateModelMultiple
      }
    }
  }
  "writes" should {

    "write to a model" when {

      "a valid request is made" in {
        Json.toJson(afterGenerateModel) shouldBe json
      }
      "a valid empty request is made" in {
        Json.toJson(generateModelEmpty) shouldBe jsonEmpty
      }
      "a valid request with mutiple fields is made" in {
        Json.toJson(afterGenerateModelMultiple) shouldBe jsonMulitple
      }
    }
  }
}

