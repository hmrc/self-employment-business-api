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

package api.definition

import api.config.Deprecation.NotDeprecated
import api.config.MockAppConfig
import api.definition.APIAccessType.{CONTROLLED, PUBLIC}
import api.definition.APIStatus.{ALPHA, BETA}
import api.mocks.MockHttpClient
import api.routing.*
import api.utils.UnitSpec
import cats.implicits.catsSyntaxValidatedId

import scala.language.reflectiveCalls

class ApiDefinitionFactorySpec extends UnitSpec {

  "calling definition" when {
    List((PUBLIC, false), (CONTROLLED, true)).foreach { (accessType, controlledAccessEnabled) =>
      s"the controlled access flag is set to $controlledAccessEnabled" should {
        s"return a valid Definition case class with the access type set to $accessType" in new Test {
          List(Version5).foreach { version =>
            MockAppConfig.apiGatewayContext.returns("individuals/self-assessment/adjustable-summary")
            MockAppConfig.deprecationFor(version).returns(NotDeprecated.valid)
            MockAppConfig.apiStatus(version).returns("BETA")
            MockAppConfig.endpointsEnabled(version).returns(true)
            MockAppConfig.controlledAccessEnabled.returns(controlledAccessEnabled)
          }

          override val apiDefinitionFactory = new ApiDefinitionFactory(mockAppConfig)

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

  "buildAPIStatus" when {
    "the 'apiStatus' parameter is present and valid" should {

      s"return the expected status" in new Test {
        setupMockConfig(Version9)
        MockAppConfig.apiStatus(Version9) returns "BETA"

        val result: APIStatus = checkBuildApiStatus(Version9)
        result shouldBe BETA
      }

    }

    "the 'apiStatus' parameter is present but invalid" should {
      s"default to alpha" in new Test {
        setupMockConfig(Version9)
        MockAppConfig.apiStatus(Version9) returns "not-a-status"

        checkBuildApiStatus(Version9) shouldBe ALPHA
      }
    }

    "the 'deprecatedOn' parameter is missing for a deprecated version" should {
      "throw an exception" in new Test {
        MockAppConfig.apiStatus(Version9) returns "DEPRECATED"

        MockAppConfig
          .deprecationFor(Version9)
          .returns("deprecatedOn date is required for a deprecated version".invalid)
          .anyNumberOfTimes()

        val exception: Exception = intercept[Exception] {
          checkBuildApiStatus(Version9)
        }

        val exceptionMessage: String = exception.getMessage
        exceptionMessage shouldBe "deprecatedOn date is required for a deprecated version"
      }
    }
  }

  trait Test extends UnitSpec with MockHttpClient with MockAppConfig {
    MockAppConfig.apiGatewayContext returns "individuals/self-assessment/adjustable-summary"

    protected val apiDefinitionFactory: ApiDefinitionFactory = new ApiDefinitionFactory(mockAppConfig) {

      override lazy val definition: Definition = Definition(
        APIDefinition(
          "test API definition",
          "description",
          "context",
          List("category"),
          List(APIVersion(Version5, APIStatus.BETA, APIAccessType.PUBLIC, endpointsEnabled = true)),
          None
        )
      )

    }

    def checkBuildApiStatus(version: Version): APIStatus = apiDefinitionFactory.buildAPIStatus(version)

    protected def setupMockConfig(version: Version): Unit = {
      MockAppConfig
        .deprecationFor(version)
        .returns(NotDeprecated.valid)
        .anyNumberOfTimes()
    }

  }

}
