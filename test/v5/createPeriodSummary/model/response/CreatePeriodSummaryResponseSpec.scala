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

package v5.createPeriodSummary.model.response

import play.api.libs.json.{JsValue, Json}
import shared.config.MockSharedAppConfig
import shared.utils.UnitSpec

class CreatePeriodSummaryResponseSpec extends UnitSpec with MockSharedAppConfig {

  val json: JsValue = Json.parse(
    """
      |{
      |   "periodId": "2017090920170909"
      |}
    """.stripMargin
  )

  val model: CreatePeriodSummaryResponse = CreatePeriodSummaryResponse("2017090920170909")

  "reads" should {
    "return a model" when {
      "passed valid json" in {
        json.as[CreatePeriodSummaryResponse] shouldBe model
      }
    }
  }

  "writes" should {
    "return json" when {
      "passed a model" in {
        Json.toJson(model) shouldBe json
      }
    }
  }

}
