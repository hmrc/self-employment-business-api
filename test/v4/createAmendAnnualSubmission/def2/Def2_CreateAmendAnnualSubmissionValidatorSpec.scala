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

package v4.createAmendAnnualSubmission.def2

import api.models.domain.ex.MtdNicExemption
import api.models.errors.{Class4ExemptionReasonFormatError, RuleBothAllowancesSuppliedError, RuleBuildingNameNumberError, RuleWrongTpaAmountSubmittedError}
import play.api.libs.json.{JsNumber, JsValue, Json}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v4.createAmendAnnualSubmission.CreateAmendAnnualSubmissionValidatorFactory
import v4.createAmendAnnualSubmission.def2.request._
import v4.createAmendAnnualSubmission.model.request.{CreateAmendAnnualSubmissionRequestData, Def2_CreateAmendAnnualSubmissionRequestData}

class Def2_CreateAmendAnnualSubmissionValidatorSpec extends UnitSpec with JsonErrorValidators {

  private implicit val correlationId: String = "1234"

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validTaxYear    = "2024-25"

  private val validAdjustments = Json.parse(
    """
     |  {
     |    "includedNonTaxableProfits": 200.12,
     |    "basisAdjustment": 200.12,
     |    "overlapReliefUsed": 200.12,
     |    "accountingAdjustment": 200.12,
     |    "averagingAdjustment": 200.12,
     |    "outstandingBusinessIncome": 200.12,
     |    "balancingChargeBpra": 200.12,
     |    "balancingChargeOther": 200.12,
     |    "goodsAndServicesOwnUse": 200.12,
     |    "transitionProfitAmount": 200.12,
     |    "transitionProfitAccelerationAmount": 200.12
     |  }
     |""".stripMargin
  )

  private val validAllowances = Json.parse(
    """
      |  {
      |    "annualInvestmentAllowance": 200.12,
      |    "businessPremisesRenovationAllowance": 200.12,
      |    "capitalAllowanceMainPool": 200.12,
      |    "capitalAllowanceSpecialRatePool": 200.12,
      |    "zeroEmissionsGoodsVehicleAllowance": 200.12,
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
      |""".stripMargin
  )

  private val validAllowancesWithOnlyTradingIncomeAllowance = Json.parse(
    """
      |  {
      |  "tradingIncomeAllowance": 200.12
      |  }
      |""".stripMargin
  )

  private val validAdjustmentsWithNoTransitionProfitValue = Json.parse(
    """
      |  {
      |    "includedNonTaxableProfits": 200.12,
      |    "basisAdjustment": 200.12,
      |    "overlapReliefUsed": 200.12,
      |    "accountingAdjustment": 200.12,
      |    "averagingAdjustment": 200.12,
      |    "outstandingBusinessIncome": 200.12,
      |    "balancingChargeBpra": 200.12,
      |    "balancingChargeOther": 200.12,
      |    "goodsAndServicesOwnUse": 200.12,
      |    "transitionProfitAccelerationAmount": 200.12
      |  }
      |""".stripMargin
  )

  private val validNonFinancials = Json.parse(
    """
      |  {
      |    "businessDetailsChangedRecently": true,
      |    "class4NicsExemptionReason": "non-resident"
      |  }
      |""".stripMargin
  )

  private def validRequestBody(adjustments: JsValue = validAdjustments,
                               allowances: JsValue = validAllowances,
                               nonFinancials: JsValue = validNonFinancials) = Json.obj(
    "adjustments"   -> adjustments,
    "allowances"    -> allowances,
    "nonFinancials" -> nonFinancials
  )

