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

package v1.models.request.createSEPeriodic

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class IncomesSpec extends UnitSpec {

  val fullModel: Incomes = Incomes(
    Some(IncomesAmountObject(500.12)),
    Some(IncomesAmountObject(500.12))
  )

  val emptyModel: Incomes = Incomes(None, None)

  val fullJson: JsValue = Json.parse(
    """
      |{
      |  "turnover": {
      |    "amount": 500.12
      |  },
      |  "other": {
      |    "amount": 500.12
      |  }
      |}
    """.stripMargin
  )

  val emptyJson: JsValue = Json.parse(""" {} """)

  "reads" should {
    "read from a model" when {
      "a valid request is made" in {
        fullJson.as[Incomes] shouldBe fullModel
      }
    }

    "read from an empty model" when {
      "a valid request with an empty model is made" in {
        emptyJson.as[Incomes] shouldBe emptyModel
      }
    }
  }

  "writes" should {
    "write to a model" when {
      val desJson = Json.parse(
        """
          |{
          |  "turnover": 500.12,
          |  "other": 500.12
          |}
        """.stripMargin
      )

      "a valid request is made" in {
        Json.toJson(fullModel) shouldBe desJson
      }
    }

    "write to an empty model" when {
      "a valid request is made" in {
        Json.toJson(emptyModel) shouldBe emptyJson
      }
    }
  }
}