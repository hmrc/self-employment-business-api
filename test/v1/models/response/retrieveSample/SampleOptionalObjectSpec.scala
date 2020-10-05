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

package v1.models.response.retrieveSample

import play.api.libs.json.{JsError, JsObject, JsValue, Json}
import support.UnitSpec
import v1.models.domain.SampleMtdEnum

class SampleOptionalObjectSpec extends UnitSpec {

  val model: SampleOptionalObject = SampleOptionalObject(
    itemIdentifier = Some("anId"),
    itemType = Some(SampleMtdEnum.One),
    deductibleAmount = Some(300.54)
  )

  "SampleOptionalObject" when {
    "read from valid JSON" should {
      "produce the expected SampleOptionalObject object (requestedItemId)" in {
        val json: JsValue = Json.parse(
          """
            |{
            |  "requestedItemId": "anId",
            |  "typeOfItem": "Type1",
            |  "paymentAmount": 300.54
            |}
          """.stripMargin
        )

        json.as[SampleOptionalObject] shouldBe model
      }

      "produce the expected SampleOptionalObject object (generatedItemId)" in {
        val json: JsValue = Json.parse(
          """
            |{
            |  "generatedItemId": "anId",
            |  "typeOfItem": "Type1",
            |  "paymentAmount": 300.54
            |}
          """.stripMargin
        )

        json.as[SampleOptionalObject] shouldBe model
      }

      "produce the expected SampleOptionalObject object (both ids)" in {
        val json: JsValue = Json.parse(
          """
            |{
            |  "requestedItemId": "anId",
            |  "generatedItemId": "anotherId",
            |  "typeOfItem": "Type1",
            |  "paymentAmount": 300.54
            |}
          """.stripMargin
        )

        json.as[SampleOptionalObject] shouldBe model
      }
    }

    "read from empty JSON" should {
      "produce an empty SampleOptionalObject object" in {
        val json: JsValue = JsObject.empty

        json.as[SampleOptionalObject] shouldBe SampleOptionalObject.empty
      }
    }

    "read from invalid JSON" should {
      "produce a JsError" in {
        val json: JsValue = Json.parse(
          """
            |{
            |  "requestedItemId": "anId",
            |  "typeOfItem": "TypeJuan",
            |  "paymentAmount": 300.54
            |}
          """.stripMargin
        )

        json.validate[SampleOptionalObject] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        val json: JsValue = Json.parse(
          """
            |{
            |  "itemIdentifier": "anId",
            |  "itemType": "One",
            |  "deductibleAmount": 300.54
            |}
          """.stripMargin
        )

        Json.toJson(model) shouldBe json
      }
    }

    "written to JSON (missing optional fields)" should {
      "produce an empty JsObject" in {
        val json: JsValue = JsObject.empty

        Json.toJson(SampleOptionalObject.empty) shouldBe json
      }
    }
  }

}
