/*
 * Copyright 2026 HM Revenue & Customs
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

package shared.definition

import play.api.libs.json.{JsValue, Json}
import shared.routing.Version5
import shared.utils.UnitSpec

class ApiModelSpec extends UnitSpec {

  val model = APIVersion(
    version = Version5,
    status = APIStatus.BETA,
    endpointsEnabled = true
  )

  "PublishingException" should {
    "be created with a message" in {
      val message   = "Test exception message"
      val exception = PublishingException(message)

      exception.message shouldBe message
      exception.getMessage shouldBe message
    }

    "extend Exception" in {
      val exception = PublishingException("Test")
      exception shouldBe a[Exception]
    }
  }

  "Access" when {
    "created with valid parameters" should {
      "have the correct type and whitelisted application IDs" in {
        val accessType = "PUBLIC"
        val appIds     = Seq("app1", "app2", "app3")
        val access     = Access(accessType, appIds)

        access.`type` shouldBe accessType
        access.whitelistedApplicationIds shouldBe appIds
      }

      "serialize to JSON correctly" in {
        val access = Access("PRIVATE", Seq("id1", "id2"))
        val json   = Json.toJson(access)

        (json \ "type").as[String] shouldBe "PRIVATE"
        (json \ "whitelistedApplicationIds").as[Seq[String]] shouldBe Seq("id1", "id2")
      }

      "deserialize from JSON correctly" in {
        val json = Json.obj(
          "type"                      -> "PUBLIC",
          "whitelistedApplicationIds" -> Json.arr("app1", "app2")
        )

        val access = json.as[Access]
        access.`type` shouldBe "PUBLIC"
        access.whitelistedApplicationIds shouldBe Seq("app1", "app2")
      }
    }

    "with empty whitelisted application IDs" should {
      "be valid and serialize correctly" in {
        val access = Access("PUBLIC", Seq())
        val json   = Json.toJson(access)

        (json \ "type").as[String] shouldBe "PUBLIC"
        (json \ "whitelistedApplicationIds").as[Seq[String]] shouldBe Seq()
      }
    }

    "serialized and deserialized" should {
      "maintain data integrity" in {
        val original     = Access("RESTRICTED", Seq("app1", "app2", "app3"))
        val json         = Json.toJson(original)
        val deserialized = json.as[Access]

        deserialized shouldBe original
      }
    }
  }

  "Parameter" when {
    "created with name only" should {
      "have required set to false by default" in {
        val param = Parameter("testParam")

        param.name shouldBe "testParam"
        param.required shouldBe false
      }
    }

    "created with required set to true" should {
      "have the correct properties" in {
        val param = Parameter("requiredParam", required = true)

        param.name shouldBe "requiredParam"
        param.required shouldBe true
      }
    }

    "serialize to JSON correctly" in {
      val param1 = Parameter("optional")
      val param2 = Parameter("mandatory", required = true)

      val json1 = Json.toJson(param1)
      val json2 = Json.toJson(param2)

      (json1 \ "name").as[String] shouldBe "optional"
      (json1 \ "required").as[Boolean] shouldBe false

      (json2 \ "name").as[String] shouldBe "mandatory"
      (json2 \ "required").as[Boolean] shouldBe true
    }

    "deserialize from JSON correctly" in {
      val json1 = Json.obj("name" -> "param1")
      val json2 = Json.obj("name" -> "param2", "required" -> true)

      val param1 = json1.as[Parameter]
      val param2 = json2.as[Parameter]

      param1.name shouldBe "param1"
      param1.required shouldBe false
      param2.name shouldBe "param2"
      param2.required shouldBe true
    }

    "with default required value" should {
      "deserialize correctly when required is omitted" in {
        val json  = Json.obj("name" -> "testParam")
        val param = json.as[Parameter]

        param.name shouldBe "testParam"
        param.required shouldBe false
      }
    }

    "serialized and deserialized" should {
      "maintain data integrity" in {
        val original1 = Parameter("param1")
        val original2 = Parameter("param2", required = true)

        original1 shouldBe Json.toJson(original1).as[Parameter]
        original2 shouldBe Json.toJson(original2).as[Parameter]
      }
    }
  }

  "Definition" when {
    "created with an APIDefinition" should {
      "store the api correctly" in {

        val apiDef = APIDefinition(
          name = "Test API",
          description = "Test Description",
          context = "test-context",
          categories = List("API"),
          versions = List(model),
          requiresTrust = Some(false)
        )
        val definition = Definition(apiDef)

        definition.api shouldBe apiDef
      }
    }

    "deserialize from JSON correctly" in {
      val json = Json.obj(
        "api" -> Json.obj(
          "name"          -> "Test API",
          "description"   -> "Test Description",
          "context"       -> "test-context",
          "categories"    -> Json.arr("API"),
          "versions"      -> Json.arr(model),
          "requiresTrust" -> false
        )
      )

      val definition = json.as[Definition]
      definition.api.name shouldBe "Test API"
      definition.api.description shouldBe "Test Description"
      definition.api.context shouldBe "test-context"
    }
  }

}
