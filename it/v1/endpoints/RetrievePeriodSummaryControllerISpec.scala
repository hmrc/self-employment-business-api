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
import play.api.http.Status
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v1.models.domain.TaxYear
import v1.models.errors._
import v1.stubs.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}

class RetrievePeriodSummaryControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino       = "AA123456A"
    val businessId = "XAIS12345678910"
    val periodId   = "2019-01-01_2020-01-01"

    def responseBody(periodId: String, fromDate: String, toDate: String): JsValue = Json.parse(s"""
         |{
         |  "periodDates":{
         |      "periodStartDate": "$fromDate",
         |      "periodEndDate":"$toDate"
         |   },
         |   "periodIncome":{
         |      "turnover":3100.00,
         |      "other":3200.00
         |   },
         |   "periodAllowableExpenses":{
         |      "costOfGoodsAllowable":900.00,
         |      "paymentsToSubcontractorsAllowable":700.00,
         |      "wagesAndStaffCostsAllowable":2500.00,
         |      "carVanTravelExpensesAllowable":2700.00,
         |      "premisesRunningCostsAllowable":2300.00,
         |      "maintenanceCostsAllowable":1700.00,
         |      "adminCostsAllowable":100.00,
         |      "businessEntertainmentCostsAllowable":2900.00,
         |      "advertisingCostsAllowable":300.00,
         |      "interestOnBankOtherLoansAllowable":1500.00,
         |      "financeChargesAllowable":1300.00,
         |      "irrecoverableDebtsAllowable":500.00,
         |      "professionalFeesAllowable":2100.00,
         |      "depreciationAllowable":1100.00,
         |      "otherExpensesAllowable":1900.00
         |   },
         |   "periodDisallowableExpenses":{
         |      "costOfGoodsDisallowable":1000.00,
         |      "paymentsToSubcontractorsDisallowable":800.00,
         |      "wagesAndStaffCostsDisallowable":2600.00,
         |      "carVanTravelExpensesDisallowable":2800.00,
         |      "premisesRunningCostsDisallowable":2400.00,
         |      "maintenanceCostsDisallowable":1800.00,
         |      "adminCostsDisallowable":200.00,
         |      "businessEntertainmentCostsDisallowable":3000.00,
         |      "advertisingCostsDisallowable":400.00,
         |      "interestOnBankOtherLoansDisallowable":1600.00,
         |      "financeChargesDisallowable":1400.00,
         |      "irrecoverableDebtsDisallowable":600.00,
         |      "professionalFeesDisallowable":2200.00,
         |      "depreciationDisallowable":1200.00,
         |      "otherExpensesDisallowable":2000.00
         |   },
         |   "links": [
         |      {
         |         "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |         "rel": "amend-self-employment-period-summary",
         |         "method": "PUT"
         |      },
         |      {
         |         "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |         "rel": "self",
         |         "method": "GET"
         |      },
         |      {
         |         "href": "/individuals/business/self-employment/$nino/$businessId/period",
         |         "rel": "list-self-employment-period-summaries",
         |         "method": "GET"
         |      }
         |   ]
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
         |            "amount": 700.00,
         |            "disallowableAmount": 800.00
         |         },
         |         "costOfGoods": {
         |            "amount": 900.00,
         |            "disallowableAmount": 1000.00
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
         |            "amount": 1700.00,
         |            "disallowableAmount": 1800.00
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
         |            "amount": 2300.00,
         |            "disallowableAmount": 2400.00
         |         },
         |         "staffCosts": {
         |            "amount": 2500.00,
         |            "disallowableAmount": 2600.00
         |         },
         |         "travelCosts": {
         |            "amount": 2700.00,
         |            "disallowableAmount": 2800.00
         |         },
         |         "businessEntertainmentCosts": {
         |            "amount": 2900.00,
         |            "disallowableAmount": 3000.00
         |         }
         |      },
         |      "incomes": {
         |         "turnover": 3100.00,
         |         "other": 3200.00
         |      }
         |   }
         |}
         |""".stripMargin)

    def setupStubs(): StubMapping

    def uri: String = s"/$nino/$businessId/period/$periodId"

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "message"
         |      }
    """.stripMargin

  }

  private trait NonTysTest extends Test {
    override val periodId = "2019-01-01_2020-01-01"
    val fromDate          = "2019-01-01"
    val toDate            = "2020-01-01"

    def downstreamUri(): String = s"/income-tax/nino/$nino/self-employments/$businessId/periodic-summary-detail"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

  }

  private trait TysTest extends Test {
    override val periodId = "2023-04-01_2024-01-01"
    val fromDate          = "2023-04-01"
    val toDate            = "2024-01-01"
    val taxYear           = "2023-24"
    val tysTaxYear        = TaxYear.fromMtd(taxYear)

    def tysDownstreamUri() = s"/income-tax/${tysTaxYear.asTysDownstream}/$nino/self-employments/$businessId/periodic-summary-detail"

    def request(): WSRequest = {
      setupStubs()
      buildRequest(s"$uri?taxYear=${tysTaxYear.asMtd}")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def invalidTaxYearRequest(taxYear: String): WSRequest = {
      setupStubs()
      buildRequest(s"$uri?taxYear=$taxYear")
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )

    }

  }

  "calling the retrieve endpoint" should {

    "return a 200 status code" when {

      "any valid request is made" in new NonTysTest {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, Status.OK, downstreamResponseBody(fromDate, toDate))
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
        response.json shouldBe responseBody(periodId, fromDate, toDate)
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Content-Type") shouldBe Some("application/json")
      }

      "any valid TYS request is made" in new TysTest {
        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(
            method = DownstreamStub.GET,
            uri = tysDownstreamUri,
            queryParams = Map[String, String]("from" -> fromDate, "to" -> toDate),
            status = Status.OK,
            body = downstreamResponseBody(fromDate, toDate)
          )
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
        response.json shouldBe responseBody(periodId, fromDate, toDate)
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Content-Type") shouldBe Some("application/json")

      }
    }

    "return error according to spec" when {
      "tys validation" when {
        "validation fails with RuleTaxYearNotSupported error" in new TysTest {

          override def setupStubs(): StubMapping = {
            AuditStub.audit()
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(invalidTaxYearRequest("2021-22").get())
          response.status shouldBe Status.BAD_REQUEST
          response.json shouldBe Json.toJson(InvalidTaxYearParameterError)
        }
      }

      "validation error" when {
        def validationErrorTest(requestNino: String,
                                requestBusinessId: String,
                                requestPeriodId: String,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new NonTysTest {

            override val nino: String       = requestNino
            override val businessId: String = requestBusinessId
            override val periodId: String   = requestPeriodId

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
          ("AA123", "XAIS12345678910", "2019-01-01_2020-01-01", Status.BAD_REQUEST, NinoFormatError),
          ("AA123456A", "203100", "2019-01-01_2020-01-01", Status.BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", "XAIS12345678910", "2020", Status.BAD_REQUEST, PeriodIdFormatError)
        )

        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "TYS validation error" when {
        def validationTysErrorTest(requestNino: String,
                                   requestBusinessId: String,
                                   requestPeriodId: String,
                                   requestTaxYear: TaxYear,
                                   expectedStatus: Int,
                                   expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new TysTest {

            override val nino: String       = requestNino
            override val businessId: String = requestBusinessId
            override val periodId: String   = requestPeriodId
            override val tysTaxYear         = requestTaxYear

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
          ("AA123", "XAIS12345678910", "2023-04-01_2024-01-01", TaxYear.fromMtd("2023-24"), Status.BAD_REQUEST, NinoFormatError),
          ("AA123456A", "203100", "2023-04-01_2024-01-01", TaxYear.fromMtd("2023-24"), Status.BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", "XAIS12345678910", "2020", TaxYear.fromMtd("2023-24"), Status.BAD_REQUEST, PeriodIdFormatError),
          ("AA123456A", "XAIS12345678910", "2023-04-01_2024-01-01", TaxYear.fromMtd("2023-2"), Status.BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "XAIS12345678910", "2023-04-01_2024-01-01", TaxYear.fromMtd("2021-22"), Status.BAD_REQUEST, InvalidTaxYearParameterError)
        )

        input.foreach(args => (validationTysErrorTest _).tupled(args))
      }

      "downstream service error" when {
        def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamCode error and status $downstreamStatus" in new TysTest {

            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.GET, tysDownstreamUri(), downstreamStatus, errorBody(downstreamCode))
            }

            val response: WSResponse = await(request().get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (Status.BAD_REQUEST, "INVALID_NINO", Status.BAD_REQUEST, NinoFormatError),
          (Status.BAD_REQUEST, "INVALID_INCOMESOURCEID", Status.BAD_REQUEST, BusinessIdFormatError),
          (Status.BAD_REQUEST, "INVALID_DATE_FROM", Status.BAD_REQUEST, PeriodIdFormatError),
          (Status.BAD_REQUEST, "INVALID_DATE_TO", Status.BAD_REQUEST, PeriodIdFormatError),
          (Status.NOT_FOUND, "NOT_FOUND_PERIOD", Status.NOT_FOUND, NotFoundError),
          (Status.NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", Status.NOT_FOUND, NotFoundError),
          (Status.INTERNAL_SERVER_ERROR, "SERVER_ERROR", Status.INTERNAL_SERVER_ERROR, InternalError),
          (Status.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", Status.INTERNAL_SERVER_ERROR, InternalError),
          (Status.BAD_REQUEST, "INVALID_TAX_YEAR", Status.BAD_REQUEST, TaxYearFormatError),
          (Status.BAD_REQUEST, "INVALID_INCOMESOURCE_ID", Status.BAD_REQUEST, BusinessIdFormatError),
          (Status.BAD_REQUEST, "INVALID_CORRELATION_ID", Status.INTERNAL_SERVER_ERROR, InternalError),
          (Status.NOT_FOUND, "INCOME_DATA_SOURCE_NOT_FOUND", Status.NOT_FOUND, NotFoundError),
          (Status.NOT_FOUND, "SUBMISSION_DATA_NOT_FOUND", Status.NOT_FOUND, NotFoundError),
          (Status.BAD_REQUEST, "TAX_YEAR_NOT_SUPPORTED", Status.BAD_REQUEST, RuleTaxYearNotSupportedError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

}
