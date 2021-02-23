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

class ExpensesAmountObjectSpec extends UnitSpec{

  val fullModel = ExpensesAmountObject(500.25, Some(500.25))

  val noOptionModel = ExpensesAmountObject(500.25, None)

  val fullJson = Json.parse(
    """
      |{
      | "amount": 500.25,
      | "disallowableAmount": 500.25
      |}
      |""".stripMargin)

  val noOptionJson = Json.parse(
    """
      |{
      | "amount": 500.25
      |}
      |""".stripMargin)


  "reads" should {

    "read from a model" when {

      "a valid request is made" in {
        fullJson.as[ExpensesAmountObject] shouldBe fullModel
      }
    }

    "read from a model with no disallowable amount" when {

      "a valid request with no disallowable amount is made" in {
        noOptionJson.as[ExpensesAmountObject] shouldBe noOptionModel
      }
    }
  }

  "writes" should {

    "write to a model" when {

      "a valid request is made" in {
        Json.toJson(fullModel) shouldBe fullJson
      }
    }

    "write to a model with no disallowable amount" when {

      "a valid request is made with no disallowable amount" in {
        Json.toJson(noOptionModel) shouldBe noOptionJson
      }
    }
  }
}


