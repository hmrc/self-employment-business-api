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
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v1.models.domain.TaxYear
import v1.models.errors._
import v1.stubs.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}

class ListPeriodSummariesControllerISpec extends IntegrationBaseSpec {

  "calling the list period summaries endpoint" should {

    "return a 200 status code" when {

      "any valid request is made" in new NonTysTest {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, downstreamResponseBody(fromDate, toDate))
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe OK
        response.json shouldBe responseBody(periodId, fromDate, toDate)
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Content-Type") shouldBe Some("application/json")
      }

      "any valid request is made for a TYS specific year" in new TysIfsTest {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, downstreamResponseBody(fromDate, toDate))
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe OK
        response.json shouldBe responseBody(periodId, fromDate, toDate)
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Content-Type") shouldBe Some("application/json")
      }
    }
    "return error according to spec" when {

      "tys specific validation" when {
        s"validation fails with INVALID_TAX_YEAR error" in new TysIfsTest {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(invalidTaxYearRequest().get())
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(TaxYearFormatError)
        }
      }
      "validation error" when {
        def validationErrorTest(requestNino: String, requestBusinessId: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new TysIfsTest {

            override val nino: String       = requestNino
            override val businessId: String = requestBusinessId

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(requestNino)
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          ("AA123", "XAIS12345678910", BAD_REQUEST, NinoFormatError),
          ("AA123456A", "203100", BAD_REQUEST, BusinessIdFormatError)
        )

        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new NonTysTest {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.GET, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_INCOME_SOURCEID", BAD_REQUEST, BusinessIdFormatError),
          (NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

  private trait Test {

    val nino       = "AA123456A"
    val businessId = "XAIS12345678910"

    def responseBody(periodId: String, fromDate: String, toDate: String): JsValue = Json.parse(
      s"""
         |{
         |  "periods": [
         |    {
         |      "periodId": "$periodId",
         |      "periodStartDate": "$fromDate",
         |      "periodEndDate": "$toDate",
         |      "links": [
         |        {
         |          "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |          "method": "GET",
         |          "rel": "self"
         |        }
         |      ]
         |    }
         |  ],
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period",
         |      "method": "POST",
         |      "rel": "create-self-employment-period-summary"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period",
         |      "method": "GET",
         |      "rel": "self"
         |    }
         |  ]
         |}
      """.stripMargin
    )

    def downstreamResponseBody(fromDate: String, toDate: String): JsValue = Json.parse(
      s"""
         |{
         |  "periods": [
         |      {
         |          "transactionReference": "1111111111",
         |          "from": "$fromDate",
         |          "to": "$toDate"
         |      }
         |  ]
         |}
       """.stripMargin
    )

    def uri: String = s"/$nino/$businessId/period"

    def setupStubs(): StubMapping

    def errorBody(code: String): String =
      s"""
         |{
         |   "code": "$code",
         |   "reason": "message"
         |}
       """.stripMargin

  }

  private trait NonTysTest extends Test {
    val periodId              = "2019-01-01_2020-01-01"
    val fromDate              = "2019-01-01"
    val toDate                = "2020-01-01"
    val downstreamUri: String = s"/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

  }

  private trait TysIfsTest extends Test {

    val periodId      = "2024-01-01_2024-01-02"
    val fromDate      = "2024-01-01"
    val toDate        = "2024-01-02"
    val taxYear       = TaxYear.fromMtd("2024-25")
    val downstreamUri = s"/income-tax/${taxYear.asTysDownstream}/$nino/self-employments/$businessId/periodic-summaries"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(s"$uri?taxYear=${taxYear.asMtd}")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def invalidTaxYearRequest(): WSRequest = {
      setupStubs()
      buildRequest(s"$uri?taxYear=1234")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

  }

}
