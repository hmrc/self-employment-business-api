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
import v1.models.domain.DesTaxYear
import v1.models.errors._
import v1.stubs.{AuthStub, DesStub, MtdIdLookupStub}

class AmendAnnualSummaryControllerISpec extends IntegrationBaseSpec {

  private trait Test {

    val nino: String = "AA123456A"
    val businessId: String = "XAIS12345678910"
    val taxYear: String = "2021-22"

    val requestBodyJson: JsValue = Json.parse(
      s"""
         |{
         |   "adjustments": {
         |        "includedNonTaxableProfits": 1.11,
         |        "basisAdjustment": 2.22,
         |        "overlapReliefUsed": 3.33,
         |        "accountingAdjustment": 4.44,
         |        "averagingAdjustment": 5.55,
         |        "outstandingBusinessIncome": 7.77,
         |        "balancingChargeBPRA": 8.88,
         |        "balancingChargeOther": 9.99,
         |        "goodsAndServicesOwnUse": 10.10
         |    },
         |    "allowances": {
         |        "annualInvestmentAllowance": 1.11,
         |        "businessPremisesRenovationAllowance": 2.22,
         |        "capitalAllowanceMainPool": 3.33,
         |        "capitalAllowanceSpecialRatePool": 4.44,
         |        "zeroEmissionGoodsVehicleAllowance": 5.55,
         |        "enhancedCapitalAllowance": 6.66,
         |        "allowanceOnSales": 7.77,
         |        "capitalAllowanceSingleAssetPool": 8.88,
         |        "tradingAllowance": 9.99,
         |        "electricChargePointAllowance": 11.11
         |    },
         |    "nonFinancials": {
         |        "class4NicInfo":{
         |            "isExempt": true,
         |            "exemptionCode": "001 - Non Resident"
         |        }
         |    }
         |}
         |""".stripMargin
    )

    val responseBody: JsValue = Json.parse(
      s"""
         |{
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
         |      "method": "GET",
         |      "rel": "self"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
         |      "method": "PUT",
         |      "rel": "create-and-amend-self-employment-annual-summary"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
         |      "method": "DELETE",
         |      "rel": "delete-self-employment-annual-summary"
         |    }
         |  ]
         |}
         |""".stripMargin)

    def uri: String = s"/$nino/$businessId/annual/$taxYear"

    def desUri: String = s"/income-tax/nino/$nino/self-employments/$businessId/annual-summaries/${DesTaxYear.fromMtd(taxYear)}"

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

    val desResponseBody: JsValue = Json.parse(
      """{
        |   "transactionReference": "2017090920170909"
        |}""".stripMargin)

  }

