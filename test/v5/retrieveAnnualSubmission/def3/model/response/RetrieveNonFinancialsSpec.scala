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

package v5.retrieveAnnualSubmission.def3.model.response

import api.models.domain.ex.MtdNicExemption
import play.api.libs.json.{JsValue, Json}
import shared.utils.UnitSpec
import v5.retrieveAnnualSubmission.def3.model.Def3_RetrieveAnnualSubmissionFixture

class RetrieveNonFinancialsSpec extends UnitSpec with Def3_RetrieveAnnualSubmissionFixture {

  "reads" should {
    "passed a valid JSON" should {
      "return the model" in {
        val requestJson: JsValue = Json.parse(s"""
             |{
             |  "businessDetailsChangedRecently": true,
             |  "class4NicsExemptionReason": "001"
             |}
             |""".stripMargin)

        requestJson.as[RetrieveNonFinancials] shouldBe RetrieveNonFinancials(
          businessDetailsChangedRecently = true,
          class4NicsExemptionReason = Some(MtdNicExemption.`non-resident`)
        )
      }
    }

    "writes" when {
      "passed a model" must {
        "return json" in {
          Json.toJson(nonFinancialsMtdJson) shouldBe
            Json.parse(s"""
                 |{
                 |    "businessDetailsChangedRecently": true,
                 |    "class4NicsExemptionReason": "non-resident"
                 |}
                 |""".stripMargin)
        }
      }

      "there is no exemption reason" must {
        "set exemptFromPayingClass4Nics true" in {
          Json.toJson(
            RetrieveNonFinancials(businessDetailsChangedRecently = true, class4NicsExemptionReason = None)
          ) shouldBe
            Json.parse(s"""
                 |{
                 |  "businessDetailsChangedRecently": true
                 |}
                 |""".stripMargin)
        }
      }
    }
  }

}
