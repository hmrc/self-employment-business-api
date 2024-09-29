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

package v4.createPeriodSummary.def2.model.request

import play.api.libs.json.{JsValue, Json}
import shared.utils.UnitSpec

class Def2_Create_PeriodIncomeSpec extends UnitSpec {

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "turnover":  100.25,
      |  "other": 200.25,
      |  "taxTakenOffTradingIncome": 300.25
      |}
    """.stripMargin
  )

  val desJson: JsValue = Json.parse(
    """
      |{
      |   "turnover": 100.25,
      |   "other": 200.25,
      |   "taxTakenOffTradingIncome": 300.25
      |}
    """.stripMargin
  )

  val model: Def2_Create_PeriodIncome = Def2_Create_PeriodIncome(
    turnover = Some(100.25),
    other = Some(200.25),
    taxTakenOffTradingIncome = Some(300.25)
  )

  "reads" should {
    "return a model" when {
      "passed a valid json" in {
        mtdJson.as[Def2_Create_PeriodIncome] shouldBe model
      }
    }
  }

  "writes" should {
    "return json" when {
      "passed a valid model" in {
        Json.toJson(model) shouldBe desJson
      }
    }
  }

}
