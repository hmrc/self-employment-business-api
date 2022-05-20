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

//
//import play.api.libs.json.Json
//import support.UnitSpec
//import v1.models.errors._
//import v1.models.request.amendPeriodic.AmendPeriodSummaryRawData
//
//class AmendPeriodicValidatorSpec extends UnitSpec {
//
//  private val validNino       = "AA123456A"
//  private val validBusinessId = "XAIS12345678901"
//  private val validPeriodId   = "2019-01-01_2019-02-02"
//
//  private val requestBodyJson = Json.parse(
//    """
//      |{
//      |    "incomes": {
//      |        "turnover": {
//      |            "amount": 200.00
//      |        },
//      |        "other": {
//      |            "amount": 200.00
//      |        }
//      |    },
//      |    "expenses": {
//      |        "costOfGoodsBought": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "cisPaymentsTo": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "staffCosts": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "travelCosts": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "premisesRunningCosts": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "maintenanceCosts": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "adminCosts": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "advertisingCosts": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "businessEntertainmentCosts": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "interestOnLoans": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "financialCharges": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "badDebt": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "professionalFees": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "depreciation": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        },
//      |        "other": {
//      |            "amount": 200.00,
//      |            "disallowableAmount": 200.00
//      |        }
//      |    }
//      |}
//      |""".stripMargin
//  )
//
//  val validator = new AmendPeriodicValidator()
//
//  "running a validation" should {
//    "return no errors" when {
//      "a valid request is supplied with expenses" in {
//        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, requestBodyJson)) shouldBe Nil
//      }
//      "a valid request is supplied with consolidated expenses" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "consolidatedExpenses": {
//            |        "consolidatedExpenses": 200.00
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe Nil
//      }
//      "only incomes is supplied" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe Nil
//      }
//      "only expenses is supplied" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe Nil
//      }
//      "only consolidatedExpenses is supplied" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "consolidatedExpenses": {
//            |        "consolidatedExpenses": 200.00
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe Nil
//      }
//    }
//    "return a path parameter error" when {
//      "an invalid nino is supplied" in {
//        validator.validate(AmendPeriodSummaryRawData("A12344A", validBusinessId, validPeriodId, requestBodyJson)) shouldBe List(NinoFormatError)
//      }
//      "an invalid businessId is supplied" in {
//        validator.validate(AmendPeriodSummaryRawData(validNino, "Walrus", validPeriodId, requestBodyJson)) shouldBe List(BusinessIdFormatError)
//      }
//      "an invalid PeriodId is supplied" in {
//        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, "2103/01", requestBodyJson)) shouldBe List(PeriodIdFormatError)
//      }
//    }
//    "return RuleIncorrectOrEmptyBodyError" when {
//      "an empty body is submitted" in {
//        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Json.parse("""{}"""))) shouldBe List(
//          RuleIncorrectOrEmptyBodyError)
//      }
//      "an empty adjustments is submitted" in {
//        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Json.parse("""{"income": {}}"""))) shouldBe List(
//          RuleIncorrectOrEmptyBodyError)
//      }
//      "an empty allowances is submitted" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Json.parse("""{"consolidatedExpenses": {}}"""))) shouldBe List(
//          RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/consolidatedExpenses/consolidatedExpenses"))))
//      }
//      "an empty nonFinancials is submitted" in {
//        validator.validate(AmendPeriodSummaryRawData(validNino, validBusinessId, validPeriodId, Json.parse("""{"expenses": {}}"""))) shouldBe List(
//          RuleIncorrectOrEmptyBodyError)
//      }
//      "a body with a mandatory field is missing is submitted" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse("""{
//          |    "incomes": {
//          |        "turnover": {
//          |            "amount": 200.00
//          |        },
//          |        "other": {
//          |            "amount": 200.00
//          |        }
//          |    },
//          |    "expenses": {
//          |        "costOfGoodsBought": {
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "cisPaymentsTo": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "staffCosts": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "travelCosts": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "premisesRunningCosts": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "maintenanceCosts": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "adminCosts": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "advertisingCosts": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "businessEntertainmentCosts": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "interestOnLoans": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "financialCharges": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "badDebt": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "professionalFees": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "depreciation": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        },
//          |        "other": {
//          |            "amount": 200.00,
//          |            "disallowableAmount": 200.00
//          |        }
//          |    }
//          |}
//          |""".stripMargin)
//          )) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(List("/expenses/costOfGoodsBought/amount"))))
//      }
//    }
//    "return RuleBothExpensesSuppliedError" when {
//      "Both expenses and consolidatedExpenses are supplied" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse("""
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "consolidatedExpenses": {
//            |        "consolidatedExpenses": 200.00
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin)
//          )) shouldBe List(RuleBothExpensesSuppliedError)
//      }
//    }
//
//    "return ValueFormatError" when {
//      "/incomes/turnover/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": -200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "consolidatedExpenses": {
//            |        "consolidatedExpenses": 200.00
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/incomes/turnover/amount"))))
//      }
//      "/incomes/other/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": -200.00
//            |        }
//            |    },
//            |    "consolidatedExpenses": {
//            |        "consolidatedExpenses": 200.00
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/incomes/other/amount"))))
//      }
//      "/consolidatedExpenses/consolidatedExpenses is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "consolidatedExpenses": {
//            |        "consolidatedExpenses": 200.046750
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/consolidatedExpenses/consolidatedExpenses"))))
//      }
//      "/expenses/costOfGoodsBought/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.07640,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/costOfGoodsBought/amount"))))
//      }
//      "/expenses/costOfGoodsBought/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.052260
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/costOfGoodsBought/disallowableAmount"))))
//      }
//      "/expenses/cisPaymentsTo/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/cisPaymentsTo/amount"))))
//      }
//      "/expenses/cisPaymentsTo/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/cisPaymentsTo/disallowableAmount"))))
//      }
//      "/expenses/staffCosts/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/staffCosts/amount"))))
//      }
//      "/expenses/staffCosts/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/staffCosts/disallowableAmount"))))
//      }
//      "/expenses/travelCosts/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/travelCosts/amount"))))
//      }
//      "/expenses/travelCosts/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/travelCosts/disallowableAmount"))))
//      }
//      "/expenses/premisesRunningCosts/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.06540,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/premisesRunningCosts/amount"))))
//      }
//      "/expenses/premisesRunningCosts/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.6534200
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/premisesRunningCosts/disallowableAmount"))))
//      }
//      "/expenses/maintenanceCosts/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.0765430,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/maintenanceCosts/amount"))))
//      }
//      "/expenses/maintenanceCosts/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.7654300
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/maintenanceCosts/disallowableAmount"))))
//      }
//      "/expenses/adminCosts/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/adminCosts/amount"))))
//      }
//      "/expenses/adminCosts/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/adminCosts/disallowableAmount"))))
//      }
//      "/expenses/advertisingCosts/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/advertisingCosts/amount"))))
//      }
//      "/expenses/advertisingCosts/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/advertisingCosts/disallowableAmount"))))
//      }
//      "/expenses/businessEntertainmentCosts/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/businessEntertainmentCosts/amount"))))
//      }
//      "/expenses/businessEntertainmentCosts/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/businessEntertainmentCosts/disallowableAmount"))))
//      }
//      "/expenses/interestOnLoans/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.06790,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/interestOnLoans/amount"))))
//      }
//      "/expenses/interestOnLoans/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.046350
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/interestOnLoans/disallowableAmount"))))
//      }
//      "/expenses/financialCharges/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.067850,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/financialCharges/amount"))))
//      }
//      "/expenses/financialCharges/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.003674
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/financialCharges/disallowableAmount"))))
//      }
//      "/expenses/badDebt/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.054680,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/badDebt/amount"))))
//      }
//      "/expenses/badDebt/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.003485
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/badDebt/disallowableAmount"))))
//      }
//      "/expenses/professionalFees/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.0093648,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/professionalFees/amount"))))
//      }
//      "/expenses/professionalFees/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/professionalFees/disallowableAmount"))))
//      }
//      "/expenses/depreciation/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.009021896,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/depreciation/amount"))))
//      }
//      "/expenses/depreciation/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.004957
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/depreciation/disallowableAmount"))))
//      }
//      "/expenses/other/amount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": 200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/other/amount"))))
//      }
//      "/expenses/other/disallowableAmount is invalid" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "adminCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": 200.00
//            |        },
//            |        "other": {
//            |            "amount": 200.00,
//            |            "disallowableAmount": -200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(ValueFormatError.copy(paths = Some(Seq("/expenses/other/disallowableAmount"))))
//      }
//    }
//    "return multiple errors" when {
//      "every path parameter format is invalid" in {
//        validator.validate(AmendPeriodSummaryRawData("AJAA12", "XASOE12", "201219", requestBodyJson)) shouldBe
//          List(NinoFormatError, BusinessIdFormatError, PeriodIdFormatError)
//      }
//      "every field in the body is invalid when consolidated expenses are supplied" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": -200.00
//            |        },
//            |        "other": {
//            |            "amount": -200.00
//            |        }
//            |    },
//            |    "consolidatedExpenses": {
//            |        "consolidatedExpenses": 200.034650
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(
//          ValueFormatError.copy(paths = Some(Seq("/incomes/turnover/amount", "/incomes/other/amount", "/consolidatedExpenses/consolidatedExpenses"))))
//      }
//      "every field in the body is invalid when expenses are supplied" in {
//        validator.validate(
//          AmendPeriodSummaryRawData(
//            validNino,
//            validBusinessId,
//            validPeriodId,
//            Json.parse(
//              """
//            |{
//            |    "incomes": {
//            |        "turnover": {
//            |            "amount": -200.00
//            |        },
//            |        "other": {
//            |            "amount": -200.00
//            |        }
//            |    },
//            |    "expenses": {
//            |        "costOfGoodsBought": {
//            |            "amount": 200.07640,
//            |            "disallowableAmount": 200.037450
//            |        },
//            |        "cisPaymentsTo": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "staffCosts": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "travelCosts": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "premisesRunningCosts": {
//            |            "amount": 200.03670,
//            |            "disallowableAmount": 200.036750
//            |        },
//            |        "maintenanceCosts": {
//            |            "amount": 200.04630,
//            |            "disallowableAmount": 200.0132540
//            |        },
//            |        "adminCosts": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "advertisingCosts": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "businessEntertainmentCosts": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "interestOnLoans": {
//            |            "amount": 200.023450,
//            |            "disallowableAmount": 200.034250
//            |        },
//            |        "financialCharges": {
//            |            "amount": 200.035420,
//            |            "disallowableAmount": 200.542300
//            |        },
//            |        "badDebt": {
//            |            "amount": 200.023450,
//            |            "disallowableAmount": 200.043520
//            |        },
//            |        "professionalFees": {
//            |            "amount": 200.034250,
//            |            "disallowableAmount": -200.00
//            |        },
//            |        "depreciation": {
//            |            "amount": 200.023450,
//            |            "disallowableAmount": 200.023450
//            |        },
//            |        "other": {
//            |            "amount": -200.00,
//            |            "disallowableAmount": -200.00
//            |        }
//            |    }
//            |}
//            |""".stripMargin
//            )
//          )) shouldBe List(
//          ValueFormatError.copy(paths = Some(Seq(
//            "/incomes/turnover/amount",
//            "/incomes/other/amount",
//            "/expenses/costOfGoodsBought/amount",
//            "/expenses/costOfGoodsBought/disallowableAmount",
//            "/expenses/cisPaymentsTo/amount",
//            "/expenses/cisPaymentsTo/disallowableAmount",
//            "/expenses/staffCosts/amount",
//            "/expenses/staffCosts/disallowableAmount",
//            "/expenses/travelCosts/amount",
//            "/expenses/travelCosts/disallowableAmount",
//            "/expenses/premisesRunningCosts/amount",
//            "/expenses/premisesRunningCosts/disallowableAmount",
//            "/expenses/maintenanceCosts/amount",
//            "/expenses/maintenanceCosts/disallowableAmount",
//            "/expenses/adminCosts/amount",
//            "/expenses/adminCosts/disallowableAmount",
//            "/expenses/advertisingCosts/amount",
//            "/expenses/advertisingCosts/disallowableAmount",
//            "/expenses/businessEntertainmentCosts/amount",
//            "/expenses/businessEntertainmentCosts/disallowableAmount",
//            "/expenses/interestOnLoans/amount",
//            "/expenses/interestOnLoans/disallowableAmount",
//            "/expenses/financialCharges/amount",
//            "/expenses/financialCharges/disallowableAmount",
//            "/expenses/badDebt/amount",
//            "/expenses/badDebt/disallowableAmount",
//            "/expenses/professionalFees/amount",
//            "/expenses/professionalFees/disallowableAmount",
//            "/expenses/depreciation/amount",
//            "/expenses/depreciation/disallowableAmount",
//            "/expenses/other/amount",
//            "/expenses/other/disallowableAmount"
//          ))))
//      }
//    }
//  }
//
//}