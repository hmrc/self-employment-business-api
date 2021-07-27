/*
 * Copyright 2021 HM Revenue & Customs
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

package v1.models.response.retrieveSEAnnual

import mocks.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.models.domain.ex.MtdEx
import v1.models.hateoas.{Link, Method}

class RetrieveSelfEmploymentAnnualSummaryResponseSpec extends UnitSpec with MockAppConfig {

  val desJson: JsValue = Json.parse(
    """
      |{
      |  "annualAdjustments": {
      |    "includedNonTaxableProfits": 500.25,
      |    "basisAdjustment": 500.25,
      |    "overlapReliefUsed": 500.25,
      |    "accountingAdjustment": 500.25,
      |    "averagingAdjustment": 500.25,
      |    "lossBroughtForward": 500.25,
      |    "outstandingBusinessIncome": 500.25,
      |    "balancingChargeBPRA": 500.25,
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
      |    "tradingIncomeAllowance":  500.25,
      |    "structureAndBuildingAllowance":  500.25,
      |    "electricChargePointAllowance":  500.25
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
      |    "lossBroughtForward": 500.25,
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
      |    "tradingAllowance": 500.25,
      |    "structureAndBuildingAllowance":  500.25,
      |    "electricChargePointAllowance":  500.25
      |  },
      |  "nonFinancials": {
      |    "class4NicInfo": {
      |      "exemptionCode": "001 - Non Resident"
      |    }
      |  }
      |}
    """.stripMargin
  )

  val model: RetrieveSelfEmploymentAnnualSummaryResponse = RetrieveSelfEmploymentAnnualSummaryResponse(
    Some(Adjustments(
      Some(500.25), Some(500.25), Some(500.25), Some(500.25), Some(500.25),
      Some(500.25), Some(500.25), Some(500.25), Some(500.25), Some(500.25)
    )),
    Some(Allowances(
      Some(500.25), Some(500.25), Some(500.25), Some(500.25), Some(500.25), Some(500.25),
      Some(500.25), Some(500.25), Some(500.25), Some(500.25), Some(500.25)
    )),
    Some(NonFinancials(Some(Class4NicInfo(Some(MtdEx.`001 - Non Resident`)))))
  )

  "reads" should {
    "return a model" when {
      "passed valid json" in {
        desJson.as[RetrieveSelfEmploymentAnnualSummaryResponse] shouldBe model
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
        val data: RetrieveSelfEmploymentAnnualSummaryHateoasData = RetrieveSelfEmploymentAnnualSummaryHateoasData(
          nino = "myNino",
          businessId = "myBusinessId",
          taxYear = "taxYear"
        )

        MockAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        RetrieveSelfEmploymentAnnualSummaryResponse.RetrieveSelfEmploymentAnnualSummaryLinksFactory.links(mockAppConfig, data) shouldBe Seq(
          Link(href = s"/my/context/${data.nino}/${data.businessId}/annual/${data.taxYear}",
            method = Method.PUT, rel = "create-and-amend-self-employment-annual-summary"),
          Link(href = s"/my/context/${data.nino}/${data.businessId}/annual/${data.taxYear}",
            method = Method.GET, rel = "self"),
          Link(href = s"/my/context/${data.nino}/${data.businessId}/annual/${data.taxYear}",
            method = Method.DELETE, rel = "delete-self-employment-annual-summary")
        )
      }
    }
  }
}