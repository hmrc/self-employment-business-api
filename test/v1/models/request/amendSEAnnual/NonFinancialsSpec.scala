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

package v1.models.request.amendSEAnnual

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.models.domain.ex.MtdEx

class NonFinancialsSpec extends UnitSpec {

  val mtdModel: NonFinancials = NonFinancials(Some(Class4NicInfo(Some(MtdEx.`001 - Non Resident`))))

  "reads" should {

    "read from JSON" when {

      val requestJson: JsValue = Json.parse(
        s"""
           |{
           |  "class4NicInfo": {
           |    "isExempt": true,
           |    "exemptionCode": "001 - Non Resident"
           |  }
           |}
           |""".stripMargin)

      "a valid request is made" in {
        requestJson.as[NonFinancials] shouldBe mtdModel
      }
    }
  }

  "Writes" should {

    "write to des" when{

      val desJson: JsValue = Json.parse(
        s"""
           |{
           |  "exemptFromPayingClass4Nics": true,
           |  "class4NicsExemptionReason": "001"
           |}
           |""".stripMargin)
      "a valid request is made" in {
        Json.toJson(mtdModel) shouldBe desJson
      }
    }
  }
}
