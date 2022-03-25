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

package v1.models.response.retrieveAnnual

import mocks.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.hateoas.{Link, Method}

class RetrieveAnnualSubmissionResponseSpec extends UnitSpec with RetrieveAnnualSubmissionFixture with MockAppConfig {

  val model: RetrieveAnnualSubmissionResponse = RetrieveAnnualSubmissionResponse(
    allowances = Some(Allowances(None, None, None, None, None, None, None, None, None, None, None, None, None)),
    adjustments = Some(Adjustments(None, None, None, None, None, None, None, None, None)),
    nonFinancials = Some(NonFinancials(businessDetailsChangedRecently = true, None))
  )

  "reads" when {
    "passed valid mtd JSON" should {
      "return the model" in {
        Json.parse(
          s"""{
             |  "allowances": {},
             |  "adjustments": {},
             |  "nonFinancials": {
             |    "businessDetailsChangedRecently": true
             |  }
             |}
             |""".stripMargin).as[RetrieveAnnualSubmissionResponse] shouldBe model
      }
    }

    "passed populated JSON" should {
      "return the corresponding model" in {
        retrieveAnnualSubmissionBodyMtdJson().as[RetrieveAnnualSubmissionResponse] shouldBe retrieveAnnualSubmissionBody()
      }
    }
  }

  "writes" when {
    "passed a model" should {
      "return downstream JSON" in {
        Json.toJson(model) shouldBe
          Json.parse(
            s"""{
               |  "annualAllowances": {},
               |  "annualAdjustments": {},
               |  "annualNonFinancials": {
               |    "businessDetailsChangedRecently": true,
               |    "exemptFromPayingClass4Nics": false
               |  }
               |}
               |""".stripMargin)
      }
    }

    "passed a populated model" should {
      "return the populated downstream JSON" in {
        Json.toJson(retrieveAnnualSubmissionBody()) shouldBe retrieveAnnualSubmissionBodyDownstreamJson()
      }
    }
  }

  val desJson: JsValue = Json.parse(
    """
      |{
      |  "annualAdjustments": {
      |    "includedNonTaxableProfits": 500.25,
      |    "basisAdjustment": 500.25,
      |    "overlapReliefUsed": 500.25,
      |    "accountingAdjustment": 500.25,
      |    "averagingAdjustment": 500.25,
      |    "outstandingBusinessIncome": 500.25,
      |    "balancingChargeBpra": 500.25,
      |    "balancingChargeOther": 500.25,
      |    "goodsAndServicesOwnUse": 500.25
      |  },
      |  "annualAllowances": {
      |    "annualInvestmentAllowance": 500.25,
      |    "businessPremisesRenovationAllowance": 500.25,
      |    "capitalAllowanceMainPool": 500.25,
      |    "capitalAllowanceSpecialRatePool": 500.25,
      |    "zeroEmissionGoodsVehicleAllowance": 500.25,
      |    "enhanceCapitalAllowance": 500.25,
      |    "allowanceOnSales": 500.25,
      |    "capitalAllowanceSingleAssetPool": 500.25,
      |    "tradingIncomeAllowance":  500.25
      |  },
      |  "annualNonFinancials": {
      |    "exemptFromPayingClass4Nics": true,
      |    "class4NicsExemptionReason": "001"
      |  }
      |}
    """.stripMargin
  )

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |  "adjustments": {
      |    "includedNonTaxableProfits": 500.25,
      |    "basisAdjustment": 500.25,
      |    "overlapReliefUsed":500.25,
      |    "accountingAdjustment": 500.25,
      |    "averagingAdjustment": 500.25,
      |    "outstandingBusinessIncome": 500.25,
      |    "balancingChargeBPRA": 500.25,
      |    "balancingChargeOther":500.25,
      |    "goodsAndServicesOwnUse": 500.25
      |  },
      |  "allowances": {
      |    "annualInvestmentAllowance": 500.25,
      |    "capitalAllowanceMainPool": 500.25,
      |    "capitalAllowanceSpecialRatePool":500.25,
      |    "zeroEmissionGoodsVehicleAllowance": 500.25,
      |    "businessPremisesRenovationAllowance": 500.25,
      |    "enhancedCapitalAllowance": 500.25,
      |    "allowanceOnSales": 500.25,
      |    "capitalAllowanceSingleAssetPool": 500.25,
      |    "tradingAllowance": 500.25
      |  },
      |  "nonFinancials": {
      |    "class4NicInfo": {
      |      "exemptionCode": "non-resident"
      |    }
      |  }
      |}
    """.stripMargin
  )

  "reads" should {
    "return a model" when {
      "passed valid json" in {
        desJson.as[RetrieveAnnualSubmissionResponse] shouldBe model
      }
    }
  }

  "writes" should {
    "return json" when {
      "passed a model" in {
        Json.toJson(model) shouldBe mtdJson
      }
    }
  }

  "LinksFactory" should {
    "produce the correct links" when {
      "called" in {
        val nino = "AA111111A"
        val businessId = "XAIS12345678910"
        val taxYear = "2019-20"

        val data: RetrieveAnnualSubmissionHateoasData = RetrieveAnnualSubmissionHateoasData(
          Nino(nino),
          BusinessId(businessId),
          TaxYear.fromMtd(taxYear)
        )

        MockAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        RetrieveAnnualSubmissionResponse.RetrieveAnnualSubmissionLinksFactory.links(mockAppConfig, data) shouldBe Seq(
          Link(href = s"/my/context/$nino/$businessId/annual/$taxYear",
            method = Method.PUT, rel = "create-and-amend-self-employment-annual-submission"),
          Link(href = s"/my/context/$nino/$businessId/annual/$taxYear",
            method = Method.GET, rel = "self"),
          Link(href = s"/my/context/$nino/$businessId/annual/$taxYear",
            method = Method.DELETE, rel = "delete-self-employment-annual-submission")
        )
      }
    }
  }
}
