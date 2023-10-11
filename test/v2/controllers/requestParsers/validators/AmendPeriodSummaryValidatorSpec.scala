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

package v2.controllers.requestParsers.validators

import api.models.errors._
import api.models.utils.JsonErrorValidators
import play.api.libs.json.{JsNumber, Json}
import support.UnitSpec
import v2.models.request.amendPeriodSummary.AmendPeriodSummaryRawData

class AmendPeriodSummaryValidatorSpec extends UnitSpec with JsonErrorValidators {

  val validator               = new AmendPeriodSummaryValidator()
  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validPeriodId   = "2019-01-01_2019-02-02"
  private val validTaxYear    = Some("2023-24")

  private val requestBodyJson = Json.parse(
    """
      |{
      |    "periodIncome": {
      |        "turnover": 1000.99,
      |        "other": 1000.99
      |    },
      |    "periodExpenses": {
      |        "costOfGoods": 1000.99,
      |        "paymentsToSubcontractors": 1000.99,
      |        "wagesAndStaffCosts": 1000.99,
      |        "carVanTravelExpenses": 1000.99,
      |        "premisesRunningCosts": 1000.99,
      |        "maintenanceCosts": 1000.99,
      |        "adminCosts": 1000.99,
      |        "businessEntertainmentCosts": 1000.99,
      |        "advertisingCosts": 1000.99,
      |        "interestOnBankOtherLoans": 1000.99,
      |        "financeCharges": 1000.99,
      |        "irrecoverableDebts": 1000.99,
      |        "professionalFees": 1000.99,
      |        "depreciation": 1000.99,
      |        "otherExpenses": 1000.99
      |    },
      |    "periodDisallowableExpenses": {
      |        "costOfGoodsDisallowable": 1000.99,
      |        "paymentsToSubcontractorsDisallowable": 1000.99,
      |        "wagesAndStaffCostsDisallowable": 1000.99,
      |        "carVanTravelExpensesDisallowable": 1000.99,
      |        "premisesRunningCostsDisallowable": 1000.99,
      |        "maintenanceCostsDisallowable": 1000.99,
      |        "adminCostsDisallowable": 1000.99,
      |        "businessEntertainmentCostsDisallowable": 1000.99,
      |        "advertisingCostsDisallowable": 1000.99,
      |        "interestOnBankOtherLoansDisallowable": 1000.99,
      |        "financeChargesDisallowable": 1000.99,
      |        "irrecoverableDebtsDisallowable": 1000.99,
      |        "professionalFeesDisallowable": 1000.99,
      |        "depreciationDisallowable": 1000.99,
      |        "otherExpensesDisallowable": 1000.99
      |    }
      |}
      |""".stripMargin
  )

