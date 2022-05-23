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

package v1.models.response.amendPeriodic

import mocks.MockAppConfig
import support.UnitSpec
import v1.models.domain.{BusinessId, Nino}
import v1.models.hateoas.{Link, Method}

class AmendPeriodicResponseSpec extends UnitSpec with MockAppConfig {

  "LinksFactory" should {
    "produce the correct links" when {
      "called" in {
        val nino                           = "AA111111A"
        val businessId                     = "id"
        val periodId                       = "periodId"
        val data: AmendPeriodicHateoasData = AmendPeriodicHateoasData(Nino(nino), BusinessId(businessId), periodId)

        MockAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        AmendPeriodicResponse.LinksFactory.links(mockAppConfig, data) shouldBe Seq(
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.PUT, rel = "amend-self-employment-period-summary"),
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.GET, rel = "self"),
          Link(href = s"/my/context/$nino/$businessId/period", method = Method.GET, rel = "list-self-employment-period-summaries")
        )
      }
    }
  }

}
