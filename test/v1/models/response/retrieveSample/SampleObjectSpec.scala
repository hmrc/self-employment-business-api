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

import play.api.libs.json.{JsError, JsValue, Json}
import support.UnitSpec
import v1.models.domain.SampleMtdEnum

class SampleObjectSpec extends UnitSpec {

  val itemModel: SampleArrayItem = SampleArrayItem(
    id = "AAA123",
    declaredAmount = Some(200.11),
    taxableAmount = Some(100.14),
    itemType = SampleMtdEnum.One,
    taxYear = "2018-19",
    finalised = true
  )

  val model: SampleObject = SampleObject(
    dateSubmitted = "01-01-2019",
    submissionItem = Some(itemModel)
  )

  "SampleObject" when {
    "read from valid JSON" should {
      "produce the expected SampleObject object" in {
        val json: JsValue = Json.parse(
          """
            |{
            |  "dateSubmitted": "01-01-2019",
            |  "submittedItems": {
            |    "income": [
            |      {
            |        "itemId": "AAA123",
            |        "submittedAmount": 200.11,
            |        "itemDetail": {
            |          "taxableAmount": 100.14
            |        },
            |        "typeOfItem": "Type1",
            |        "taxYear": "2019",
            |        "isFinalised": true
            |      },
            |      {
            |        "itemId": "AAA123",
            |        "submittedAmount": 200.11,
            |        "itemDetail": {
            |          "taxableAmount": 100.14
            |        },
            |        "typeOfItem": "Type2",
            |        "taxYear": "2019",
            |        "isFinalised": true
            |      }
            |    ]
            |  }
            |}
          """.stripMargin
        )

        json.as[SampleObject] shouldBe model
      }

      "produce the expected SampleObject object (empty object)" in {
        val json: JsValue = Json.parse(
          """
            |{
            |  "dateSubmitted": "01-01-2019",
            |  "submittedItems": {}
            |}
          """.stripMargin
        )

        json.as[SampleObject] shouldBe model.copy(submissionItem = None)
      }

      "produce the expected SampleObject object (empty array)" in {
        val json: JsValue = Json.parse(
          """
            |{
            |  "dateSubmitted": "01-01-2019",
            |  "submittedItems": {
            |    "income": []
            |  }
            |}
          """.stripMargin
        )

        json.as[SampleObject] shouldBe model.copy(submissionItem = None)
      }

      "produce the expected SampleObject object (no valid objects)" in {
        val json: JsValue = Json.parse(
          """
            |{
            |  "dateSubmitted": "01-01-2019",
            |  "submittedItems": {
            |    "income": [
            |      {
            |        "itemId": "AAA123",
            |        "submittedAmount": 200.11,
            |        "itemDetail": {
            |          "taxableAmount": 100.14
            |        },
            |        "typeOfItem": "Type2",
            |        "taxYear": "2019",
            |        "isFinalised": true
            |      }
            |    ]
            |  }
            |}
          """.stripMargin
        )

        json.as[SampleObject] shouldBe model.copy(submissionItem = None)
      }
    }

    "read from invalid JSON" should {
      "produce a JsError" in {
        val json: JsValue = Json.parse(
          """
            |{
            |  "dateSubmitted": "01-01-2019",
            |  "submittedItems": {
            |    "income": [
            |      {
            |        "itemId": "AAA123",
            |        "submittedAmount": 200.11,
            |        "itemDetail": {
            |          "taxableAmount": 100.14
            |        },
            |        "typeOfItem": "Type1",
            |        "taxYear": "2019",
            |        "isFinalised": "true"
            |      }
            |    ]
            |  }
            |}
          """.stripMargin
        )

        json.validate[SampleObject] shouldBe a[JsError]
      }
    }

    "written to JSON" should {
      "produce the expected JsObject" in {
        val json: JsValue = Json.parse(
          """
            |{
            |    "dateSubmitted": "01-01-2019",
            |    "submissionItem": {
            |        "declaredAmount": 200.11,
            |        "finalised": true,
            |        "id": "AAA123",
            |        "itemType": "One",
            |        "taxableAmount": 100.14,
            |        "taxYear": "2018-19"
            |    }
            |}
          """.stripMargin
        )

        Json.toJson(model) shouldBe json
      }
    }

    "written to JSON with missing optional fields" should {
      "produce the expected JsObject" in {
        val json: JsValue = Json.parse(
          """
            |{
            |  "dateSubmitted": "01-01-2019"
            |}
          """.stripMargin
        )

        Json.toJson(model.copy(submissionItem = None)) shouldBe json
      }
    }
  }

}
