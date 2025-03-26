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

package v3.amendPeriodSummary.def1

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors._
import shared.models.utils.JsonErrorValidators
import shared.services.{AuthStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import stubs.BaseDownstreamStub
import v3.amendPeriodSummary.def1.model.Def1_AmendPeriodSummaryFixture

class Def1_AmendPeriodSummaryControllerISpec extends IntegrationBaseSpec with JsonErrorValidators with Def1_AmendPeriodSummaryFixture {

  val requestBodyJson: JsValue = def1_AmendPeriodSummaryBodyMtdJson

  "The V3 Amend Period Summary endpoint" should {

    "return a 404 status code" when {

      "given a valid request for a non-tys tax year" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          BaseDownstreamStub
            .when(BaseDownstreamStub.PUT, downstreamUri, downstreamQueryParams)
            .thenReturn(status = OK, JsObject.empty)
        }

        val response: WSResponse = await(request().put(requestBodyJson))
        response.status shouldBe NOT_FOUND
        response.json shouldBe Json.toJson(NotFoundError)
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }

      "given an invalid request for a non-tys tax year" in new Test {
        val invalidBusinessId: String = "BAD_BUSINESS_ID"
        override def mtdUri: String   = s"/$nino/$invalidBusinessId/period/$periodId"

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().put(requestBodyJson))
        response.status shouldBe NOT_FOUND
        response.json shouldBe Json.toJson(NotFoundError)
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }

      "when downstream returns an different error code" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BaseDownstreamStub.onError(BaseDownstreamStub.PUT, downstreamUri, downstreamQueryParams, BAD_REQUEST, errorBody("INVALID_INCOME_SOURCE"))
        }

        val response: WSResponse = await(request().put(requestBodyJson))
        response.status shouldBe NOT_FOUND
        response.json shouldBe Json.toJson(NotFoundError)
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }
  }

  private trait Test {
    val nino: String       = "AA123456A"
    val businessId: String = "XAIS12345678910"
    val periodId: String   = "2019-01-01_2020-01-01"
    val from               = "2019-01-01"
    val to                 = "2020-01-01"

    def mtdUri: String        = s"/$nino/$businessId/period/$periodId"
    def downstreamUri: String = s"/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries"
    def setupStubs(): StubMapping

    def downstreamQueryParams: Map[String, String] = Map(
      "from" -> from,
      "to"   -> to
    )

    def request(): WSRequest = {
      setupStubs()
      buildRequest(mtdUri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.3.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def errorBody(code: String): String =
      s"""
         | {
         |   "code": "$code",
         |   "reason": "message"
         | }
    """.stripMargin

  }

}
