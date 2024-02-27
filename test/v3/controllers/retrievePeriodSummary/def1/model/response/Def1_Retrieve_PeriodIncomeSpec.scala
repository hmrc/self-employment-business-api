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

package v3.controllers.retrievePeriodSummary.def1.model.response

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class Def1_Retrieve_PeriodIncomeSpec extends UnitSpec {

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "turnover":  100.25,
      |  "other": 200.25
      |}
    """.stripMargin
  )

  val desJson: JsValue = Json.parse(
    """
      |{
      |   "turnover": 100.25,
      |   "other": 200.25
      |}
    """.stripMargin
  )

  private val periodIncome = Def1_Retrieve_PeriodIncome(
    turnover = Some(100.25),
    other = Some(200.25)
  )

  private val emptyPeriodIncome = Def1_Retrieve_PeriodIncome(None, None)

  "reads" should {
    "return the parsed object" when {
      "given a valid json" in {
          val result = mtdJson.as[Def1_Retrieve_PeriodIncome]
        result shouldBe periodIncome
      }
    }
  }

  "writes" should {
    "return json" when {
      "given a valid object" in {
          val result = Json.toJson(periodIncome)
        result shouldBe desJson
      }
    }
  }

  "isEmptyObject" should {
    "return true" when {
      "all fields are None" in {
        emptyPeriodIncome.isEmptyObject shouldBe true
      }
    }

    "return false" when {
      "turnover has a value" in {
        val periodIncome = emptyPeriodIncome.copy(turnover = Some(100.99))
        periodIncome.isEmptyObject shouldBe false
      }
      "other has a value" in {
        val periodIncome = emptyPeriodIncome.copy(other = Some(200.99))
        periodIncome.isEmptyObject shouldBe false
      }
    }

  }

}
