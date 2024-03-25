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

package v3.amendPeriodSummary.def1.model.request

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class Def1_Amend_PeriodExpensesSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      |        "consolidatedExpenses": 1.12,
      |        "costOfGoods": 2.12,
      |        "paymentsToSubcontractors": 3.12,
      |        "wagesAndStaffCosts": 4.12,
      |        "carVanTravelExpenses": 5.12,
      |        "premisesRunningCosts": 6.12,
      |        "maintenanceCosts": 7.12,
      |        "adminCosts": 8.12,
      |        "businessEntertainmentCosts": 9.12,
      |        "advertisingCosts": 10.12,
      |        "interestOnBankOtherLoans": 11.12,
      |        "financeCharges": 12.12,
      |        "irrecoverableDebts": 13.12,
      |        "professionalFees": 14.12,
      |        "depreciation": 15.12,
      |        "otherExpenses": 16.12
      |}
    """.stripMargin
  )

  val desJson: JsValue = Json.parse(
    """
      |{
      |        "simplifiedExpenses": 1.12,
      |        "costOfGoods": {
      |            "amount": 2.12
      |        },
      |        "constructionIndustryScheme": {
      |            "amount": 3.12
      |        },
      |        "staffCosts": {
      |            "amount": 4.12
      |        },
      |        "travelCosts": {
      |            "amount": 5.12
      |        },
      |        "premisesRunningCosts": {
      |            "amount": 6.12
      |        },
      |        "maintenanceCosts": {
      |            "amount": 7.12
      |        },
      |        "adminCosts": {
      |            "amount": 8.12
      |        },
      |        "businessEntertainmentCosts": {
      |            "amount": 9.12
      |        },
      |        "advertisingCosts": {
      |            "amount": 10.12
      |        },
      |        "interest": {
      |            "amount": 11.12
      |        },
      |        "financialCharges": {
      |            "amount": 12.12
      |        },
      |        "badDebt": {
      |            "amount": 13.12
      |        },
      |        "professionalFees": {
      |            "amount": 14.12
      |        },
      |        "depreciation": {
      |            "amount": 15.12
      |        },
      |        "other": {
      |            "amount": 16.12
      |        }
      |}
    """.stripMargin
  )

  val model: Def1_Amend_PeriodExpenses = Def1_Amend_PeriodExpenses(
    consolidatedExpenses = Some(1.12),
    costOfGoods = Some(2.12),
    paymentsToSubcontractors = Some(3.12),
    wagesAndStaffCosts = Some(4.12),
    carVanTravelExpenses = Some(5.12),
    premisesRunningCosts = Some(6.12),
    maintenanceCosts = Some(7.12),
    adminCosts = Some(8.12),
    businessEntertainmentCosts = Some(9.12),
    advertisingCosts = Some(10.12),
    interestOnBankOtherLoans = Some(11.12),
    financeCharges = Some(12.12),
    irrecoverableDebts = Some(13.12),
    professionalFees = Some(14.12),
    depreciation = Some(15.12),
    otherExpenses = Some(16.12)
  )

  "reads" should {
    "return a model" when {
      "passed a valid json" in {
        json.as[Def1_Amend_PeriodExpenses] shouldBe model
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
