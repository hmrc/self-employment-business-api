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
import v1.models.errors._
import v1.stubs.{AuditStub, AuthStub, DesStub, MtdIdLookupStub}

class RetrieveSelfEmploymentAnnualSummaryControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino = "AA123456A"
    val businessId = "XAIS12345678910"
    val taxYear = "2021-22"
    val desTaxYear = "2022"

    val responseBody = Json.parse(
      s"""
         |{
         |  "adjustments": {
         |    "includedNonTaxableProfits": 200.00,
         |    "basisAdjustment": 200.00,
         |    "overlapReliefUsed": 200.00,
         |    "accountingAdjustment": 200.00,
         |    "averagingAdjustment": 200.00,
         |    "lossBroughtForward": 200.00,
         |    "outstandingBusinessIncome": 200.00,
         |    "balancingChargeBPRA": 200.00,
         |    "balancingChargeOther": 200.00,
         |    "goodsAndServicesOwnUse": 200.00
         |  },
         |  "allowances": {
         |    "annualInvestmentAllowance": 200.00,
         |    "capitalAllowanceMainPool": 200.00,
         |    "capitalAllowanceSpecialRatePool": 200.00,
         |    "zeroEmissionGoodsVehicleAllowance": 200.00,
         |    "businessPremisesRenovationAllowance": 200.00,
         |    "enhancedCapitalAllowance": 200.00,
         |    "allowanceOnSales": 200.00,
         |    "capitalAllowanceSingleAssetPool": 200.00,
         |    "tradingAllowance": 200.00
         |  },
         |  "nonFinancials": {
         |    "class4NicInfo": {
         |      "exemptionCode": "002 - Trustee"
         |    }
         |  },
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
         |      "rel": "create-and-amend-self-employment-annual-summary",
         |      "method": "PUT"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
         |      "rel": "self",
         |      "method": "GET"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
         |      "rel": "delete-self-employment-annual-summary",
         |      "method": "DELETE"
         |    }
         |  ]
         |}
         |""".stripMargin)

    val desResponseBody = Json.parse(
      s"""
         |{
         |   "annualAdjustments": {
         |      "includedNonTaxableProfits": 200.00,
         |      "basisAdjustment": 200.00,
         |      "overlapReliefUsed": 200.00,
         |      "accountingAdjustment": 200.00,
         |      "averagingAdjustment": 200.00,
         |      "lossBroughtForward": 200.00,
         |      "outstandingBusinessIncome": 200.00,
         |      "balancingChargeBPRA": 200.00,
         |      "balancingChargeOther": 200.00,
         |      "goodsAndServicesOwnUse": 200.00
         |   },
         |   "annualAllowances": {
         |      "annualInvestmentAllowance": 200.00,
         |      "capitalAllowanceMainPool": 200.00,
         |      "capitalAllowanceSpecialRatePool": 200.00,
         |      "zeroEmissionGoodsVehicleAllowance": 200.00,
         |      "businessPremisesRenovationAllowance": 200.00,
         |      "enhancedCapitalAllowance": 200.00,
         |      "allowanceOnSales": 200.00,
         |      "capitalAllowanceSingleAssetPool": 200.00,
         |      "tradingIncomeAllowance":  200.00
         |   },
         |   "annualNonFinancials": {
         |      "exemptFromPayingClass4Nics": true,
         |      "class4NicsExemptionReason": "002"
         |   }
         |}
         |""".stripMargin)

    def setupStubs(): StubMapping

    def uri: String = s"/$nino/$businessId/annual/$taxYear"

    def desUri: String = s"/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/$desTaxYear"

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
          DesStub.onSuccess(DesStub.GET, desUri, Status.OK, desResponseBody)
        }


        val response: WSResponse = await(request().get())
        response.status shouldBe Status.OK
        response.json shouldBe responseBody
        response.header("X-CorrelationId").nonEmpty shouldBe true
        response.header("Content-Type") shouldBe Some("application/json")
      }
    }
    "return error according to spec" when {

      "validation error" when {
        def validationErrorTest(requestNino: String, requestBusinessId: String, requestTaxYear: String,
                                expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"validation fails with ${expectedBody.code} error" in new Test {

            override val nino: String = requestNino
            override val businessId: String = requestBusinessId
            override val taxYear: String = requestTaxYear


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
          ("AA123", "XAIS12345678910", "2021-22", Status.BAD_REQUEST, NinoFormatError),
          ("AA123456A", "203100", "2021-22", Status.BAD_REQUEST, BusinessIdFormatError),
          ("AA123456A", "XAIS12345678910", "2020", Status.BAD_REQUEST, TaxYearFormatError),
          ("AA123456A", "XAIS12345678910", "2020-22", Status.BAD_REQUEST, RuleTaxYearRangeInvalidError),
          ("AA123456A", "XAIS12345678910", "2016-17", Status.BAD_REQUEST, RuleTaxYearNotSupportedError)
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

            val response: WSResponse = await(request().get())
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (Status.BAD_REQUEST, "INVALID_NINO", Status.BAD_REQUEST, NinoFormatError),
          (Status.BAD_REQUEST, "INVALID_INCOME_SOURCE", Status.BAD_REQUEST, BusinessIdFormatError),
          (Status.BAD_REQUEST, "INVALID_TAX_YEAR", Status.BAD_REQUEST, TaxYearFormatError),
          (Status.NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", Status.NOT_FOUND, NotFoundError),
          (Status.INTERNAL_SERVER_ERROR, "SERVER_ERROR", Status.INTERNAL_SERVER_ERROR, DownstreamError),
          (Status.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", Status.INTERNAL_SERVER_ERROR, DownstreamError)
        )

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
