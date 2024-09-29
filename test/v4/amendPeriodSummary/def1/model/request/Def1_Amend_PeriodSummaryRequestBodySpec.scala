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

package v4.amendPeriodSummary.def1.model.request

import play.api.libs.json.{JsObject, JsValue, Json}
import shared.utils.UnitSpec
import v4.amendPeriodSummary.def1.model.Def1_AmendPeriodSummaryFixture
import v4.amendPeriodSummary.model.request.Def1_AmendPeriodSummaryRequestBody

class Def1_Amend_PeriodSummaryRequestBodySpec extends UnitSpec with Def1_AmendPeriodSummaryFixture {

  val emptyJson: JsValue = JsObject.empty

  val emptyMtdModel: Def1_AmendPeriodSummaryRequestBody = Def1_AmendPeriodSummaryRequestBody(None, None, None)

  "reads" should {
    "return a model" when {
      "a valid request with all (non-consolidated) data is made" in {
        def1_AmendPeriodSummaryBodyMtdJson.as[Def1_AmendPeriodSummaryRequestBody] shouldBe def1_AmendPeriodSummaryBody
      }

      "a valid request with some data is made" in {
        def1_AmendPeriodSummaryConsolidatedBodyMtdJson.as[Def1_AmendPeriodSummaryRequestBody] shouldBe def1_AmendPeriodSummaryConsolidatedBody
      }

      "a valid request with no data is made" in {
        emptyJson.as[Def1_AmendPeriodSummaryRequestBody] shouldBe emptyMtdModel
      }
    }
  }

  "writes" should {
    "return downstream json" when {
      "a valid request is made with full body" in {
        Json.toJson(def1_AmendPeriodSummaryBody) shouldBe def1_AmendPeriodSummaryBodyDownstreamJson
      }

      "a valid request is made with partial body" in {
        Json.toJson(def1_AmendPeriodSummaryConsolidatedBody) shouldBe def1_AmendPeriodSummaryConsolidatedBodyDownstreamJson
      }

      "a valid request is made with empty body" in {
        Json.toJson(emptyMtdModel) shouldBe emptyJson
      }
    }
  }

}
