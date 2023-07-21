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

package v3.models.response.retrievePeriodSummary

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class PeriodIncomeSpec extends UnitSpec {

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

  val model: PeriodIncome = PeriodIncome(
    turnover = Some(100.25),
    other = Some(200.25),
    taxTakenOffTradingIncome = Some(300.25)
  )

  val emptyModel = PeriodIncome(None, None, None)

  "reads" should {
    "return a model" when {
      "passed a valid json" in {
        mtdJson.as[PeriodIncome] shouldBe model
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

  "isEmptyObject" should {
    "return true" when {
      "all fields are None" in {
        emptyModel.isEmptyObject shouldBe true
      }
    }

    "return false" when {
      "turnover has a value" in {
        val periodIncome = emptyModel.copy(turnover = Some(100.99))
        periodIncome.isEmptyObject shouldBe false
      }
      "other has a value" in {
        val periodIncome = emptyModel.copy(other = Some(200.99))
        periodIncome.isEmptyObject shouldBe false
      }
      "taxTakenOffTradingIncome has a value" in {
        val periodIncome = emptyModel.copy(taxTakenOffTradingIncome = Some(300.99))
        periodIncome.isEmptyObject shouldBe false
      }
    }

  }
}
