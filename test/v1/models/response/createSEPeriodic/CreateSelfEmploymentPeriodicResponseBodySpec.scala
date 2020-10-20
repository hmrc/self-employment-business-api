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

package v1.models.response.createSEPeriodic

import play.api.libs.json.Json
import support.UnitSpec

class CreateSelfEmploymentPeriodicResponseBodySpec extends UnitSpec {

  val desJson = Json.parse(
    """
      |{
      |   "transactionReference": "2017090920170909"
      |}
      |""".stripMargin)

  val mtdJson = Json.parse(
    """
      |{
      |   "periodId": "2017090920170909"
      |}
      |""".stripMargin)

  val model = CreateSelfEmploymentPeriodicResponseBody("2017090920170909")

  "reads" should {
    "return a model" when {
      "passed valid json" in {
        desJson.as[CreateSelfEmploymentPeriodicResponseBody] shouldBe model
      }
    }
  }

  "writes" should {
    "return json" when {
      "passed a model" in {
        Json.toJson(model) shouldBe mtdJson
      }
    }
  }
}
