/*
 * Copyright 2026 HM Revenue & Customs
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

package v5.deleteAnnualSubmission.def1

import api.models.errors.RuleOutsideAmendmentWindowError
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status
import play.api.http.Status.*
import play.api.libs.json.Json
import play.api.libs.ws.DefaultBodyReadables.readableAsString
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors.*
import shared.services.{AuditStub, AuthStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import stubs.BaseDownstreamStub

class DeleteAnnualSubmissionControllerHipISpec extends IntegrationBaseSpec {

  private trait Test {
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

    def nino: String = "AA123456A"

    def businessId = "XAIS12345678910"

    def taxYear: String = "2025-26"

    def downstreamUri: String = s"/itsa/income-tax/v1/25-26/$nino/self-employments/$businessId/annual-summaries"
  }

  "calling the 'Delete Self-Employment Annual Submission' endpoint" when {
    "successful" should {
      "return a 204 status code" when {
        "a valid TYS request is made for Tax Year 2025-26 onwards" in new Test {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)

            BaseDownstreamStub.onSuccess(BaseDownstreamStub.DELETE, downstreamUri, NO_CONTENT)

          }

          val response: WSResponse = await(request().delete())
          response.status shouldBe NO_CONTENT
          response.body shouldBe ""
          response.header("X-CorrelationId").nonEmpty shouldBe true
        }
      }
    }

    "return the correct error code" when {
      def validationErrorTest(requestNino: String,
                              requestBusinessId: String,
                              requestTaxYear: String,
                              expectedStatus: Int,
                              expectedBody: MtdError): Unit = {

        s"validation fails with ${expectedBody.code} error in" in new Test {

          override def nino: String = requestNino

          override def taxYear: String = requestTaxYear

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
        ("Hippo", "XAIS12345678910", "2025-26", BAD_REQUEST, NinoFormatError),
        ("AA123456A", "notABusinessId", "2025-26", BAD_REQUEST, BusinessIdFormatError),
        ("AA123456A", "XAIS12345678910", "NOT_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
        ("AA123456A", "XAIS12345678910", "2016-17", BAD_REQUEST, RuleTaxYearNotSupportedError),
        ("AA123456A", "XAIS12345678910", "2025-27", BAD_REQUEST, RuleTaxYearRangeInvalidError)
      )

      input.foreach(args => validationErrorTest.tupled(args))

      "downstream returns a service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              BaseDownstreamStub.onError(BaseDownstreamStub.DELETE, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().delete())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        def errorBody(`type`: String): String =
          s"""
             |{
             |    "origin": "HoD",
             |    "response": {
             |        "failures": [
             |            {
             |                "type": "${`type`}",
             |                "reason": "downstream message"
             |            }
             |        ]
             |    }
             |}
      """.stripMargin

        val errors = Seq(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_INCOMESOURCE_ID", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "PERIOD_NOT_FOUND", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "INCOME_SOURCE_DATA_NOT_FOUND", NOT_FOUND, NotFoundError),
          (GONE, "PERIOD_ALREADY_DELETED", NOT_FOUND, NotFoundError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (UNPROCESSABLE_ENTITY, "OUTSIDE_AMENDMENT_WINDOW", BAD_REQUEST, RuleOutsideAmendmentWindowError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )

        errors.foreach(args => serviceErrorTest.tupled(args))
      }
    }
  }

}
