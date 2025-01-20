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

package v5.createAmendAnnualSubmission.def1.model.request

import api.models.domain.ex.MtdNicExemption
import play.api.libs.json.{JsValue, Json}
import shared.utils.UnitSpec

class Def1_CreateAmend_NonFinancialsSpec extends UnitSpec {

  "reads" should {
    "passed valid mtd JSON" should {
      "return the model" in {
        val requestJson: JsValue = Json.parse(s"""
             |{
             |    "businessDetailsChangedRecently": true,
             |    "class4NicsExemptionReason": "non-resident"
             |  }
             |""".stripMargin)

        requestJson.as[Def1_CreateAmend_NonFinancials] shouldBe Def1_CreateAmend_NonFinancials(
          businessDetailsChangedRecently = true,
          class4NicsExemptionReason = Some(MtdNicExemption.`non-resident`)
        )
      }
    }

    "writes" when {
      "there is an exemption reason" must {
        "set exemptFromPayingClass4Nics false" in {
          Json.toJson(
            Def1_CreateAmend_NonFinancials(
              businessDetailsChangedRecently = true,
              class4NicsExemptionReason = Some(MtdNicExemption.`non-resident`))) shouldBe
            Json.parse(s"""
                 |{
                 |  "businessDetailsChangedRecently": true,
                 |  "exemptFromPayingClass4Nics": true,
                 |  "class4NicsExemptionReason": "001"
                 |}
                 |""".stripMargin)
        }
      }

      "there is no exemption reason" must {
        "set exemptFromPayingClass4Nics true" in {
          Json.toJson(Def1_CreateAmend_NonFinancials(businessDetailsChangedRecently = true, class4NicsExemptionReason = None)) shouldBe
            Json.parse(s"""
                 |{
                 |  "businessDetailsChangedRecently": true,
                 |  "exemptFromPayingClass4Nics": false
                 |}
                 |""".stripMargin)
        }
      }
    }
  }

}
