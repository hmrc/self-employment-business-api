/*
 * Copyright 2026 HM Revenue & Customs
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

package definition

import api.config.Deprecation.NotDeprecated
import api.config.MockAppConfig
import api.definition.{APIAccessType, Definition}
import api.routing.Version5
import api.utils.UnitSpec
import cats.implicits.catsSyntaxValidatedId

class SeBusinessApiDefinitionFactorySpec extends UnitSpec with MockAppConfig {

  "SeBusinessApiDefinitionFactory" when {

    "the access level is set" when {
      "the controlled access flag is enabled" should {
        "return CONTROLLED" in {

          MockAppConfig.apiGatewayContext returns "individuals/self-assessment/adjustable-summary"
          MockAppConfig.endpointsEnabled(Version5)
          MockAppConfig.apiStatus(Version5) returns "BETA"
          MockAppConfig.deprecationFor(Version5).returns(NotDeprecated.valid).anyNumberOfTimes()

          MockAppConfig.controlledAccessEnabled returns true

          val definition: Definition = new SeBusinessApiDefinitionFactory(mockAppConfig).definition

          definition.api.versions.head.access shouldBe APIAccessType.CONTROLLED
        }
      }

      "the controlled access flag is disabled" should {
        "return PUBLIC" in {

          MockAppConfig.apiGatewayContext returns "individuals/self-assessment/adjustable-summary"
          MockAppConfig.endpointsEnabled(Version5)
          MockAppConfig.apiStatus(Version5) returns "BETA"
          MockAppConfig.deprecationFor(Version5).returns(NotDeprecated.valid).anyNumberOfTimes()

          MockAppConfig.controlledAccessEnabled returns false

          val definition: Definition = new SeBusinessApiDefinitionFactory(mockAppConfig).definition

          definition.api.versions.head.access shouldBe APIAccessType.PUBLIC
        }
      }
    }
  }

}
