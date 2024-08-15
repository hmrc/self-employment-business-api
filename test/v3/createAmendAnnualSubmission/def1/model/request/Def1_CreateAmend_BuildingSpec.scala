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
import shared.utils.UnitSpec

class Def1_CreateAmend_BuildingSpec extends UnitSpec {

  val model: Def1_CreateAmend_Building =
    Def1_CreateAmend_Building(Some("house name"), Some("house number"), "GF49JH")

  "reads" when {
    "passed a valid JSON" should {
      "return the model" in {
        Json
          .parse("""
            |{
            |  "name": "house name",
            |  "number": "house number",
            |  "postcode": "GF49JH"
            |}
            |""".stripMargin)
          .as[Def1_CreateAmend_Building] shouldBe model
      }
    }
  }

  "writes" when {
    "passed a model" should {
      "return downstream JSON" in {
        Json.toJson(model) shouldBe Json.parse("""
            |{
            |  "name": "house name",
            |  "number": "house number",
            |  "postCode": "GF49JH"
            |}
            |""".stripMargin)
      }
    }
  }

}
