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
import play.api.http.Status
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v1.models.errors._
import v1.stubs.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}

class DeleteAnnualSubmissionControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino       = "AA123456A"
    val businessId = "XAIS12345678910"
    val taxYear    = "2021-22"
    val desTaxYear = "2022"

    def uri: String = s"/$nino/$businessId/annual/$taxYear"

    def downstreamUri: String = s"/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/$desTaxYear"

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "message"
         |      }
    """.stripMargin

  }

  "Calling the deleteAnnualSubmission endpoint" should {

    "return a 204 status code" when {

      "any valid request is made" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          DownstreamStub
            .when(method = DownstreamStub.PUT, uri = downstreamUri)
            .withRequestBody(Json.parse("{}"))
            .thenReturn(status = Status.NO_CONTENT)
        }

        val response: WSResponse = await(request().delete())
        response.status shouldBe Status.NO_CONTENT
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }

    "return error according to spec" when {

      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestBusinessId: String,
                                requestTaxYear: String,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String       = requestNino
            override val businessId: String = requestBusinessId
            override val taxYear: String    = requestTaxYear

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request().delete())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = Seq(
          ("Walrus", "XAIS12345678910", "2019-20", Status.BAD_REQUEST, NinoFormatError),
          ("AA123456A", "notABusinessId", "2019-20", Status.BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", "XAIS12345678910", "203100", Status.BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "XAIS12345678910", "2016-17", Status.BAD_REQUEST, RuleTaxYearNotSupportedError),
          ("AA123456A", "XAIS12345678910", "2018-20", Status.BAD_REQUEST, RuleTaxYearRangeInvalidError)
        )

        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.PUT, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().delete())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (Status.BAD_REQUEST, "INVALID_NINO", Status.BAD_REQUEST, NinoFormatError),
          (Status.BAD_REQUEST, "INVALID_INCOME_SOURCE", Status.BAD_REQUEST, BusinessIdFormatError),
          (Status.BAD_REQUEST, "INVALID_TAX_YEAR", Status.BAD_REQUEST, TaxYearFormatError),
          (Status.FORBIDDEN, "ALLOWANCE_NOT_SUPPORTED", Status.INTERNAL_SERVER_ERROR, InternalError),
          (Status.NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", Status.NOT_FOUND, NotFoundError),
          (Status.NOT_FOUND, "NOT_FOUND", Status.NOT_FOUND, NotFoundError),
          (Status.GONE, "GONE", Status.NOT_FOUND, NotFoundError),
          (Status.BAD_REQUEST, "INVALID_CORRELATIONID", Status.INTERNAL_SERVER_ERROR, InternalError),
          (Status.BAD_REQUEST, "INVALID_PAYLOAD", Status.INTERNAL_SERVER_ERROR, InternalError),
          (Status.BAD_REQUEST, "MISSING_EXEMPTION_REASON", Status.INTERNAL_SERVER_ERROR, InternalError),
          (Status.BAD_REQUEST, "MISSING_EXEMPTION_INDICATOR", Status.INTERNAL_SERVER_ERROR, InternalError),
          (Status.BAD_REQUEST, "BAD_GATEWAY", Status.INTERNAL_SERVER_ERROR, InternalError),
          (Status.INTERNAL_SERVER_ERROR, "SERVER_ERROR", Status.INTERNAL_SERVER_ERROR, InternalError),
          (Status.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", Status.INTERNAL_SERVER_ERROR, InternalError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

}
