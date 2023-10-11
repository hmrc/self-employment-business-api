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

package v1.controllers.requestParsers.validators

import api.models.errors._
import play.api.libs.json.Json
import support.UnitSpec
import v1.models.request.amendSEAnnual.AmendAnnualSubmissionRawData

class AmendAnnualSubmissionValidatorSpec extends UnitSpec {

  val validator = new AmendAnnualSubmissionValidator()
  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validTaxYear    = "2021-22"
  private val requestBodyJson = Json.parse(
    """
      |{
      |  "adjustments": {
      |    "includedNonTaxableProfits": 200.12,
      |    "basisAdjustment": 200.12,
      |    "overlapReliefUsed": 200.12,
      |    "accountingAdjustment": 200.12,
      |    "averagingAdjustment": 200.12,
      |    "outstandingBusinessIncome": 200.12,
      |    "balancingChargeBpra": 200.12,
      |    "balancingChargeOther": 200.12,
      |    "goodsAndServicesOwnUse": 200.12
      |  },
      |  "allowances": {
      |    "annualInvestmentAllowance": 200.12,
      |    "capitalAllowanceMainPool": 200.12,
      |    "capitalAllowanceSpecialRatePool": 200.12,
      |    "zeroEmissionsGoodsVehicleAllowance": 200.12,
      |    "businessPremisesRenovationAllowance": 200.12,
      |    "enhancedCapitalAllowance": 200.12,
      |    "allowanceOnSales": 200.12,
      |    "capitalAllowanceSingleAssetPool": 200.12,
      |    "electricChargePointAllowance": 200.12,
      |    "zeroEmissionsCarAllowance": 200.12,
      |    "structuredBuildingAllowance": [
      |      {
      |        "amount": 1.23,
      |        "firstYear": {
      |          "qualifyingDate": "2021-11-11",
      |          "qualifyingAmountExpenditure": 1.23
      |        },
      |        "building": {
      |          "name": "Plaza 2",
      |          "postcode": "TF3 4NT"
      |        }
      |      }
      |    ],
      |    "enhancedStructuredBuildingAllowance": [
      |      {
      |        "amount": 1.23,
      |        "firstYear": {
      |          "qualifyingDate": "2021-11-11",
      |          "qualifyingAmountExpenditure": 1.23
      |        },
      |        "building": {
      |          "name": "Plaza 2",
      |          "postcode": "TF3 4NT"
      |        }
      |      }
      |    ]
      |  },
      |  "nonFinancials": {
      |    "businessDetailsChangedRecently": true,
      |    "class4NicsExemptionReason": "non-resident"
      |  }
      |}
      |""".stripMargin
  )

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(AmendAnnualSubmissionRawData(validNino, validBusinessId, validTaxYear, requestBodyJson)) shouldBe Nil
      }

      "a minimal adjustments request is supplied" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |   "adjustments":{
            |         "includedNonTaxableProfits": 216.12
            |   }
            |}
            |""".stripMargin
            )
          )) shouldBe Nil
      }
      "only adjustments is supplied" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "adjustments": {
            |    "includedNonTaxableProfits": 200.12,
            |    "basisAdjustment": 200.12,
            |    "overlapReliefUsed": 200.12,
            |    "accountingAdjustment": 200.12,
            |    "averagingAdjustment": 200.12,
            |    "outstandingBusinessIncome": 200.12,
            |    "balancingChargeBpra": 200.12,
            |    "balancingChargeOther": 200.12,
            |    "goodsAndServicesOwnUse": 200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe Nil
      }
      "only allowances is supplied" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |   "allowances": {
            |    "annualInvestmentAllowance": 200.12,
            |    "capitalAllowanceMainPool": 200.12,
            |    "capitalAllowanceSpecialRatePool": 200.12,
            |    "zeroEmissionsGoodsVehicleAllowance": 200.12,
            |    "businessPremisesRenovationAllowance": 200.12,
            |    "enhancedCapitalAllowance": 200.12,
            |    "allowanceOnSales": 200.12,
            |    "capitalAllowanceSingleAssetPool": 200.12,
            |    "electricChargePointAllowance": 200.12,
            |    "zeroEmissionsCarAllowance": 200.12,
            |    "structuredBuildingAllowance": [
            |      {
            |        "amount": 1.23,
            |        "firstYear": {
            |          "qualifyingDate": "2021-11-11",
            |          "qualifyingAmountExpenditure": 1.23
            |        },
            |        "building": {
            |          "name": "Plaza 2",
            |          "postcode": "TF3 4NT"
            |        }
            |      }
            |    ],
            |    "enhancedStructuredBuildingAllowance": [
            |      {
            |        "amount": 1.23,
            |        "firstYear": {
            |          "qualifyingDate": "2021-11-11",
            |          "qualifyingAmountExpenditure": 1.23
            |        },
            |        "building": {
            |          "name": "Plaza 2",
            |          "postcode": "TF3 4NT"
            |        }
            |      }
            |    ]
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe Nil
      }
      "only tradingIncomeAllowance is supplied" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |   "allowances": {
            |    "tradingIncomeAllowance": 200.20
            |   }
            |}
            |""".stripMargin
            )
          )) shouldBe Nil
      }
    }
    "return a path parameter error" when {
      "an invalid nino is supplied" in {
        validator.validate(AmendAnnualSubmissionRawData("A12344A", validBusinessId, validTaxYear, requestBodyJson)) shouldBe List(NinoFormatError)
      }
      "an invalid businessId is supplied" in {
        validator.validate(AmendAnnualSubmissionRawData(validNino, "Walrus", validTaxYear, requestBodyJson)) shouldBe List(BusinessIdFormatError)
      }
      "an invalid taxYear is supplied" in {
        validator.validate(AmendAnnualSubmissionRawData(validNino, validBusinessId, "2103/01", requestBodyJson)) shouldBe List(TaxYearFormatError)
      }
      "an invalid taxYear range is supplied" in {
        validator.validate(AmendAnnualSubmissionRawData(validNino, validBusinessId, "2022-24", requestBodyJson)) shouldBe List(
          RuleTaxYearRangeInvalidError)
      }
      "a below minimum taxYear is supplied" in {
        validator.validate(AmendAnnualSubmissionRawData(validNino, validBusinessId, "2010-11", requestBodyJson)) shouldBe List(
          RuleTaxYearNotSupportedError)
      }
    }
    "return RuleIncorrectOrEmptyBodyError" when {
      "an empty body is submitted" in {
        validator.validate(AmendAnnualSubmissionRawData(validNino, validBusinessId, validTaxYear, Json.parse("""{}"""))) shouldBe List(
          RuleIncorrectOrEmptyBodyError)
      }
      "an empty adjustments is submitted" in {
        validator.validate(
          AmendAnnualSubmissionRawData(validNino, validBusinessId, validTaxYear, Json.parse("""{"adjustments": {}}"""))) shouldBe List(
          RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/adjustments"))))
      }
      "an empty allowances is submitted" in {
        validator.validate(
          AmendAnnualSubmissionRawData(validNino, validBusinessId, validTaxYear, Json.parse("""{"allowances": {}}"""))) shouldBe List(
          RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/allowances"))))
      }
      "an empty nonFinancials is submitted" in {
        validator.validate(
          AmendAnnualSubmissionRawData(validNino, validBusinessId, validTaxYear, Json.parse("""{"nonFinancials": {}}"""))) shouldBe List(
          RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/nonFinancials/businessDetailsChangedRecently"))))
      }
      "structuredBuildingAllowance with a building without the mandatory postcode is submitted" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse("""{
            |   "allowances": {
            |    "structuredBuildingAllowance": [
            |      {
            |        "amount": 1.23,
            |        "building": {
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
          )) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/building/postcode"))))
      }
      "structuredBuildingAllowance without the mandatory amount field is submitted" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse("""{
            |   "allowances": {
            |    "structuredBuildingAllowance": [
            |      {
            |        "building": {
            |           "name": "Burn house",
            |           "postcode": "THB 8HA"
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
          )) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/amount"))))
      }
      "structuredBuildingAllowance with a firstYear without the mandatory qualifyingDate field is submitted" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse("""{
            |   "allowances": {
            |    "structuredBuildingAllowance": [
            |      {
            |        "amount": 1.45,
            |        "firstYear": {
            |           "qualifyingAmountExpenditure": 23.42
            |        },
            |        "building": {
            |           "name": "Burn house",
            |           "postcode": "THB 8HA"
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
          )) shouldBe List(
          RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/firstYear/qualifyingDate"))))
      }
      "structuredBuildingAllowance with a firstYear without the mandatory qualifyingAmountExpenditure field is submitted" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse("""{
            |   "allowances": {
            |    "structuredBuildingAllowance": [
            |      {
            |        "amount": 1.45,
            |        "firstYear": {
            |           "qualifyingDate": "2020-01-01"
            |        },
            |        "building": {
            |           "name": "Burn house",
            |           "postcode": "THB 8HA"
            |        }
            |      }
            |    ]
            |  }
            |}""".stripMargin)
          )) shouldBe List(
          RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/firstYear/qualifyingAmountExpenditure"))))
      }
    }
    "return ValueFormatError" when {
      "/adjustments/includedNonTaxableProfits is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "adjustments": {
            |    "includedNonTaxableProfits": -1
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/includedNonTaxableProfits"))))
      }
      "/adjustments/basisAdjustment is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "adjustments": {
            |    "basisAdjustment": -1.999
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.forPathAndRange(path = "/adjustments/basisAdjustment", min = "-99999999999.99", max = "99999999999.99"))
      }
      "/adjustments/overlapReliefUsed is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "adjustments": {
            |    "overlapReliefUsed": -1
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/overlapReliefUsed"))))
      }
      "/adjustments/accountingAdjustment is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "adjustments": {
            |    "accountingAdjustment": -1
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/accountingAdjustment"))))
      }
      "/adjustments/averagingAdjustment is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "adjustments": {
            |    "averagingAdjustment": -1.999
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(
          ValueFormatError.forPathAndRange(path = "/adjustments/averagingAdjustment", min = "-99999999999.99", max = "99999999999.99"))
      }

      "/adjustments/outstandingBusinessIncome is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "adjustments": {
            |    "outstandingBusinessIncome": -1
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/outstandingBusinessIncome"))))
      }
      "/adjustments/balancingChargeBpra is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "adjustments": {
            |    "balancingChargeBpra": -200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/balancingChargeBpra"))))
      }
      "/adjustments/balancingChargeOther is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "adjustments": {
            |    "balancingChargeOther": -200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/balancingChargeOther"))))
      }
      "/adjustments/goodsAndServicesOwnUse is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "adjustments": {
            |    "goodsAndServicesOwnUse": -200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/adjustments/goodsAndServicesOwnUse"))))
      }
      "/allowances/annualInvestmentAllowance is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "allowances": {
            |    "annualInvestmentAllowance": -200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/annualInvestmentAllowance"))))
      }
      "/allowances/businessPremisesRenovationAllowance is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "allowances": {
            |    "businessPremisesRenovationAllowance": -200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/businessPremisesRenovationAllowance"))))
      }
      "/allowances/capitalAllowanceMainPool is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "allowances": {
            |    "capitalAllowanceMainPool": -200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/capitalAllowanceMainPool"))))
      }
      "/allowances/capitalAllowanceSpecialRatePool is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "allowances": {
            |    "capitalAllowanceSpecialRatePool": -200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/capitalAllowanceSpecialRatePool"))))
      }
      "/allowances/zeroEmissionsGoodsVehicleAllowance is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "allowances": {
            |    "zeroEmissionsGoodsVehicleAllowance": -200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/zeroEmissionsGoodsVehicleAllowance"))))
      }
      "/allowances/enhancedCapitalAllowance is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "allowances": {
            |    "enhancedCapitalAllowance": -200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/enhancedCapitalAllowance"))))
      }
      "/allowances/allowanceOnSales is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "allowances": {
            |    "allowanceOnSales": -200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/allowanceOnSales"))))
      }
      "/allowances/capitalAllowanceSingleAssetPool is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "allowances": {
            |    "capitalAllowanceSingleAssetPool": -200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/capitalAllowanceSingleAssetPool"))))
      }
      "/allowances/electricChargePointAllowance is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "allowances": {
            |    "electricChargePointAllowance": -200.12
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/electricChargePointAllowance"))))
      }
      "/allowances/zeroEmissionsCarAllowance is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "zeroEmissionsCarAllowance": -200.12
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/zeroEmissionsCarAllowance"))))
      }
      "/allowances/tradingIncomeAllowance is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "tradingIncomeAllowance": -100.20
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(
          ValueFormatError.forPathAndRange("", min = "0", max = "1000").copy(paths = Some(Seq("/allowances/tradingIncomeAllowance"))))
      }
      "/allowances/structuredBuildingAllowance/amount is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "structuredBuildingAllowance": [
           |      {
           |        "amount": 1.233,
           |        "building": {
           |          "name": "Plaza 2",
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/amount"))))
      }
      "/allowances/structuredBuildingAllowance/firstYear/qualifyingDate is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "structuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "firstYear": {
           |          "qualifyingDate": "2021-131-11",
           |          "qualifyingAmountExpenditure": 1.23
           |        },
           |        "building": {
           |          "name": "Plaza 2",
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(DateFormatError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/firstYear/qualifyingDate"))))
      }
      "/allowances/structuredBuildingAllowance/firstYear/qualifyingDate is out of range" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
                |{
                |  "allowances": {
                |    "structuredBuildingAllowance": [
                |      {
                |        "amount": 1.23,
                |        "firstYear": {
                |          "qualifyingDate": "0010-01-01",
                |          "qualifyingAmountExpenditure": 1.23
                |        },
                |        "building": {
                |          "name": "Plaza 2",
                |          "postcode": "TF3 4NT"
                |        }
                |      }
                |    ]
                |  }
                |}
                |""".stripMargin
            )
          )) shouldBe List(DateFormatError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/firstYear/qualifyingDate"))))
      }
      "/allowances/structuredBuildingAllowance/firstYear/qualiyfingAmountExpenditure is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "structuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "firstYear": {
           |          "qualifyingDate": "2021-11-11",
           |          "qualifyingAmountExpenditure": 1.323
           |        },
           |        "building": {
           |          "name": "Plaza 2",
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/firstYear/qualifyingAmountExpenditure"))))
      }
      "/allowances/structuredBuildingAllowance/building/name is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "structuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "building": {
           |          "name": "Plaza 2#",
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(StringFormatError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/building/name"))))
      }
      "/allowances/structuredBuildingAllowance/building/number is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "structuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "building": {
           |          "number": "#2",
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(StringFormatError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/building/number"))))
      }
      "/allowances/structuredBuildingAllowance/building/postcode is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "structuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "building": {
           |          "name": "Plaza 2",
           |          "postcode": "TF3 4NT#"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(StringFormatError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/building/postcode"))))
      }

      "/allowances/enhancedStructuredBuildingAllowance/amount is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "enhancedStructuredBuildingAllowance": [
           |      {
           |        "amount": 1.233,
           |        "building": {
           |          "name": "Plaza 2",
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/allowances/enhancedStructuredBuildingAllowance/0/amount"))))
      }
      "/allowances/enhancedStructuredBuildingAllowance/firstYear/qualifyingDate is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "enhancedStructuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "firstYear": {
           |          "qualifyingDate": "2021-121-11",
           |          "qualifyingAmountExpenditure": 1.23
           |        },
           |        "building": {
           |          "name": "Plaza 2",
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(DateFormatError.copy(paths = Some(Seq("/allowances/enhancedStructuredBuildingAllowance/0/firstYear/qualifyingDate"))))
      }

      "/allowances/enhancedStructuredBuildingAllowance/firstYear/qualifyingDate is out of range" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
                |{
                |  "allowances": {
                |    "enhancedStructuredBuildingAllowance": [
                |      {
                |        "amount": 1.23,
                |        "firstYear": {
                |          "qualifyingDate": "0010-01-01",
                |          "qualifyingAmountExpenditure": 1.23
                |        },
                |        "building": {
                |          "name": "Plaza 2",
                |          "postcode": "TF3 4NT"
                |        }
                |      }
                |    ]
                |  }
                |}
                |""".stripMargin
            )
          )) shouldBe List(DateFormatError.copy(paths = Some(Seq("/allowances/enhancedStructuredBuildingAllowance/0/firstYear/qualifyingDate"))))
      }

      "/allowances/enhancedStructuredBuildingAllowance/firstYear/qualiyfingAmountExpenditure is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "enhancedStructuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "firstYear": {
           |          "qualifyingDate": "2021-11-11",
           |          "qualifyingAmountExpenditure": 1.233
           |        },
           |        "building": {
           |          "name": "Plaza 2",
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(Seq("/allowances/enhancedStructuredBuildingAllowance/0/firstYear/qualifyingAmountExpenditure"))))
      }
      "/allowances/enhancedStructuredBuildingAllowance/building/name is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "enhancedStructuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "building": {
           |          "name": "Plaza 2#",
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(StringFormatError.copy(paths = Some(Seq("/allowances/enhancedStructuredBuildingAllowance/0/building/name"))))
      }
      "/allowances/enhancedStructuredBuildingAllowance/building/number is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "enhancedStructuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "building": {
           |          "number": "#32",
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(StringFormatError.copy(paths = Some(Seq("/allowances/enhancedStructuredBuildingAllowance/0/building/number"))))
      }
      "/allowances/enhancedStructuredBuildingAllowance/building/postcode is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "enhancedStructuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "building": {
           |          "name": "Plaza 2",
           |          "postcode": "TF3 4NT#"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(StringFormatError.copy(paths = Some(Seq("/allowances/enhancedStructuredBuildingAllowance/0/building/postcode"))))
      }
      "/nonFinancials/class4NicsExemptionReason is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "nonFinancials": {
           |    "businessDetailsChangedRecently": true,
           |    "class4NicsExemptionReason": "non-resident 131231"
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(Class4ExemptionReasonFormatError)
      }
      "both allowances are supplied" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "annualInvestmentAllowance": 200.12,
           |    "capitalAllowanceMainPool": 200.12,
           |    "capitalAllowanceSpecialRatePool": 200.12,
           |    "zeroEmissionsGoodsVehicleAllowance": 200.12,
           |    "businessPremisesRenovationAllowance": 200.12,
           |    "enhancedCapitalAllowance": 200.12,
           |    "allowanceOnSales": 200.12,
           |    "capitalAllowanceSingleAssetPool": 200.12,
           |    "electricChargePointAllowance": 200.12,
           |    "zeroEmissionsCarAllowance": 200.12,
           |    "tradingIncomeAllowance": 320.12,
           |    "structuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "firstYear": {
           |          "qualifyingDate": "2021-11-11",
           |          "qualifyingAmountExpenditure": 1.23
           |        },
           |        "building": {
           |          "name": "Plaza 2",
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ],
           |    "enhancedStructuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "firstYear": {
           |          "qualifyingDate": "2021-11-11",
           |          "qualifyingAmountExpenditure": 1.23
           |        },
           |        "building": {
           |          "name": "Plaza 2",
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(RuleBothAllowancesSuppliedError)
      }
      "name or number aren't supplied with a postcode" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
           |{
           |  "allowances": {
           |    "structuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "building": {
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ],
           |    "enhancedStructuredBuildingAllowance": [
           |      {
           |        "amount": 1.23,
           |        "building": {
           |          "postcode": "TF3 4NT"
           |        }
           |      }
           |    ]
           |  }
           |}
           |""".stripMargin
            )
          )) shouldBe List(
          RuleBuildingNameNumberError.copy(paths =
            Some(Seq("/allowances/structuredBuildingAllowance/0/building", "/allowances/enhancedStructuredBuildingAllowance/0/building"))))
      }
    }
    "return multiple errors" when {
      "every path parameter format is invalid" in {
        validator.validate(AmendAnnualSubmissionRawData("AJAA12", "XASOE12", "201219", requestBodyJson)) shouldBe List(
          NinoFormatError,
          BusinessIdFormatError,
          TaxYearFormatError)
      }
      "every field in the body is invalid" in {
        validator.validate(
          AmendAnnualSubmissionRawData(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
            |{
            |  "adjustments": {
            |    "includedNonTaxableProfits": 200.132,
            |    "basisAdjustment": 200.123,
            |    "overlapReliefUsed": 200.132,
            |    "accountingAdjustment": 200.132,
            |    "averagingAdjustment": 200.132,
            |    "outstandingBusinessIncome": 200.123,
            |    "balancingChargeBpra": 200.123,
            |    "balancingChargeOther": 200.132,
            |    "goodsAndServicesOwnUse": 200.132
            |  },
            |  "allowances": {
            |    "annualInvestmentAllowance": 200.132,
            |    "capitalAllowanceMainPool": 200.132,
            |    "capitalAllowanceSpecialRatePool": 200.132,
            |    "zeroEmissionsGoodsVehicleAllowance": 200.123,
            |    "businessPremisesRenovationAllowance": 200.132,
            |    "enhancedCapitalAllowance": 200.132,
            |    "allowanceOnSales": 200.132,
            |    "capitalAllowanceSingleAssetPool": 200.312,
            |    "electricChargePointAllowance": -200.132,
            |    "zeroEmissionsCarAllowance": 200.132,
            |    "structuredBuildingAllowance": [
            |      {
            |        "amount": 1.323,
            |        "firstYear": {
            |          "qualifyingDate": "2021-11-113",
            |          "qualifyingAmountExpenditure": 1.233
            |        },
            |        "building": {
            |          "name": "Plaza 2#",
            |          "postcode": "TF3 4NT#"
            |        }
            |      }
            |    ],
            |    "enhancedStructuredBuildingAllowance": [
            |      {
            |        "amount": 1.233,
            |        "firstYear": {
            |          "qualifyingDate": "2021-11-113",
            |          "qualifyingAmountExpenditure": 1.233
            |        },
            |        "building": {
            |          "number": "Plaza #2",
            |          "postcode": "TF3 4N#T"
            |        }
            |      }
            |    ]
            |  },
            |  "nonFinancials": {
            |    "businessDetailsChangedRecently": true,
            |    "class4NicsExemptionReason": "non-resident"
            |  }
            |}
            |""".stripMargin
            )
          )) shouldBe List(
          DateFormatError.copy(paths = Some(
            Seq(
              "/allowances/structuredBuildingAllowance/0/firstYear/qualifyingDate",
              "/allowances/enhancedStructuredBuildingAllowance/0/firstYear/qualifyingDate"))),
          ValueFormatError
            .forPathAndRange(path = "", min = "-99999999999.99", max = "99999999999.99")
            .copy(paths = Some(Seq("/adjustments/basisAdjustment", "/adjustments/averagingAdjustment"))),
          StringFormatError.copy(paths = Some(Seq(
            "/allowances/structuredBuildingAllowance/0/building/name",
            "/allowances/structuredBuildingAllowance/0/building/postcode",
            "/allowances/enhancedStructuredBuildingAllowance/0/building/number",
            "/allowances/enhancedStructuredBuildingAllowance/0/building/postcode"
          ))),
          ValueFormatError.copy(paths = Some(Seq(
            "/adjustments/includedNonTaxableProfits",
            "/adjustments/overlapReliefUsed",
            "/adjustments/accountingAdjustment",
            "/adjustments/outstandingBusinessIncome",
            "/adjustments/balancingChargeBpra",
            "/adjustments/balancingChargeOther",
            "/adjustments/goodsAndServicesOwnUse",
            "/allowances/annualInvestmentAllowance",
            "/allowances/businessPremisesRenovationAllowance",
            "/allowances/capitalAllowanceMainPool",
            "/allowances/capitalAllowanceSpecialRatePool",
            "/allowances/zeroEmissionsGoodsVehicleAllowance",
            "/allowances/enhancedCapitalAllowance",
            "/allowances/allowanceOnSales",
            "/allowances/capitalAllowanceSingleAssetPool",
            "/allowances/electricChargePointAllowance",
            "/allowances/zeroEmissionsCarAllowance",
            "/allowances/structuredBuildingAllowance/0/amount",
            "/allowances/structuredBuildingAllowance/0/firstYear/qualifyingAmountExpenditure",
            "/allowances/enhancedStructuredBuildingAllowance/0/amount",
            "/allowances/enhancedStructuredBuildingAllowance/0/firstYear/qualifyingAmountExpenditure"
          )))
        )
      }
    }
  }

}
