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
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v1.models.errors._
import v1.stubs.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}

class CreatePeriodSummaryControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino: String = "AA123456A"
    val businessId: String = "XAIS12345678910"
    val periodId: String = "2019-08-24_2019-08-24"

    val requestBodyJson: JsValue = Json.parse(
      s"""
         |{
         |     "periodDates": {
         |           "periodStartDate": "2019-08-24",
         |           "periodEndDate": "2019-08-24"
         |     },
         |     "periodIncome": {
         |          "turnover": 1000.99,
         |          "other": 1000.99
         |     },
         |     "periodAllowableExpenses": {
         |          "costOfGoodsAllowable": 1000.99,
         |          "paymentsToSubcontractorsAllowable": 1000.99,
         |          "wagesAndStaffCostsAllowable": 1000.99,
         |          "carVanTravelExpensesAllowable": 1000.99,
         |          "premisesRunningCostsAllowable": -99999.99,
         |          "maintenanceCostsAllowable": -1000.99,
         |          "adminCostsAllowable": 1000.99,
         |          "businessEntertainmentCostsAllowable": 1000.99,
         |          "advertisingCostsAllowable": 1000.99,
         |          "interestOnBankOtherLoansAllowable": -1000.99,
         |          "financeChargesAllowable": -1000.99,
         |          "irrecoverableDebtsAllowable": -1000.99,
         |          "professionalFeesAllowable": -99999999999.99,
         |          "depreciationAllowable": -1000.99,
         |          "otherExpensesAllowable": 1000.99
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
         |   "periodId":"2019-08-24_2019-08-24",
         |   "links":[
         |      {
         |         "href":"/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |         "method":"PUT",
         |         "rel":"amend-self-employment-period-summary"
         |      },
         |      {
         |         "href":"/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |         "method":"GET",
         |         "rel":"self"
         |      },
         |      {
         |         "href":"/individuals/business/self-employment/$nino/$businessId/period",
         |         "method":"GET",
         |         "rel":"list-self-employment-period-summaries"
         |      }
         |   ]
         |}
         |""".stripMargin)

    val desResponse: JsValue = Json.parse(
      s"""
         |{
         |  "transactionReference": "2017090920170909"
         |}
         |""".stripMargin)

    def uri: String = s"/$nino/$businessId/period"

    def desUri: String = s"/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries"

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.1.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "des message"
         |      }
    """.stripMargin

  }

  "Calling the create endpoint" should {

    "return a 200 status code" when {
      "any valid request is made" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.POST, desUri, OK, desResponse)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }

    "return validation error according to spec" when {
      "an invalid NINO is provided" in new Test {
        override val nino: String = "INVALID_NINO"

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(NinoFormatError)
      }

      "an invalid business id is provided" in new Test {
        override val businessId: String = "INVALID_BUSINESSID"

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(BusinessIdFormatError)
      }
      "an invalid Start date is provided" in new Test {
        override val requestBodyJson: JsValue = Json.parse(
          s"""
             |{
             |     "periodDates": {
             |           "periodStartDate": "202019-08-24",
             |           "periodEndDate": "2019-08-24"
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
        response.json shouldBe Json.toJson(StartDateFormatError)
      }
      "an invalid End date is provided" in new Test {
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
        response.json shouldBe Json.toJson(EndDateFormatError)
      }
      "an end date is provided which is before the provided start date" in new Test {
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
        response.json shouldBe Json.toJson(RuleEndDateBeforeStartDateError)
      }
      "a single invalid amount is provided" in new Test {
        override val requestBodyJson: JsValue = Json.parse(
          s"""
             |{
             |     "periodDates": {
             |           "periodStartDate": "2019-08-24",
             |           "periodEndDate": "2019-08-24"
             |     },
             |     "periodIncome": {
             |          "turnover": 1000.99,
             |          "other": 1000.99
             |     },
             |     "periodAllowableExpenses": {
             |          "costOfGoodsAllowable": 1000.99,
             |          "paymentsToSubcontractorsAllowable": 1000.99,
             |          "wagesAndStaffCostsAllowable": 1000.99,
             |          "carVanTravelExpensesAllowable": 1000.99,
             |          "premisesRunningCostsAllowable": -99999.99,
             |          "maintenanceCostsAllowable": -1000.99,
             |          "adminCostsAllowable": 1000.99,
             |          "businessEntertainmentCostsAllowable": 1000.99,
             |          "advertisingCostsAllowable": 1000.99,
             |          "interestOnBankOtherLoansAllowable": -1000.99,
             |          "financeChargesAllowable": -1000.99,
             |          "irrecoverableDebtsAllowable": -1000.99,
             |          "professionalFeesAllowable": -99999999999.99,
             |          "depreciationAllowable": -1000.99,
             |          "otherExpensesAllowable": 1000.99
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
             |          "professionalFeesDisallowable": -99999999999.99,
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
        response.json shouldBe Json.toJson(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/professionalFeesDisallowable"))))
      }

      "multiple invalid amounts are provided" in new Test {
        override val requestBodyJson: JsValue = Json.parse(
          s"""
             |{
             |     "periodDates": {
             |           "periodStartDate": "2019-08-24",
             |           "periodEndDate": "2019-08-24"
             |     },
             |     "periodIncome": {
             |          "turnover": 1000.99,
             |          "other": 1000.99
             |     },
             |     "periodAllowableExpenses": {
             |          "costOfGoodsAllowable": 1000.99,
             |          "paymentsToSubcontractorsAllowable": 1000.99,
             |          "wagesAndStaffCostsAllowable": 1000.99,
             |          "carVanTravelExpensesAllowable": 1000.99,
             |          "premisesRunningCostsAllowable": -999999999999999.99,
             |          "maintenanceCostsAllowable": -1000.99,
             |          "adminCostsAllowable": 1000.99,
             |          "businessEntertainmentCostsAllowable": 1000.99,
             |          "advertisingCostsAllowable": 1000.99,
             |          "interestOnBankOtherLoansAllowable": -1000.99,
             |          "financeChargesAllowable": -100000000000000.99,
             |          "irrecoverableDebtsAllowable": -1000.99,
             |          "professionalFeesAllowable": -99999999999.99,
             |          "depreciationAllowable": -1000.99,
             |          "otherExpensesAllowable": 1000.99
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
             |          "professionalFeesDisallowable": -99999999999.99,
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
        response.json shouldBe Json.toJson(ValueFormatError.copy(paths = Some(Seq(
          "/periodAllowableExpenses/premisesRunningCostsAllowable",
          "/periodAllowableExpenses/financeChargesAllowable",
          "/periodDisallowableExpenses/adminCostsDisallowable",
          "/periodDisallowableExpenses/professionalFeesDisallowable"
        ))))
      }

      "both expenses and consolidated expenses are provided" in new Test {
        override val requestBodyJson: JsValue = Json.parse(
          s"""
             |{
             |     "periodDates": {
             |           "periodStartDate": "2019-08-24",
             |           "periodEndDate": "2019-08-24"
             |     },
             |     "periodIncome": {
             |          "turnover": 1000.99,
             |          "other": 1000.99
             |     },
             |     "periodAllowableExpenses": {
             |          "consolidatedExpenses": 1000.99,
             |          "costOfGoodsAllowable": 1000.99,
             |          "paymentsToSubcontractorsAllowable": 1000.99,
             |          "wagesAndStaffCostsAllowable": 1000.99,
             |          "carVanTravelExpensesAllowable": 1000.99,
             |          "premisesRunningCostsAllowable": -99999.99,
             |          "maintenanceCostsAllowable": -1000.99,
             |          "adminCostsAllowable": 1000.99,
             |          "businessEntertainmentCostsAllowable": 1000.99,
             |          "advertisingCostsAllowable": 1000.99,
             |          "interestOnBankOtherLoansAllowable": -1000.99,
             |          "financeChargesAllowable": -1000.99,
             |          "irrecoverableDebtsAllowable": -1000.99,
             |          "professionalFeesAllowable": -99999999999.99,
             |          "depreciationAllowable": -1000.99,
             |          "otherExpensesAllowable": 1000.99
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

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(RuleBothExpensesSuppliedError)
      }

      "an empty body is provided" in new Test {
        override val requestBodyJson: JsValue = Json.parse("""{}""")

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(RuleIncorrectOrEmptyBodyError)
      }

      s"a body missing mandatory fields is provided" in new Test {
        override val requestBodyJson: JsValue = Json.parse(
          """
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

    "des service error" when {
      def serviceErrorTest(desStatus: Int, desCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
        s"des returns an $desCode error and status $desStatus" in new Test {

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
            DownstreamStub.onError(DownstreamStub.POST, desUri, desStatus, errorBody(desCode))
          }

          val response: WSResponse = await(request().post(requestBodyJson))
          response.status shouldBe expectedStatus
          response.json shouldBe Json.toJson(expectedBody)
        }
      }

      val input = Seq(
        (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
        (BAD_REQUEST, "INVALID_INCOME_SOURCE", BAD_REQUEST, BusinessIdFormatError),
        (CONFLICT, "INVALID_PERIOD", BAD_REQUEST, RuleEndDateBeforeStartDateError),
        (CONFLICT, "OVERLAPS_IN_PERIOD", BAD_REQUEST, RuleOverlappingPeriod),
        (CONFLICT, "NOT_ALIGN_PERIOD", BAD_REQUEST, RuleMisalignedPeriod),
        (CONFLICT, "NOT_CONTIGUOUS_PERIOD", BAD_REQUEST, RuleNotContiguousPeriod),
        (CONFLICT, "NOT_ALLOWED_SIMPLIFIED_EXPENSES", BAD_REQUEST, RuleNotAllowedConsolidatedExpenses),
        (NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", NOT_FOUND, NotFoundError),
        (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, DownstreamError),
        (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
        (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError)
      )

      input.foreach(args => (serviceErrorTest _).tupled(args))
    }
  }
}
