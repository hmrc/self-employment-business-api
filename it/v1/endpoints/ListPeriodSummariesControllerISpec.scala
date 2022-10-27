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
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri(), OK, downstreamResponseBody(fromDate, toDate))
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
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri(), OK, downstreamResponseBody(fromDate, toDate))
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe OK
        response.json shouldBe responseBody(periodId, fromDate, toDate)
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Content-Type") shouldBe Some("application/json")
      }
    }
    "return error according to spec" when {

      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestBusinessId: String,
                                requestTaxYear: String,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new TysIfsTest {

            override val nino: String       = requestNino
            override val businessId: String = requestBusinessId
            override val taxYear: String    = requestTaxYear

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
          ("AA123", "XAIS12345678910", "2023-24", BAD_REQUEST, NinoFormatError),
          ("AA123456A", "203100", "2023-24", BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", "XAIS12345678910", "2021-2", BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "XAIS12345678910", "2023-25", BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "XAIS12345678910", "2021-22", BAD_REQUEST, InvalidTaxYearParameterError)
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
              DownstreamStub.onError(DownstreamStub.GET, downstreamUri(), downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_INCOME_SOURCEID", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_INCOMESOURCE_ID", BAD_REQUEST, BusinessIdFormatError),
          (NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (INTERNAL_SERVER_ERROR, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

  private trait Test {

    val nino       = "AA123456A"
    val businessId = "XAIS12345678910"

    val retrievePeriodSummaryHateoasUri: String
    val listPeriodSummariesHateoasUri: String

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
         |          "href": "$retrievePeriodSummaryHateoasUri",
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
         |      "href": "$listPeriodSummariesHateoasUri",
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
    val periodId = "2019-01-01_2020-01-01"
    val fromDate = "2019-01-01"
    val toDate   = "2020-01-01"

    val retrievePeriodSummaryHateoasUri: String = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId"
    val listPeriodSummariesHateoasUri: String   = s"/individuals/business/self-employment/$nino/$businessId/period"

    def downstreamUri(): String = s"/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries"

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

    val periodId        = "2024-01-01_2024-01-02"
    val fromDate        = "2024-01-01"
    val toDate          = "2024-01-02"
    val taxYear         = "2024-25"
    lazy val tysTaxYear = TaxYear.fromMtd(taxYear)

    val retrievePeriodSummaryHateoasUri: String = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId?taxYear=$taxYear"
    val listPeriodSummariesHateoasUri: String   = s"/individuals/business/self-employment/$nino/$businessId/period?taxYear=$taxYear"

    def downstreamUri(): String = s"/income-tax/${tysTaxYear.asTysDownstream}/$nino/self-employments/$businessId/periodic-summaries"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(s"$uri?taxYear=$taxYear")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

  }

}
