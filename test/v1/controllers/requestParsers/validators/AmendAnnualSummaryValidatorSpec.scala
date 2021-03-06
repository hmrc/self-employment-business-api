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

package v1.controllers.requestParsers.validators

import play.api.libs.json.Json
import support.UnitSpec
import v1.models.errors._
import v1.models.request.amendSEAnnual.AmendAnnualSummaryRawData

class AmendAnnualSummaryValidatorSpec extends UnitSpec {

  private val validNino = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validTaxYear = "2021-22"
  private val requestBodyJson = Json.parse(
    """
      |{
      |   "adjustments": {
      |        "includedNonTaxableProfits": 216.12,
      |        "basisAdjustment": 626.53,
      |        "overlapReliefUsed": 153.89,
      |        "accountingAdjustment": 514.24,
      |        "averagingAdjustment": 124.98,
      |        "lossBroughtForward": 571.27,
      |        "outstandingBusinessIncome": 751.03,
      |        "balancingChargeBPRA": 719.23,
      |        "balancingChargeOther": 956.47,
      |        "goodsAndServicesOwnUse": 157.43
      |    },
      |    "allowances": {
      |        "annualInvestmentAllowance": 561.32,
      |        "businessPremisesRenovationAllowance": 198.45,
      |        "capitalAllowanceMainPool": 825.34,
      |        "capitalAllowanceSpecialRatePool": 647.12,
      |        "zeroEmissionGoodsVehicleAllowance": 173.64,
      |        "enhancedCapitalAllowance": 115.98,
      |        "allowanceOnSales": 548.15,
      |        "capitalAllowanceSingleAssetPool": 901.67,
      |        "tradingAllowance": 521.34,
      |        "structureAndBuildingAllowance": "354.58",
      |        "electricChargePointAllowance": "832.52"
      |    },
      |    "nonFinancials": {
      |        "class4NicInfo":{
      |            "isExempt": true,
      |            "exemptionCode": "002 - Trustee"
      |        }
      |    }
      |}
      |""".stripMargin
  )

