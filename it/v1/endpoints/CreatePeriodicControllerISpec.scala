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
import support.IntegrationBaseSpec
import v1.models.errors._
import v1.stubs.{AuthStub, DownstreamStub, MtdIdLookupStub}

class CreatePeriodicControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino: String = "AA123456A"
    val businessId: String = "XAIS12345678910"
    val periodId: String = "2017-01-25_2017-04-25"

    val requestBodyJson: JsValue = Json.parse(
      s"""
         |{
         |   "periodFromDate": "2017-01-25",
         |   "periodToDate": "2017-04-25",
         |   "incomes": {
         |      "turnover": {
         |         "amount": 500.25
         |      },
         |      "other": {
         |         "amount": 500.25
         |      }
         |   },
         |   "expenses": {
         |      "costOfGoodsBought": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "cisPaymentsTo": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "staffCosts": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "travelCosts": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "premisesRunningCosts": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "maintenanceCosts": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "adminCosts": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "advertisingCosts": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "businessEntertainmentCosts": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "interestOnLoans": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "financialCharges": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "badDebt": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "professionalFees": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "depreciation": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      },
         |      "other": {
         |         "amount": 500.25,
         |         "disallowableAmount": 500.25
         |      }
         |   }
         |}
         |""".stripMargin
    )

    val responseBody: JsValue = Json.parse(
      s"""
         |{
         |  "periodId": "2017-01-25_2017-04-25",
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "method": "GET",
         |      "rel": "self"
         |    }
         |  ]
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
        .withHttpHeaders((ACCEPT, "application/vnd.hmrc.1.0+json"))
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

    "return error according to spec" when {

      "validation error" when {
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
        "an invalid taxYear is provided" in new Test {
          override val businessId: String = "INVALID_BUSINESSID"

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().post(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(BusinessIdFormatError)
        }
        "an invalid From date is provided" in new Test {
          override val requestBodyJson: JsValue = Json.parse(
            s"""
               |{
               |   "periodFromDate": "2025417-01-25",
               |   "periodToDate": "2017-04-25",
               |   "incomes": {
               |      "turnover": {
               |         "amount": 500.25
               |      },
               |      "other": {
               |         "amount": 500.25
               |      }
               |   },
               |   "expenses": {
               |      "costOfGoodsBought": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "cisPaymentsTo": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "staffCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "travelCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "premisesRunningCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "maintenanceCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "adminCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "advertisingCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "businessEntertainmentCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "interestOnLoans": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "financialCharges": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "badDebt": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "professionalFees": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "depreciation": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "other": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      }
               |   }
               |}
               |""".stripMargin
          )

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().post(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(FromDateFormatError)
        }
        "an invalid To date is provided" in new Test {
          override val requestBodyJson: JsValue = Json.parse(
            s"""
               |{
               |   "periodFromDate": "2017-01-25",
               |   "periodToDate": "2078917-04-25",
               |   "incomes": {
               |      "turnover": {
               |         "amount": 500.25
               |      },
               |      "other": {
               |         "amount": 500.25
               |      }
               |   },
               |   "expenses": {
               |      "costOfGoodsBought": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "cisPaymentsTo": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "staffCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "travelCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "premisesRunningCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "maintenanceCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "adminCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "advertisingCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "businessEntertainmentCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "interestOnLoans": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "financialCharges": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "badDebt": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "professionalFees": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "depreciation": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "other": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      }
               |   }
               |}
               |""".stripMargin
          )

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().post(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(ToDateFormatError)
        }
        "a to date is provided which is before the provided from date" in new Test {
          override val requestBodyJson: JsValue = Json.parse(
            s"""
               |{
               |   "periodFromDate": "2017-06-25",
               |   "periodToDate": "2017-04-25",
               |   "incomes": {
               |      "turnover": {
               |         "amount": 500.25
               |      },
               |      "other": {
               |         "amount": 500.25
               |      }
               |   },
               |   "expenses": {
               |      "costOfGoodsBought": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "cisPaymentsTo": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "staffCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "travelCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "premisesRunningCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "maintenanceCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "adminCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "advertisingCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "businessEntertainmentCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "interestOnLoans": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "financialCharges": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "badDebt": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "professionalFees": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "depreciation": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "other": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      }
               |   }
               |}
               |""".stripMargin
          )

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().post(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(RuleToDateBeforeFromDateError)
        }
        "a single invalid amount is provided" in new Test {
          override val requestBodyJson: JsValue = Json.parse(
            s"""
               |{
               |   "periodFromDate": "2017-01-25",
               |   "periodToDate": "2017-04-25",
               |   "incomes": {
               |      "turnover": {
               |         "amount": 500.25
               |      },
               |      "other": {
               |         "amount": 500.25
               |      }
               |   },
               |   "expenses": {
               |      "costOfGoodsBought": {
               |         "amount": 500.25325,
               |         "disallowableAmount": 500.25
               |      },
               |      "cisPaymentsTo": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "staffCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "travelCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "premisesRunningCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "maintenanceCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "adminCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "advertisingCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "businessEntertainmentCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "interestOnLoans": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "financialCharges": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "badDebt": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "professionalFees": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "depreciation": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "other": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      }
               |   }
               |}
               |""".stripMargin
          )

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().post(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(ValueFormatError.copy(paths = Some(Seq("/expenses/costOfGoodsBought/amount"))))
        }

        "multiple invalid amounts are provided" in new Test {
          override val requestBodyJson: JsValue = Json.parse(
            s"""
               |{
               |   "periodFromDate": "2017-01-25",
               |   "periodToDate": "2017-04-25",
               |   "incomes": {
               |      "turnover": {
               |         "amount": 500.25
               |      },
               |      "other": {
               |         "amount": 500.25
               |      }
               |   },
               |   "expenses": {
               |      "costOfGoodsBought": {
               |         "amount": 500.25325,
               |         "disallowableAmount": 500.25
               |      },
               |      "cisPaymentsTo": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "staffCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "travelCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "premisesRunningCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "maintenanceCosts": {
               |         "amount": 500.235645,
               |         "disallowableAmount": 500.25
               |      },
               |      "adminCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "advertisingCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "businessEntertainmentCosts": {
               |         "amount": 500.2565,
               |         "disallowableAmount": 500.25
               |      },
               |      "interestOnLoans": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "financialCharges": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "badDebt": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.24565
               |      },
               |      "professionalFees": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "depreciation": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "other": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.687925
               |      }
               |   }
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
            "/expenses/costOfGoodsBought/amount",
            "/expenses/maintenanceCosts/amount",
            "/expenses/businessEntertainmentCosts/amount",
            "/expenses/badDebt/disallowableAmount",
            "/expenses/other/disallowableAmount"
          ))))
        }

        "both expenses and consolidated expenses are provided" in new Test {
          override val requestBodyJson: JsValue = Json.parse(
            s"""
               |{
               |   "periodFromDate": "2017-01-25",
               |   "periodToDate": "2017-04-25",
               |   "incomes": {
               |      "turnover": {
               |         "amount": 500.25
               |      },
               |      "other": {
               |         "amount": 500.25
               |      }
               |   },
               |   "consolidatedExpenses": {
               |      "consolidatedExpenses": 500.25
               |   },
               |   "expenses": {
               |      "costOfGoodsBought": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "cisPaymentsTo": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "staffCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "travelCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "premisesRunningCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "maintenanceCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "adminCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "advertisingCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "businessEntertainmentCosts": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "interestOnLoans": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "financialCharges": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "badDebt": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "professionalFees": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "depreciation": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      },
               |      "other": {
               |         "amount": 500.25,
               |         "disallowableAmount": 500.25
               |      }
               |   }
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
            """{
              |   "periodToDate": "2017-04-25",
              |   "incomes": {
              |      "turnover": {
              |         "amount": 500.25
              |      },
              |      "other": {
              |         "amount": 500.25
              |      }
              |   },
              |   "expenses": {
              |      "costOfGoodsBought": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "cisPaymentsTo": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "staffCosts": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "travelCosts": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "premisesRunningCosts": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "maintenanceCosts": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "adminCosts": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "advertisingCosts": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "businessEntertainmentCosts": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "interestOnLoans": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "financialCharges": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "badDebt": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "professionalFees": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "depreciation": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      },
              |      "other": {
              |         "amount": 500.25,
              |         "disallowableAmount": 500.25
              |      }
              |   }
              |}
              |""".stripMargin)

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().post(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(RuleIncorrectOrEmptyBodyError.copy(paths = Some(List("/periodFromDate"))))
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
          (CONFLICT, "INVALID_PERIOD", BAD_REQUEST, RuleToDateBeforeFromDateError),
          (CONFLICT, "OVERLAPS_IN_PERIOD", BAD_REQUEST, RuleOverlappingPeriod),
          (CONFLICT, "NOT_ALIGN_PERIOD", BAD_REQUEST, RuleMisalignedPeriod),
          (CONFLICT, "NOT_CONTIGUOUS_PERIOD", BAD_REQUEST, RuleNotContiguousPeriod),
          (CONFLICT, "NOT_ALLOWED_SIMPLIFIED_EXPENSES", BAD_REQUEST, RuleNotAllowedConsolidatedExpenses),
          (NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError))

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
