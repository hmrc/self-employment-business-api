/*
 * Copyright 2020 HM Revenue & Customs
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
import play.api.libs.json.Json
import play.api.libs.ws.{WSRequest, WSResponse}
import support.IntegrationBaseSpec
import v1.models.errors.{BusinessIdFormatError, DownstreamError, MtdError, NinoFormatError, NotFoundError, PeriodIdFormatError}
import v1.stubs.{AuditStub, AuthStub, DesStub, MtdIdLookupStub}

class RetrieveSelfEmploymentPeriodicControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino = "AA123456A"
    val businessId = "XAIS12345678910"
    val periodId = "2019-01-01_2020-01-01"
    val fromDate = "2019-01-01"
    val toDate = "2020-01-01"

    val responseBody = Json.parse(
      s"""
         |{
         |  "periodFromDate": "2019-01-01",
         |  "periodToDate": "2020-01-01",
         |  "incomes": {
         |    "turnover": {
         |      "amount": 172.89
         |    },
         |    "other": {
         |      "amount": 634.14
         |    }
         |  },
         |    "expenses": {
         |      "costOfGoodsBought": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "cisPaymentsTo": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "staffCosts": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "travelCosts": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "premisesRunningCosts": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "maintenanceCosts": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "adminCosts": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "advertisingCosts": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "businessEntertainmentCosts": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "interestOnLoans": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "financialCharges": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "badDebt": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "professionalFees": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "depreciation": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      },
         |      "other": {
         |        "amount": 627.90,
         |        "disallowableAmount": 657.02
         |      }
         |    },
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "rel": "amend-periodic-update",
         |      "method": "PUT"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "rel": "self",
         |      "method": "GET"
         |    }
         |  ]
         |}
         |""".stripMargin)

    val desResponseBody = Json.parse(
      s"""
         |{
         |   "from": "2019-01-01",
         |   "to": "2020-01-01",
         |   "financials": {
         |      "deductions": {
         |         "adminCosts": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "advertisingCosts": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "badDebt": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "businessEntertainmentCosts": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "constructionIndustryScheme": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "costOfGoods": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "depreciation": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "financialCharges": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "interest": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "maintenanceCosts": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "other": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "professionalFees": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "premisesRunningCosts": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "staffCosts": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         },
         |         "travelCosts": {
         |            "amount": 627.90,
         |            "disallowableAmount": 657.02
         |         }
         |      },
         |      "incomes": {
         |         "turnover": 172.89,
         |         "other": 634.14
         |      }
         |   }
         |}
         |""".stripMargin)

    def setupStubs(): StubMapping

    def uri: String = s"/$nino/$businessId/period/$periodId"

    def queryParams: Map[String, String] = Map(
      "from" -> fromDate,
      "to" -> toDate
    )

    def desUri: String = s"/income-store/nino/$nino/self-employments/$businessId/periodic-summary-detail"

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

  "calling the retrieve endpoint" should {

    "return a 200 status code" when {

      "any valid request is made" in new Test {

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.GET, desUri, queryParams, Status.OK, desResponseBody)
        }


        val response: WSResponse = await(request().withQueryStringParameters("from" -> fromDate, "to" -> toDate).get())
        response.status shouldBe Status.OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Content-Type") shouldBe Some("application/json")
      }
    }
    "return error according to spec" when {

      "validation error" when {
        def validationErrorTest(requestNino: String, requestBusinessId: String, requestPeriodId: String,
                                expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String = requestNino
            override val businessId: String = requestBusinessId
            override val periodId: String = requestPeriodId


            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(requestNino)
            }

            val response: WSResponse = await(request().withQueryStringParameters("from" -> fromDate, "to" -> toDate).get())
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

      "des service error" when {
        def serviceErrorTest(desStatus: Int, desCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"des returns an $desCode error and status $desStatus" in new Test {


            override def setupStubs(): StubMapping = {
              AuditStub.audit()
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DesStub.onError(DesStub.GET, desUri, desStatus, errorBody(desCode))
            }

            val response: WSResponse = await(request().withQueryStringParameters("from" -> fromDate, "to" -> toDate).get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (Status.BAD_REQUEST, "INVALID_NINO", Status.BAD_REQUEST, NinoFormatError),
          (Status.BAD_REQUEST, "INVALID_INCOME_SOURCE_ID", Status.BAD_REQUEST, BusinessIdFormatError),
          (Status.BAD_REQUEST, "INVALID_DATE_FROM", Status.BAD_REQUEST, PeriodIdFormatError),
          (Status.BAD_REQUEST, "INVALID_DATE_TO", Status.BAD_REQUEST, PeriodIdFormatError),
          (Status.NOT_FOUND, "NOT_FOUND_NINO", Status.NOT_FOUND, NotFoundError),
          (Status.NOT_FOUND, "NOT_FOUND_PERIOD", Status.NOT_FOUND, NotFoundError),
          (Status.NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", Status.NOT_FOUND, NotFoundError),
          (Status.BAD_REQUEST, "INVALID_ORIGINATOR_ID", Status.INTERNAL_SERVER_ERROR, DownstreamError),
          (Status.INTERNAL_SERVER_ERROR, "SERVER_ERROR", Status.INTERNAL_SERVER_ERROR, DownstreamError),
          (Status.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", Status.INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
