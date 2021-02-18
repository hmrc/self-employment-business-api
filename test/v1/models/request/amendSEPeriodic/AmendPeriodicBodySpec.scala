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

package v1.models.request.amendSEPeriodic

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class AmendPeriodicBodySpec extends UnitSpec {

  val fullRequestJson: JsValue = Json.parse(
    s"""
       |{
       |    "incomes": {
       |        "turnover": {
       |            "amount": 200.00
       |        },
       |        "other": {
       |            "amount": 200.00
       |        }
       |    },
       |    "consolidatedExpenses": {
       |        "consolidatedExpenses": 200.00
       |    },
       |    "expenses": {
       |        "costOfGoodsBought": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "cisPaymentsTo": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "staffCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "travelCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "premisesRunningCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "maintenanceCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "adminCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "advertisingCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "businessEntertainmentCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "interestOnLoans": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "financialCharges": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "badDebt": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "professionalFees": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "depreciation": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "other": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        }
       |    }
       |}
       |""".stripMargin)

  val partialRequestJson: JsValue = Json.parse(
    s"""
       |{
       |    "consolidatedExpenses": {
       |        "consolidatedExpenses": 200.00
       |    },
       |    "expenses": {
       |        "costOfGoodsBought": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "cisPaymentsTo": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "staffCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "travelCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "premisesRunningCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "maintenanceCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "adminCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "advertisingCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "businessEntertainmentCosts": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "interestOnLoans": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "financialCharges": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "badDebt": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "professionalFees": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "depreciation": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        },
       |        "other": {
       |            "amount": 200.00,
       |            "disallowableAmount": 200.00
       |        }
       |    }
       |}
       |""".stripMargin)

   val emptyRequestJson: JsValue = Json.parse(
    s"""
       |{}
       |""".stripMargin)

  val fullDesJson: JsValue = Json.parse(
    s"""
       |{
       |   "incomes": {
       |      "turnover": 200.00,
       |      "other": 200.00
       |   },
       |   "deductions": {
       |      "costOfGoods": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "constructionIndustryScheme": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "staffCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "travelCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "premisesRunningCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "maintenanceCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "adminCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "advertisingCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "businessEntertainmentCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "interest": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "financialCharges": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "badDebt": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "professionalFees": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "depreciation": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "other": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "simplifiedExpenses": 200.00
       |   }
       |}
       |""".stripMargin)

  val partialDesJson: JsValue = Json.parse(
    s"""
       |{
       |   "deductions": {
       |      "costOfGoods": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "constructionIndustryScheme": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "staffCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "travelCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "premisesRunningCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "maintenanceCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "adminCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "advertisingCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "businessEntertainmentCosts": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "interest": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "financialCharges": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "badDebt": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "professionalFees": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "depreciation": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "other": {
       |         "amount": 200.00,
       |         "disallowableAmount": 200.00
       |      },
       |      "simplifiedExpenses": 200.00
       |   }
       |}
       |""".stripMargin)

  val emptyDesJson: JsValue = Json.parse(
    s"""
       |{}
       |""".stripMargin)

  val fullMtdModel = AmendPeriodicBody(
    Some(Incomes(
      Some(IncomesAmountObject(
        200.00
      )),
      Some(IncomesAmountObject(
        200.00
      ))
    )),
    Some(ConsolidatedExpenses(200.00)),
    Some(Expenses(
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00)))))
  )

  val partialMtdModel = AmendPeriodicBody(
    None,
    Some(ConsolidatedExpenses(200.00)),
    Some(Expenses(
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00))),
      Some(ExpensesAmountObject(200.00, Some(200.00)))))
  )

  val emptyMtdModel = AmendPeriodicBody(
    None,
    None,
    None
  )


  "reads" should {
    "return a model" when {
      "a valid request with all data is made" in {
        fullRequestJson.as[AmendPeriodicBody] shouldBe fullMtdModel
      }

      "a valid request with some data is made" in {
        partialRequestJson.as[AmendPeriodicBody] shouldBe partialMtdModel
      }

      "a valid request with no data is made" in {
        emptyRequestJson.as[AmendPeriodicBody] shouldBe emptyMtdModel
      }
    }
  }

  "writes" should {
    "return json" when {
      "a valid request is made with full body" in {
        Json.toJson(fullMtdModel) shouldBe fullDesJson
      }

      "a valid request is made with partial body" in {
        Json.toJson(partialMtdModel) shouldBe partialDesJson
      }

      "a valid request is made with empty body" in {
        Json.toJson(emptyMtdModel) shouldBe emptyDesJson
      }

    }
  }
}
