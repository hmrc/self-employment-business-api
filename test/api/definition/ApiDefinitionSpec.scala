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

package api.definition

import api.routing.Version5
import api.utils.UnitSpec
import play.api.libs.json.{JsValue, Json}

class ApiDefinitionSpec extends UnitSpec {

  private val apiVersion: APIVersion       = APIVersion(Version5, APIStatus.ALPHA, APIAccessType.PUBLIC, endpointsEnabled = true)
  private val apiDefinition: APIDefinition = APIDefinition("b", "c", "d", List("category"), List(apiVersion), Some(false))
  private val definition: Definition       = Definition(apiDefinition)

  private val apiVersionJson: JsValue = Json.parse(s"""
       |{
       | "version": "5.0",
       |"status": "ALPHA",
       |"access": "PUBLIC",
       |"endpointsEnabled": true
       |}
       |""".stripMargin)

  private val apiDefinitionJson: JsValue = Json.parse(s"""{
       |"name": "b",
       |"description": "c",
       |"context": "d",
       |"categories": ["category"],
       |"versions": [$apiVersionJson],
       |"requiresTrust": false
       |}
       |""".stripMargin)

  private val definitionJson: JsValue = Json.parse(s"""{
       |"api": $apiDefinitionJson
       |}
       |""".stripMargin)

  "Definition" when {
    "the full model is present" should {
      "correctly write the model to json" in {
        Json.toJson(definition) shouldBe definitionJson
      }
    }

    "the full Json is present" should {
      "correctly read JSON to a model" in {
        definitionJson.as[Definition] shouldBe definition
      }
    }
  }

  "APIDefinition" when {
    "the 'name' parameter is empty" should {
      "throw an 'IllegalArgumentException'" in {
        assertThrows[IllegalArgumentException](
          apiDefinition.copy(name = "")
        )
      }
    }
  }

  "the 'description' parameter is empty" should {
    "throw an 'IllegalArgumentException'" in {
      assertThrows[IllegalArgumentException](
        apiDefinition.copy(description = "")
      )
    }
  }

  "the 'context' parameter is empty" should {
    "throw an 'IllegalArgumentException'" in {
      assertThrows[IllegalArgumentException](
        apiDefinition.copy(context = "")
      )
    }
  }

  "the 'versions' parameter is not unique" should {
    "throw an 'IllegalArgumentException'" in {
      assertThrows[IllegalArgumentException](
        apiDefinition.copy(versions = List(apiVersion, apiVersion))
      )
    }
  }

  "the 'versions' parameter is empty" should {
    "throw an 'IllegalArgumentException'" in {
      assertThrows[IllegalArgumentException](
        apiDefinition.copy(versions = Nil)
      )
    }
  }

}
