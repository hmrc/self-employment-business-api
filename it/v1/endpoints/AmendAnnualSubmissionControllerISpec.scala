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
import play.api.libs.json.{JsNumber, JsString, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v1.models.errors._
import v1.models.request.amendSEAnnual.AmendAnnualSubmissionFixture
import v1.models.utils.JsonErrorValidators
import v1.stubs.{AuthStub, DownstreamStub, MtdIdLookupStub}

class AmendAnnualSubmissionControllerISpec extends IntegrationBaseSpec with AmendAnnualSubmissionFixture with JsonErrorValidators {

  val requestBodyJson: JsValue           = amendAnnualSubmissionBodyMtdJson()
  val downstreamRequestBodyJson: JsValue = amendAnnualSubmissionBodyDownstreamJson()

  private trait Test {
    val nino: String              = "AA123456A"
    val businessId: String        = "XAIS12345678910"
    val taxYear: String           = "2017-18"
    val downstreamTaxYear: String = "2018"

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

    def downstreamUri: String = s"/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/$downstreamTaxYear"

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(s"/$nino/$businessId/annual/$taxYear")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
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

    val downstreamResponseBody: JsValue = Json.parse("""{
        |   "transactionReference": "ignored"
        |}""".stripMargin)

  }

  "Calling the amend endpoint" should {

    "return a 200 status code" when {

      "any valid request is made" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub
            .when(method = DownstreamStub.PUT, uri = downstreamUri)
            .withRequestBody(downstreamRequestBodyJson)
            .thenReturn(status = OK, downstreamResponseBody)
        }

        val response: WSResponse = await(request().put(requestBodyJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }

    "return bad request error" when {
      "badly formed json body" in new Test {
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
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String       = requestNino
            override val businessId: String = requestBusinessId
            override val taxYear: String    = requestTaxYear

            override def setupStubs(): StubMapping = {
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
          ("AA123456A", "XAIS12345678910", "20223", requestBodyJson, BAD_REQUEST, TaxYearFormatError),
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
              allowances = Some(allowancesMtdJsonWith(
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
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.PUT, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().put(requestBodyJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_INCOME_SOURCE", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, DownstreamError),
          (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "MISSING_EXEMPTION_REASON", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "MISSING_EXEMPTION_INDICATOR", INTERNAL_SERVER_ERROR, DownstreamError),
          (FORBIDDEN, "ALLOWANCE_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", NOT_FOUND, NotFoundError),
          (GONE, "GONE", INTERNAL_SERVER_ERROR, DownstreamError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (BAD_GATEWAY, "BAD_GATEWAY", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

}
