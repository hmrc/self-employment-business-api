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

package v5.retrievePeriodSummary.def2

import api.models.errors.PeriodIdFormatError
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status.*
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.models.domain.TaxYear
import shared.models.errors.*
import shared.services.{AuditStub, AuthStub, MtdIdLookupStub}
import shared.support.IntegrationBaseSpec
import stubs.BaseDownstreamStub

class Def2_RetrievePeriodSummaryControllerCl290EnabledISpec extends IntegrationBaseSpec {

  override def servicesConfig: Map[String, Any] = {
    super.servicesConfig ++ Map(
      "feature-switch.cl290.enabled" -> "true"
    )
  }

  "The V5 retrieve endpoint" should {

    "return a 200 status code" when {

      "given a valid TYS request" in new Test {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          val queryParams = Map[String, String]("from" -> fromDate, "to" -> toDate)

          BaseDownstreamStub
            .when(BaseDownstreamStub.GET, tysDownstreamUri(), queryParams)
            .thenReturn(OK, downstreamResponseBody(fromDate, toDate))
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe OK
        response.json shouldBe responseBody(fromDate, toDate, includeTaxTakenOffTradingIncome = true)
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Content-Type") shouldBe Some("application/json")

      }
    }

    "return error according to spec" when {

      "TYS validation error" when {
        def validationTysErrorTest(requestNino: String,
                                   requestBusinessId: String,
                                   requestPeriodId: String,
                                   requestTaxYear: String,
                                   expectedStatus: Int,
                                   expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String       = requestNino
            override val businessId: String = requestBusinessId
            override val periodId: String   = requestPeriodId
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
          ("AA123", "XAIS12345678910", "2023-04-01_2024-01-01", "2023-24", BAD_REQUEST, NinoFormatError),
          ("AA123456A", "203100", "2023-04-01_2024-01-01", "2023-24", BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", "XAIS12345678910", "2020", "2023-24", BAD_REQUEST, PeriodIdFormatError),
          ("AA123456A", "XAIS12345678910", "2023-04-01_2024-01-01", "NOT_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "XAIS12345678910", "2023-04-01_2024-01-01", "2023-25", BAD_REQUEST, RuleTaxYearRangeInvalidError)
        )

        input.foreach(args => validationTysErrorTest.tupled(args))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              BaseDownstreamStub.onError(BaseDownstreamStub.GET, tysDownstreamUri(), downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = List(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_INCOMESOURCEID", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_DATE_FROM", BAD_REQUEST, PeriodIdFormatError),
          (BAD_REQUEST, "INVALID_DATE_TO", BAD_REQUEST, PeriodIdFormatError),
          (NOT_FOUND, "NOT_FOUND_PERIOD", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_INCOMESOURCE_ID", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_CORRELATION_ID", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "INCOME_DATA_SOURCE_NOT_FOUND", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "SUBMISSION_DATA_NOT_FOUND", NOT_FOUND, NotFoundError),
          (BAD_REQUEST, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError)
        )

        input.foreach(args => serviceErrorTest.tupled(args))
      }
    }
  }

  private trait Test {

    val nino                     = "AA123456A"
    val businessId               = "XAIS12345678910"
    val periodId                 = "2023-04-01_2024-01-01"
    val fromDate                 = "2023-04-01"
    val toDate                   = "2024-01-01"
    val mtdTaxYear               = "2023-24"
    lazy val tysTaxYear: TaxYear = TaxYear.fromMtd(mtdTaxYear)

    def tysDownstreamUri() = s"/income-tax/${tysTaxYear.asTysDownstream}/$nino/self-employments/$businessId/periodic-summary-detail"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.5.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def responseBody(fromDate: String, toDate: String, includeTaxTakenOffTradingIncome: Boolean): JsValue = Json.parse(s"""
      |{
      |  "periodDates":{
      |      "periodStartDate": "$fromDate",
      |      "periodEndDate":"$toDate"
      |   },
      |   "periodIncome":{
      |      ${if (includeTaxTakenOffTradingIncome) "\"taxTakenOffTradingIncome\": 3000.99," else ""}
      |      "turnover":3100.00,
      |      "other":3200.00
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
      |   "from": "$fromDate",
      |   "to": "$toDate",
      |   "financials": {
      |      "deductions": {
      |         "adminCosts": {
      |            "amount": 100.00,
      |            "disallowableAmount": 200.00
      |         },
      |         "advertisingCosts": {
      |            "amount": 300.00,
      |            "disallowableAmount": 400.00
      |         },
      |         "badDebt": {
      |            "amount": 500.00,
      |            "disallowableAmount": 600.00
      |         },
      |         "constructionIndustryScheme": {
      |            "amount": -700.00,
      |            "disallowableAmount": -800.00
      |         },
      |         "costOfGoods": {
      |            "amount": -900.00,
      |            "disallowableAmount": -1000.00
      |         },
      |         "depreciation": {
      |            "amount": 1100.00,
      |            "disallowableAmount": 1200.00
      |         },
      |         "financialCharges": {
      |            "amount": 1300.00,
      |            "disallowableAmount": 1400.00
      |         },
      |         "interest": {
      |            "amount": 1500.00,
      |            "disallowableAmount": 1600.00
      |         },
      |         "maintenanceCosts": {
      |            "amount": -1700.00,
      |            "disallowableAmount": -1800.00
      |         },
      |         "other": {
      |            "amount": 1900.00,
      |            "disallowableAmount": 2000.00
      |         },
      |         "professionalFees": {
      |            "amount": 2100.00,
      |            "disallowableAmount": 2200.00
      |         },
      |         "premisesRunningCosts": {
      |            "amount": -2300.00,
      |            "disallowableAmount": -2400.00
      |         },
      |         "staffCosts": {
      |            "amount": -2500.00,
      |            "disallowableAmount": -2600.00
      |         },
      |         "travelCosts": {
      |            "amount": -2700.00,
      |            "disallowableAmount": -2800.00
      |         },
      |         "businessEntertainmentCosts": {
      |            "amount": 2900.00,
      |            "disallowableAmount": 3000.00
      |         }
      |      },
      |      "incomes": {
      |         "turnover": 3100.00,
      |         "other": 3200.00,
      |         "taxTakenOffTradingIncome": 3000.99
      |      }
      |   }
      |}
      |""".stripMargin)

    def setupStubs(): StubMapping

    def uri: String = s"/$nino/$businessId/period/$mtdTaxYear/$periodId"

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "message"
         |      }
    """.stripMargin

  }

}
