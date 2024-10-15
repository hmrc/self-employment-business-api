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

package v4.retrievePeriodSummary.def2.model.response

import play.api.libs.json.{JsValue, Json}
import shared.config.MockSharedAppConfig
import shared.utils.UnitSpec
import v4.retrievePeriodSummary.def2.model.Def2_RetrievePeriodSummaryFixture
import v4.retrievePeriodSummary.model.response.Def2_RetrievePeriodSummaryResponse

class Def2_RetrievePeriodSummaryResponseSpec extends UnitSpec with MockSharedAppConfig with Def2_RetrievePeriodSummaryFixture {

  "round trip" should {
    "return mtd json" when {
      "given valid full downstream json" in {
        val result = Json.toJson(def2_DownstreamFullJson.as[Def2_RetrievePeriodSummaryResponse])
        result shouldBe def2_MtdFullJson
      }
      "given valid consolidated downstream json" in {
        val result: JsValue = Json.toJson(def2_DownstreamConsolidatedJson.as[Def2_RetrievePeriodSummaryResponse])
        result shouldBe def2_MtdConsolidatedJson
      }
      "given valid minimal downstream json" in {
        Json.toJson(def2_DownstreamMinimalJson.as[Def2_RetrievePeriodSummaryResponse]) shouldBe def2_MtdMinimalJson
      }
    }
  }

}
