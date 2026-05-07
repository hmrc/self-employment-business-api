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

package v5.createAmendAnnualSubmission.def3

import api.models.errors.*
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.*
import play.api.libs.json.{JsNumber, JsString, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors.*
import shared.models.utils.JsonErrorValidators
import shared.services.{AuditStub, AuthStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import stubs.BaseDownstreamStub
import v5.createAmendAnnualSubmission.def3.request.Def3_CreateAmendAnnualSubmissionFixture
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue

class Def3_CreateAmendAnnualSubmissionControllerHipISpec
    extends IntegrationBaseSpec
    with Def3_CreateAmendAnnualSubmissionFixture
    with JsonErrorValidators {

  val requestBodyJson: JsValue           = createAmendAnnualSubmissionRequestBodyMtdJson()
  val downstreamRequestBodyJson: JsValue = createAmendAnnualSubmissionRequestBodyDownstreamJson()

  "Calling the Create and Amend Self-Employment Annual Submission endpoint" should {
    "return a 204 status code" when {
      "given any valid Def3 request" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BaseDownstreamStub
            .when(method = BaseDownstreamStub.PUT, uri = downstreamUri)
            .withRequestBody(downstreamRequestBodyJson)
            .thenReturn(status = OK, downstreamResponseBody)
        }

        val response: WSResponse = await(request().put(requestBodyJson))
        response.status shouldBe NO_CONTENT
        response.header("X-CorrelationId").nonEmpty shouldBe true
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
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request().put(requestBody))
            response.json shouldBe Json.toJson(expectedBody)
            response.status shouldBe expectedStatus
          }
        }

        val input = List(
          ("AA1123A", "XAIS12345678910", "2025-26", requestBodyJson, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "XAIS12345678910", "NOT_TAX_YEAR", requestBodyJson, BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "XAIS12345678910", "2021-23", requestBodyJson, BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "XAIS12345678910", "2016-17", requestBodyJson, BAD_REQUEST, RuleTaxYearNotSupportedError),
          (
            "AA123456A",
            "XAIS12345678910",
            "2025-26",
            requestBodyJson.update("/adjustments/includedNonTaxableProfits", JsNumber(1.234)),
            BAD_REQUEST,
            ValueFormatError.copy(paths = Some(List("/adjustments/includedNonTaxableProfits")))),
          ("AA123456A", "XA***IS1", "2022-23", requestBodyJson, BAD_REQUEST, BusinessIdFormatError),
          (
            "AA123456A",
            "XAIS12345678910",
            "2025-26",
            createAmendAnnualSubmissionRequestBodyMtdJson(
              allowances = Some(
                allowancesMtdJsonWith(
                  structuredBuildingAllowances = List(structuredBuildingAllowanceMtdJson
                    .removeProperty("/building/name")
                    .removeProperty("/building/number")))),
              adjustments = None,
              nonFinancials = None
            ),
            BAD_REQUEST,
            RuleBuildingNameNumberError.copy(paths = Some(List("/allowances/structuredBuildingAllowance/0/building")))),
          (
            "AA123456A",
            "XAIS12345678910",
            "2025-26",
            createAmendAnnualSubmissionRequestBodyMtdJson(
              allowances = Some(
                allowancesMtdJsonWith(structuredBuildingAllowances = List(structuredBuildingAllowanceMtdJson
                  .update("/building/postcode", JsString("X" * 91))))),
              adjustments = None,
              nonFinancials = None
            ),
            BAD_REQUEST,
            StringFormatError.copy(paths = Some(List("/allowances/structuredBuildingAllowance/0/building/postcode")))),
          (
            "AA123456A",
            "XAIS12345678910",
            "2025-26",
            requestBodyJson.update("/allowances/tradingIncomeAllowance", JsNumber(1.23)),
            BAD_REQUEST,
            RuleBothAllowancesSuppliedError),
          (
            "AA123456A",
            "XAIS12345678910",
            "2025-26",
            requestBodyJson.replaceWithEmptyObject("/allowances"),
            BAD_REQUEST,
            RuleIncorrectOrEmptyBodyError.copy(paths = Some(List("/allowances")))),
          (
            "AA123456A",
            "XAIS12345678910",
            "2025-26",
            createAmendAnnualSubmissionRequestBodyMtdJson(
              allowances = Some(allowancesMtdJsonWith(
                structuredBuildingAllowances = List(structuredBuildingAllowanceMtdJson.update("/firstYear/qualifyingDate", JsString("NOT-A-DATE"))))),
              adjustments = None,
              nonFinancials = None
            ),
            BAD_REQUEST,
            DateFormatError.copy(paths = Some(List("/allowances/structuredBuildingAllowance/0/firstYear/qualifyingDate")))),
          (
            "AA123456A",
            "XAIS12345678910",
            "2025-26",
            createAmendAnnualSubmissionRequestBodyMtdJson(
              nonFinancials = Some(nonFinancialsMtdJson.update("/class4NicsExemptionReason", JsString("not-a-valid-reason"))),
              adjustments = None,
              allowances = None
            ),
            BAD_REQUEST,
            Class4ExemptionReasonFormatError)
        )

        input.foreach(args => validationErrorTest.tupled(args))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedError: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {

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

        val errors = List(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_INCOMESOURCE_ID", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "MISSING_EXEMPTION_REASON", INTERNAL_SERVER_ERROR, InternalError),
          (FORBIDDEN, "MISSING_EXEMPTION_INDICATOR", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (UNPROCESSABLE_ENTITY, "WRONG_TPA_AMOUNT_SUBMITTED", BAD_REQUEST, RuleWrongTpaAmountSubmittedError),
          (UNPROCESSABLE_ENTITY, "OUTSIDE_AMENDMENT_WINDOW", BAD_REQUEST, RuleOutsideAmendmentWindowError),
          (FORBIDDEN, "ALLOWANCE_NOT_SUPPORTED", BAD_REQUEST, RuleAllowanceNotSupportedError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "INCOME_SOURCE_NOT_FOUND", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )

        errors.foreach(args => serviceErrorTest.tupled(args))
      }
    }
  }

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

    val downstreamResponseBody: JsValue = Json.parse("""{
        |   "transactionReference": "ignored"
        |}""".stripMargin)

    private def uri: String = s"/$nino/$businessId/annual/$taxYear"

    def nino: String = "AA123456A"

    def businessId = "XAIS12345678910"

    def taxYear: String = "2025-26"

    def downstreamUri: String = s"/itsa/income-tax/v1/25-26/$nino/self-employments/$businessId/annual-summaries"

    def errorBody(code: String): String =
      s"""
         |   {
         |      "origin": "HoD",
         |      "response": {
         |        "failures": [
         |          {
         |            "type": "$code",
         |            "reason": "downstream message"
         |          }
         |         ]
         |       }
         |     }
          """.stripMargin

  }

}
