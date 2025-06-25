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

package v5.retrieveCumulativePeriodSummary.def1

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.errors._
import shared.services.{AuditStub, AuthStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import stubs.BaseDownstreamStub

class Def1_RetrieveCumulativePeriodSummaryISpec extends IntegrationBaseSpec {

  "The V5 retrieve cumulative endpoint" should {

    "return a 200 status code" when {

      "given a valid request" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          BaseDownstreamStub
            .when(BaseDownstreamStub.GET, downstreamUri)
            .thenReturn(OK, downstreamResponseBody(fromDate, toDate))
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe OK
        response.json shouldBe responseBody(fromDate, toDate)
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
            override val mtdTaxYear: String = requestTaxYear

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

        val input = List(
          ("AA123", "XAIS12345678910", "2025-26", BAD_REQUEST, NinoFormatError),
          ("AA123456A", "203100", "2025-26", BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", "XAIS12345678910", "NOT_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "XAIS12345678910", "2025-27", BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "XAIS12345678910", "2021-22", BAD_REQUEST, RuleTaxYearNotSupportedError)
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
              BaseDownstreamStub.onError(BaseDownstreamStub.GET, downstreamUri, downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = List(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_INCOME_SOURCE_ID", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "NOT_FOUND", NOT_FOUND, NotFoundError),
          (BAD_REQUEST, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

  private trait Test {

    val nino              = "AA123456A"
    val businessId        = "XAIS12345678910"
    val mtdTaxYear        = "2025-26"
    val downstreamTaxYear = "25-26"

    val fromDate = "2025-06-07"
    val toDate   = "2025-08-10"

    def downstreamUri: String = s"/income-tax/$downstreamTaxYear/self-employments/periodic-summary-detail/$nino/$businessId"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.5.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def responseBody(fromDate: String, toDate: String): JsValue = Json.parse(s"""
      |{
      |  "periodDates":{
      |      "periodStartDate": "$fromDate",
      |      "periodEndDate":"$toDate"
      |   },
      |   "periodIncome":{
      |      "turnover":3100.00,
      |      "other":3200.00,
      |      "taxTakenOffTradingIncome":3000.99
      |   },
      |   "periodExpenses":{
      |      "costOfGoods":-900.00,
      |      "paymentsToSubcontractors":-700.00,
      |      "wagesAndStaffCosts":-2500.00,
      |      "carVanTravelExpenses":-2700.00,
      |      "premisesRunningCosts":-2300.00,
      |      "maintenanceCosts":-1700.00,
      |      "adminCosts":100.00,
      |      "businessEntertainmentCosts":2900.00,
      |      "advertisingCosts":300.00,
      |      "interestOnBankOtherLoans":1500.00,
      |      "financeCharges":1300.00,
      |      "irrecoverableDebts":500.00,
      |      "professionalFees":2100.00,
      |      "depreciation":1100.00,
      |      "otherExpenses":1900.00
      |   },
      |   "periodDisallowableExpenses":{
      |      "costOfGoodsDisallowable":-1000.00,
      |      "paymentsToSubcontractorsDisallowable":-800.00,
      |      "wagesAndStaffCostsDisallowable":-2600.00,
      |      "carVanTravelExpensesDisallowable":-2800.00,
      |      "premisesRunningCostsDisallowable":-2400.00,
      |      "maintenanceCostsDisallowable":-1800.00,
      |      "adminCostsDisallowable":200.00,
      |      "businessEntertainmentCostsDisallowable":3000.00,
      |      "advertisingCostsDisallowable":400.00,
      |      "interestOnBankOtherLoansDisallowable":1600.00,
      |      "financeChargesDisallowable":1400.00,
      |      "irrecoverableDebtsDisallowable":600.00,
      |      "professionalFeesDisallowable":2200.00,
      |      "depreciationDisallowable":1200.00,
      |      "otherExpensesDisallowable":2000.00
      |   }
      |}
      |""".stripMargin)

    def downstreamResponseBody(fromDate: String, toDate: String): JsValue = Json.parse(s"""
      |{
      |  "selfEmploymentPeriodDates": {
      |    "periodStartDate": "$fromDate",
      |    "periodEndDate": "$toDate"
      |  },
      |  "selfEmploymentPeriodIncome": {
      |    "turnover": 3100.00,
      |    "other": 3200.00,
      |    "taxTakenOffTradingIncome": 3000.99
      |  },
      |  "selfEmploymentPeriodDeductions": {
      |    "costOfGoods": {
      |      "amount": -900.00,
      |      "disallowableAmount": -1000.00
      |    },
      |    "constructionIndustryScheme": {
      |      "amount": -700.00,
      |      "disallowableAmount": -800.00
      |    },
      |    "staffCosts": {
      |      "amount": -2500.00,
      |      "disallowableAmount": -2600.00
      |    },
      |    "travelCosts": {
      |      "amount": -2700.00,
      |      "disallowableAmount": -2800.00
      |    },
      |    "premisesRunningCosts": {
      |      "amount": -2300.00,
      |      "disallowableAmount": -2400.00
      |    },
      |    "maintenanceCosts": {
      |      "amount": -1700.00,
      |      "disallowableAmount": -1800.00
      |    },
      |    "adminCosts": {
      |      "amount": 100.00,
      |      "disallowableAmount": 200.00
      |    },
      |    "businessEntertainmentCosts": {
      |      "amount": 2900.00,
      |      "disallowableAmount": 3000.00
      |    },
      |    "advertisingCosts": {
      |      "amount": 300.00,
      |      "disallowableAmount": 400.00
      |    },
      |    "interest": {
      |      "amount": 1500.00,
      |      "disallowableAmount": 1600.00
      |    },
      |    "financialCharges": {
      |      "amount": 1300.00,
      |      "disallowableAmount": 1400.00
      |    },
      |    "badDebt": {
      |      "amount": 500.00,
      |      "disallowableAmount": 600.00
      |    },
      |    "professionalFees": {
      |      "amount": 2100.00,
      |      "disallowableAmount": 2200.00
      |    },
      |    "depreciation": {
      |      "amount": 1100.00,
      |      "disallowableAmount": 1200.00
      |    },
      |    "other": {
      |      "amount": 1900.00,
      |      "disallowableAmount": 2000.00
      |    }
      |  }
      |}
      |""".stripMargin)

    def setupStubs(): StubMapping

    def uri: String = s"/$nino/$businessId/cumulative/$mtdTaxYear"

    def errorBody(code: String): String =
      s"""
         | {
         |   "code": "$code",
         |   "reason": "message"
         | }
    """.stripMargin

  }

}
