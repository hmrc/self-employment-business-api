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

package v3.endpoints

import api.models.errors._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status
import play.api.libs.json.{JsObject, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import stubs.{AuditStub, AuthStub, BaseDownstreamStub, MtdIdLookupStub}
import support.IntegrationBaseSpec

class DeleteAnnualSubmissionControllerISpec extends IntegrationBaseSpec {

  private trait Test {
    def taxYear: String
    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, s"application/vnd.hmrc.3.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def uri: String = s"/$nino/$businessId/annual/$taxYear"

    def nino: String = "AA123456A"

    def businessId   = "XAIS12345678910"

  }

  private trait NonTysTest extends Test {
    override def taxYear: String = "2021-22"
    def downstreamUri: String    = s"/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/2022"
  }

  private trait TysIfsTest extends Test {
    override def taxYear: String = "2023-24"
    def downstreamUri: String    = s"/income-tax/23-24/$nino/self-employments/$businessId/annual-summaries"
  }

  "calling the V3 deleteAnnualSubmission endpoint" should {
    "return a 204 status" when {
        s"any valid non-TYS request is made" in new NonTysTest {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            BaseDownstreamStub
              .when(method = BaseDownstreamStub.PUT, uri = downstreamUri)
              .withRequestBody(Json.parse("{}"))
              .thenReturn(status = Status.NO_CONTENT)
          }

          val response: WSResponse = await(request().delete())
          response.status shouldBe Status.NO_CONTENT
          response.body shouldBe ""
          response.header("X-CorrelationId").nonEmpty shouldBe true
        }

        s"any valid TYS request is made" in new TysIfsTest {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)

            BaseDownstreamStub.onSuccess(BaseDownstreamStub.DELETE, downstreamUri, Status.NO_CONTENT, JsObject.empty)

          }

          val response: WSResponse = await(request().delete())
          response.status shouldBe Status.NO_CONTENT
          response.body shouldBe ""
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

          s"validation fails with ${expectedBody.code} error in" in new NonTysTest {

            override def nino: String       = requestNino
            override def taxYear: String    = requestTaxYear
            override def businessId: String = requestBusinessId

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
          ("Hippo", "XAIS12345678910", "2019-20", Status.BAD_REQUEST, NinoFormatError),
          ("AA123456A", "notABusinessId", "2019-20", Status.BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", "XAIS12345678910", "203100", Status.BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "XAIS12345678910", "2016-17", Status.BAD_REQUEST, RuleTaxYearNotSupportedError),
          ("AA123456A", "XAIS12345678910", "2018-20", Status.BAD_REQUEST, RuleTaxYearRangeInvalidError)
        )

        input.foreach(args => validationErrorTest(args._1, args._2, args._3, args._4, args._5))
      }
    }

    "downstream service error" when {
      def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"downstream returns an $downstreamCode error and status $downstreamStatus" in new NonTysTest {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            BaseDownstreamStub.onError(BaseDownstreamStub.PUT, downstreamUri, downstreamStatus, errorBody(downstreamCode))
          }

          val response: WSResponse = await(request().delete())
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
        }
      }

      def errorBody(code: String): String =
        s"""
             |      {
             |        "code": "$code",
             |        "reason": "downstream message"
             |      }
    """.stripMargin

      val errors = Seq(
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

      val extraTysErrors = Seq(
        (Status.BAD_REQUEST, "INVALID_INCOME_SOURCE_ID", Status.BAD_REQUEST, BusinessIdFormatError),
        (Status.BAD_REQUEST, "INVALID_CORRELATION_ID", Status.INTERNAL_SERVER_ERROR, InternalError),
        (Status.NOT_FOUND, "PERIOD_NOT_FOUND", Status.NOT_FOUND, NotFoundError),
        (Status.NOT_FOUND, "INCOME_SOURCE_DATA_NOT_FOUND", Status.NOT_FOUND, NotFoundError),
        (Status.GONE, "PERIOD_ALREADY_DELETED", Status.NOT_FOUND, NotFoundError),
        (Status.UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", Status.BAD_REQUEST, RuleTaxYearNotSupportedError)
      )

      (errors ++ extraTysErrors).foreach(args => serviceErrorTest(args._1, args._2, args._3, args._4))
    }
  }

}
