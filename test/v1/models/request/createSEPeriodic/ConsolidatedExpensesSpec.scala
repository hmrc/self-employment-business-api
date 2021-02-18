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

package v1.models.request.createSEPeriodic

import play.api.libs.json.Json
import support.UnitSpec

class ConsolidatedExpensesSpec extends UnitSpec {

  val model = ConsolidatedExpenses(500.12)

  val json = Json.parse(
    """{
      |"consolidatedExpenses": 500.12
      |}
      |""".stripMargin)

  "reads" should {

    "read from a json" when {

      "a valid request is made" in  {
        json.as[ConsolidatedExpenses] shouldBe model
      }
    }
  }
  "writes" should {

    "write to a model" when {

      "a valid request is made" in {
        Json.toJson(model) shouldBe json
      }
    }
  }
}
