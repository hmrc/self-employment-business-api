/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.connectors

import mocks.MockAppConfig
import play.api.libs.json.{Json, Reads}
import v1.mocks.MockHttpClient
import v1.models.outcomes.ResponseWrapper

import scala.concurrent.Future

class DeleteRetrieveConnectorSpec extends ConnectorSpec {

  val nino: String = "AA111111A"
  val taxYear: String = "2019"

  class Test extends MockHttpClient with MockAppConfig {

    val connector: DeleteRetrieveConnector = new DeleteRetrieveConnector(
      http = mockHttpClient,
      appConfig = mockAppConfig
    )

    val desRequestHeaders: Seq[(String, String)] = Seq(
      "Environment" -> "des-environment",
      "Authorization" -> s"Bearer des-token"
    )

    MockedAppConfig.desBaseUrl returns baseUrl
    MockedAppConfig.desToken returns "des-token"
    MockedAppConfig.desEnvironment returns "des-environment"
  }

  "DeleteRetrieveConnector" when {
    "delete" must {
      "return a 204 status for a success scenario" in new Test {

        val outcome = Right(ResponseWrapper(correlationId, ()))
        implicit val desUri: DesUri[Unit] = DesUri[Unit](s"some-placeholder/savings/$nino/$taxYear")

        MockedHttpClient
          .delete(
            url = s"$baseUrl/some-placeholder/savings/$nino/$taxYear",
            requiredHeaders = desRequestHeaders: _*
          )
          .returns(Future.successful(outcome))

        await(connector.delete()) shouldBe outcome
      }
    }

    "retrieve" must {
      "return a 200 status for a success scenario" in new Test {

        case class Data(field: String)

        object Data {
          implicit val reads: Reads[Data] = Json.reads[Data]
        }

        val outcome = Right(ResponseWrapper(correlationId, Data("value")))
        implicit val desUri: DesUri[Data] = DesUri[Data](s"some-placeholder/savings/$nino/$taxYear")

        MockedHttpClient
          .get(
            url = s"$baseUrl/some-placeholder/savings/$nino/$taxYear",
            requiredHeaders = desRequestHeaders: _*
          )
          .returns(Future.successful(outcome))

        await(connector.retrieve[Data]()) shouldBe outcome
      }
    }
  }
}