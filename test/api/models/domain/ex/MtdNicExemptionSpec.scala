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

import api.models.domain.ex.MtdNicExemption.*
import play.api.libs.json.{JsError, JsValue, Json}
import shared.utils.enums.EnumJsonSpecSupport
import shared.utils.UnitSpec

class MtdNicExemptionSpec extends UnitSpec with EnumJsonSpecSupport {

  val desJson: JsValue = Json.toJson("")

  testRoundTrip[MtdNicExemption](
    ("non-resident", `non-resident`),
    ("trustee", trustee),
    ("diver", diver),
    ("ITTOIA-2005", `ITTOIA-2005`),
    ("over-state-pension-age", `over-state-pension-age`),
    ("under-16", `under-16`)
  )

  "MtdExemptionCodeSpec" when {
    "given an invalid field" should {
      "return a JsError" in {
        desJson.validate[MtdNicExemption] shouldBe a[JsError]
      }
    }

    "toDownstream" should {
      "produce the correct DesExemptionCode object" in {
        `non-resident`.toDownstream shouldBe "001"
        trustee.toDownstream shouldBe "002"
        diver.toDownstream shouldBe "003"
        `ITTOIA-2005`.toDownstream shouldBe "004"
        `over-state-pension-age`.toDownstream shouldBe "005"
        `under-16`.toDownstream shouldBe "006"
      }
    }
  }

}
