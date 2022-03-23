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

package v1.models.request.amendPeriodic

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class ExpensesSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      |   "costOfGoodsBought":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "cisPaymentsTo":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "staffCosts":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "travelCosts":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "premisesRunningCosts":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "maintenanceCosts":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "adminCosts":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "advertisingCosts":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "interestOnLoans":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "financialCharges":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "badDebt":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "professionalFees":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "depreciation":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "other":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   }
      |}
    """.stripMargin
  )

  val desJson: JsValue = Json.parse(
    """
      |{
      |   "adminCosts":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "advertisingCosts":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "badDebt":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "constructionIndustryScheme":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "costOfGoods":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "depreciation":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "financialCharges":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "interest":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "maintenanceCosts":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "other":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "professionalFees":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "premisesRunningCosts":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "staffCosts":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   },
      |   "travelCosts":{
      |      "amount":500.25,
      |      "disallowableAmount":500.25
      |   }
      |}
    """.stripMargin
  )

  val model: Expenses = Expenses(
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    None,
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    Some(ExpensesAmountObject(500.25,Some(500.25))),
    Some(ExpensesAmountObject(500.25,Some(500.25)))
  )

  "reads" should {
    "return a model" when {
      "passed a valid json" in {
        json.as[Expenses] shouldBe model
      }
    }
  }

  "writes" should {
    "return json" when {
      "passed a valid model" in {
        Json.toJson(model) shouldBe desJson
      }
    }
  }
}