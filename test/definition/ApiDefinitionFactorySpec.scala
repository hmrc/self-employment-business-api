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

package definition

import api.mocks.MockHttpClient
import config.ConfidenceLevelConfig
import definition.APIStatus.{ALPHA, BETA}
import mocks.MockAppConfig
import play.api.Configuration
import routing.{Version1, Version2, Version3}
import support.UnitSpec
import uk.gov.hmrc.auth.core.ConfidenceLevel

class ApiDefinitionFactorySpec extends UnitSpec {

  private val confidenceLevel: ConfidenceLevel = ConfidenceLevel.L200

  class Test extends MockHttpClient with MockAppConfig {
    val apiDefinitionFactory = new ApiDefinitionFactory(mockAppConfig)
    MockAppConfig.apiGatewayContext returns "individuals/business/self-employment"
  }

  "definition" when {
    "called" should {
      "return a valid Definition case class" in new Test {
        MockAppConfig.featureSwitches returns Configuration.empty
        MockAppConfig.apiStatus(Version1) returns "ALPHA"
        MockAppConfig.apiStatus(Version2) returns "BETA"
        MockAppConfig.apiStatus(Version3) returns "BETA"
        MockAppConfig.endpointsEnabled(Version1).returns(false).anyNumberOfTimes()
        MockAppConfig.endpointsEnabled(Version2).returns(true).anyNumberOfTimes()
        MockAppConfig.endpointsEnabled(Version3).returns(true).anyNumberOfTimes()

        MockAppConfig.confidenceLevelCheckEnabled
          .returns(ConfidenceLevelConfig(confidenceLevel = confidenceLevel, definitionEnabled = true, authValidationEnabled = true))
          .anyNumberOfTimes()

        private val readScope  = "read:self-assessment"
        private val writeScope = "write:self-assessment"

        apiDefinitionFactory.definition shouldBe
          Definition(
            scopes = Seq(
              Scope(
                key = readScope,
                name = "View your Self Assessment information",
                description = "Allow read access to self assessment data",
                confidenceLevel
              ),
              Scope(
                key = writeScope,
                name = "Change your Self Assessment information",
                description = "Allow write access to self assessment data",
                confidenceLevel
              )
            ),
            api = APIDefinition(
              name = "Self-Employment Business (MTD)",
              description = "An API for providing Annual and Periodic Summary data",
              context = "individuals/business/self-employment",
              categories = Seq("INCOME_TAX_MTD"),
              versions = Seq(
                APIVersion(
                  version = Version1,
                  status = ALPHA,
                  endpointsEnabled = false
                ),
                APIVersion(
                  version = Version2,
                  status = BETA,
                  endpointsEnabled = true
                ),
                APIVersion(
                  version = Version3,
                  status = BETA,
                  endpointsEnabled = true
                )
              ),
              requiresTrust = None
            )
          )
      }
    }
  }

  "confidenceLevel" when {
    Seq(
      (true, ConfidenceLevel.L250, ConfidenceLevel.L250),
      (true, ConfidenceLevel.L200, ConfidenceLevel.L200),
      (false, ConfidenceLevel.L200, ConfidenceLevel.L50)
    ).foreach { case (definitionEnabled, configCL, expectedDefinitionCL) =>
      s"confidence-level-check.definition.enabled is $definitionEnabled and confidence-level = $configCL" should {
        s"return confidence level $expectedDefinitionCL" in new Test {
          MockAppConfig.confidenceLevelCheckEnabled returns ConfidenceLevelConfig(
            confidenceLevel = configCL,
            definitionEnabled = definitionEnabled,
            authValidationEnabled = true)
          apiDefinitionFactory.confidenceLevel shouldBe expectedDefinitionCL
        }
      }
    }
  }

  "buildAPIStatus" when {
    "the 'apiStatus' parameter is present and valid" should {
      Seq(
        (Version1, ALPHA),
        (Version2, BETA),
        (Version3, BETA)
      ).foreach { case (version, status) =>
        s"return the correct $status for $version " in new Test {
          MockAppConfig.apiStatus(version) returns status.toString
          apiDefinitionFactory.buildAPIStatus(version) shouldBe status
        }
      }
    }

    "the 'apiStatus' parameter is present and invalid" should {
      Seq(Version1, Version2, Version3).foreach { version =>
        s"default to alpha for $version " in new Test {
          MockAppConfig.apiStatus(version) returns "ALPHO"
          apiDefinitionFactory.buildAPIStatus(version) shouldBe ALPHA
        }
      }
    }

  }

}
