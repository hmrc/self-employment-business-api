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

package v2.models.request.amendPeriodSummary

import play.api.libs.json.{JsValue, Json}
import shared.UnitSpec
class AmendPeriodSummaryBodySpec extends UnitSpec with AmendPeriodSummaryFixture {

  val emptyJson: JsValue = Json.parse(
    """
      |{}
    """.stripMargin
  )

  val emptyMtdModel: AmendPeriodSummaryBody = AmendPeriodSummaryBody(
    None,
    None,
    None
  )

  "reads" should {
    "return a model" when {
      "a valid request with all (non-consolidated) data is made" in {
        amendPeriodSummaryBodyMtdJson.as[AmendPeriodSummaryBody] shouldBe amendPeriodSummaryBody
      }

      "a valid request with some data is made" in {
        amendPeriodSummaryConsolidatedBodyMtdJson.as[AmendPeriodSummaryBody] shouldBe amendPeriodSummaryConsolidatedBody
      }

      "a valid request with no data is made" in {
        emptyJson.as[AmendPeriodSummaryBody] shouldBe emptyMtdModel
      }
    }
  }

  "writes" should {
    "return downstream json" when {
      "a valid request is made with full body" in {
        Json.toJson(amendPeriodSummaryBody) shouldBe amendPeriodSummaryBodyDownstreamJson
      }

      "a valid request is made with partial body" in {
        Json.toJson(amendPeriodSummaryConsolidatedBody) shouldBe amendPeriodSummaryConsolidatedBodyDownstreamJson
      }

      "a valid request is made with empty body" in {
        Json.toJson(emptyMtdModel) shouldBe emptyJson
      }
    }
  }

}
