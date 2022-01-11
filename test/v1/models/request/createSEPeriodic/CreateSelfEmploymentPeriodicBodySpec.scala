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

package v1.models.request.createSEPeriodic

import play.api.libs.json.Json
import support.UnitSpec

class CreateSelfEmploymentPeriodicBodySpec extends UnitSpec {

  val fullMtdModel: CreateSelfEmploymentPeriodicBody = CreateSelfEmploymentPeriodicBody(
    "2017-01-25",
    "2018-01-24",
    Some(Incomes(
      Some(IncomesAmountObject(500.25)),
      Some(IncomesAmountObject(500.25))
    )),
    Some(ConsolidatedExpenses(
      500.25
    )),
    Some(Expenses(
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25)))
    ))
  )

  val someOptionalMtdModel: CreateSelfEmploymentPeriodicBody = CreateSelfEmploymentPeriodicBody(
    "2017-01-25",
    "2018-01-24",
    Some(Incomes(
      Some(IncomesAmountObject(500.25)),
      Some(IncomesAmountObject(500.25))
    )),
    Some(ConsolidatedExpenses(
      500.25
    )),
    None
  )

  val noOptionalMtdModel: CreateSelfEmploymentPeriodicBody = CreateSelfEmploymentPeriodicBody(
    "2017-01-25",
    "2018-01-24",
    None,
    None,
    None
  )

  "reads" should {
    "read from JSON" when {
      val fullRequestJson = Json.parse(
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

      val someOptionalRequestJson = Json.parse(
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
          |   }
          |}
        """.stripMargin
      )

      val noOptionalRequestJson = Json.parse(
        """
          |{
          |   "periodFromDate": "2017-01-25",
          |   "periodToDate": "2018-01-24"
          |}
        """.stripMargin
      )

      "a valid request with all optional fields is made" in {
        fullRequestJson.as[CreateSelfEmploymentPeriodicBody] shouldBe fullMtdModel
      }

      "a valid request with some optional fields is made" in {
        someOptionalRequestJson.as[CreateSelfEmploymentPeriodicBody] shouldBe someOptionalMtdModel
      }

      "a valid request with no optional fields is made" in {
        noOptionalRequestJson.as[CreateSelfEmploymentPeriodicBody] shouldBe noOptionalMtdModel
      }

    }
  }

  "writes" should {
    "write to des" when {
      val nonConsolidatedJson = Json.parse(
        """{
          |   "from": "2017-01-25",
          |   "to": "2018-01-24",
          |   "financials": {
          |     "incomes": {
          |       "turnover":  500.25,
          |       "other": 500.25
          |     },
          |     "deductions": {
          |       "costOfGoods": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "constructionIndustryScheme": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "staffCosts": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "travelCosts": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "premisesRunningCosts": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "maintenanceCosts": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "adminCosts": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "advertisingCosts": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "businessEntertainmentCosts": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "interest": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "financialCharges": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "badDebt": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "professionalFees": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "depreciation": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       },
          |       "other": {
          |         "amount": 500.25,
          |         "disallowableAmount": 500.25
          |       }
          |     }
          |  }
          |}
        """.stripMargin
      )

      val consolidatedJson = Json.parse(
        """{
          |   "from": "2017-01-25",
          |   "to": "2018-01-24",
          |   "financials": {
          |     "incomes": {
          |       "turnover":  500.25,
          |       "other":  500.25
          |     },
          |     "deductions": {
          |       "simplifiedExpenses": 500.25
          |     }
          |   }
          |}
        """.stripMargin
      )

      "a valid request is made with non-consolidated body" in {
        Json.toJson(fullMtdModel.copy(consolidatedExpenses = None)) shouldBe nonConsolidatedJson
      }

      "a valid request is made with consolidated body" in {
        Json.toJson(fullMtdModel.copy(expenses = None)) shouldBe consolidatedJson
      }
    }
  }
}