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
import v1.models.domain.ex.MtdEx._

class MtdExSpec extends UnitSpec with EnumJsonSpecSupport {

  val desJson: JsValue = Json.toJson("")

  testRoundTrip[MtdEx](
    ("001 - Non Resident", `001 - Non Resident`),
    ("002 - Trustee", `002 - Trustee`),
    ("003 - Diver", `003 - Diver`),
    ("004 - Employed earner taxed under ITTOIA 2005", `004 - Employed earner taxed under ITTOIA 2005`),
    ("005 - Over state pension age", `005 - Over state pension age`),
    ("006 - Under 16", `006 - Under 16`)
  )

  "MtdExemptionCodeSpec" when {
    "given an invalid field" should {
      "return a JsError" in {
        desJson.validate[MtdEx] shouldBe a[JsError]
      }
    }

    "toDes" should {
      "produce the correct DesExemptionCode object" in {
        `001 - Non Resident`.toDes shouldBe DesEx.`001`
        `002 - Trustee`.toDes shouldBe DesEx.`002`
        `003 - Diver`.toDes shouldBe DesEx.`003`
        `004 - Employed earner taxed under ITTOIA 2005`.toDes shouldBe DesEx.`004`
        `005 - Over state pension age`.toDes shouldBe DesEx.`005`
        `006 - Under 16`.toDes shouldBe DesEx.`006`
      }
    }
  }
}
