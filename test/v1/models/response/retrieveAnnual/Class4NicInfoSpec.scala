/*
 * Copyright 2022 HM Revenue & Customs
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

package v1.models.response.retrieveAnnual

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.models.domain.ex.MtdNicExemption

class Class4NicInfoSpec extends UnitSpec {
  val desJson: JsValue = Json.parse(
    """
      |{
      |  "exemptFromPayingClass4Nics": true,
      |  "class4NicsExemptionReason": "001"
      |}
      |""".stripMargin)

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "exemptionCode": "non-resident"
      |}
      |""".stripMargin)

  val model: Class4NicInfo = Class4NicInfo(Some(MtdNicExemption.`non-resident`))

  "reads" should {
    "return a model" when {
      "passed valid json" in {
        desJson.as[Class4NicInfo] shouldBe model
      }
    }
  }

  "writes" should {
    "return json" when {
      "passed a model" in {
        Json.toJson(model) shouldBe mtdJson
      }
    }
  }
}