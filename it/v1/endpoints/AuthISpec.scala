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

package v1.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v1.stubs.{AuditStub, AuthStub, DesStub, MtdIdLookupStub}

class AuthISpec extends IntegrationBaseSpec {

  private trait Test {
    val nino: String = "AA123456A"
    val businessId: String = "XAIS12345678910"
    val taxYear: String = "2020-21"
    val desTaxYear: String = "2021"
    val data: String = "someData"

    val requestJson: String =
      s"""
         |{
         |   "adjustments": {
         |        "includedNonTaxableProfits": 1.11
         |    }
         |}
        """.stripMargin

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(s"/$nino/$businessId/annual/$taxYear")
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.1.0+json"))
    }

    def desUri: String = s"/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/$desTaxYear"

    val desResponseBody: JsValue = Json.parse(
      """{
        |   "transactionReference": "2017090920170909"
        |}""".stripMargin)
  }

  "Calling the sample endpoint" when {
    "the NINO cannot be converted to a MTD ID" should {
      "return 500" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.internalServerError(nino)
        }

        val response: WSResponse = await(request().put(Json.parse(requestJson)))
        response.status shouldBe INTERNAL_SERVER_ERROR
      }
    }

    "an MTD ID is successfully retrieve from the NINO and the user is authorised" should {
      "return 200" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.PUT, desUri, OK, desResponseBody)
        }

        val response: WSResponse = await(request().put(Json.parse(requestJson)))
        response.status shouldBe OK
      }
    }

    "an MTD ID is successfully retrieve from the NINO and the user is NOT logged in" should {
      "return 403" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)
          AuthStub.unauthorisedNotLoggedIn()
        }

        val response: WSResponse = await(request().put(Json.parse(requestJson)))
        response.status shouldBe FORBIDDEN
      }
    }

    "an MTD ID is successfully retrieve from the NINO and the user is NOT authorised" should {
      "return 403" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.ninoFound(nino)
          AuthStub.unauthorisedOther()
        }

        val response: WSResponse = await(request().put(Json.parse(requestJson)))
        response.status shouldBe FORBIDDEN
      }
    }
  }
}
