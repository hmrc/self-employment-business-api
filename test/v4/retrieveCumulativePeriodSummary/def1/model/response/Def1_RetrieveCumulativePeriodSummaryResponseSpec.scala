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

package v4.retrieveCumulativePeriodSummary.def1.model.response

import play.api.libs.json.Json
import shared.config.MockAppConfig
import shared.utils.UnitSpec
import v4.retrieveCumulativePeriodSummary.def1.model.Def1_RetrieveCumulativePeriodSummaryFixture

class Def1_RetrieveCumulativePeriodSummaryResponseSpec extends UnitSpec with MockAppConfig with Def1_RetrieveCumulativePeriodSummaryFixture {

  "round trip" should {
    "return mtd json" when {
      "passed valid full downstream json" in {
        Json.toJson(def1_DownstreamFullJson.as[Def1_RetrieveCumulativePeriodSummaryResponse]) shouldBe def1_MtdFullJson
      }
      "passed valid consolidated downstream json" in {
        Json.toJson(def1_DownstreamConsolidatedJson.as[Def1_RetrieveCumulativePeriodSummaryResponse]) shouldBe def1_MtdConsolidatedJson
      }
      "passed valid minimal downstream json" in {
        Json.toJson(def1_DownstreamMinimalJson.as[Def1_RetrieveCumulativePeriodSummaryResponse]) shouldBe def1_MtdMinimalJson
      }
    }
  }

}
