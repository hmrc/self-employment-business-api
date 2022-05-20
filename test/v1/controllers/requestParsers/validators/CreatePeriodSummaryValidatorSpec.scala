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

import play.api.libs.json.Json
import support.UnitSpec
import v1.models.errors._
import v1.models.request.createPeriodSummary.CreatePeriodSummaryRawData

class CreatePeriodValidatorSpec extends UnitSpec {

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"

  val validator = new CreatePeriodSummaryValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid expenses request is supplied" in {
        validator.validate(CreatePeriodSummaryRawData(validNino, validBusinessId, requestBodyJson)) shouldBe Nil
      }

      "a valid consolidated expenses request is supplied" in {
        validator.validate(CreatePeriodSummaryRawData(validNino, validBusinessId, requestBodyConsolidatedExpensesJson)) shouldBe Nil
      }

      "the minimum fields are supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodDates": {
            |     "periodStartDate": "2019-08-24",
            |     "periodEndDate": "2019-08-24"
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe Nil
      }

      "only incomes are supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodDates": {
            |     "periodStartDate": "2019-08-24",
            |     "periodEndDate": "2019-08-24"
            |    },
            |    "periodIncome": {
            |       "turnover": 1000.99,
            |       "other": 1000.09
            |     }
            |}
          """.stripMargin
            )
          )) shouldBe Nil
      }

      "only expenses are supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |
            |{
            |     "periodDates": {
            |           "periodStartDate": "2019-08-24",
            |           "periodEndDate": "2019-08-24"
            |     },
            |     "periodIncome": {
            |          "turnover": 1000.99,
            |          "other": 1000.09
            |     },
            |     "periodAllowableExpenses": {
            |          "costOfGoodsAllowable": 1000.99,
            |          "paymentsToSubcontractorsAllowable": 1000.99,
            |          "wagesAndStaffCostsAllowable": 1000.99,
            |          "carVanTravelExpensesAllowable": 1000.99,
            |          "premisesRunningCostsAllowable": -99999.99,
            |          "maintenanceCostsAllowable": -1000.99,
            |          "adminCostsAllowable": 1000.99,
            |          "businessEntertainmentCostsAllowable": 1000.99,
            |          "advertisingCostsAllowable": 1000.99,
            |          "interestOnBankOtherLoansAllowable": -1000.99,
            |          "financeChargesAllowable": -1000.99,
            |          "irrecoverableDebtsAllowable": -1000.99,
            |          "professionalFeesAllowable": -99999999999.99,
            |          "depreciationAllowable": -1000.99,
            |          "otherExpensesAllowable": 1000.99
            |      },
            |     "periodDisallowableExpenses": {
            |          "costOfGoodsDisallowable": 91000.99,
            |          "paymentsToSubcontractorsDisallowable": 1000.99,
            |          "wagesAndStaffCostsDisallowable": 1000.99,
            |          "carVanTravelExpensesDisallowable": 1000.99,
            |          "premisesRunningCostsDisallowable": -1000.99,
            |          "maintenanceCostsDisallowable": -999.99,
            |          "adminCostsDisallowable": 1000.99,
            |          "businessEntertainmentCostsDisallowable": 1000.99,
            |          "advertisingCostsDisallowable": 1000.99,
            |          "interestOnBankOtherLoansDisallowable": -1000.99,
            |          "financeChargesDisallowable": -9999.99,
            |          "irrecoverableDebtsDisallowable": 1000.99,
            |          "professionalFeesDisallowable": -99999999999.99,
            |          "depreciationDisallowable": -99999999999.99,
            |          "otherExpensesDisallowable": 1000.99
            |      }
            |}
          """.stripMargin
            )
          )) shouldBe Nil
      }

      "only consolidatedExpenses are supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodDates": {
            |     "periodStartDate": "2019-08-24",
            |     "periodEndDate": "2019-08-24"
            |     },
            |     "periodIncome": {
            |       "turnover": 1000.99,
            |       "other": 1000.09
            |     },
            |     "periodAllowableExpenses": {
            |       "consolidatedExpenses": 99999999999.99
            |      },
            |     "periodDisallowableExpenses": {
            |       "costOfGoodsDisallowable": 91000.99,
            |       "paymentsToSubcontractorsDisallowable": 1000.99,
            |       "wagesAndStaffCostsDisallowable": 1000.99,
            |       "carVanTravelExpensesDisallowable": 1000.99,
            |       "premisesRunningCostsDisallowable": -1000.99,
            |       "maintenanceCostsDisallowable": 1000.99,
            |       "adminCostsDisallowable": 1000.99,
            |       "businessEntertainmentCostsDisallowable": 1000.99,
            |       "advertisingCostsDisallowable": 1000.99,
            |       "interestOnBankOtherLoansDisallowable": 1000.99,
            |       "financeChargesDisallowable": -9999999.99,
            |       "irrecoverableDebtsDisallowable": 1000.99,
            |       "professionalFeesDisallowable": 1000.99,
            |       "depreciationDisallowable": 1000.99,
            |       "otherExpensesDisallowable": 1000.99
            |     }
            |}
          """.stripMargin
            )
          )) shouldBe Nil
      }
    }

    "return a path parameter error" when {
      "an invalid nino is supplied" in {
        validator.validate(CreatePeriodSummaryRawData("walrus", validBusinessId, requestBodyJson)) shouldBe List(NinoFormatError)
      }

      "an invalid businessId is supplied" in {
        validator.validate(CreatePeriodSummaryRawData(validNino, "beans", requestBodyJson)) shouldBe List(BusinessIdFormatError)
      }
    }

    "return RuleIncorrectOrEmptyBodyError" when {
      "an empty body is supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """{}""".stripMargin
            ))) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "an empty Incomes object is supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {}
            |}
          """.stripMargin
            )
          )) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }

      "an empty Expenses object is supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "expenses": {}
            |}
          """.stripMargin
            )
          )) shouldBe List(RuleIncorrectOrEmptyBodyError)
      }
    }

    "return date errors" when {
      "an invalid fromDate is supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2319-123",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "consolidatedExpenses": {
            |     "consolidatedExpenses": 500.25
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(FromDateFormatError)
      }

      "an invalid toDate is supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "3129921",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "consolidatedExpenses": {
            |     "consolidatedExpenses": 500.25
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(ToDateFormatError)
      }

      "toDate is before fromDate" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2020-01-25",
            |   "periodToDate": "2019-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "consolidatedExpenses": {
            |     "consolidatedExpenses": 500.25
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(RuleToDateBeforeFromDateError)
      }

      "both dates are invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-312325",
            |   "periodToDate": "2018-0229224",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "consolidatedExpenses": {
            |     "consolidatedExpenses": 500.25
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(FromDateFormatError, ToDateFormatError)
      }
    }

    "return RuleBothExpensesSuppliedError" when {
      "expenses and consolidatedExpenses are supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "consolidatedExpenses": {
            |     "consolidatedExpenses": 500.25
            |   },
            |   "expenses": {
            |     "costOfGoodsBought": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "cisPaymentsTo": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "staffCosts": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "travelCosts": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "premisesRunningCosts": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "maintenanceCosts": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "adminCosts": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "advertisingCosts": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "businessEntertainmentCosts": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "interestOnLoans": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "financialCharges": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "badDebt": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "professionalFees": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "depreciation": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(RuleBothExpensesSuppliedError)
      }
    }

    "return ValueFormatError" when {
      "/incomes/turnover/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": -500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/incomes/turnover/amount"
            )))
        )
      }

      "/incomes/other/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "other": {
            |       "amount": -500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/incomes/other/amount"
            )))
        )
      }

      "/expenses/costOfGoodsBought/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "costOfGoodsBought": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/costOfGoodsBought/amount"
            )))
        )
      }

      "/expenses/costOfGoodsBought/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "costOfGoodsBought": {
            |       "amount": 500.25,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/costOfGoodsBought/disallowableAmount"
            )))
        )
      }

      "/expenses/cisPaymentsTo/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "cisPaymentsTo": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/cisPaymentsTo/amount"
            )))
        )
      }

      "/expenses/cisPaymentsTo/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "cisPaymentsTo": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/cisPaymentsTo/disallowableAmount"
            )))
        )
      }

      "/expenses/staffCosts/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "staffCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/staffCosts/amount"
            )))
        )
      }

      "/expenses/staffCosts/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "staffCosts": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/staffCosts/disallowableAmount"
            )))
        )
      }

      "/expenses/travelCosts/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "travelCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/travelCosts/amount"
            )))
        )
      }

      "/expenses/travelCosts/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "travelCosts": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/travelCosts/disallowableAmount"
            )))
        )
      }

      "/expenses/premisesRunningCosts/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "premisesRunningCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/premisesRunningCosts/amount"
            )))
        )
      }

      "/expenses/premisesRunningCosts/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "premisesRunningCosts": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/premisesRunningCosts/disallowableAmount"
            )))
        )
      }

      "/expenses/maintenanceCosts/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "maintenanceCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/maintenanceCosts/amount"
            )))
        )
      }

      "/expenses/maintenanceCosts/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "maintenanceCosts": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/maintenanceCosts/disallowableAmount"
            )))
        )
      }

      "/expenses/adminCosts/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "adminCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/adminCosts/amount"
            )))
        )
      }

      "/expenses/adminCosts/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "adminCosts": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/adminCosts/disallowableAmount"
            )))
        )
      }

      "/expenses/advertisingCosts/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "advertisingCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/advertisingCosts/amount"
            )))
        )
      }

      "/expenses/advertisingCosts/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "advertisingCosts": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/advertisingCosts/disallowableAmount"
            )))
        )
      }

      "/expenses/businessEntertainmentCosts/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "businessEntertainmentCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/businessEntertainmentCosts/amount"
            )))
        )
      }

      "/expenses/businessEntertainmentCosts/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "businessEntertainmentCosts": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/businessEntertainmentCosts/disallowableAmount"
            )))
        )
      }

      "/expenses/interestOnLoans/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "interestOnLoans": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/interestOnLoans/amount"
            )))
        )
      }

      "/expenses/interestOnLoans/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "interestOnLoans": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/interestOnLoans/disallowableAmount"
            )))
        )
      }

      "/expenses/financialCharges/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "financialCharges": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/financialCharges/amount"
            )))
        )
      }

      "/expenses/financialCharges/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "financialCharges": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/financialCharges/disallowableAmount"
            )))
        )
      }

      "/expenses/badDebt/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "badDebt": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/badDebt/amount"
            )))
        )
      }

      "/expenses/badDebt/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "badDebt": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/badDebt/disallowableAmount"
            )))
        )
      }

      "/expenses/professionalFees/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "professionalFees": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/professionalFees/amount"
            )))
        )
      }

      "/expenses/professionalFees/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "professionalFees": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/professionalFees/disallowableAmount"
            )))
        )
      }

      "/expenses/depreciation/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "depreciation": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/depreciation/amount"
            )))
        )
      }

      "/expenses/depreciation/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "depreciation": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/depreciation/disallowableAmount"
            )))
        )
      }

      "/expenses/other/amount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "other": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 500.25
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/other/amount"
            )))
        )
      }

      "/expenses/other/disallowableAmount is invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 500.25
            |     },
            |     "other": {
            |       "amount": 500.25
            |     }
            |   },
            |   "expenses": {
            |     "other": {
            |       "amount": 500.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(
            Seq(
              "/expenses/other/disallowableAmount"
            )))
        )
      }
    }

    "return all errors" when {
      "all path parameters are invalid" in {
        validator.validate(CreatePeriodSummaryRawData("walrus", "beans", requestBodyJson)) shouldBe
          List(NinoFormatError, BusinessIdFormatError)
      }

      "all fields are invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
            |{
            |   "periodFromDate": "2017-01-25",
            |   "periodToDate": "2018-01-24",
            |   "incomes": {
            |     "turnover": {
            |       "amount": 100000000000.99
            |     },
            |     "other": {
            |       "amount": 100000000000.99
            |     }
            |   },
            |   "expenses": {
            |     "costOfGoodsBought": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "cisPaymentsTo": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "staffCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "travelCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "premisesRunningCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "maintenanceCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "adminCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "advertisingCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "businessEntertainmentCosts": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "interestOnLoans": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "financialCharges": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "badDebt": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "professionalFees": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "depreciation": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     },
            |     "other": {
            |       "amount": 100000000000.99,
            |       "disallowableAmount": 100000000000.99
            |     }
            |   }
            |}
          """.stripMargin
            )
          )) shouldBe List(
          ValueFormatError.copy(paths = Some(Seq(
            "/incomes/turnover/amount",
            "/incomes/other/amount",
            "/expenses/costOfGoodsBought/amount",
            "/expenses/costOfGoodsBought/disallowableAmount",
            "/expenses/cisPaymentsTo/amount",
            "/expenses/cisPaymentsTo/disallowableAmount",
            "/expenses/staffCosts/amount",
            "/expenses/staffCosts/disallowableAmount",
            "/expenses/travelCosts/amount",
            "/expenses/travelCosts/disallowableAmount",
            "/expenses/premisesRunningCosts/amount",
            "/expenses/premisesRunningCosts/disallowableAmount",
            "/expenses/maintenanceCosts/amount",
            "/expenses/maintenanceCosts/disallowableAmount",
            "/expenses/adminCosts/amount",
            "/expenses/adminCosts/disallowableAmount",
            "/expenses/advertisingCosts/amount",
            "/expenses/advertisingCosts/disallowableAmount",
            "/expenses/businessEntertainmentCosts/amount",
            "/expenses/businessEntertainmentCosts/disallowableAmount",
            "/expenses/interestOnLoans/amount",
            "/expenses/interestOnLoans/disallowableAmount",
            "/expenses/financialCharges/amount",
            "/expenses/financialCharges/disallowableAmount",
            "/expenses/badDebt/amount",
            "/expenses/badDebt/disallowableAmount",
            "/expenses/professionalFees/amount",
            "/expenses/professionalFees/disallowableAmount",
            "/expenses/depreciation/amount",
            "/expenses/depreciation/disallowableAmount",
            "/expenses/other/amount",
            "/expenses/other/disallowableAmount"
          )))
        )
      }
    }
  }

}
