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

package v5.createAmendAnnualSubmission.def4

import api.models.errors.*
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.libs.json.{JsNumber, JsString, JsValue, Json}
import play.api.libs.ws.DefaultBodyReadables.readableAsString
import play.api.libs.ws.DefaultBodyWritables.writeableOf_String
import play.api.libs.ws.JsonBodyWritables.writeableOf_JsValue
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.*
import shared.models.errors.*
import shared.models.utils.JsonErrorValidators
import shared.services.{AuditStub, AuthStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import stubs.BaseDownstreamStub
import v5.createAmendAnnualSubmission.def4.request.Def4_CreateAmendAnnualSubmissionFixture

class Def4_CreateAmendAnnualSubmissionControllerHipISpec
    extends IntegrationBaseSpec
    with Def4_CreateAmendAnnualSubmissionFixture
    with JsonErrorValidators {

  val mtdRequestBodyJson: JsValue        = createAmendAnnualSubmissionRequestBodyMtdJson()
  val downstreamRequestBodyJson: JsValue = createAmendAnnualSubmissionRequestBodyDownstreamJson()

  "Calling the Create and Amend Self-Employment Annual Submission endpoint" should {
    "return a 204 status code" when {
      "any valid request is made" in new Test {
        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BaseDownstreamStub
            .when(method = BaseDownstreamStub.PUT, uri = downstreamUri)
            .withRequestBody(downstreamRequestBodyJson)
            .thenReturn(status = OK, downstreamResponseBody)
        }

        val response: WSResponse = await(request().put(mtdRequestBodyJson))
        response.status shouldBe NO_CONTENT
        response.body shouldBe ""
        response.header("Content-Type") shouldBe None
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }

    "return bad request error" when {
      "given a badly formed json body" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().addHttpHeaders(("Content-Type", "application/json")).put("{ badJson }"))
        response.json shouldBe Json.toJson(BadRequestError)
        response.status shouldBe BAD_REQUEST
        response.header("Content-Type") shouldBe Some("application/json")
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
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val input = List(
          ("AA1123A", "XAIS12345678910", "2026-27", mtdRequestBodyJson, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "XA***IS1", "2026-27", mtdRequestBodyJson, BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", "XAIS12345678910", "NOT_TAX_YEAR", mtdRequestBodyJson, BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "XAIS12345678910", "2026-28", mtdRequestBodyJson, BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "XAIS12345678910", "2016-17", mtdRequestBodyJson, BAD_REQUEST, RuleTaxYearNotSupportedError),
          (
            "AA123456A",
            "XAIS12345678910",
            "2026-27",
            mtdRequestBodyJson.replaceWithEmptyObject("/allowances"),
            BAD_REQUEST,
            RuleIncorrectOrEmptyBodyError.withPath("/allowances")
          ),
          (
            "AA123456A",
            "XAIS12345678910",
            "2026-27",
            mtdRequestBodyJson.update("/adjustments/includedNonTaxableProfits", JsNumber(1.234)),
            BAD_REQUEST,
            ValueFormatError.withPath("/adjustments/includedNonTaxableProfits")
          ),
          (
            "AA123456A",
            "XAIS12345678910",
            "2026-27",
            createAmendAnnualSubmissionRequestBodyMtdJson(
              allowances = Some(
                allowancesMtdJsonWith(
                  structuredBuildingAllowances = List(
                    structuredBuildingAllowanceMtdJson.removeProperty("/building/name").removeProperty("/building/number")
                  )
                )
              )
            ),
            BAD_REQUEST,
            RuleBuildingNameNumberError.withPath("/allowances/structuredBuildingAllowance/0/building")
          ),
          (
            "AA123456A",
            "XAIS12345678910",
            "2026-27",
            createAmendAnnualSubmissionRequestBodyMtdJson(
              allowances = Some(
                allowancesMtdJsonWith(
                  structuredBuildingAllowances = List(
                    structuredBuildingAllowanceMtdJson.update("/building/postcode", JsString("X" * 91))
                  )
                )
              )
            ),
            BAD_REQUEST,
            StringFormatError.withPath("/allowances/structuredBuildingAllowance/0/building/postcode")
          ),
          (
            "AA123456A",
            "XAIS12345678910",
            "2026-27",
            mtdRequestBodyJson.update("/allowances/tradingIncomeAllowance", JsNumber(1.23)),
            BAD_REQUEST,
            RuleBothAllowancesSuppliedError
          ),
          (
            "AA123456A",
            "XAIS12345678910",
            "2026-27",
            createAmendAnnualSubmissionRequestBodyMtdJson(
              allowances = Some(
                allowancesMtdJsonWith(
                  structuredBuildingAllowances = List(
                    structuredBuildingAllowanceMtdJson.update("/firstYear/qualifyingDate", JsString("NOT-A-DATE"))
                  )
                )
              )
            ),
            BAD_REQUEST,
            DateFormatError.withPath("/allowances/structuredBuildingAllowance/0/firstYear/qualifyingDate")
          ),
          (
            "AA123456A",
            "XAIS12345678910",
            "2026-27",
            createAmendAnnualSubmissionRequestBodyMtdJson(
              nonFinancials = Some(nonFinancialsMtdJson.update("/class4NicsExemptionReason", JsString("not-a-valid-reason")))
            ),
            BAD_REQUEST,
            Class4ExemptionReasonFormatError
          ),
          (
            "AA123456A",
            "XAIS12345678910",
            "2026-27",
            mtdRequestBodyJson.update("/adjustments/overlapReliefUsed", JsNumber(200.12)),
            BAD_REQUEST,
            RuleOverlapReliefUsedNotAllowedError.withPath("/adjustments/overlapReliefUsed")
          )
        )

        input.foreach(validationErrorTest.tupled)
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamType: String, expectedStatus: Int, expectedError: MtdError): Unit = {
          s"downstream returns a type $downstreamType error and status $downstreamStatus" in new Test {
            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              BaseDownstreamStub.onError(BaseDownstreamStub.PUT, downstreamUri, downstreamStatus, errorBody(downstreamType))
            }

            val response: WSResponse = await(request().put(mtdRequestBodyJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedError)
            response.header("X-CorrelationId").nonEmpty shouldBe true
            response.header("Content-Type") shouldBe Some("application/json")
          }
        }

        val errors = List(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_INCOMESOURCE_ID", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "INCOME_SOURCE_NOT_FOUND", NOT_FOUND, NotFoundError),
          (UNPROCESSABLE_ENTITY, "MISSING_EXEMPTION_REASON", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "MISSING_EXEMPTION_INDICATOR", INTERNAL_SERVER_ERROR, InternalError),
          (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (UNPROCESSABLE_ENTITY, "WRONG_TPA_AMOUNT_SUBMITTED", BAD_REQUEST, RuleWrongTpaAmountSubmittedError),
          (UNPROCESSABLE_ENTITY, "OUTSIDE_AMENDMENT_WINDOW", BAD_REQUEST, RuleOutsideAmendmentWindowError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )

        errors.foreach(serviceErrorTest.tupled)
      }
    }
  }

  private trait Test {
    val nino: String       = "AA123456A"
    val businessId: String = "XAIS12345678910"

    val downstreamResponseBody: JsValue = Json.parse(
      """
        |{
        |  "transactionReference": "ignored"
        |}
      """.stripMargin
    )

    def taxYear: String = "2026-27"

    def downstreamUri: String = s"/itsa/income-tax/v1/26-27/$nino/self-employments/$businessId/annual-summaries"

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(s"/$nino/$businessId/annual/$taxYear")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.5.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
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

  }

}
