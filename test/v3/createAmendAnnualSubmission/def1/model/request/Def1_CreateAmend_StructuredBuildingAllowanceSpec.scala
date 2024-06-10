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

package v3.createAmendAnnualSubmission.def1.model.request

import play.api.libs.json.Json
import shared.UnitSpec
class Def1_CreateAmend_StructuredBuildingAllowanceSpec extends UnitSpec with Def1_CreateAmend_StructuredBuildingAllowanceFixture {

  "reads" when {
    "given a valid JSON object" should {
      "return the deserialised Scala object" in {
        structuredBuildingAllowanceMtdJson.as[Def1_CreateAmend_StructuredBuildingAllowance] shouldBe structuredBuildingAllowance
      }
    }
  }

  "writes" when {
    "given a Scala object" should {
      "return downstream JSON" in {
        Json.toJson(structuredBuildingAllowance) shouldBe structuredBuildingAllowanceDownstreamJson
      }
    }
  }

}
