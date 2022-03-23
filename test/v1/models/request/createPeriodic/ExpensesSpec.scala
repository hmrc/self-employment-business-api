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

package v1.models.request.createPeriodic

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class ExpensesSpec extends UnitSpec {

  val fullModel: Expenses = Expenses(
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
  )

  val emptyModel: Expenses = Expenses(
    None, None, None, None, None, None, None, None, None, None, None, None, None, None, None
  )

  val fullJson: JsValue = Json.parse(
    """
      |{
      |   "costOfGoodsBought": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "cisPaymentsTo": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "staffCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "travelCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "premisesRunningCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "maintenanceCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "adminCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "advertisingCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "businessEntertainmentCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "interestOnLoans": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "financialCharges": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "badDebt": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "professionalFees": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "depreciation": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "other": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   }
      |}
    """.stripMargin
  )

  val emptyJson: JsValue = Json.parse(""" {} """)

  "reads" should {
    "read from a json" when {
      "a valid request is made" in  {
        fullJson.as[Expenses] shouldBe fullModel
      }
    }

    "read from an empty json" when {
      "a valid request with an empty json" in {
        emptyJson.as[Expenses] shouldBe emptyModel
      }
    }
  }

  "writes" should {
    "write to a model" when {
      val fullDesJson = Json.parse(
        """
          |{
          |    "costOfGoods": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "constructionIndustryScheme": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "staffCosts": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "travelCosts": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "premisesRunningCosts": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "maintenanceCosts": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "adminCosts": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "advertisingCosts": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "businessEntertainmentCosts": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "interest": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "financialCharges": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "badDebt": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "professionalFees": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "depreciation": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    },
          |    "other": {
          |      "amount": 500.25,
          |      "disallowableAmount": 500.25
          |    }
          |}
        """.stripMargin
      )

      "a valid request is made" in {
        Json.toJson(fullModel) shouldBe fullDesJson
      }
    }

    "write to an empty model" when {
      "a valid request is made with an empty body" in {
        Json.toJson(emptyModel) shouldBe emptyJson
      }
    }
  }
}