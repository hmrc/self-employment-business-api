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

class PeriodDetailsSpec extends UnitSpec {

  val beforeGenerateModel = PeriodDetails("", "2020-01-01", "2020-02-02")
  val afterGenerateModel = PeriodDetails("2020-01-01_2020-01-01", "2020-01-01", "2020-02-02")

  val json = Json.parse(
    """
      |{
      |   "periodId": "2020-01-01_2020-01-01",
      |   "from": "2020-01-01",
      |   "to": "2020-02-02"
      |}
      |""".stripMargin
  )

  val desJson = Json.parse(
    """
      |{
      |   "transactionReference": "1111111111",
      |   "from": "2020-01-01",
      |   "to": "2020-02-02"
      |}
      |""".stripMargin
  )

  "reads" should {

    "read from a json" when {

      "a valid request is made" in  {
        desJson.as[PeriodDetails] shouldBe beforeGenerateModel
      }
    }
  }
  "writes" should {

    "write to a model" when {

      "a valid request is made" in {
        Json.toJson(afterGenerateModel) shouldBe json
      }
    }
  }
}