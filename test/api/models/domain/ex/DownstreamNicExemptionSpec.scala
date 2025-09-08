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

package api.models.domain.ex

import api.models.domain.ex.DownstreamNicExemption.*
import play.api.libs.json.{JsError, JsValue, Json}
import shared.utils.enums.EnumJsonSpecSupport
import shared.utils.UnitSpec

class DownstreamNicExemptionSpec extends UnitSpec with EnumJsonSpecSupport {

  val desJson: JsValue = Json.toJson("")

  testRoundTrip[DownstreamNicExemption](
    ("001", `001`),
    ("002", `002`),
    ("003", `003`),
    ("004", `004`),
    ("005", `005`),
    ("006", `006`)
  )

  "DesExemptionCode" when {
    "given an invalid field" should {
      "return a JsError" in {
        desJson.validate[DownstreamNicExemption] shouldBe a[JsError]
      }
    }

    "toMtd" should {
      "produce the correct MtdExemptionCode object" in {
        `001`.toMtd shouldBe MtdNicExemption.`non-resident`
        `002`.toMtd shouldBe MtdNicExemption.trustee
        `003`.toMtd shouldBe MtdNicExemption.diver
        `004`.toMtd shouldBe MtdNicExemption.`ITTOIA-2005`
        `005`.toMtd shouldBe MtdNicExemption.`over-state-pension-age`
        `006`.toMtd shouldBe MtdNicExemption.`under-16`
      }
    }
  }

}