  private val requestBodyWithNegativesJson = Json.parse(
    """
      |{
      |    "periodIncome": {
      |        "turnover": 1000.99,
      |        "other": 1000.99
      |    },
      |    "periodExpenses": {
      |        "costOfGoods": -1000.99,
      |        "paymentsToSubcontractors": -1000.99,
      |        "wagesAndStaffCosts": -1000.99,
      |        "carVanTravelExpenses": -1000.99,
      |        "premisesRunningCosts": -1000.99,
      |        "maintenanceCosts": -1000.99,
      |        "adminCosts": -1000.99,
      |        "businessEntertainmentCosts": -1000.99,
      |        "advertisingCosts": -1000.99,
      |        "interestOnBankOtherLoans": -1000.99,
      |        "financeCharges": -1000.99,
      |        "irrecoverableDebts": -1000.99,
      |        "professionalFees": -1000.99,
      |        "depreciation": -1000.99,
      |        "otherExpenses": -1000.99
      |    },
      |    "periodDisallowableExpenses": {
      |        "costOfGoodsDisallowable": -1000.99,
      |        "paymentsToSubcontractorsDisallowable": -1000.99,
      |        "wagesAndStaffCostsDisallowable": -1000.99,
      |        "carVanTravelExpensesDisallowable": -1000.99,
      |        "premisesRunningCostsDisallowable": -1000.99,
      |        "maintenanceCostsDisallowable": -1000.99,
      |        "adminCostsDisallowable": -1000.99,
      |        "businessEntertainmentCostsDisallowable": -1000.99,
      |        "advertisingCostsDisallowable": -1000.99,
      |        "interestOnBankOtherLoansDisallowable": -1000.99,
      |        "financeChargesDisallowable": -1000.99,
      |        "irrecoverableDebtsDisallowable": -1000.99,
      |        "professionalFeesDisallowable": -1000.99,
      |        "depreciationDisallowable": -1000.99,
      |        "otherExpensesDisallowable": -1000.99
      |    }
      |}
      |""".stripMargin
  )

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied with expenses" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, requestBodyJson, None)) shouldBe Nil
      }
      "a valid request is supplied with negative expenses and includeNegatives is true" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyWithNegativesJson,
            None,
            includeNegatives = true)) shouldBe Nil
      }
      "a valid TYS request is supplied with expenses" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, requestBodyJson, validTaxYear)) shouldBe Nil
      }

      "a valid request is supplied with consolidated expenses" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            Json.parse(
              """
                |{
                |    "periodIncome": {
                |        "turnover": 1000.99,
                |        "other": 1000.99
                |    },
                |    "periodExpenses": {
                |        "consolidatedExpenses": 1000.99
                |    }
                |}
            |""".stripMargin
            ),
            None
          )) shouldBe Nil
      }
      "only periodIncome is supplied" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            Json.parse(
              """
                |{
                |    "periodIncome": {
                |        "turnover": 1000.99,
                |        "other": 1000.99
                |    }
                |}
            |""".stripMargin
            ),
            None
          )) shouldBe Nil
      }
      "only expenses is supplied" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            Json.parse(
              """
                |{
                |    "periodExpenses": {
                |        "costOfGoods": 1000.99,
                |        "paymentsToSubcontractors": 1000.99,
                |        "wagesAndStaffCosts": 1000.99,
                |        "carVanTravelExpenses": 1000.99,
                |        "premisesRunningCosts": 1000.99,
                |        "maintenanceCosts": 1000.99,
                |        "adminCosts": 1000.99,
                |        "businessEntertainmentCosts": 1000.99,
                |        "advertisingCosts": 1000.99,
                |        "interestOnBankOtherLoans": 1000.99,
                |        "financeCharges": 1000.99,
                |        "irrecoverableDebts": 1000.99,
                |        "professionalFees": 1000.99,
                |        "depreciation": 1000.99,
                |        "otherExpenses": 1000.99
                |    },
                |    "periodDisallowableExpenses": {
                |        "costOfGoodsDisallowable": 1000.99,
                |        "paymentsToSubcontractorsDisallowable": 1000.99,
                |        "wagesAndStaffCostsDisallowable": 1000.99,
                |        "carVanTravelExpensesDisallowable": 1000.99,
                |        "premisesRunningCostsDisallowable": 1000.99,
                |        "maintenanceCostsDisallowable": 1000.99,
                |        "adminCostsDisallowable": 1000.99,
                |        "businessEntertainmentCostsDisallowable": 1000.99,
                |        "advertisingCostsDisallowable": 1000.99,
                |        "interestOnBankOtherLoansDisallowable": 1000.99,
                |        "financeChargesDisallowable": 1000.99,
                |        "irrecoverableDebtsDisallowable": 1000.99,
                |        "professionalFeesDisallowable": 1000.99,
                |        "depreciationDisallowable": 1000.99,
                |        "otherExpensesDisallowable": 1000.99
                |    }
                |}
            |""".stripMargin
            ),
            None
          )) shouldBe Nil
      }
    }
    "return a path parameter error" when {
      "an invalid nino is supplied" in {
        validator.validate(AmendPeriodSummaryRawData("A12344A", validBusinessId, validPeriodId, requestBodyJson, None)) shouldBe List(NinoFormatError)
      }
      "an invalid businessId is supplied" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, "Walrus", validPeriodId, requestBodyJson, None)) shouldBe List(BusinessIdFormatError)
      }
      "an invalid PeriodId is supplied" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, "2103/01", requestBodyJson, None)) shouldBe List(PeriodIdFormatError)
      }
      "a PeriodId containing an out of range date is supplied" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, "0010-01-01_2019-02-02", requestBodyJson, None)) shouldBe List(PeriodIdFormatError)
      }
    }
    "return RuleIncorrectOrEmptyBodyError" when {
      "an empty body is submitted" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Json.parse("""{}"""), None)) shouldBe List(
          RuleIncorrectOrEmptyBodyError)
      }
      "an empty income is submitted" in {
        validator.validate(
          AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Json.parse("""{"periodIncome": {}}"""), None)) shouldBe List(
          RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/periodIncome"))))
      }
      "an empty period expenses is submitted" in {
        validator.validate(
          AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Json.parse("""{"periodExpenses": {}}"""), None)) shouldBe List(
          RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/periodExpenses"))))
      }
      "an empty disallowable expenses is submitted" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            Json.parse("""{"periodDisallowableExpenses": {}}"""),
            None)) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/periodDisallowableExpenses"))))
      }
    }

    "return TaxYearFormatError error" when {
      "an invalid tax year format is supplied" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, requestBodyJson, Some("202324"))) shouldBe
          List(TaxYearFormatError)
      }
    }

    "return InvalidTaxYearParameter" when {
      "an invalid tax year is supplied" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, requestBodyJson, Some("2022-23"))) shouldBe
          List(InvalidTaxYearParameterError)
      }
    }

    "return RuleTaxYearRangeInvalidError error" when {
      "an invalid tax year range is supplied" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, requestBodyJson, Some("2023-26"))) shouldBe
          List(RuleTaxYearRangeInvalidError)
      }
    }

    "return ValueFormatError" when {

      "/periodIncome/turnover is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodIncome/turnover", JsNumber(123.123)),
            None
          )
        ) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodIncome/turnover"))))
      }
      "/periodIncome/other is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodIncome/other", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodIncome/other"))))
      }

      "/periodExpenses/consolidatedExpenses is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            Json.parse(
              """
                |{
                |    "periodIncome": {
                |        "turnover": 1000.99,
                |        "other": 1000.99
                |    },
                |    "periodExpenses": {
                |        "consolidatedExpenses": -1000.99
                |    }
                |}
                |""".stripMargin
            ),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/consolidatedExpenses"))))
      }
      "/periodExpenses/costOfGoods is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/costOfGoods", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/costOfGoods"))))
      }

      "/periodExpenses/paymentsToSubcontractors is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/paymentsToSubcontractors", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/paymentsToSubcontractors"))))
      }
      "/periodExpenses/wagesAndStaffCosts is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/wagesAndStaffCosts", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/wagesAndStaffCosts"))))
      }
      "/periodExpenses/carVanTravelExpenses is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/carVanTravelExpenses", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/carVanTravelExpenses"))))
      }

      "/periodExpenses/premisesRunningCosts is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/premisesRunningCosts", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/premisesRunningCosts"))))
      }

      "/periodExpenses/maintenanceCosts is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/maintenanceCosts", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/maintenanceCosts"))))
      }

      "/periodExpenses/adminCosts is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/adminCosts", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/adminCosts"))))
      }

      "/periodExpenses/businessEntertainmentCosts is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/businessEntertainmentCosts", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/businessEntertainmentCosts"))))
      }

      "/periodExpenses/advertisingCosts is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/advertisingCosts", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/advertisingCosts"))))
      }

      "/periodExpenses/interestOnBankOtherLoans is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/interestOnBankOtherLoans", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/interestOnBankOtherLoans"))))
      }

      "/periodExpenses/financeCharges is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/financeCharges", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/financeCharges"))))
      }

      "/periodExpenses/irrecoverableDebts is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/irrecoverableDebts", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/irrecoverableDebts"))))
      }

      "/periodExpenses/professionalFees is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/professionalFees", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/professionalFees"))))
      }

      "/periodExpenses/depreciation is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/depreciation", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/depreciation"))))
      }

      "/periodExpenses/otherExpenses is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodExpenses/otherExpenses", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodExpenses/otherExpenses"))))
      }

      "/periodDisallowableExpenses/costOfGoodsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/costOfGoodsDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/costOfGoodsDisallowable"))))
      }

      "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable"))))
      }

      "/periodDisallowableExpenses/wagesAndStaffCostsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/wagesAndStaffCostsDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/wagesAndStaffCostsDisallowable"))))
      }

      "/periodDisallowableExpenses/carVanTravelExpensesDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/carVanTravelExpensesDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/carVanTravelExpensesDisallowable"))))
      }

      "/periodDisallowableExpenses/premisesRunningCostsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/premisesRunningCostsDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/premisesRunningCostsDisallowable"))))
      }

      "/periodDisallowableExpenses/maintenanceCostsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/maintenanceCostsDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/maintenanceCostsDisallowable"))))
      }

      "/periodDisallowableExpenses/adminCostsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/adminCostsDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/adminCostsDisallowable"))))
      }

      "/periodDisallowableExpenses/businessEntertainmentCostsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/businessEntertainmentCostsDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/businessEntertainmentCostsDisallowable"))))
      }

      "/periodDisallowableExpenses/advertisingCostsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/advertisingCostsDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/advertisingCostsDisallowable"))))
      }

      "/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable"))))
      }

      "/periodDisallowableExpenses/financeChargesDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/financeChargesDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/financeChargesDisallowable"))))
      }

      "/periodDisallowableExpenses/irrecoverableDebtsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/irrecoverableDebtsDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/irrecoverableDebtsDisallowable"))))
      }

      "/periodDisallowableExpenses/professionalFeesDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/professionalFeesDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/professionalFeesDisallowable"))))
      }

      "/periodDisallowableExpenses/depreciationDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/depreciationDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/depreciationDisallowable"))))
      }

      "/periodDisallowableExpenses/otherExpensesDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/otherExpensesDisallowable", JsNumber(123.123)),
            None
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/otherExpensesDisallowable"))))
      }

      "negative field in periodExpenses and periodDisallowableExpenses are provided and includeNegatives is false" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, requestBodyWithNegativesJson, None)) shouldBe List(
          ValueFormatError.copy(paths = Some(Seq(
            "/periodExpenses/paymentsToSubcontractors",
            "/periodExpenses/wagesAndStaffCosts",
            "/periodExpenses/carVanTravelExpenses",
            "/periodExpenses/adminCosts",
            "/periodExpenses/businessEntertainmentCosts",
            "/periodExpenses/advertisingCosts",
            "/periodExpenses/otherExpenses",
            "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable",
            "/periodDisallowableExpenses/wagesAndStaffCostsDisallowable",
            "/periodDisallowableExpenses/carVanTravelExpensesDisallowable",
            "/periodDisallowableExpenses/adminCostsDisallowable",
            "/periodDisallowableExpenses/businessEntertainmentCostsDisallowable",
            "/periodDisallowableExpenses/advertisingCostsDisallowable",
            "/periodDisallowableExpenses/professionalFeesDisallowable",
            "/periodDisallowableExpenses/otherExpensesDisallowable"
          ))))
      }
    }
    "return multiple errors" when {
      "every path parameter format is invalid" in {
        validator.validate(AmendPeriodSummaryRawData("AJAA12", "XASOE12", "201219", requestBodyJson, None)) shouldBe
          List(NinoFormatError, BusinessIdFormatError, PeriodIdFormatError)
      }
      "every field in the body is invalid when expenses are supplied" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            Json.parse(
              """
                |{
                |    "periodIncome": {
                |        "turnover": -1000.999,
                |        "other": -1000.999
                |    },
                |    "periodExpenses": {
                |        "costOfGoods": -1000.999,
                |        "paymentsToSubcontractors": -1000.999,
                |        "wagesAndStaffCosts": -1000.999,
                |        "carVanTravelExpenses": -1000.999,
                |        "premisesRunningCosts": -1000.999,
                |        "maintenanceCosts": -1000.999,
                |        "adminCosts": -1000.999,
                |        "businessEntertainmentCosts": -1000.999,
                |        "advertisingCosts": -1000.999,
                |        "interestOnBankOtherLoans": -1000.999,
                |        "financeCharges": -1000.999,
                |        "irrecoverableDebts": -1000.999,
                |        "professionalFees": -1000.999,
                |        "depreciation": -1000.999,
                |        "otherExpenses": -1000.999
                |    },
                |    "periodDisallowableExpenses": {
                |        "costOfGoodsDisallowable": -1000.999,
                |        "paymentsToSubcontractorsDisallowable": -1000.999,
                |        "wagesAndStaffCostsDisallowable": -1000.999,
                |        "carVanTravelExpensesDisallowable": -1000.999,
                |        "premisesRunningCostsDisallowable": -1000.999,
                |        "maintenanceCostsDisallowable": -1000.999,
                |        "adminCostsDisallowable": -1000.999,
                |        "businessEntertainmentCostsDisallowable": -1000.999,
                |        "advertisingCostsDisallowable": -1000.999,
                |        "interestOnBankOtherLoansDisallowable": -1000.999,
                |        "financeChargesDisallowable": -1000.999,
                |        "irrecoverableDebtsDisallowable": -1000.999,
                |        "professionalFeesDisallowable": -1000.999,
                |        "depreciationDisallowable": -1000.999,
                |        "otherExpensesDisallowable": -1000.999
                |    }
                |}
                |""".stripMargin
            ),
            None
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(Seq(
            "/periodIncome/turnover",
            "/periodIncome/other",
            "/periodExpenses/costOfGoods",
            "/periodExpenses/paymentsToSubcontractors",
            "/periodExpenses/wagesAndStaffCosts",
            "/periodExpenses/carVanTravelExpenses",
            "/periodExpenses/premisesRunningCosts",
            "/periodExpenses/maintenanceCosts",
            "/periodExpenses/adminCosts",
            "/periodExpenses/businessEntertainmentCosts",
            "/periodExpenses/advertisingCosts",
            "/periodExpenses/interestOnBankOtherLoans",
            "/periodExpenses/financeCharges",
            "/periodExpenses/irrecoverableDebts",
            "/periodExpenses/professionalFees",
            "/periodExpenses/depreciation",
            "/periodExpenses/otherExpenses",
            "/periodDisallowableExpenses/costOfGoodsDisallowable",
            "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable",
            "/periodDisallowableExpenses/wagesAndStaffCostsDisallowable",
            "/periodDisallowableExpenses/carVanTravelExpensesDisallowable",
            "/periodDisallowableExpenses/premisesRunningCostsDisallowable",
            "/periodDisallowableExpenses/maintenanceCostsDisallowable",
            "/periodDisallowableExpenses/adminCostsDisallowable",
            "/periodDisallowableExpenses/businessEntertainmentCostsDisallowable",
            "/periodDisallowableExpenses/advertisingCostsDisallowable",
            "/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable",
            "/periodDisallowableExpenses/financeChargesDisallowable",
            "/periodDisallowableExpenses/irrecoverableDebtsDisallowable",
            "/periodDisallowableExpenses/professionalFeesDisallowable",
            "/periodDisallowableExpenses/depreciationDisallowable",
            "/periodDisallowableExpenses/otherExpensesDisallowable"
          ))))
      }
    }
  }

}
