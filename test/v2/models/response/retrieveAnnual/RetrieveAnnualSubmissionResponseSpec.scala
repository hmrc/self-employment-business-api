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

package v2.models.response.retrieveAnnual

import api.hateoas.{Link, Method}
import api.models.domain.{BusinessId, Nino}
import mocks.MockAppConfig
import play.api.libs.json.Json
import support.UnitSpec
import v2.fixtures.RetrieveAnnualSubmissionFixture

class RetrieveAnnualSubmissionResponseSpec extends UnitSpec with MockAppConfig with RetrieveAnnualSubmissionFixture {

  val model: RetrieveAnnualSubmissionResponse = RetrieveAnnualSubmissionResponse(
    allowances = Some(Allowances(None, None, None, None, None, None, None, None, None, None, None, None, None)),
    adjustments = Some(Adjustments(None, None, None, None, None, None, None, None, None)),
    nonFinancials = Some(NonFinancials(businessDetailsChangedRecently = true, None))
  )

  "reads" should {
    "return a valid model" when {
      "passed valid JSON" in {
        Json
          .parse(s"""{
             |  "annualAllowances": {},
             |  "annualAdjustments": {},
             |  "annualNonFinancials": {
             |    "businessDetailsChangedRecently": true
             |  }
             |}
             |""".stripMargin)
          .as[RetrieveAnnualSubmissionResponse] shouldBe model
      }
    }
  }

  "writes" should {
    "return valid JSON" when {
      "passed a valid model" in {
        Json.toJson(model) shouldBe
          Json.parse(s"""{
               |  "allowances": {},
               |  "adjustments": {},
               |  "nonFinancials": {
               |    "businessDetailsChangedRecently": true
               |  }
               |}
               |""".stripMargin)
      }
    }
  }

  "LinksFactory" should {
    "produce the correct links" when {
      "called" in {
        val nino       = "AA111111A"
        val businessId = "XAIS12345678910"
        val taxYear    = "2019-20"

        val data: RetrieveAnnualSubmissionHateoasData = RetrieveAnnualSubmissionHateoasData(
          Nino(nino),
          BusinessId(businessId),
          taxYear
        )

        MockAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        RetrieveAnnualSubmissionResponse.RetrieveAnnualSubmissionLinksFactory.links(mockAppConfig, data) shouldBe Seq(
          Link(
            href = s"/my/context/$nino/$businessId/annual/$taxYear",
            method = Method.PUT,
            rel = "create-and-amend-self-employment-annual-submission"),
          Link(href = s"/my/context/$nino/$businessId/annual/$taxYear", method = Method.GET, rel = "self"),
          Link(href = s"/my/context/$nino/$businessId/annual/$taxYear", method = Method.DELETE, rel = "delete-self-employment-annual-submission")
        )
      }
    }
  }

}
