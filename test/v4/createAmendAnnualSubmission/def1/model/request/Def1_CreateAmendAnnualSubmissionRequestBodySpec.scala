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

package v4.createAmendAnnualSubmission.def1.model.request

import play.api.libs.json.Json
import shared.utils.UnitSpec

class Def1_CreateAmendAnnualSubmissionRequestBodySpec extends UnitSpec with Def1_CreateAmendAnnualSubmissionFixture {

  val model: Def1_CreateAmendAnnualSubmissionRequestBody = Def1_CreateAmendAnnualSubmissionRequestBody(
    allowances = Some(Def1_CreateAmend_Allowances(None, None, None, None, None, None, None, None, None, None, None, None, None)),
    adjustments = Some(Def1_CreateAmend_Adjustments(None, None, None, None, None, None, None, None, None)),
    nonFinancials = Some(Def1_CreateAmend_NonFinancials(businessDetailsChangedRecently = true, None))
  )

  "reads" when {
    "given a valid mtd json object" should {
      "return the deserialised Scala object" in {
        val result = Json
          .parse(s"""{
             |  "allowances": {},
             |  "adjustments": {},
             |  "nonFinancials": {
             |    "businessDetailsChangedRecently": true
             |  }
             |}
             |""".stripMargin)
          .as[Def1_CreateAmendAnnualSubmissionRequestBody]

        result shouldBe model
      }
    }

    "given populated JSON" should {
      "return the deserialised Scala object" in {
        val result = createAmendAnnualSubmissionRequestBodyWithAdditionalFieldsMtdJson()
          .as[Def1_CreateAmendAnnualSubmissionRequestBody]

        result shouldBe createAmendAnnualSubmissionRequestBody()
      }
    }
  }

  "writes" when {
    "given a case class" should {
      "return downstream JSON" in {
        val result = Json.toJson(model)

        result shouldBe Json.parse(
          s"""{
            |  "annualAllowances": {},
            |  "annualAdjustments": {},
            |  "annualNonFinancials": {
            |    "businessDetailsChangedRecently": true,
            |    "exemptFromPayingClass4Nics": false
            |  }
            |}
            |""".stripMargin
        )
      }
    }

    "given a populated case class" should {
      "return the populated downstream JSON" in {
        val result = Json.toJson(createAmendAnnualSubmissionRequestBodyWithAdditionalFieldsMtdJson())
        result shouldBe createAmendAnnualSubmissionRequestBodyWithAdditionalFieldsMtdJson()
      }
    }
  }

}
