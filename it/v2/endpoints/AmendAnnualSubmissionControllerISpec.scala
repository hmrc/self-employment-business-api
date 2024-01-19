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

package v2.endpoints

import api.models.errors._
import api.models.utils.JsonErrorValidators
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsNumber, JsString, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import stubs.{AuditStub, AuthStub, BaseDownstreamStub, MtdIdLookupStub}
import support.IntegrationBaseSpec
import v2.fixtures.AmendAnnualSubmissionFixture

class AmendAnnualSubmissionControllerISpec extends IntegrationBaseSpec with AmendAnnualSubmissionFixture with JsonErrorValidators {

  val requestBodyJson: JsValue           = amendAnnualSubmissionBodyMtdJson()
  val downstreamRequestBodyJson: JsValue = amendAnnualSubmissionBodyDownstreamJson()

  private trait Test {
    val nino: String       = "AA123456A"
    val businessId: String = "XAIS12345678910"
    val responseBody: JsValue = Json.parse(s"""
         |{
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
         |      "method": "PUT",
         |      "rel": "create-and-amend-self-employment-annual-submission"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
         |      "method": "GET",
         |      "rel": "self"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
         |      "method": "DELETE",
         |      "rel": "delete-self-employment-annual-submission"
         |    }
         |  ]
         |}
         |""".stripMargin)
    val downstreamResponseBody: JsValue = Json.parse("""{
        |   "transactionReference": "ignored"
        |}""".stripMargin)

    def taxYear: String

    def downstreamTaxYear: String

