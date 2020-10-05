/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.domain

import play.api.libs.json.{JsError, JsValue, Json}
import support.UnitSpec
import utils.enums.EnumJsonSpecSupport
import v1.models.domain.SampleMtdEnum._

class SampleMtdEnumSpec extends UnitSpec with EnumJsonSpecSupport {

  val desJson: JsValue = Json.toJson("")

  testRoundTrip[SampleMtdEnum](
    ("One", One),
    ("Two", Two),
    ("Three", Three),
    ("Four", Four)
  )

  "SampleMtdEnum" when {
    "given an invalid field" should {
      "return a JsError" in {
        desJson.validate[SampleMtdEnum] shouldBe a[JsError]
      }
    }

    "toDesEnum" should {
      "produce the correct SampleMtdEnum object" in {
        One.toDesEnum shouldBe SampleDesEnum.Type1
        Two.toDesEnum shouldBe SampleDesEnum.Type2
        Three.toDesEnum shouldBe SampleDesEnum.Type3
        Four.toDesEnum shouldBe SampleDesEnum.Type4
      }
    }
  }
}
