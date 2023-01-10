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

package v1.models.request.amendSEAnnual

import play.api.libs.json.Json
import support.UnitSpec

class AmendAnnualSubmissionBodySpec extends UnitSpec with AmendAnnualSubmissionFixture {

  val model: AmendAnnualSubmissionBody = AmendAnnualSubmissionBody(
    allowances = Some(Allowances(None, None, None, None, None, None, None, None, None, None, None, None, None)),
    adjustments = Some(Adjustments(None, None, None, None, None, None, None, None, None)),
    nonFinancials = Some(NonFinancials(businessDetailsChangedRecently = true, None))
  )

  "reads" when {
    "passed valid mtd JSON" should {
      "return the model" in {
        Json
          .parse(s"""{
             |  "allowances": {},
             |  "adjustments": {},
             |  "nonFinancials": {
             |    "businessDetailsChangedRecently": true
             |  }
             |}
             |""".stripMargin)
          .as[AmendAnnualSubmissionBody] shouldBe model
      }
    }

    "passed populated JSON" should {
      "return the corresponding model" in {
        amendAnnualSubmissionBodyMtdJson().as[AmendAnnualSubmissionBody] shouldBe amendAnnualSubmissionBody()
      }
    }
  }

  "writes" when {
    "passed a model" should {
      "return downstream JSON" in {
        Json.toJson(model) shouldBe
          Json.parse(s"""{
               |  "annualAllowances": {},
               |  "annualAdjustments": {},
               |  "annualNonFinancials": {
               |    "businessDetailsChangedRecently": true,
               |    "exemptFromPayingClass4Nics": false
               |  }
               |}
               |""".stripMargin)
      }
    }

    "passed a populated model" should {
      "return the populated downstream JSON" in {
        Json.toJson(amendAnnualSubmissionBody()) shouldBe amendAnnualSubmissionBodyDownstreamJson()
      }
    }
  }

}
