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

package v5.createPeriodSummary.def1

import api.models.errors._
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

class Def1_CreatePeriodSummaryControllerISpec extends IntegrationBaseSpec {

  "The V5 create endpoint" should {

    "return a 200 status code" when {
      "given a valid non-TYS request" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BaseDownstreamStub
            .onSuccess(BaseDownstreamStub.POST, downstreamUri, OK, downstreamResponse)
        }
        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }

    "return validation error according to spec" when {
      "given an invalid NINO" in new Test {
        override val nino: String = "INVALID_NINO"

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(NinoFormatError)
      }

      "given an invalid business id" in new Test {
        override val businessId: String = "INVALID_BUSINESSID"

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(BusinessIdFormatError)
      }

      "given an invalid Start date" in new Test {
        override val requestBodyJson: JsValue = Json.parse(
          s"""
             |{
             |     "periodDates": {
             |           "periodStartDate": "202019-08-24",
             |           "periodEndDate": "2019-08-24"
             |     },
             |     "periodIncome": {
             |          "turnover": 1000.99,
             |          "other": 2000.99
             |     }
             |}
             |""".stripMargin
        )

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(StartDateFormatError)
      }

      "given an invalid End date" in new Test {
        override val requestBodyJson: JsValue = Json.parse(
          s"""
             |{
             |     "periodDates": {
             |           "periodStartDate": "2019-08-24",
             |           "periodEndDate": "20119-08-24"
             |     },
             |     "periodIncome": {
             |          "turnover": 1000.99,
             |          "other": 1000.99
             |     }
             |}
             |""".stripMargin
        )

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(EndDateFormatError.withPath("periodDates/periodEndDate"))
      }

      "given an end date which is before the provided start date" in new Test {
        override val requestBodyJson: JsValue = Json.parse(
          s"""
             |{
             |     "periodDates": {
             |           "periodStartDate": "2019-08-24",
             |           "periodEndDate": "2019-06-24"
             |     },
             |     "periodIncome": {
             |          "turnover": 1000.99,
             |          "other": 1000.99
             |     }
             |}
             |""".stripMargin
        )

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(RuleEndBeforeStartDateError)
      }

      "given a single invalid amount" in new Test {
        override val requestBodyJson: JsValue = Json.parse(
          s"""
             |{
             |     "periodDates": {
             |           "periodStartDate": "2019-08-24",
             |           "periodEndDate": "2019-09-24"
             |     },
             |     "periodIncome": {
             |          "turnover": 1000.99,
             |          "other": 1000.99
             |     },
             |     "periodExpenses": {
             |          "costOfGoods": 1000.99,
             |          "paymentsToSubcontractors": 1000.99,
             |          "wagesAndStaffCosts": 1000.99,
             |          "carVanTravelExpenses": 1000.99,
             |          "premisesRunningCosts": -99999.99,
             |          "maintenanceCosts": -1000.99,
             |          "adminCosts": 1000.99,
             |          "businessEntertainmentCosts": 1000.99,
             |          "advertisingCosts": 1000.99,
             |          "interestOnBankOtherLoans": -1000.99,
             |          "financeCharges": -1000.99,
             |          "irrecoverableDebts": -1000.99,
             |          "professionalFees": -99999999999.99,
             |          "depreciation": -1000.99,
             |          "otherExpenses": 1000.99
             |      },
             |     "periodDisallowableExpenses": {
             |          "costOfGoodsDisallowable": 1000.99,
             |          "paymentsToSubcontractorsDisallowable": 1000.99,
             |          "wagesAndStaffCostsDisallowable": 1000.99,
             |          "carVanTravelExpensesDisallowable": 1000.99,
             |          "premisesRunningCostsDisallowable": -1000.99,
             |          "maintenanceCostsDisallowable": -999.99,
             |          "adminCostsDisallowable": 1000.99,
             |          "businessEntertainmentCostsDisallowable": 1000.99,
             |          "advertisingCostsDisallowable": 1000.99,
             |          "interestOnBankOtherLoansDisallowable": -1000.99,
             |          "financeChargesDisallowable": -9999.99,
             |          "irrecoverableDebtsDisallowable": -1000.99,
             |          "professionalFeesDisallowable": -1000000000000.99,
             |          "depreciationDisallowable": -99999999999.99,
             |          "otherExpensesDisallowable": 1000.99
             |      }
             |}
             |""".stripMargin
        )

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(
          ValueFormatError.forPathAndRange("/periodDisallowableExpenses/professionalFeesDisallowable", "-99999999999.99", "99999999999.99"))
      }

      "multiple invalid amounts are provided" in new Test {
        override val requestBodyJson: JsValue = Json.parse(
          s"""
             |{
             |     "periodDates": {
             |           "periodStartDate": "2019-08-24",
             |           "periodEndDate": "2019-09-24"
             |     },
             |     "periodIncome": {
             |          "turnover": 1000.99,
             |          "other": 1000.99
             |     },
             |     "periodExpenses": {
             |          "costOfGoods": 1000.99,
             |          "paymentsToSubcontractors": 1000.99,
             |          "wagesAndStaffCosts": 1000.99,
             |          "carVanTravelExpenses": 1000.99,
             |          "premisesRunningCosts": -999999999999999.99,
             |          "maintenanceCosts": -1000.99,
             |          "adminCosts": 1000.99,
             |          "businessEntertainmentCosts": 1000.99,
             |          "advertisingCosts": 1000.99,
             |          "interestOnBankOtherLoans": -1000.99,
             |          "financeCharges": -100000000000000.99,
             |          "irrecoverableDebts": -1000.99,
             |          "professionalFees": -99999999999.99,
             |          "depreciation": -1000.99,
             |          "otherExpenses": 1000.99
             |      },
             |     "periodDisallowableExpenses": {
             |          "costOfGoodsDisallowable": 1000.99,
             |          "paymentsToSubcontractorsDisallowable": 1000.99,
             |          "wagesAndStaffCostsDisallowable": 1000.99,
             |          "carVanTravelExpensesDisallowable": 1000.99,
             |          "premisesRunningCostsDisallowable": -1000.99,
             |          "maintenanceCostsDisallowable": -999.99,
             |          "adminCostsDisallowable": 1000000000000.99,
             |          "businessEntertainmentCostsDisallowable": 1000.99,
             |          "advertisingCostsDisallowable": 1000.99,
             |          "interestOnBankOtherLoansDisallowable": -1000.99,
             |          "financeChargesDisallowable": -9999.99,
             |          "irrecoverableDebtsDisallowable": -1000.99,
             |          "professionalFeesDisallowable": -1000000000000.99,
             |          "depreciationDisallowable": -99999999999.99,
             |          "otherExpensesDisallowable": 1000.99
             |      }
             |}
             |""".stripMargin
        )

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(
          ValueFormatError
            .forPathAndRange("", "-99999999999.99", "99999999999.99")
            .withPaths(List(
              "/periodExpenses/premisesRunningCosts",
              "/periodExpenses/financeCharges",
              "/periodDisallowableExpenses/adminCostsDisallowable",
              "/periodDisallowableExpenses/professionalFeesDisallowable"
            )))
      }

      "given an empty body" in new Test {
        override val requestBodyJson: JsValue = Json.parse("""{}""")

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(RuleIncorrectOrEmptyBodyError)
      }

      s"given a body missing mandatory fields" in new Test {
        override val requestBodyJson: JsValue = Json.parse("""
                                                             | {
                                                             |     "periodDates": {
                                                             |           "periodEndDate": "2019-08-24"
                                                             |     }
                                                             |}
                                                             |""".stripMargin)

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(RuleIncorrectOrEmptyBodyError.copy(paths = Some(List("/periodDates/periodStartDate"))))
      }
    }