    def downstreamUri: String

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(s"/$nino/$businessId/annual/$taxYear")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.2.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "downstream message"
         |      }
    """.stripMargin

  }

  private trait NonTysTest extends Test {
    def taxYear: String = "2020-21"

    def downstreamUri: String = s"/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/$downstreamTaxYear"

    def downstreamTaxYear: String = "2021"
  }

  private trait TysIfsTest extends Test {
    def taxYear: String = "2023-24"

    def downstreamUri: String = s"/income-tax/$downstreamTaxYear/$nino/self-employments/$businessId/annual-summaries"

    def downstreamTaxYear: String = "23-24"
  }

  "Calling the V2 amend endpoint" should {
    "return a 200 status code" when {
      "any valid request is made" in new NonTysTest {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BaseDownstreamStub
            .when(method = BaseDownstreamStub.PUT, uri = downstreamUri)
            .withRequestBody(downstreamRequestBodyJson)
            .thenReturn(status = OK, downstreamResponseBody)
        }

        val response: WSResponse = await(request().put(requestBodyJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Content-Type") shouldBe Some("application/json")
      }

      "a valid request is made for a Tax Year Specific tax year" in new TysIfsTest {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BaseDownstreamStub
            .when(method = BaseDownstreamStub.PUT, uri = downstreamUri)
            .withRequestBody(downstreamRequestBodyJson)
            .thenReturn(status = OK, downstreamResponseBody)
        }

        val response: WSResponse = await(request().put(requestBodyJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Content-Type") shouldBe Some("application/json")
      }
    }

    "return bad request error" when {
      "badly formed json body" in new NonTysTest {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().addHttpHeaders(("Content-Type", "application/json")).put("{ badJson }"))
        response.json shouldBe Json.toJson(BadRequestError)
        response.status shouldBe BAD_REQUEST
      }
    }

    "return error according to spec" when {
      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestBusinessId: String,
                                requestTaxYear: String,
                                requestBody: JsValue,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new NonTysTest {

            override val nino: String = requestNino
            override val businessId: String = requestBusinessId
            override val taxYear: String = requestTaxYear

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request().put(requestBody))
            response.json shouldBe Json.toJson(expectedBody)
            response.status shouldBe expectedStatus
          }
        }

        val input = Seq(
          ("AA1123A", "XAIS12345678910", "2017-18", requestBodyJson, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "XAIS12345678910", "NOT_TAX_YEAR", requestBodyJson, BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "XAIS12345678910", "2021-23", requestBodyJson, BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "XAIS12345678910", "2016-17", requestBodyJson, BAD_REQUEST, RuleTaxYearNotSupportedError),
          (
            "AA123456A",
            "XAIS12345678910",
            "2021-22",
            requestBodyJson.update("/adjustments/includedNonTaxableProfits", JsNumber(1.234)),
            BAD_REQUEST,
            ValueFormatError.copy(paths = Some(Seq("/adjustments/includedNonTaxableProfits")))),
          ("AA123456A", "XA***IS1", "2022-23", requestBodyJson, BAD_REQUEST, BusinessIdFormatError),
          (
            "AA123456A",
            "XAIS12345678910",
            "2021-22",
            amendAnnualSubmissionBodyMtdJson(
              allowances = Some(
                allowancesMtdJsonWith(
                  structuredBuildingAllowances = Seq(structuredBuildingAllowanceMtdJson
                    .removeProperty("/building/name")
                    .removeProperty("/building/number")))),
              adjustments = None,
              nonFinancials = None
            ),
            BAD_REQUEST,
            RuleBuildingNameNumberError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/building")))),
          (
            "AA123456A",
            "XAIS12345678910",
            "2021-22",
            amendAnnualSubmissionBodyMtdJson(
              allowances = Some(
                allowancesMtdJsonWith(structuredBuildingAllowances = Seq(structuredBuildingAllowanceMtdJson
                  .update("/building/postcode", JsString("X" * 91))))),
              adjustments = None,
              nonFinancials = None
            ),
            BAD_REQUEST,
            StringFormatError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/building/postcode")))),
          (
            "AA123456A",
            "XAIS12345678910",
            "2021-22",
            requestBodyJson.update("/allowances/tradingIncomeAllowance", JsNumber(1.23)),
            BAD_REQUEST,
            RuleBothAllowancesSuppliedError),
          (
            "AA123456A",
            "XAIS12345678910",
            "2021-22",
            requestBodyJson.replaceWithEmptyObject("/allowances"),
            BAD_REQUEST,
            RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/allowances")))),
          (
            "AA123456A",
            "XAIS12345678910",
            "2021-22",
            amendAnnualSubmissionBodyMtdJson(
              allowances = Some(
                allowancesMtdJsonWith(
                  structuredBuildingAllowances = Seq(structuredBuildingAllowanceMtdJson.update("/firstYear/qualifyingDate", JsString("NOT-A-DATE"))))),
              adjustments = None,
              nonFinancials = None
            ),
            BAD_REQUEST,
            DateFormatError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/firstYear/qualifyingDate")))),
          (
            "AA123456A",
            "XAIS12345678910",
            "2021-22",
            amendAnnualSubmissionBodyMtdJson(
              nonFinancials = Some(nonFinancialsMtdJson.update("/class4NicsExemptionReason", JsString("not-a-valid-reason"))),
              adjustments = None,
              allowances = None
            ),
            BAD_REQUEST,
            Class4ExemptionReasonFormatError)
        )

        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedError: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new NonTysTest {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              BaseDownstreamStub.onError(BaseDownstreamStub.PUT, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().put(requestBodyJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedError)
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val errors = Seq(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_INCOME_SOURCE", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "MISSING_EXEMPTION_REASON", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "MISSING_EXEMPTION_INDICATOR", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "ALLOWANCE_NOT_SUPPORTED", BAD_REQUEST, RuleAllowanceNotSupportedError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", NOT_FOUND, NotFoundError),
          (GONE, "GONE", INTERNAL_SERVER_ERROR, InternalError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_GATEWAY, "BAD_GATEWAY", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )

        val extraTysErrors = Seq(
          (BAD_REQUEST, "INVALID_INCOME_SOURCE_ID", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "INCOME_SOURCE_NOT_FOUND", NOT_FOUND, NotFoundError),
          (BAD_REQUEST, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
