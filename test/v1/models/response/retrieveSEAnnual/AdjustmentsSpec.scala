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

package v1.models.response.retrieveSEAnnual

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class AdjustmentsSpec extends UnitSpec {

  val desJson: JsValue = Json.parse(
    """
      |{
      |  "includedNonTaxableProfits": 500.25,
      |  "basisAdjustment": 500.25,
      |  "overlapReliefUsed": 500.25,
      |  "accountingAdjustment": 500.25,
      |  "averagingAdjustment": 500.25,
      |  "lossBroughtForward": 500.25,
      |  "outstandingBusinessIncome": 500.25,
      |  "balancingChargeBPRA": 500.25,
      |  "balancingChargeOther": 500.25,
      |  "goodsAndServicesOwnUse": 500.25
      |}
      |""".stripMargin)

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "includedNonTaxableProfits": 500.25,
      |  "basisAdjustment": 500.25,
      |  "overlapReliefUsed":500.25,
      |  "accountingAdjustment": 500.25,
      |  "averagingAdjustment": 500.25,
      |  "lossBroughtForward": 500.25,
      |  "outstandingBusinessIncome": 500.25,
      |  "balancingChargeBPRA": 500.25,
      |  "balancingChargeOther":500.25,
      |  "goodsAndServicesOwnUse": 500.25
      |}
      |""".stripMargin)

  val model: Adjustments =
    Adjustments(Some(500.25), Some(500.25), Some(500.25), Some(500.25), Some(500.25), Some(500.25), Some(500.25), Some(500.25), Some(500.25), Some(500.25))

  "reads" should {
    "return a model" when {
      "passed valid json" in {
        desJson.as[Adjustments] shouldBe model
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
