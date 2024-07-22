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

package v2.endpoints

import com.github.tomakehurst.wiremock.stubbing.StubMapping
import play.api.http.HeaderNames.ACCEPT
import play.api.http.Status._
import play.api.libs.json.{JsValue, Json}
import play.api.libs.ws.{WSRequest, WSResponse}
import play.api.test.Helpers.AUTHORIZATION
import shared.stubs.{AuditStub, AuthStub, DownstreamStub, MtdIdLookupStub}
import support.IntegrationBaseSpec

class AuthISpec extends IntegrationBaseSpec {

  private trait Test {
    val nino           = "AA123456A"
    val taxYear        = "2022"
    val incomeSourceId = "XAIS12345678910"

    val downstreamResponseJson: JsValue = Json.parse("""
        |
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
        |    "exemptFromPayingClass4Nics": true,
        |    "class4NicsExemptionReason": "001"
        |  }
        |}
      """.stripMargin)

    def downstreamUri: String = s"/income-tax/nino/$nino/self-employments/$incomeSourceId/annual-summaries/$taxYear"

    def setupStubs(): StubMapping

    def request(): WSRequest = {
      setupStubs()
      buildRequest(uri)
        .withHttpHeaders(
          (ACCEPT, "application/vnd.hmrc.2.0+json"),
          (AUTHORIZATION, "Bearer 123") // some bearer token
        )
    }

    private def uri: String = s"/$nino/$incomeSourceId/annual/2021-22"
  }

  "Calling the sample endpoint" when {

    "MTD ID lookup fails with a 500" should {

      "return 500" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.error(nino, INTERNAL_SERVER_ERROR)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe INTERNAL_SERVER_ERROR
      }
    }

    "MTD ID lookup fails with a 403" should {

      "return 403" in new Test {
        override val nino: String = "AA123456A"

        override def setupStubs(): StubMapping = {
          AuditStub.audit()
          MtdIdLookupStub.error(nino, FORBIDDEN)
        }

        val response: WSResponse = await(request().get())
        response.status shouldBe FORBIDDEN
      }
    }
  }

  "MTD ID lookup succeeds and the user is authorised" should {

    "return 200" in new Test {
      override def setupStubs(): StubMapping = {
        AuditStub.audit()
        AuthStub.authorised()
        MtdIdLookupStub.ninoFound(nino)
        DownstreamStub.onSuccess(DownstreamStub.GET, downstreamUri, OK, downstreamResponseJson)
      }

      val response: WSResponse = await(request().get())
      response.status shouldBe OK
      response.header("Content-Type") shouldBe Some("application/json")
    }
  }

  "MTD ID lookup succeeds but the user is NOT logged in" should {

    "return 403" in new Test {
      override val nino: String = "AA123456A"

      override def setupStubs(): StubMapping = {
        AuditStub.audit()
        MtdIdLookupStub.ninoFound(nino)
        AuthStub.unauthorisedNotLoggedIn()
      }

      val response: WSResponse = await(request().delete())
      response.status shouldBe FORBIDDEN
    }
  }

  "MTD ID lookup succeeds but the user is NOT authorised" should {

    "return 403" in new Test {
      override val nino: String = "AA123456A"

      override def setupStubs(): StubMapping = {
        AuditStub.audit()
        MtdIdLookupStub.ninoFound(nino)
        AuthStub.unauthorisedOther()
      }

      val response: WSResponse = await(request().get())
      response.status shouldBe FORBIDDEN
    }
  }

}
