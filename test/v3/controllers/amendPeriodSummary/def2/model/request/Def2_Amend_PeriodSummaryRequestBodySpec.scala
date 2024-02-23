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

package v3.controllers.amendPeriodSummary.def2.model.request

import play.api.libs.json.{JsObject, JsValue, Json}
import support.UnitSpec
import v3.controllers.amendPeriodSummary.def2.model.Def2_AmendPeriodSummaryFixture
import v3.controllers.amendPeriodSummary.model.request.Def2_AmendPeriodSummaryRequestBody

class Def2_Amend_PeriodSummaryRequestBodySpec extends UnitSpec with Def2_AmendPeriodSummaryFixture {

  val emptyJson: JsValue = JsObject.empty

  val parsedEmptyMtd: Def2_AmendPeriodSummaryRequestBody = Def2_AmendPeriodSummaryRequestBody(None, None, None)

  "reads" should {
    "return a model" when {
      "a valid request with all (non-consolidated) data is made" in {
        val result = def2_AmendPeriodSummaryBodyMtdJson.as[Def2_AmendPeriodSummaryRequestBody]
        result shouldBe def2_AmendPeriodSummaryBody
      }

      "a valid request with some data is made" in {
        val result = def2_AmendPeriodSummaryConsolidatedBodyMtdJson.as[Def2_AmendPeriodSummaryRequestBody]
        result shouldBe def2_AmendPeriodSummaryConsolidatedBody
      }

      "a valid request with no data is made" in {
        val result: Def2_AmendPeriodSummaryRequestBody = emptyJson.as[Def2_AmendPeriodSummaryRequestBody]
        result shouldBe parsedEmptyMtd
      }
    }
  }

  "writes" should {
    "return downstream json" when {
      "a valid request is made with full body" in {
        val result: JsValue = Json.toJson(def2_AmendPeriodSummaryBody)
        result shouldBe def2_AmendPeriodSummaryBodyDownstreamJson
      }

      "a valid request is made with partial body" in {
        val result: JsValue = Json.toJson(def2_AmendPeriodSummaryConsolidatedBody)
        result shouldBe def2_AmendPeriodSummaryConsolidatedBodyDownstreamJson
      }

      "a valid request is made with empty body" in {
        val result: JsValue = Json.toJson(parsedEmptyMtd)
        result shouldBe emptyJson
      }
    }
  }

}
