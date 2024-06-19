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

package v3.createAmendAnnualSubmission.model.response

import shared.config.MockAppConfig
import shared.hateoas.Link
import shared.hateoas.Method.{DELETE, GET, PUT}
import shared.models.domain.{BusinessId, Nino}
import shared.UnitSpec
class CreateAmendAnnualSubmissionResponseSpec extends UnitSpec with MockAppConfig {

  "LinksFactory" should {
    "return the correct links" in {
      val nino       = "AA111111A"
      val businessId = "XAIS12345678910"
      val taxYear    = "2019-20"

      MockAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

      CreateAmendAnnualSubmissionResponse.LinksFactory.links(
        mockAppConfig,
        CreateAmendAnnualSubmissionHateoasData(Nino(nino), BusinessId(businessId), taxYear)) shouldBe
        List(
          Link(s"/my/context/$nino/$businessId/annual/$taxYear", PUT, "create-and-amend-self-employment-annual-submission"),
          Link(s"/my/context/$nino/$businessId/annual/$taxYear", GET, "self"),
          Link(s"/my/context/$nino/$businessId/annual/$taxYear", DELETE, "delete-self-employment-annual-submission")
        )
    }
  }

}
