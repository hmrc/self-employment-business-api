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

package v1.models.response.retrieveSEPeriodic

import play.api.libs.json.Json
import support.UnitSpec

class IncomesSpec extends UnitSpec {

  val mtdJson = Json.parse(
    """
      |{
      |"turnover": {
      |   "amount": 500.25
      |   },
      |"other": {
      |   "amount": 500.25
      |   }
      |}
      |""".stripMargin)

  val desJson = Json.parse(
    """
      |{
      |         "turnover": 500.25,
      |         "other": 500.25
      |}
      |
      |""".stripMargin)

  val model = Incomes(
    Some(IncomesAmountObject(500.25)),
    Some(IncomesAmountObject(500.25))
  )



  "reads" should {
    "return a model" when {
      "passed a valid json" in {
        desJson.as[Incomes] shouldBe model
      }
    }
  }

  "writes" should {
    "return json" when {
      "passed a valid model" in {
        Json.toJson(model) shouldBe mtdJson
      }
    }
  }
}
