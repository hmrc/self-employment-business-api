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
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import stubs.{AuditStub, AuthStub, BaseDownstreamStub, MtdIdLookupStub}
import support.IntegrationBaseSpec
import v2.fixtures.RetrieveAnnualSubmissionFixture

class RetrieveAnnualSubmissionControllerISpec extends IntegrationBaseSpec with RetrieveAnnualSubmissionFixture {

  "calling the V3 retrieve endpoint" should {

    "return a 200 status code" when {
        s"any valid request is made" in new NonTysTest {
          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            BaseDownstreamStub.onSuccess(BaseDownstreamStub.GET, downstreamUri, OK, downstreamRetrieveResponseJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe OK
          response.json shouldBe mtdRetrieveAnnualSubmissionJsonWithHateoas(nino, businessId, taxYear)
          response.header("X-CorrelationId").nonEmpty shouldBe true
          response.header("Content-Type") shouldBe Some("application/json")
        }

        s"any valid request is made with a TYS tax year" in new TysIfsTest {
          override def setupStubs(): StubMapping = {

            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            BaseDownstreamStub.onSuccess(BaseDownstreamStub.GET, downstreamUri, OK, downstreamRetrieveResponseJson)
          }

          val response: WSResponse = await(request().get())
          response.status shouldBe OK
          response.json shouldBe mtdRetrieveAnnualSubmissionJsonWithHateoas(nino, businessId, taxYear)
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
          s"validation fails with ${expectedBody.code} error" in new NonTysTest {

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
          ("AA123", "XAIS12345678910", "2021-22", BAD_REQUEST, NinoFormatError),
          ("AA123456A", "203100", "2021-22", BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", "XAIS12345678910", "203100", BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "XAIS12345678910", "2020-22", BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "XAIS12345678910", "2016-17", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )

        input.foreach(args => validationErrorTest(args._1, args._2, args._3, args._4, args._5))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new NonTysTest {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              BaseDownstreamStub.onError(BaseDownstreamStub.GET, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val errors = Seq(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_INCOMESOURCEID", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (NOT_FOUND, "INCOME_SOURCE_NOT_FOUND", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "NOT_FOUND_PERIOD", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )

        val extraTysErrors = Seq(
          (BAD_REQUEST, "INVALID_INCOMESOURCE_ID", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_DELETED_RETURN_PERIOD", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "SUBMISSION_DATA_NOT_FOUND", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "INCOME_DATA_SOURCE_NOT_FOUND", NOT_FOUND, NotFoundError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )
        (errors ++ extraTysErrors).foreach(args => serviceErrorTest(args._1, args._2, args._3, args._4))
      }
    }
  }

  private trait Test {

    val nino       = "AA123456A"
    val businessId = "XAIS12345678910"

    def taxYear: String
    def downstreamUri: String

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

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "downstream message"
         |      }
    """.stripMargin

  }

  private trait TysIfsTest extends Test {
    def taxYear: String = "2023-24"

    def downstreamUri: String = s"/income-tax/23-24/$nino/self-employments/$businessId/annual-summaries"

  }

  private trait NonTysTest extends Test {
    def taxYear: String = "2022-23"

    def downstreamUri: String = s"/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/2023"
  }

}