  private val parsedNino       = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)
  private val parsedTaxYear    = TaxYear.fromMtd(validTaxYear)

  private val parsedAdjustments = Def2_CreateAmend_Adjustments(
    includedNonTaxableProfits = Some(200.12),
    basisAdjustment = Some(200.12),
    overlapReliefUsed = Some(200.12),
    accountingAdjustment = Some(200.12),
    averagingAdjustment = Some(200.12),
    outstandingBusinessIncome = Some(200.12),
    balancingChargeBpra = Some(200.12),
    balancingChargeOther = Some(200.12),
    goodsAndServicesOwnUse = Some(200.12),
    transitionProfitAmount = Some(200.12),
    transitionProfitAccelerationAmount = Some(200.12)
  )

  private val parsedFirstYear = Def2_CreateAmend_FirstYear(qualifyingDate = "2021-11-11", qualifyingAmountExpenditure = 1.23)
  private val parsedBuilding  = Def2_CreateAmend_Building(name = Some("Plaza 2"), number = None, postcode = "TF3 4NT")

  private val parsedStructuredBuildingAllowance =
    Def2_CreateAmend_StructuredBuildingAllowance(amount = 1.23, firstYear = Some(parsedFirstYear), building = parsedBuilding)

  private val parsedAllowances = Def2_CreateAmend_Allowances(
    annualInvestmentAllowance = Some(200.12),
    businessPremisesRenovationAllowance = Some(200.12),
    capitalAllowanceMainPool = Some(200.12),
    capitalAllowanceSpecialRatePool = Some(200.12),
    zeroEmissionsGoodsVehicleAllowance = Some(200.12),
    enhancedCapitalAllowance = Some(200.12),
    allowanceOnSales = Some(200.12),
    capitalAllowanceSingleAssetPool = Some(200.12),
    tradingIncomeAllowance = None,
    electricChargePointAllowance = Some(200.12),
    zeroEmissionsCarAllowance = Some(200.12),
    structuredBuildingAllowance = Some(Seq(parsedStructuredBuildingAllowance)),
    enhancedStructuredBuildingAllowance = Some(Seq(parsedStructuredBuildingAllowance))
  )

  private val parsedAllowancesWithOnlyTradingIncomeAllowance =
    Def2_CreateAmend_Allowances(None, None, None, None, None, None, None, None, tradingIncomeAllowance = Some(200.12), None, None, None, None)

  private val parsedNonFinancials =
    Def2_CreateAmend_NonFinancials(businessDetailsChangedRecently = true, class4NicsExemptionReason = Some(MtdNicExemption.parser("non-resident")))

  private val parsedRequestBody =
    Def2_CreateAmendAnnualSubmissionRequestBody(Some(parsedAdjustments), Some(parsedAllowances), Some(parsedNonFinancials))

  private val validatorFactory = new CreateAmendAnnualSubmissionValidatorFactory

  private def validator(nino: String, businessId: String, taxYear: String, body: JsValue) =
    validatorFactory.validator(nino, businessId, taxYear, body)

  "validate()" should {
    "return the parsed domain object" when {
      "a valid request is made" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, validTaxYear, validRequestBody()).validateAndWrapResult()

        result shouldBe Right(
          Def2_CreateAmendAnnualSubmissionRequestData(parsedNino, parsedBusinessId, parsedTaxYear, parsedRequestBody)
        )
      }

      "a minimal adjustments request is supplied" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(
            validNino,
            validBusinessId,
            validTaxYear,
            Json.parse(
              """
              | {
              |   "adjustments": {
              |         "includedNonTaxableProfits": 216.12
              |   }
              | }
              |""".stripMargin
            )
          ).validateAndWrapResult()

        val expected = Def2_CreateAmendAnnualSubmissionRequestData(
          parsedNino,
          parsedBusinessId,
          parsedTaxYear,
          parsedRequestBody.copy(
            Some(parsedAdjustments.copy(includedNonTaxableProfits = Some(216.12), None, None, None, None, None, None, None, None, None, None)),
            None,
            None)
        )

        result shouldBe Right(expected)
      }

      "only adjustments is supplied" in {
        val requestBody: JsValue = validRequestBody().removeProperty("/allowances").removeProperty("/nonFinancials")

        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, validTaxYear, requestBody).validateAndWrapResult()

        val expected = Def2_CreateAmendAnnualSubmissionRequestData(
          parsedNino,
          parsedBusinessId,
          parsedTaxYear,
          parsedRequestBody.copy(allowances = None, nonFinancials = None))

        result shouldBe Right(expected)
      }

      "only allowances is supplied" in {
        val requestBody: JsValue = validRequestBody().removeProperty("/adjustments").removeProperty("/nonFinancials")

        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, validTaxYear, requestBody).validateAndWrapResult()
        result shouldBe Right(
          Def2_CreateAmendAnnualSubmissionRequestData(
            parsedNino,
            parsedBusinessId,
            parsedTaxYear,
            parsedRequestBody.copy(adjustments = None, nonFinancials = None))
        )
      }

      "only tradingIncomeAllowance is supplied" in {
        val requestBody: JsValue =
          validRequestBody(allowances = validAllowancesWithOnlyTradingIncomeAllowance).removeProperty("/adjustments").removeProperty("/nonFinancials")

        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, validTaxYear, requestBody).validateAndWrapResult()
        result shouldBe Right(
          Def2_CreateAmendAnnualSubmissionRequestData(
            parsedNino,
            parsedBusinessId,
            parsedTaxYear,
            parsedRequestBody.copy(adjustments = None, allowances = Some(parsedAllowancesWithOnlyTradingIncomeAllowance), nonFinancials = None)
          )
        )
      }
    }
    "return a path parameter error" when {
      "an invalid nino is supplied" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator("A12344A", validBusinessId, validTaxYear, validRequestBody()).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, NinoFormatError)
        )
      }
      "an invalid businessId is supplied" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, "Walrus", validTaxYear, validRequestBody()).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(correlationId, BusinessIdFormatError)
        )
      }
      "an invalid taxYear is supplied" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, "2103/01", validRequestBody()).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, TaxYearFormatError)
        )
      }
      "an invalid taxYear range is supplied" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, "2022-24", validRequestBody()).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError)
        )
      }
      "a below minimum taxYear is supplied" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, "2010-11", validRequestBody()).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleTaxYearNotSupportedError)
        )
      }
    }
    "return RuleIncorrectOrEmptyBodyError" when {
      "an empty body is submitted" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, validTaxYear, Json.parse("""{}""")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError)
        )
      }
      "an empty adjustments is submitted" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, validTaxYear, Json.parse("""{"adjustments": {}}""")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/adjustments"))))
        )
      }
      "an empty allowances is submitted" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, validTaxYear, Json.parse("""{"allowances": {}}""")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/allowances"))))
        )
      }
      "an empty nonFinancials is submitted" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, validTaxYear, Json.parse("""{"nonFinancials": {}}""")).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/nonFinancials/businessDetailsChangedRecently"))))
        )
      }
      "structuredBuildingAllowance with a building without the mandatory postcode is submitted" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(
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
          ).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/building/postcode"))))
        )
      }
      "structuredBuildingAllowance without the mandatory amount field is submitted" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(
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
          ).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/amount"))))
        )
      }
      "structuredBuildingAllowance with a firstYear without the mandatory qualifyingDate field is submitted" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(
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
          ).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/firstYear/qualifyingDate"))))
        )
      }
      "structuredBuildingAllowance with a firstYear without the mandatory qualifyingAmountExpenditure field is submitted" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(
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
          ).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/allowances/structuredBuildingAllowance/0/firstYear/qualifyingAmountExpenditure"))))
        )
      }
    }

    "return RuleWrongTpaAmountSubmittedError" when {
      "an valid body with no transition profit value is submitted" in {
        val requestBody: JsValue =
          validRequestBody(adjustments = validAdjustmentsWithNoTransitionProfitValue)

        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, validTaxYear, requestBody).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(correlationId, RuleWrongTpaAmountSubmittedError)
        )
      }
    }
    def test(error: MtdError)(body: JsValue, path: String): Unit = {
      s"passed an invalid value at $path returns $error" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
          validator(validNino, validBusinessId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, error))
      }
    }

    val badNumber = JsNumber(123.123)

    List(
      "/adjustments/includedNonTaxableProfits",
      "/adjustments/overlapReliefUsed",
      "/adjustments/accountingAdjustment",
      "/adjustments/outstandingBusinessIncome",
      "/adjustments/balancingChargeBpra",
      "/adjustments/balancingChargeOther",
      "/adjustments/goodsAndServicesOwnUse",
      "/adjustments/transitionProfitAmount",
      "/adjustments/transitionProfitAccelerationAmount",
      "/allowances/annualInvestmentAllowance",
      "/allowances/businessPremisesRenovationAllowance",
      "/allowances/capitalAllowanceMainPool",
      "/allowances/capitalAllowanceSpecialRatePool",
      "/allowances/zeroEmissionsGoodsVehicleAllowance",
      "/allowances/enhancedCapitalAllowance",
      "/allowances/allowanceOnSales",
      "/allowances/capitalAllowanceSingleAssetPool",
      "/allowances/electricChargePointAllowance",
      "/allowances/zeroEmissionsCarAllowance"
    ).foreach(path => test(ValueFormatError.withPath(path))(validRequestBody().update(path, badNumber), path))

    List(
      "/adjustments/basisAdjustment",
      "/adjustments/averagingAdjustment"
    ).foreach(path =>
      test(ValueFormatError.forPathAndRange(path, min = "-99999999999.99", max = "99999999999.99"))(validRequestBody().update(path, badNumber), path))

    test(ValueFormatError.forPathAndRange("/allowances/tradingIncomeAllowance", min = "0", max = "1000"))(
      validRequestBody(allowances = Json.obj("tradingIncomeAllowance" -> badNumber)),
      "/allowances/tradingIncomeAllowance"
    )

    test(ValueFormatError.withPath("/allowances/structuredBuildingAllowance/0/amount"))(
      validRequestBody(allowances = Json.parse(
        """
          |{
          |    "structuredBuildingAllowance": [
          |      {
          |        "amount": 1.233,
          |        "building": {
          |          "name": "Plaza 2",
          |          "postcode": "TF3 4NT"
          |        }
          |      }
          |    ]
          |}
          |""".stripMargin
      )),
      "/allowances/structuredBuildingAllowance/0/amount"
    )

    test(DateFormatError.withPath("/allowances/enhancedStructuredBuildingAllowance/0/firstYear/qualifyingDate"))(
      validRequestBody(allowances = Json.parse(
        """
          |{
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
          |}
          |""".stripMargin
      )),
      "/allowances/enhancedStructuredBuildingAllowance/0/firstYear/qualifyingDate"
    )

    test(ValueFormatError.withPath("/allowances/enhancedStructuredBuildingAllowance/0/firstYear/qualifyingAmountExpenditure"))(
      validRequestBody(allowances = Json.parse(
        """
          |{
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
          |}
          |""".stripMargin
      )),
      "/allowances/enhancedStructuredBuildingAllowance/0/firstYear/qualifyingAmountExpenditure"
    )

    "/allowances/structuredBuildingAllowance/0/building/name is invalid" in {
      val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
        validator(
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
        ).validateAndWrapResult()
      result shouldBe Left(
        ErrorWrapper(correlationId, StringFormatError.withPath("/allowances/structuredBuildingAllowance/0/building/name"))
      )
    }
    "/allowances/structuredBuildingAllowance/0/building/number is invalid" in {
      val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
        validator(
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
        ).validateAndWrapResult()
      result shouldBe Left(
        ErrorWrapper(correlationId, StringFormatError.withPath("/allowances/structuredBuildingAllowance/0/building/number"))
      )
    }
    "/allowances/structuredBuildingAllowance/0/building/postcode is invalid" in {
      val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
        validator(
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
        ).validateAndWrapResult()
      result shouldBe Left(
        ErrorWrapper(correlationId, StringFormatError.withPath("/allowances/structuredBuildingAllowance/0/building/postcode"))
      )
    }

    "/nonFinancials/class4NicsExemptionReason is invalid" in {
      val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
        validator(
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
        ).validateAndWrapResult()
      result shouldBe Left(
        ErrorWrapper(correlationId, Class4ExemptionReasonFormatError)
      )
    }

    "both allowances are supplied" in {
      val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
        validator(
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
        ).validateAndWrapResult()
      result shouldBe Left(
        ErrorWrapper(correlationId, RuleBothAllowancesSuppliedError)
      )
    }

    "name or number aren't supplied with a postcode" in {
      val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] =
        validator(
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
        ).validateAndWrapResult()
      result shouldBe Left(
        ErrorWrapper(
          correlationId,
          RuleBuildingNameNumberError.withPaths(
            Seq("/allowances/structuredBuildingAllowance/0/building", "/allowances/enhancedStructuredBuildingAllowance/0/building"))
        ))
    }

    "return multiple errors" when {
      "every field in the body is invalid" in {
        val result: Either[ErrorWrapper, CreateAmendAnnualSubmissionRequestData] = validator(
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
              |    "goodsAndServicesOwnUse": 200.132,
              |    "transitionProfitAmount": 200.132,
              |    "transitionProfitAccelerationAmount": 200.132
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
        ).validateAndWrapResult()
        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(
              DateFormatError.withPaths(Seq(
                "/allowances/structuredBuildingAllowance/0/firstYear/qualifyingDate",
                "/allowances/enhancedStructuredBuildingAllowance/0/firstYear/qualifyingDate")),
              StringFormatError.withPaths(Seq(
                "/allowances/structuredBuildingAllowance/0/building/name",
                "/allowances/structuredBuildingAllowance/0/building/postcode",
                "/allowances/enhancedStructuredBuildingAllowance/0/building/number",
                "/allowances/enhancedStructuredBuildingAllowance/0/building/postcode"
              )),
              ValueFormatError
                .forPathAndRange(path = "", min = "-99999999999.99", max = "99999999999.99")
                .withPaths(Seq("/adjustments/basisAdjustment", "/adjustments/averagingAdjustment")),
              ValueFormatError.withPaths(Seq(
                "/adjustments/includedNonTaxableProfits",
                "/adjustments/overlapReliefUsed",
                "/adjustments/accountingAdjustment",
                "/adjustments/outstandingBusinessIncome",
                "/adjustments/balancingChargeBpra",
                "/adjustments/balancingChargeOther",
                "/adjustments/goodsAndServicesOwnUse",
                "/adjustments/transitionProfitAmount",
                "/adjustments/transitionProfitAccelerationAmount",
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
              ))
            ))
          ))
      }
    }
  }

}