  "Calling the amend endpoint" should {

    "return a 200 status code" when {

      "any valid request is made" in new Test {

        override def setupStubs(): StubMapping = {
          AuthStub.authorised()
          MtdIdLookupStub.ninoFound(nino)
          DesStub.onSuccess(DesStub.PUT, desUri, OK, desResponseBody)
        }

        val response: WSResponse = await(request().put(requestBodyJson))
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

          val response: WSResponse = await(request().put(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(NinoFormatError)
        }
        "an invalid taxYear is provided" in new Test {
          override val taxYear: String = "INVALID_TAXYEAR"

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().put(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(TaxYearFormatError)
        }
        "a single invalid amount is provided" in new Test {
          override val requestBodyJson: JsValue = Json.parse(
            s"""
               |{
               |   "adjustments": {
               |        "includedNonTaxableProfits": 1.11,
               |        "basisAdjustment": 2.22,
               |        "overlapReliefUsed": 3.33,
               |        "accountingAdjustment": 4.44,
               |        "averagingAdjustment": 5.55,
               |        "outstandingBusinessIncome": 7.77,
               |        "balancingChargeBPRA": 8.88,
               |        "balancingChargeOther": 9.99,
               |        "goodsAndServicesOwnUse": 10.10
               |    },
               |    "allowances": {
               |        "annualInvestmentAllowance": 1.11,
               |        "businessPremisesRenovationAllowance": 2.22,
               |        "capitalAllowanceMainPool": 3.33,
               |        "capitalAllowanceSpecialRatePool": -4.44,
               |        "zeroEmissionGoodsVehicleAllowance": 5.55,
               |        "enhancedCapitalAllowance": 6.66,
               |        "allowanceOnSales": 7.77,
               |        "capitalAllowanceSingleAssetPool": 8.88,
               |        "tradingAllowance": 9.99,
               |        "electricChargePointAllowance": 11.11
               |    },
               |    "nonFinancials": {
               |        "class4NicInfo":{
               |            "isExempt": true,
               |            "exemptionCode": "001 - Non Resident"
               |        }
               |    }
               |}
               |""".stripMargin
          )

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().put(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(ValueFormatError.copy(paths = Some(Seq("/allowances/capitalAllowanceSpecialRatePool"))))
        }

        "multiple invalid amounts are provided" in new Test {
          override val requestBodyJson: JsValue = Json.parse(
            s"""
               |{
               |   "adjustments": {
               |        "includedNonTaxableProfits": 1.11,
               |        "basisAdjustment": 2.22,
               |        "overlapReliefUsed": 3.33,
               |        "accountingAdjustment": 4.44,
               |        "averagingAdjustment": -5.556543456,
               |        "outstandingBusinessIncome": 7.77,
               |        "balancingChargeBPRA": -8.88,
               |        "balancingChargeOther": 9.99,
               |        "goodsAndServicesOwnUse": 10.10
               |    },
               |    "allowances": {
               |        "annualInvestmentAllowance": 1.11,
               |        "businessPremisesRenovationAllowance": 2.22,
               |        "capitalAllowanceMainPool": 3.33,
               |        "capitalAllowanceSpecialRatePool": -4.44,
               |        "zeroEmissionGoodsVehicleAllowance": 5.55,
               |        "enhancedCapitalAllowance": 6.664365,
               |        "allowanceOnSales": 7.77,
               |        "capitalAllowanceSingleAssetPool": 8.88,
               |        "tradingAllowance": 9.99,
               |        "electricChargePointAllowance": 11.11
               |    },
               |    "nonFinancials": {
               |        "class4NicInfo":{
               |            "isExempt": true,
               |            "exemptionCode": "001 - Non Resident"
               |        }
               |    }
               |}
               |""".stripMargin
          )

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().put(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(ValueFormatError.copy(paths = Some(Seq(
            "/adjustments/averagingAdjustment",
            "/adjustments/balancingChargeBPRA",
            "/allowances/capitalAllowanceSpecialRatePool",
            "/allowances/enhancedCapitalAllowance"
          ))))
        }

        "a taxYear with range of greater than a year is provided" in new Test {
          override val taxYear: String = "2019-21"

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().put(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(RuleTaxYearRangeInvalidError)
        }

        "a taxYear below 2017-18 is provided" in new Test {
          override val taxYear: String = "2016-17"

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().put(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(RuleTaxYearNotSupportedError)
        }

        "an empty body is provided" in new Test {
          override val requestBodyJson: JsValue = Json.parse("""{}""")

          override def setupStubs(): StubMapping = {
            AuthStub.authorised()
            MtdIdLookupStub.ninoFound(nino)
          }

          val response: WSResponse = await(request().put(requestBodyJson))
          response.status shouldBe BAD_REQUEST
          response.json shouldBe Json.toJson(RuleIncorrectOrEmptyBodyError)
        }
      }

      "des service error" when {
        def serviceErrorTest(desStatus: Int, desCode: String, expectedStatus: Int, expectedBody: MtdError): Unit = {
          s"des returns an $desCode error and status $desStatus" in new Test {

            override def setupStubs(): StubMapping = {
              AuthStub.authorised()
              MtdIdLookupStub.ninoFound(nino)
              DesStub.onError(DesStub.PUT, desUri, desStatus, errorBody(desCode))
            }

            val response: WSResponse = await(request().put(requestBodyJson))
            response.status shouldBe expectedStatus
            response.json shouldBe Json.toJson(expectedBody)
          }
        }

        val input = Seq(
          (BAD_REQUEST, "INVALID_NINO", BAD_REQUEST, NinoFormatError),
          (BAD_REQUEST, "INVALID_INCOME_SOURCE", BAD_REQUEST, BusinessIdFormatError),
          (BAD_REQUEST, "INVALID_TAX_YEAR", BAD_REQUEST, TaxYearFormatError),
          (BAD_REQUEST, "INVALID_PAYLOAD", INTERNAL_SERVER_ERROR, DownstreamError),
          (NOT_FOUND, "NOT_FOUND_INCOME_SOURCE", NOT_FOUND, NotFoundError),
          (SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", INTERNAL_SERVER_ERROR, DownstreamError),
          (GONE, "GONE", NOT_FOUND, NotFoundError),
          (INTERNAL_SERVER_ERROR, "SERVER_ERROR", INTERNAL_SERVER_ERROR, DownstreamError))

        input.foreach(args => (serviceErrorTest _).tupled(args))
      }
    }
  }
}
