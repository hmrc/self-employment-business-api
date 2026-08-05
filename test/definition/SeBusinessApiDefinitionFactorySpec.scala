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
import api.definition.*
import api.definition.APIAccessType.{CONTROLLED, PUBLIC}
import api.definition.APIStatus.BETA
import api.routing.Version5
import api.utils.UnitSpec
import cats.implicits.catsSyntaxValidatedId

class SeBusinessApiDefinitionFactorySpec extends UnitSpec with MockAppConfig {

  "calling definition" when {
    List((PUBLIC, false), (CONTROLLED, true)).foreach { (accessType, controlledAccessEnabled) =>
      s"the controlled access flag is set to $controlledAccessEnabled" should {
        s"return a valid Definition case class with the access type set to $accessType" in {
          List(Version5).foreach { version =>
            MockAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary")
            MockAppConfig.deprecationFor(version).returns(NotDeprecated.valid)
            MockAppConfig.apiStatus(version).returns("BETA")
            MockAppConfig.endpointsEnabled(version).returns(true)
            MockAppConfig.controlledAccessEnabled.returns(controlledAccessEnabled)
          }

          val apiDefinitionFactory = new SeBusinessApiDefinitionFactory(mockAppConfig)

          apiDefinitionFactory.definition shouldBe
            Definition(
              api = APIDefinition(
                name = "Self Employment Business (MTD)",
                description = "An API for providing Annual and Periodic Summary data",
                context = "individuals/self-assessment/adjustable-summary",
                categories = List("INCOME_TAX_MTD"),
                versions = List(
                  APIVersion(
                    version = Version5,
                    status = BETA,
                    access = accessType,
                    endpointsEnabled = true
                  )
                ),
                requiresTrust = None
              )
            )
        }
      }
    }
  }

}
