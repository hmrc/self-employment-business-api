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

package v1.controllers.requestParsers.validators

import play.api.libs.json.{JsNumber, Json}
import support.UnitSpec
import v1.models.errors._
import v1.models.request.amendPeriodSummary.AmendPeriodSummaryRawData
import v1.models.utils.JsonErrorValidators

class AmendPeriodSummaryValidatorSpec extends UnitSpec with JsonErrorValidators {

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validPeriodId   = "2019-01-01_2019-02-02"

  private val requestBodyJson = Json.parse(
    """
      |{
      |    "periodIncome": {
      |        "turnover": 1000.99,
      |        "other": 1000.99
      |    },
      |    "periodAllowableExpenses": {
      |        "costOfGoodsAllowable": 1000.99,
      |        "paymentsToSubcontractorsAllowable": 1000.99,
      |        "wagesAndStaffCostsAllowable": 1000.99,
      |        "carVanTravelExpensesAllowable": 1000.99,
      |        "premisesRunningCostsAllowable": 1000.99,
      |        "maintenanceCostsAllowable": 1000.99,
      |        "adminCostsAllowable": 1000.99,
      |        "businessEntertainmentCostsAllowable": 1000.99,
      |        "advertisingCostsAllowable": 1000.99,
      |        "interestOnBankOtherLoansAllowable": 1000.99,
      |        "financeChargesAllowable": 1000.99,
      |        "irrecoverableDebtsAllowable": 1000.99,
      |        "professionalFeesAllowable": 1000.99,
      |        "depreciationAllowable": 1000.99,
      |        "otherExpensesAllowable": 1000.99
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

  val validator = new AmendPeriodSummaryValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied with expenses" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, requestBodyJson)) shouldBe Nil
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
                |    "periodAllowableExpenses": {
                |        "consolidatedExpenses": 1000.99
                |    }
                |}
            |""".stripMargin
            )
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
            )
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
                |    "periodAllowableExpenses": {
                |        "costOfGoodsAllowable": 1000.99,
                |        "paymentsToSubcontractorsAllowable": 1000.99,
                |        "wagesAndStaffCostsAllowable": 1000.99,
                |        "carVanTravelExpensesAllowable": 1000.99,
                |        "premisesRunningCostsAllowable": 1000.99,
                |        "maintenanceCostsAllowable": 1000.99,
                |        "adminCostsAllowable": 1000.99,
                |        "businessEntertainmentCostsAllowable": 1000.99,
                |        "advertisingCostsAllowable": 1000.99,
                |        "interestOnBankOtherLoansAllowable": 1000.99,
                |        "financeChargesAllowable": 1000.99,
                |        "irrecoverableDebtsAllowable": 1000.99,
                |        "professionalFeesAllowable": 1000.99,
                |        "depreciationAllowable": 1000.99,
                |        "otherExpensesAllowable": 1000.99
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
          )) shouldBe Nil
      }
    }
    "return a path parameter error" when {
      "an invalid nino is supplied" in {
        validator.validate(AmendPeriodSummaryRawData("A12344A", validBusinessId, validPeriodId, requestBodyJson)) shouldBe List(NinoFormatError)
      }
      "an invalid businessId is supplied" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, "Walrus", validPeriodId, requestBodyJson)) shouldBe List(BusinessIdFormatError)
      }
      "an invalid PeriodId is supplied" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, "2103/01", requestBodyJson)) shouldBe List(PeriodIdFormatError)
      }
    }
    "return RuleIncorrectOrEmptyBodyError" when {
      "an empty body is submitted" in {
        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Json.parse("""{}"""))) shouldBe List(
          RuleIncorrectOrEmptyBodyError)
      }
      "an empty income is submitted" in {
        validator.validate(
          AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Json.parse("""{"periodIncome": {}}"""))) shouldBe List(
          RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/periodIncome"))))
      }
      "an empty allowable expenses is submitted" in {
        validator.validate(
          AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Json.parse("""{"periodAllowableExpenses": {}}"""))) shouldBe List(
          RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/periodAllowableExpenses"))))
      }
      "an empty disallowable expenses is submitted" in {
        validator.validate(
          AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Json.parse("""{"periodDisallowableExpenses": {}}"""))) shouldBe List(
          RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/periodDisallowableExpenses"))))
      }
    }
    "return RuleBothExpensesSuppliedError" when {
      "Both expenses and consolidatedExpenses are supplied" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            Json.parse("""
            |{
            |    "periodIncome": {
            |        "turnover": 1000.99,
            |        "other": 1000.99
            |    },
            |    "periodAllowableExpenses": {
            |        "consolidatedExpenses": 1000.99,
            |        "costOfGoodsAllowable": 1000.99,
            |        "paymentsToSubcontractorsAllowable": 1000.99,
            |        "wagesAndStaffCostsAllowable": 1000.99,
            |        "carVanTravelExpensesAllowable": 1000.99,
            |        "premisesRunningCostsAllowable": 1000.99,
            |        "maintenanceCostsAllowable": 1000.99,
            |        "adminCostsAllowable": 1000.99,
            |        "businessEntertainmentCostsAllowable": 1000.99,
            |        "advertisingCostsAllowable": 1000.99,
            |        "interestOnBankOtherLoansAllowable": 1000.99,
            |        "financeChargesAllowable": 1000.99,
            |        "irrecoverableDebtsAllowable": 1000.99,
            |        "professionalFeesAllowable": 1000.99,
            |        "depreciationAllowable": 1000.99,
            |        "otherExpensesAllowable": 1000.99
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
            |""".stripMargin)
          )) shouldBe List(RuleBothExpensesSuppliedError)
      }
    }

    "return ValueFormatError" when {

      "/periodIncome/turnover is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodIncome/turnover", JsNumber(123.123))
          )
        ) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodIncome/turnover"))))
      }
      "/periodIncome/other is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodIncome/other", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodIncome/other"))))
      }
      "/periodAllowableExpenses/consolidatedExpenses is invalid" in {
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
                |    "periodAllowableExpenses": {
                |        "consolidatedExpenses": -1000.99
                |    }
                |}
                |""".stripMargin
            )
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/consolidatedExpenses"))))
      }
      "/periodAllowableExpenses/costOfGoodsAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/costOfGoodsAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/costOfGoodsAllowable"))))
      }

      "/periodAllowableExpenses/paymentsToSubcontractorsAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/paymentsToSubcontractorsAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/paymentsToSubcontractorsAllowable"))))
      }
      "/periodAllowableExpenses/wagesAndStaffCostsAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/wagesAndStaffCostsAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/wagesAndStaffCostsAllowable"))))
      }
      "/periodAllowableExpenses/carVanTravelExpensesAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/carVanTravelExpensesAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/carVanTravelExpensesAllowable"))))
      }

      "/periodAllowableExpenses/premisesRunningCostsAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/premisesRunningCostsAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/premisesRunningCostsAllowable"))))
      }

      "/periodAllowableExpenses/maintenanceCostsAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/maintenanceCostsAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/maintenanceCostsAllowable"))))
      }

      "/periodAllowableExpenses/adminCostsAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/adminCostsAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/adminCostsAllowable"))))
      }

      "/periodAllowableExpenses/businessEntertainmentCostsAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/businessEntertainmentCostsAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/businessEntertainmentCostsAllowable"))))
      }

      "/periodAllowableExpenses/advertisingCostsAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/advertisingCostsAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/advertisingCostsAllowable"))))
      }

      "/periodAllowableExpenses/interestOnBankOtherLoansAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/interestOnBankOtherLoansAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/interestOnBankOtherLoansAllowable"))))
      }

      "/periodAllowableExpenses/financeChargesAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/financeChargesAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/financeChargesAllowable"))))
      }

      "/periodAllowableExpenses/irrecoverableDebtsAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/irrecoverableDebtsAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/irrecoverableDebtsAllowable"))))
      }

      "/periodAllowableExpenses/professionalFeesAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/professionalFeesAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/professionalFeesAllowable"))))
      }

      "/periodAllowableExpenses/depreciationAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/depreciationAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/depreciationAllowable"))))
      }

      "/periodAllowableExpenses/otherExpensesAllowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodAllowableExpenses/otherExpensesAllowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodAllowableExpenses/otherExpensesAllowable"))))
      }

      "/periodDisallowableExpenses/costOfGoodsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/costOfGoodsDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/costOfGoodsDisallowable"))))
      }

      "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable"))))
      }

      "/periodDisallowableExpenses/wagesAndStaffCostsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/wagesAndStaffCostsDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/wagesAndStaffCostsDisallowable"))))
      }

      "/periodDisallowableExpenses/carVanTravelExpensesDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/carVanTravelExpensesDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/carVanTravelExpensesDisallowable"))))
      }

      "/periodDisallowableExpenses/premisesRunningCostsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/premisesRunningCostsDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/premisesRunningCostsDisallowable"))))
      }

      "/periodDisallowableExpenses/maintenanceCostsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/maintenanceCostsDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/maintenanceCostsDisallowable"))))
      }

      "/periodDisallowableExpenses/adminCostsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/adminCostsDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/adminCostsDisallowable"))))
      }

      "/periodDisallowableExpenses/businessEntertainmentCostsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/businessEntertainmentCostsDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/businessEntertainmentCostsDisallowable"))))
      }

      "/periodDisallowableExpenses/advertisingCostsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/advertisingCostsDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/advertisingCostsDisallowable"))))
      }

      "/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable"))))
      }

      "/periodDisallowableExpenses/financeChargesDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/financeChargesDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/financeChargesDisallowable"))))
      }

      "/periodDisallowableExpenses/irrecoverableDebtsDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/irrecoverableDebtsDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/irrecoverableDebtsDisallowable"))))
      }

      "/periodDisallowableExpenses/professionalFeesDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/professionalFeesDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/professionalFeesDisallowable"))))
      }

      "/periodDisallowableExpenses/depreciationDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/depreciationDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/depreciationDisallowable"))))
      }

      "/periodDisallowableExpenses/otherExpensesDisallowable is invalid" in {
        validator.validate(
          AmendPeriodSummaryRawData(
            validNino,
            validBusinessId,
            validPeriodId,
            requestBodyJson.update("/periodDisallowableExpenses/otherExpensesDisallowable", JsNumber(123.123))
          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/periodDisallowableExpenses/otherExpensesDisallowable"))))
      }
    }
    "return multiple errors" when {
      "every path parameter format is invalid" in {
        validator.validate(AmendPeriodSummaryRawData("AJAA12", "XASOE12", "201219", requestBodyJson)) shouldBe
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
                |    "periodAllowableExpenses": {
                |        "costOfGoodsAllowable": -1000.999,
                |        "paymentsToSubcontractorsAllowable": -1000.999,
                |        "wagesAndStaffCostsAllowable": -1000.999,
                |        "carVanTravelExpensesAllowable": -1000.999,
                |        "premisesRunningCostsAllowable": -1000.999,
                |        "maintenanceCostsAllowable": -1000.999,
                |        "adminCostsAllowable": -1000.999,
                |        "businessEntertainmentCostsAllowable": -1000.999,
                |        "advertisingCostsAllowable": -1000.999,
                |        "interestOnBankOtherLoansAllowable": -1000.999,
                |        "financeChargesAllowable": -1000.999,
                |        "irrecoverableDebtsAllowable": -1000.999,
                |        "professionalFeesAllowable": -1000.999,
                |        "depreciationAllowable": -1000.999,
                |        "otherExpensesAllowable": -1000.999
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
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(Seq(
            "/periodIncome/turnover",
            "/periodIncome/other",
            "/periodAllowableExpenses/costOfGoodsAllowable",
            "/periodAllowableExpenses/paymentsToSubcontractorsAllowable",
            "/periodAllowableExpenses/wagesAndStaffCostsAllowable",
            "/periodAllowableExpenses/carVanTravelExpensesAllowable",
            "/periodAllowableExpenses/premisesRunningCostsAllowable",
            "/periodAllowableExpenses/maintenanceCostsAllowable",
            "/periodAllowableExpenses/adminCostsAllowable",
            "/periodAllowableExpenses/businessEntertainmentCostsAllowable",
            "/periodAllowableExpenses/advertisingCostsAllowable",
            "/periodAllowableExpenses/interestOnBankOtherLoansAllowable",
            "/periodAllowableExpenses/financeChargesAllowable",
            "/periodAllowableExpenses/irrecoverableDebtsAllowable",
            "/periodAllowableExpenses/professionalFeesAllowable",
            "/periodAllowableExpenses/depreciationAllowable",
            "/periodAllowableExpenses/otherExpensesAllowable",
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
