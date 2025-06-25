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

package v5.retrieveAnnualSubmission.def2

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.domain.TaxYear
import shared.models.errors._
import shared.services.{AuditStub, AuthStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import stubs.BaseDownstreamStub
import v5.retrieveAnnualSubmission.def2.model.Def2_RetrieveAnnualSubmissionFixture

class Def2_RetrieveAnnualSubmissionControllerISpec extends IntegrationBaseSpec with Def2_RetrieveAnnualSubmissionFixture {

  "calling the V5 retrieve endpoint" should {

    "return a 200 status code" when {
      s"any valid request is made" in new Test {
        override def setupStubs(): StubMapping = {

          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BaseDownstreamStub.onSuccess(BaseDownstreamStub.GET, downstreamUri, OK, downstreamRetrieveResponseJson)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe OK
        response.json shouldBe mtdRetrieveResponseJson
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
          s"validation fails with ${expectedBody.code} error" in new Test {

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
          ("AA123456A", "XAIS12345678910", "NOT_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "XAIS12345678910", "2020-22", BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "XAIS12345678910", "2016-17", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )

        input.foreach(args => validationErrorTest(args._1, args._2, args._3, args._4, args._5))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {

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

    def taxYear: String       = "2024-25"
    def downstreamUri: String = s"/income-tax/${TaxYear.fromMtd(taxYear).asTysDownstream}/$nino/self-employments/$businessId/annual-summaries"

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, s"application/vnd.hmrc.5.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    private def uri: String = s"/$nino/$businessId/annual/$taxYear"

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "downstream message"
         |      }
    """.stripMargin

  }

}
