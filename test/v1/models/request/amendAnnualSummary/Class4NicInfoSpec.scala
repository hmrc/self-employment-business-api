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

package v1.models.request.amendAnnualSummary

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.models.domain.exemptionCode.MtdExemptionCode

class Class4NicInfoSpec extends UnitSpec {

  val trueMtdModel: Class4NicInfo = Class4NicInfo(isExempt = true, Some(MtdExemptionCode.`001 - Non Resident`))
  val falseMtdModel: Class4NicInfo = Class4NicInfo(isExempt = false, None)

  "reads" should {

    "read from JSON" when {

      val trueRequestJson: JsValue = Json.parse(
        s"""
           |{
           |  "isExempt": true,
           |  "exemptionCode": "001 - Non Resident"
           |}
           |""".stripMargin)

      val falseRequestJson: JsValue = Json.parse(
        s"""
           |{
           |  "isExempt": false
           |}
           |""".stripMargin)

      "a valid request is made with isExempt true" in {
        trueRequestJson.as[Class4NicInfo] shouldBe trueMtdModel
      }

      "a valid request is made with isExempt false" in {
        falseRequestJson.as[Class4NicInfo] shouldBe falseMtdModel
      }

    }
  }

  "Writes" should {

    "write to des" when{

      val trueDesJson: JsValue = Json.parse(
        s"""
           |{
           |  "exemptFromPayingClass4Nics": true,
           |  "class4NicsExemptionReason": "001"
           |}
           |""".stripMargin)

      val falseDesJson: JsValue = Json.parse(
        s"""
           |{
           |  "exemptFromPayingClass4Nics": false
           |}
           |""".stripMargin)

      "a valid request is made with isExempt true" in {
        Json.toJson(trueMtdModel) shouldBe trueDesJson
      }

      "a valid request is made with isExempt false" in {
        Json.toJson(falseMtdModel) shouldBe falseDesJson
      }

    }
  }
}