    "downstream service error" when {
      def serviceErrorTest(downstreamStatus: Int, downstreamCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"downstream returns an $downstreamCode error and status $downstreamStatus" in new Test {

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            BaseDownstreamStub.onError(BaseDownstreamStub.POST, downstreamUri, downstreamStatus, errorBody(downstreamCode))
          }

          val response: WSResponse = await(request().post(requestBodyJson))
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
        }
      }

      val errors = List(
        (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
        (BAD_REQUEST, "INVALID_INCOME_SOURCE", BAD_REQUEST, BusinessIdFormatError),
        (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
        (CONFLICT, "INVALID_PERIOD", BAD_REQUEST, RuleEndBeforeStartDateError),
        (CONFLICT, "OVERLAPS_IN_PERIOD", BAD_REQUEST, RuleOverlappingPeriod),
        (CONFLICT, "NOT_ALIGN_PERIOD", BAD_REQUEST, RuleMisalignedPeriod),
        (CONFLICT, "NOT_CONTIGUOUS_PERIOD", BAD_REQUEST, RuleNotContiguousPeriod),
        (CONFLICT, "NOT_ALLOWED_SIMPLIFIED_EXPENSES", BAD_REQUEST, RuleNotAllowedConsolidatedExpenses),
        (CONFLICT, "BOTH_EXPENSES_SUPPLIED", BAD_REQUEST, RuleBothExpensesSuppliedError),
        (NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", NOT_FOUND, NotFoundError),
        (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError),
        (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError)
      )

      errors.foreach(args => (serviceErrorTest _).tupled(args))
    }
  }

  private trait Test {

    val nino: String       = "AA123456A"
    val businessId: String = "XAIS12345678910"
    val periodId: String   = s"${periodStartDate}_$periodEndDate"

    def downstreamUri: String   = s"/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries"
    def periodStartDate: String = "2019-07-24"
    def periodEndDate: String   = "2019-08-24"

    def uri: String = s"/$nino/$businessId/period"

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.5.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    val requestBodyJson: JsValue = Json.parse(
      s"""
         |{
         |     "periodDates": {
         |           "periodStartDate": "$periodStartDate",
         |           "periodEndDate": "$periodEndDate"
         |     },
         |     "periodIncome": {
         |          "turnover": 1000.99,
         |          "other": 2000.99
         |     },
         |     "periodExpenses": {
         |          "costOfGoods": 1000.99,
         |          "paymentsToSubcontractors": 1000.99,
         |          "wagesAndStaffCosts": 1000.99,
         |          "carVanTravelExpenses": 1000.99,
         |          "premisesRunningCosts": -99999.99,
         |          "maintenanceCosts": -1000.99,
         |          "adminCosts": 1000.99,
         |          "businessEntertainmentCosts": 1000.99,
         |          "advertisingCosts": 1000.99,
         |          "interestOnBankOtherLoans": -1000.99,
         |          "financeCharges": -1000.99,
         |          "irrecoverableDebts": -1000.99,
         |          "professionalFees": -99999999999.99,
         |          "depreciation": -1000.99,
         |          "otherExpenses": 1000.99
         |      },
         |     "periodDisallowableExpenses": {
         |          "costOfGoodsDisallowable": 1000.99,
         |          "paymentsToSubcontractorsDisallowable": 1000.99,
         |          "wagesAndStaffCostsDisallowable": 1000.99,
         |          "carVanTravelExpensesDisallowable": 1000.99,
         |          "premisesRunningCostsDisallowable": -1000.99,
         |          "maintenanceCostsDisallowable": -999.99,
         |          "adminCostsDisallowable": 1000.99,
         |          "businessEntertainmentCostsDisallowable": 1000.99,
         |          "advertisingCostsDisallowable": 1000.99,
         |          "interestOnBankOtherLoansDisallowable": -1000.99,
         |          "financeChargesDisallowable": -9999.99,
         |          "irrecoverableDebtsDisallowable": -1000.99,
         |          "professionalFeesDisallowable": 10000.89,
         |          "depreciationDisallowable": -99999999999.99,
         |          "otherExpensesDisallowable": 1000.99
         |      }
         |}
         |""".stripMargin
    )

    val responseBody: JsValue = Json.parse(
      s"""
         |{
         |   "periodId":"$periodId"
         |}
         |""".stripMargin
    )

    val downstreamResponse: JsValue = Json.parse(
      s"""
         |{
         |  "transactionReference": "2017090920170909"
         |}
         |""".stripMargin
    )

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "message"
         |      }
    """.stripMargin

  }

}
