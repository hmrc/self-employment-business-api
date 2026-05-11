/*
 * Copyright 2026 HM Revenue & Customs
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

package v5.createAmendAnnualSubmission.def3.request

import play.api.libs.json.{JsObject, Json}
import shared.utils.UnitSpec

class Def3_CreateAmend_AdjustmentsSpec extends UnitSpec with Def3_CreateAmendAnnualSubmissionFixture {

  private val downstreamJson: JsObject = adjustmentsDownstreamJson.as[JsObject] ++ Json.obj(
    "transitionProfitAmount"             -> 9.12,
    "transitionProfitAccelerationAmount" -> 9.12
  )

  "reads" when {
    "passed valid MTD JSON" should {
      "return the expected model" in {
        adjustmentsWithAdditionalFieldsMtdJson.as[Def3_CreateAmend_Adjustments] shouldBe adjustments
      }
    }
  }

  "writes" when {
    "passed a valid model" should {
      "return the expected downstream JSON" in {
        Json.toJson(adjustments) shouldBe downstreamJson
      }
    }
  }

}