  val validator = new AmendSelfEmploymentAnnualSummaryValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, requestBodyJson)) shouldBe Nil
      }
      "a minimal adjustments request is supplied" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments":{
            |         "includedNonTaxableProfits": 216.12
            |   }
            |}
            |""".stripMargin
        ))) shouldBe Nil
      }
      "only adjustments is supplied" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |   }
            |}
            |""".stripMargin
        ))) shouldBe Nil
      }
      "only allowances is supplied" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |   }
            |}
            |""".stripMargin
        ))) shouldBe Nil
      }
    }
    "return a path parameter error" when {
      "an invalid nino is supplied" in {
        validator.validate(AmendAnnualSummaryRawData("A12344A", validBusinessId, validTaxYear, requestBodyJson)) shouldBe List(NinoFormatError)
      }
      "an invalid businessId is supplied" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, "Walrus", validTaxYear, requestBodyJson)) shouldBe List(BusinessIdFormatError)
      }
      "an invalid taxYear is supplied" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, "2103/01", requestBodyJson)) shouldBe List(TaxYearFormatError)
      }
      "an invalid taxYear range is supplied" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, "2022-24", requestBodyJson)) shouldBe List(RuleTaxYearRangeInvalidError)
      }
      "a below minimum taxYear is supplied" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, "2010-11", requestBodyJson)) shouldBe List(RuleTaxYearNotSupportedError)
      }
    }
    "return RuleIncorrectOrEmptyBodyError" when {
      "an empty body is submitted" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
        """{}"""))) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
      "an empty adjustments is submitted" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
        """{"adjustments": {}}"""))) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
      "an empty allowances is submitted" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
        """{"allowances": {}}"""))) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
      "an empty nonFinancials is submitted" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
        """{"nonFinancials": {}}"""))) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
    }
    "return ValueFormatError" when {
      "/adjustments/includedNonTaxableProfits is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": -1,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/includedNonTaxableProfits"))))
      }
      "/adjustments/basisAdjustment is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": -1.6432632,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/basisAdjustment"))))
      }
      "/adjustments/overlapReliefUsed is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": -1,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/overlapReliefUsed"))))
      }
      "/adjustments/accountingAdjustment is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": -1,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/accountingAdjustment"))))
      }
      "/adjustments/averagingAdjustment is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": -1.4632636,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/averagingAdjustment"))))
      }
      "/adjustments/lossBroughtForward is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": -1,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/lossBroughtForward"))))
      }
      "/adjustments/outstandingBusinessIncome is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": -1,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/outstandingBusinessIncome"))))
      }
      "/adjustments/balancingChargeBPRA is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": -1,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/balancingChargeBPRA"))))
      }
      "/adjustments/balancingChargeOther is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": -1,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/balancingChargeOther"))))
      }
      "/adjustments/goodsAndServicesOwnUse is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": -1
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/goodsAndServicesOwnUse"))))
      }
      "/allowances/annualInvestmentAllowance is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": -1,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/annualInvestmentAllowance"))))
      }
      "/allowances/businessPremisesRenovationAllowance is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": -1,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/businessPremisesRenovationAllowance"))))
      }
      "/allowances/capitalAllowanceMainPool is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": -1,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/capitalAllowanceMainPool"))))
      }
      "/allowances/capitalAllowanceSpecialRatePool is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": -1,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/capitalAllowanceSpecialRatePool"))))
      }
      "/allowances/zeroEmissionGoodsVehicleAllowance is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": -1,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/zeroEmissionGoodsVehicleAllowance"))))
      }
      "/allowances/enhancedCapitalAllowance is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |        "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": -1,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/enhancedCapitalAllowance"))))
      }
      "/allowances/allowanceOnSales is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |       "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": -1,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/allowanceOnSales"))))
      }
      "/allowances/capitalAllowanceSingleAssetPool is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |       "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": -1,
            |        "tradingAllowance": 521.34,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/capitalAllowanceSingleAssetPool"))))
      }
      "/allowances/tradingAllowance is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |       "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": -1,
            |        "structureAndBuildingAllowance": "354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/tradingAllowance"))))
      }
      "/allowances/structureAndBuildingAllowance is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |       "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 1263.23,
            |        "structureAndBuildingAllowance": "-354.58",
            |        "electricChargePointAllowance": "832.52"
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/structureAndBuildingAllowance"))))
      }
      "/allowances/electricChargePointAllowance is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": 216.12,
            |        "basisAdjustment": 626.53,
            |        "overlapReliefUsed": 153.89,
            |        "accountingAdjustment": 514.24,
            |        "averagingAdjustment": 124.98,
            |        "lossBroughtForward": 571.27,
            |        "outstandingBusinessIncome": 751.03,
            |        "balancingChargeBPRA": 719.23,
            |        "balancingChargeOther": 956.47,
            |        "goodsAndServicesOwnUse": 157.43
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": 561.32,
            |        "businessPremisesRenovationAllowance": 198.45,
            |        "capitalAllowanceMainPool": 825.34,
            |       "capitalAllowanceSpecialRatePool": 647.12,
            |        "zeroEmissionGoodsVehicleAllowance": 173.64,
            |        "enhancedCapitalAllowance": 115.98,
            |        "allowanceOnSales": 548.15,
            |        "capitalAllowanceSingleAssetPool": 901.67,
            |        "tradingAllowance": 532.78,
            |        "structureAndBuildingAllowance": 354.58,
            |        "electricChargePointAllowance": -832.52
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "002 - Trustee"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/electricChargePointAllowance"))))
      }
    }
    "return multiple errors" when {
      "every path parameter format is invalid" in {
        validator.validate(AmendAnnualSummaryRawData("AJAA12", "XASOE12", "201219", requestBodyJson)) shouldBe List(NinoFormatError, BusinessIdFormatError, TaxYearFormatError)
      }
      "every field in the body is invalid" in {
        validator.validate(AmendAnnualSummaryRawData(validNino, validBusinessId, validTaxYear, Json.parse(
          """
            |{
            |   "adjustments": {
            |        "includedNonTaxableProfits": -1,
            |        "basisAdjustment": -1.56345,
            |        "overlapReliefUsed": -1,
            |        "accountingAdjustment": -1,
            |        "averagingAdjustment": -1.6543643,
            |        "lossBroughtForward": -1,
            |        "outstandingBusinessIncome": -1,
            |        "balancingChargeBPRA": -1,
            |        "balancingChargeOther": -1,
            |        "goodsAndServicesOwnUse": -1
            |    },
            |    "allowances": {
            |        "annualInvestmentAllowance": -1,
            |        "businessPremisesRenovationAllowance": -1,
            |        "capitalAllowanceMainPool": -1,
            |        "capitalAllowanceSpecialRatePool": -1,
            |        "zeroEmissionGoodsVehicleAllowance": -1,
            |        "enhancedCapitalAllowance": -1,
            |        "allowanceOnSales": -1,
            |        "capitalAllowanceSingleAssetPool": -1,
            |        "tradingAllowance": -1,
            |        "structureAndBuildingAllowance": -1,
            |        "electricChargePointAllowance": -1
            |    },
            |    "nonFinancials": {
            |        "class4NicInfo":{
            |            "isExempt": true,
            |            "exemptionCode": "001 - Non Resident"
            |        }
            |    }
            |}
            |""".stripMargin
        ))) shouldBe List(
          ValueFormatError.copy(paths = Some(Seq(
            "/adjustments/includedNonTaxableProfits",
            "/adjustments/basisAdjustment",
            "/adjustments/overlapReliefUsed",
            "/adjustments/accountingAdjustment",
            "/adjustments/averagingAdjustment",
            "/adjustments/lossBroughtForward",
            "/adjustments/outstandingBusinessIncome",
            "/adjustments/balancingChargeBPRA",
            "/adjustments/balancingChargeOther",
            "/adjustments/goodsAndServicesOwnUse",
            "/allowances/annualInvestmentAllowance",
            "/allowances/businessPremisesRenovationAllowance",
            "/allowances/capitalAllowanceMainPool",
            "/allowances/capitalAllowanceSpecialRatePool",
            "/allowances/zeroEmissionGoodsVehicleAllowance",
            "/allowances/enhancedCapitalAllowance",
            "/allowances/allowanceOnSales",
            "/allowances/capitalAllowanceSingleAssetPool",
            "/allowances/tradingAllowance",
            "/allowances/structureAndBuildingAllowance",
            "/allowances/electricChargePointAllowance"))))
      }
    }
  }
}