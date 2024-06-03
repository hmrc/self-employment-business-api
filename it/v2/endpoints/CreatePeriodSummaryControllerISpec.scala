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

import shared.models.errors._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import stubs.{AuditStub, AuthStub, BaseDownstreamStub, MtdIdLookupStub}
import support.IntegrationBaseSpec

class CreatePeriodSummaryControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino: String       = "AA123456A"
    val businessId: String = "XAIS12345678910"
    val periodId: String   = s"${periodStartDate}_$periodEndDate"

    val requestBodyJson: JsValue = Json.parse(
      s"""
         |{
         |     "periodDates": {
         |           "periodStartDate": "$periodStartDate",
         |           "periodEndDate": "$periodEndDate"
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
         |   "periodId":"$periodId",
         |   "links":[
         |      {
         |         "href":"$amendPeriodSummaryHateoasUri",
         |         "method":"PUT",
         |         "rel":"amend-self-employment-period-summary"
         |      },
         |      {
         |         "href":"$retrievePeriodSummaryHateoasUri",
         |         "method":"GET",
         |         "rel":"self"
         |      },
         |      {
         |         "href":"$listPeriodSummariesHateoasUri",
         |         "method":"GET",
         |         "rel":"list-self-employment-period-summaries"
         |      }
         |   ]
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

    def periodStartDate: String

    def periodEndDate: String

    def amendPeriodSummaryHateoasUri: String

    def retrievePeriodSummaryHateoasUri: String

    def listPeriodSummariesHateoasUri: String

    def downstreamUri: String

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.2.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def uri: String = s"/$nino/$businessId/period"

    def errorBody(code: String): String =
      s"""
         |      {
         |        "code": "$code",
         |        "reason": "message"
         |      }
    """.stripMargin

  }

  private trait NonTysTest extends Test {
    override def downstreamUri: String   = s"/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries"
    override def periodStartDate: String = "2019-07-24"
    override def periodEndDate: String   = "2019-08-24"

    def amendPeriodSummaryHateoasUri: String    = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId"
    def retrievePeriodSummaryHateoasUri: String = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId"
    def listPeriodSummariesHateoasUri: String   = s"/individuals/business/self-employment/$nino/$businessId/period"
  }

  private trait TysIfsTest extends Test {
    override def downstreamUri: String = s"/income-tax/$downstreamTaxYear/$nino/self-employments/$businessId/periodic-summaries"

    def downstreamTaxYear: String = "23-24"

    override def periodStartDate: String = "2023-07-24"

    override def periodEndDate: String = "2023-08-24"

    def amendPeriodSummaryHateoasUri: String = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId?taxYear=$mtdTaxYear"

    def mtdTaxYear: String = "2023-24"

    def retrievePeriodSummaryHateoasUri: String = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId?taxYear=$mtdTaxYear"
    def listPeriodSummariesHateoasUri: String   = s"/individuals/business/self-employment/$nino/$businessId/period?taxYear=$mtdTaxYear"

  }

  "Calling the V2 create endpoint" should {

    "return a 200 status code" when {
      "any valid request is made" in new NonTysTest {

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

      "any valid Tax Year Specific request is made" in new TysIfsTest {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          BaseDownstreamStub.onSuccess(BaseDownstreamStub.POST, downstreamUri, CREATED, downstreamResponse)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }

    "return validation error according to spec" when {
      "an invalid NINO is provided" in new NonTysTest {
        override val nino: String = "INVALID_NINO"

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(NinoFormatError)
      }

      "an invalid business id is provided" in new NonTysTest {
        override val businessId: String = "INVALID_BUSINESSID"

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(BusinessIdFormatError)
      }

      "an invalid Start date is provided" in new NonTysTest {
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
      "an invalid End date is provided" in new NonTysTest {
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
      "an end date is provided which is before the provided start date" in new NonTysTest {
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
      "a single invalid amount is provided" in new NonTysTest {
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
          ValueFormatError
            .forPathAndRange("/periodDisallowableExpenses/professionalFeesDisallowable", "-99999999999.99", "99999999999.99"))
      }

      "multiple invalid amounts are provided" in new NonTysTest {
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
            .forPathAndRange(path = "", min = "-99999999999.99", max = "99999999999.99")
            .withPaths(List(
              "/periodExpenses/premisesRunningCosts",
              "/periodExpenses/financeCharges",
              "/periodDisallowableExpenses/adminCostsDisallowable",
              "/periodDisallowableExpenses/professionalFeesDisallowable"
            ))
        )
      }

      "an empty body is provided" in new NonTysTest {
        override val requestBodyJson: JsValue = Json.parse("""{}""")

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
        }

        val response: WSResponse = await(request().post(requestBodyJson))
        response.status shouldBe BAD_REQUEST
        response.json shouldBe Json.toJson(RuleIncorrectOrEmptyBodyError)
      }

      s"a body missing mandatory fields is provided" in new NonTysTest {
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
        s"downstream returns an $downstreamCode error and status $downstreamStatus" in new NonTysTest {

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

      val errors = Seq(
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
      val extraTysErrors = Seq(
        (BAD_REQUEST, "INVALID_CORRELATIONID", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_TAX_YEAR", INTERNAL_SERVER_ERROR, InternalError),
        (BAD_REQUEST, "INVALID_INCOME_SOURCE_ID", BAD_REQUEST, BusinessIdFormatError),
        (CONFLICT, "PERIOD_EXISTS", BAD_REQUEST, RuleDuplicateSubmissionError),
        (UNPROCESSABLE_ENTITY, "PERIOD_OVERLAP", BAD_REQUEST, RuleOverlappingPeriod),
        (UNPROCESSABLE_ENTITY, "PERIOD_ALIGNMENT", BAD_REQUEST, RuleMisalignedPeriod),
        (UNPROCESSABLE_ENTITY, "PERIOD_HAS_GAPS", BAD_REQUEST, RuleNotContiguousPeriod),
        (UNPROCESSABLE_ENTITY, "END_BEFORE_START", BAD_REQUEST, RuleEndBeforeStartDateError),
        (UNPROCESSABLE_ENTITY, "BOTH_EXPENSES_SUPPLIED", BAD_REQUEST, RuleBothExpensesSuppliedError),
        (UNPROCESSABLE_ENTITY, "TAX_YEAR_NOT_SUPPORTED", BAD_REQUEST, RuleTaxYearNotSupportedError),
        (NOT_FOUND, "INCOME_SOURCE_NOT_FOUND", NOT_FOUND, NotFoundError)
//        (UNPROCESSABLE_ENTITY, "INVALID_SUBMISSION_PERIOD", BAD_REQUEST, RuleInvalidSubmissionPeriodError), // To be reinstated, see MTDSA-15595
//        (UNPROCESSABLE_ENTITY, "INVALID_SUBMISSION_END_DATE", BAD_REQUEST, RuleInvalidSubmissionEndDateError) // To be reinstated, see MTDSA-15595
      )

      (errors ++ extraTysErrors).foreach(args => (serviceErrorTest _).tupled(args))
    }
  }

}
