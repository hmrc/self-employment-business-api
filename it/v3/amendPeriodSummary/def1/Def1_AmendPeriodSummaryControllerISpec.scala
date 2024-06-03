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

package v3.amendPeriodSummary.def1

import shared.models.errors._
import api.models.utils.JsonErrorValidators
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import stubs.{AuthStub, BaseDownstreamStub, MtdIdLookupStub}
import support.IntegrationBaseSpec
import v3.amendPeriodSummary.def1.model.Def1_AmendPeriodSummaryFixture

class Def1_AmendPeriodSummaryControllerISpec extends IntegrationBaseSpec with JsonErrorValidators with Def1_AmendPeriodSummaryFixture {

  val requestBodyJson: JsValue = def1_AmendPeriodSummaryBodyMtdJson

  "The V3 Amend Period Summary endpoint" should {

    "return a 200 status code" when {

      "given a valid request for a non-tys tax year" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)

          BaseDownstreamStub
            .when(BaseDownstreamStub.PUT, downstreamUri, downstreamQueryParams)
            .thenReturn(status = OK, JsObject.empty)
        }

        val response: WSResponse = await(request().put(requestBodyJson))
        response.status shouldBe OK
        response.json shouldBe responseJson
        response.header("X-CorrelationId").nonEmpty shouldBe true
      }
    }

    "return validation error according to spec" when {

      "validation error" when {

        def validationErrorTest(requestNino: String,
                                requestBusinessId: String,
                                requestPeriodId: String,
                                requestBody: JsValue,
                                expectedStatus: Int,
                                expectedBody: MtdError): Unit = {

          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String       = requestNino
            override val businessId: String = requestBusinessId
            override val periodId: String   = requestPeriodId

            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
            }

            val response: WSResponse = await(request().put(requestBody))
            response.json shouldBe Json.toJson(expectedBody)
            response.status shouldBe expectedStatus
          }
        }

        val input = List(
          ("BADNINO", "XAIS12345678910", "2019-01-01_2020-01-01", requestBodyJson, BAD_REQUEST, NinoFormatError),
          ("AA123456A", "BAD_BUSINESS_ID", "2019-01-01_2020-01-01", requestBodyJson, BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", "XAIS12345678910", "BAD_PERIOD_ID", requestBodyJson, BAD_REQUEST, PeriodIdFormatError),
          (
            "AA123456A",
            "XAIS12345678910",
            "2019-01-01_2020-01-01",
            requestBodyJson.update("/periodIncome/turnover", JsNumber(1.234)),
            BAD_REQUEST,
            ValueFormatError.copy(paths = Some(List("/periodIncome/turnover")))
          ),
          (
            "AA123456A",
            "XAIS12345678910",
            "2019-01-01_2020-01-01",
            requestBodyJson.replaceWithEmptyObject("/periodExpenses"),
            BAD_REQUEST,
            RuleIncorrectOrEmptyBodyError.copy(paths = Some(List("/periodExpenses")))
          )
        )

        input.foreach(args => (validationErrorTest _).tupled(args))
      }

      "downstream service error" when {

        def serviceErrorTest(downstreamStatus: Int, downstreamErrorCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"downstream returns an $downstreamErrorCode error and status $downstreamStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              BaseDownstreamStub.onError(
                BaseDownstreamStub.PUT,
                downstreamUri,
                downstreamQueryParams,
                downstreamStatus,
                errorBody(downstreamErrorCode))
            }

            val response: WSResponse = await(request().put(requestBodyJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val errors = List(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_INCOME_SOURCE", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_DATE_FROM", BAD_REQUEST, PeriodIdFormatError),
          (BAD_REQUEST, "INVALID_DATE_TO", BAD_REQUEST, PeriodIdFormatError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, InternalError),
          (NOT_FOUND, "NOT_FOUND_PERIOD", NOT_FOUND, NotFoundError),
          (NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", NOT_FOUND, NotFoundError),
          (CONFLICT, "BOTH_EXPENSES_SUPPLIED", BAD_REQUEST, RuleBothExpensesSuppliedError),
          (CONFLICT, "NOT_ALLOWED_SIMPLIFIED_EXPENSES", BAD_REQUEST, RuleNotAllowedConsolidatedExpenses),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, InternalError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, InternalError)
        )

        errors.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }

  private trait Test {

    val nino: String       = "AA123456A"
    val businessId: String = "XAIS12345678910"
    val periodId: String   = "2019-01-01_2020-01-01"
    val from               = "2019-01-01"
    val to                 = "2020-01-01"

    def mtdUri: String        = s"/$nino/$businessId/period/$periodId"
    def downstreamUri: String = s"/income-tax/nino/$nino/self-employments/$businessId/periodic-summaries"

    def amendPeriodSummaryHateoasUri: String    = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId"
    def retrievePeriodSummaryHateoasUri: String = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId"
    def listPeriodSummariesHateoasUri: String   = s"/individuals/business/self-employment/$nino/$businessId/period"

    val responseJson: JsValue = Json.parse(s"""
         |{
         |  "links": [
         |    {
         |      "href": "$amendPeriodSummaryHateoasUri",
         |      "rel": "amend-self-employment-period-summary",
         |      "method": "PUT"
         |    },
         |    {
         |      "href": "$retrievePeriodSummaryHateoasUri",
         |      "rel": "self",
         |      "method": "GET"
         |    },
         |    {
         |      "href": "$listPeriodSummariesHateoasUri",
         |      "rel": "list-self-employment-period-summaries",
         |      "method": "GET"
         |    }
         |  ]
         |}
         |""".stripMargin)

    def setupStubs(): StubMapping

    def downstreamQueryParams: Map[String, String] = Map(
      "from" -> from,
      "to"   -> to
    )

    def request(): WSRequest = {
      setupStubs()
      buildRequest(mtdUri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.3.0+json"),
          (AUTHORIZATION, "Bearer 123")
        )
    }

    def errorBody(code: String): String =
      s"""
         | {
         |   "code": "$code",
         |   "reason": "message"
         | }
    """.stripMargin

  }

}
