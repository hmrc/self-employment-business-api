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

package v1.models.domain.ex

import play.api.libs.json.{JsError, JsValue, Json}
import support.UnitSpec
import utils.enums.EnumJsonSpecSupport
import v1.models.domain.ex.DesEx._

class DesExSpec extends UnitSpec with EnumJsonSpecSupport {

  val desJson: JsValue = Json.toJson("")

  testRoundTrip[DesEx](
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
        desJson.validate[DesEx] shouldBe a[JsError]
      }
    }

    "toMtd" should {
      "produce the correct MtdExemptionCode object" in {
        `001`.toMtd shouldBe MtdEx.`001 - Non Resident`
        `002`.toMtd shouldBe MtdEx.`002 - Trustee`
        `003`.toMtd shouldBe MtdEx.`003 - Diver`
        `004`.toMtd shouldBe MtdEx.`004 - Employed earner taxed under ITTOIA 2005`
        `005`.toMtd shouldBe MtdEx.`005 - Over state pension age`
        `006`.toMtd shouldBe MtdEx.`006 - Under 16`
      }
    }
  }
}
