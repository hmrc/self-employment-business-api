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

import shared.config.AppConfig
import shared.definition._
import shared.routing.{Version2, Version3, Version4}

import javax.inject.{Inject, Singleton}

@Singleton
class SeBusinessApiDefinitionFactory @Inject() (protected val appConfig: AppConfig) extends ApiDefinitionFactory {

  lazy val definition: Definition =
    Definition(
      scopes = scopes,
      api = APIDefinition(
        name = "Self-Employment Business (MTD)",
        description = "An API for providing Annual and Periodic Summary data",
        context = appConfig.apiGatewayContext,
        categories = List(mtdCategory),
        versions = List(
          APIVersion(
            version = Version2,
            status = buildAPIStatus(Version2),
            endpointsEnabled = appConfig.endpointsEnabled(Version2)
          ),
          APIVersion(
            version = Version3,
            status = buildAPIStatus(Version3),
            endpointsEnabled = appConfig.endpointsEnabled(Version3)
          ),
          APIVersion(
            version = Version4,
            status = buildAPIStatus(Version4),
            endpointsEnabled = appConfig.endpointsEnabled(Version4)
          )
        ),
        requiresTrust = None
      )
    )

}
