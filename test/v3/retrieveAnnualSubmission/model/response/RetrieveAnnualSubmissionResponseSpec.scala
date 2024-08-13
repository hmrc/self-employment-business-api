/*
 * Copyright 2024 HM Revenue & Customs
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

package v3.retrieveAnnualSubmission.model.response

import shared.utils.UnitSpec
import shared.config.MockAppConfig
import shared.hateoas.{Link, Method}
import shared.models.domain.{BusinessId, Nino}
import v3.retrieveAnnualSubmission.model.response.RetrieveAnnualSubmissionResponse.RetrieveAnnualSubmissionLinksFactory

class RetrieveAnnualSubmissionResponseSpec extends UnitSpec with MockAppConfig {

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

        val result = RetrieveAnnualSubmissionLinksFactory.links(mockAppConfig, data)

        result shouldBe List(
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
