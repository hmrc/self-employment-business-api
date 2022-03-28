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
import support.IntegrationBaseSpec
import v1.models.errors._
import v1.stubs.{AuditStub, AuthStub, DesStub, MtdIdLookupStub}

class RetrieveAnnualSubmissionControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino = "AA123456A"
    val businessId = "XAIS12345678910"
    val taxYear = "2021-22"
    val downstreamTaxYear = "2022"

    val responseBody: JsValue = Json.parse(
      s"""
         |{
         |  "adjustments": {
         |    "includedNonTaxableProfits": 210,
         |    "basisAdjustment": 178.23,
         |    "overlapReliefUsed": 123.78,
         |    "accountingAdjustment": 678.9,
         |    "averagingAdjustment": 674.98,
         |    "outstandingBusinessIncome": 342.67,
         |    "balancingChargeBpra": 145.98,
         |    "balancingChargeOther": 457.23,
         |    "goodsAndServicesOwnUse": 432.9
         |  },
         |  "allowances": {
         |    "annualInvestmentAllowance": 564.76,
         |    "capitalAllowanceMainPool": 456.98,
         |    "capitalAllowanceSpecialRatePool": 352.87,
         |    "zeroEmissionsGoodsVehicleAllowance": 653.9,
         |    "businessPremisesRenovationAllowance": 452.98,
         |    "enhancedCapitalAllowance": 563.23,
         |    "allowanceOnSales": 678.9,
         |    "capitalAllowanceSingleAssetPool": 563.89,
         |    "electricChargePointAllowance": 0,
         |    "structuredBuildingAllowance": [
         |      {
         |        "amount": 564.89,
         |        "firstYear": {
         |          "qualifyingDate": "2019-05-29",
         |          "qualifyingAmountExpenditure": 567.67
         |        },
         |        "building": {
         |          "name": "Victoria Building",
         |          "number": "23",
         |          "postcode": "TF3 5GH"
         |        }
         |      }
         |    ],
         |    "enhancedStructuredBuildingAllowance": [
         |      {
         |        "amount": 445.56,
         |        "firstYear": {
         |          "qualifyingDate": "2019-09-29",
         |          "qualifyingAmountExpenditure": 565.56
         |        },
         |        "building": {
         |          "name": "Trinity House",
         |          "number": "20",
         |          "postcode": "TF4 7HJ"
         |        }
         |      }
         |    ],
         |    "zeroEmissionsCarAllowance": 678.78
         |  },
         |  "nonFinancials": {
         |    "businessDetailsChangedRecently": true,
         |    "class4NicsExemptionReason": "non-resident"
         |  },
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/TC663795B/XAIS12345678910/annual/2019-20",
         |      "rel": "create-and-amend-self-employment-annual-submission",
         |      "method": "PUT"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/TC663795B/XAIS12345678910/annual/2019-20",
         |      "rel": "self",
         |      "method": "GET"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/TC663795B/XAIS12345678910/annual/2019-20",
         |      "rel": "delete-self-employment-annual-submission",
         |      "method": "DELETE"
         |    }
         |  ]
         |}
         |""".stripMargin)

    val desResponseBody: JsValue = Json.parse(
      s"""
         |{
         |  "annualAdjustments": {
         |    "includedNonTaxableProfits": 210,
         |    "basisAdjustment": 178.23,
         |    "overlapReliefUsed": 123.78,
         |    "accountingAdjustment": 678.9,
         |    "averagingAdjustment": 674.98,
         |    "lossBroughtForward": 124.78,
         |    "outstandingBusinessIncome": 342.67,
         |    "balancingChargeBpra": 145.98,
         |    "balancingChargeOther": 457.23,
         |    "goodsAndServicesOwnUse": 432.9
         |  },
         |  "annualAllowances": {
         |    "annualInvestmentAllowance": 564.76,
         |    "capitalAllowanceMainPool": 456.98,
         |    "capitalAllowanceSpecialRatePool": 352.87,
         |    "zeroEmissionGoodsVehicleAllowance": 653.9,
         |    "businessPremisesRenovationAllowance": 452.98,
         |    "enhanceCapitalAllowance": 563.23,
         |    "allowanceOnSales": 678.9,
         |    "capitalAllowanceSingleAssetPool": 563.89,
         |    "electricChargePointAllowance": 0,
         |    "structuredBuildingAllowance": [
         |      {
         |        "amount": 564.89,
         |        "firstYear": {
         |          "qualifyingDate": "2019-05-29",
         |          "qualifyingAmountExpenditure": 567.67
         |        },
         |        "building": {
         |          "name": "Victoria Building",
         |          "number": "23",
         |          "postCode": "TF3 5GH"
         |        }
         |      }
         |    ],
         |    "enhancedStructuredBuildingAllowance": [
         |      {
         |        "amount": 445.56,
         |        "firstYear": {
         |          "qualifyingDate": "2019-09-29",
         |          "qualifyingAmountExpenditure": 565.56
         |        },
         |        "building": {
         |          "name": "Trinity House",
         |          "number": "20",
         |          "postCode": "TF4 7HJ"
         |        }
         |      }
         |    ],
         |    "zeroEmissionsCarAllowance": 678.78
         |  },
         |  "annualNonFinancials": {
         |    "businessDetailsChangedRecently": true,
         |    "class4NicsExemptionReason": "001"
         |  }
         |}
         |""".stripMargin)

    def setupStubs(): StubMapping

    def uri: String = s"/$nino/$businessId/annual/$taxYear"

    def desUri: String = s"/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/$downstreamTaxYear"

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
