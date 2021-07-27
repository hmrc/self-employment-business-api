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

package v1.models.response.createSEPeriodic

import mocks.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.models.hateoas.{Link, Method}

class CreateSelfEmploymentPeriodicResponseBodySpec extends UnitSpec with MockAppConfig  {

  val json: JsValue = Json.parse(
    """
      |{
      |   "periodId": "2017090920170909"
      |}
    """.stripMargin
  )

  val model: CreateSelfEmploymentPeriodicResponse = CreateSelfEmploymentPeriodicResponse("2017090920170909")

  "reads" should {
    "return a model" when {
      "passed valid json" in {
        json.as[CreateSelfEmploymentPeriodicResponse] shouldBe model
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

  "LinksFactory" should {
    "produce the correct links" when {
      "called" in {
        val data: CreateSelfEmploymentPeriodicHateoasData = CreateSelfEmploymentPeriodicHateoasData("mynino", "myBusinessId", "myPeriodId")

        MockAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        CreateSelfEmploymentPeriodicResponse.LinksFactory.links(mockAppConfig, data) shouldBe Seq(
          Link(href = s"/my/context/${data.nino}/${data.businessId}/period/${data.periodId}", method = Method.GET, rel = "self")
        )
      }
    }
  }
}