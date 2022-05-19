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

class PeriodDisallowableExpensesSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      |        "costOfGoodsDisallowable": 500.25,
      |        "paymentsToSubcontractorsDisallowable": 500.25,
      |        "wagesAndStaffCostsDisallowable": 500.25,
      |        "carVanTravelExpensesDisallowable": 500.25,
      |        "premisesRunningCostsDisallowable": 500.25,
      |        "maintenanceCostsDisallowable": 500.25,
      |        "adminCostsDisallowable": 500.25,
      |        "businessEntertainmentCostsDisallowable": 500.25,
      |        "advertisingCostsDisallowable": 500.25,
      |        "interestOnBankOtherLoansDisallowable": 500.25,
      |        "financeChargesDisallowable": 500.25,
      |        "irrecoverableDebtsDisallowable": 500.25,
      |        "professionalFeesDisallowable": 500.25,
      |        "depreciationDisallowable": 500.25,
      |        "otherExpensesDisallowable": 500.25
      |}
    """.stripMargin
  )

  val desJson: JsValue = Json.parse(
    """
      |{
      |        "costOfGoods": {
      |            "disallowableAmount": 500.25
      |        },
      |        "constructionIndustryScheme": {
      |            "disallowableAmount": 500.25
      |        },
      |        "staffCosts": {
      |            "disallowableAmount": 500.25
      |        },
      |        "travelCosts": {
      |            "disallowableAmount": 500.25
      |        },
      |        "premisesRunningCosts": {
      |            "disallowableAmount": 500.25
      |        },
      |        "maintenanceCosts": {
      |            "disallowableAmount": 500.25
      |        },
      |        "adminCosts": {
      |            "disallowableAmount": 500.25
      |        },
      |        "businessEntertainmentCosts": {
      |            "disallowableAmount": 500.25
      |        },
      |        "advertisingCosts": {
      |            "disallowableAmount": 500.25
      |        },
      |        "interest": {
      |            "disallowableAmount": 500.25
      |        },
      |        "financialCharges": {
      |            "disallowableAmount": 500.25
      |        },
      |        "badDebt": {
      |            "disallowableAmount": 500.25
      |        },
      |        "professionalFees": {
      |            "disallowableAmount": 500.25
      |        },
      |        "depreciation": {
      |            "disallowableAmount": 500.25
      |        },
      |        "other": {
      |            "disallowableAmount": 500.25
      |        }
      |}
    """.stripMargin
  )

  val model: PeriodDisallowableExpenses = PeriodDisallowableExpenses(
    Some(500.25),
    Some(500.25),
    Some(500.25),
    Some(500.25),
    Some(500.25),
    Some(500.25),
    Some(500.25),
    Some(500.25),
    Some(500.25),
    Some(500.25),
    Some(500.25),
    Some(500.25),
    Some(500.25),
    Some(500.25),
    Some(500.25)
  )

  "reads" should {
    "return a model" when {
      "passed a valid json" in {
        json.as[PeriodDisallowableExpenses] shouldBe model
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
