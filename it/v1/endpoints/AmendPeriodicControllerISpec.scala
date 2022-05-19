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
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import support.IntegrationBaseSpec
import v1.models.errors._
import v1.stubs.{AuthStub, DownstreamStub, MtdIdLookupStub}

class AmendPeriodicControllerISpec extends IntegrationBaseSpec {

  private trait Test {
    val nino: String       = "AA123456A"
    val businessId: String = "XAIS12345678910"
    val periodId: String   = "2019-01-01_2020-01-01"
    val fromDate: String   = "2019-01-01"
    val toDate: String     = "2020-01-01"

    val requestJson: JsValue = Json.parse("""
        |{
        |    "incomes": {
        |        "turnover": {
        |            "amount": 172.89
        |        },
        |        "other": {
        |            "amount": 634.14
        |        }
        |    },
        |    "consolidatedExpenses": {
        |        "consolidatedExpenses": 647.89
        |    }
        |}
        |""".stripMargin)

    val unconsolidatedRequestJson: JsValue = Json.parse("""
        |{
        |    "incomes": {
        |        "turnover": {
        |            "amount": 172.89
        |        },
        |        "other": {
        |            "amount": 634.14
        |        }
        |    },
        |    "expenses": {
        |        "costOfGoodsBought": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "cisPaymentsTo": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "staffCosts": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "travelCosts": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "premisesRunningCosts": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "maintenanceCosts": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "adminCosts": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "advertisingCosts": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "businessEntertainmentCosts": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "interestOnLoans": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "financialCharges": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "badDebt": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "professionalFees": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "depreciation": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        },
        |        "other": {
        |            "amount": 627.90,
        |            "disallowableAmount": 657.02
        |        }
        |    }
        |}
        |""".stripMargin)

    val responseJson: JsValue = Json.parse(s"""
         |{
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "method": "PUT",
         |      "rel": "amend-self-employment-period-summary"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "method": "GET",
         |      "rel": "self"
         |    }
         |  ]
         |}
         |""".stripMargin)

    def uri: String = s"/$nino/$businessId/period/$periodId"

    def queryParams: Map[String, String] = Map(
      "from" -> fromDate,
      "to"   -> toDate
    )

    def desUri: String = s"/income-store/nino/$nino/self-employments/$businessId/periodic-summaries"

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

  "Calling the amend endpoint" should {

    "return a 200 status code" when {

      "any valid consolidated request is made" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.PUT, desUri, queryParams, NO_CONTENT, JsObject.empty)
        }

        val response: WSResponse = await(request().put(requestJson))
        response.status shouldBe OK
        response.json shouldBe responseJson
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }

      "a valid unconsolidated request is made" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DownstreamStub.onSuccess(DownstreamStub.PUT, desUri, queryParams, NO_CONTENT, JsObject.empty)
        }

        val response: WSResponse = await(request().put(unconsolidatedRequestJson))
        response.status shouldBe OK
        response.json shouldBe responseJson
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

          val response: WSResponse = await(request().put(requestJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(NinoFormatError)
        }
        "an invalid businessId is provided" in new Test {
          override val businessId: String = "INVALID_BUSINESS_ID"

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().put(requestJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(BusinessIdFormatError)
        }
        "an invalid periodId is provided" in new Test {
          override val periodId: String = "INVALID_PERIOD_ID"

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().put(requestJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(PeriodIdFormatError)
        }
        "a single invalid amount is provided" in new Test {
          override val requestJson: JsValue = Json.parse("""
              |{
              |    "incomes": {
              |        "turnover": {
              |            "amount": 172.89
              |        },
              |        "other": {
              |            "amount": 90.0796
              |        }
              |    },
              |    "consolidatedExpenses": {
              |        "consolidatedExpenses": 647.89
              |    }
              |}
              |""".stripMargin)

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().put(requestJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(ValueFormatError.copy(paths = Some(Seq("/incomes/other/amount"))))
        }

        "multiple invalid amounts are provided" in new Test {
          override val requestJson: JsValue = Json.parse("""
              |{
              |    "incomes": {
              |        "turnover": {
              |            "amount": 172.4325
              |        },
              |        "other": {
              |            "amount": 634.1442
              |        }
              |    },
              |    "consolidatedExpenses": {
              |        "consolidatedExpenses": 632.4521
              |    }
              |}
              |""".stripMargin)

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().put(requestJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(
            ValueFormatError.copy(paths = Some(
              Seq(
                "/incomes/turnover/amount",
                "/incomes/other/amount",
                "/consolidatedExpenses/consolidatedExpenses"
              ))))
        }

      }

      "des service error" when {
        def serviceErrorTest(desStatus: Int, desCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"des returns an $desCode error and status $desStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DownstreamStub.onError(DownstreamStub.PUT, desUri, desStatus, errorBody(desCode))
            }

            val response: WSResponse = await(request().put(requestJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_INCOME_SOURCE", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_DATE_FROM", BAD_REQUEST, PeriodIdFormatError),
          (BAD_REQUEST, "INVALID_DATE_TO", BAD_REQUEST, PeriodIdFormatError),
          (CONFLICT, "BOTH_EXPENSES_SUPPLIED", BAD_REQUEST, RuleBothExpensesSuppliedError),
          (CONFLICT, "NOT_ALLOWED_SIMPLIFIED_EXPENSES", BAD_REQUEST, RuleNotAllowedConsolidatedExpenses),
          (NOT_FOUND, "NOT_FOUND_PERIOD", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "NOT_FOUND_NINO", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", NOT_FOUND, NotFoundError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

}
