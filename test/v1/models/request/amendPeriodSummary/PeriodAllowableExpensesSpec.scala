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

package v1.models.request.amendPeriodSummary

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class PeriodAllowableExpensesSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      |        "consolidatedExpenses": 1.12,
      |        "costOfGoodsAllowable": 2.12,
      |        "paymentsToSubcontractorsAllowable": 3.12,
      |        "wagesAndStaffCostsAllowable": 4.12,
      |        "carVanTravelExpensesAllowable": 5.12,
      |        "premisesRunningCostsAllowable": 6.12,
      |        "maintenanceCostsAllowable": 7.12,
      |        "adminCostsAllowable": 8.12,
      |        "businessEntertainmentCostsAllowable": 9.12,
      |        "advertisingCostsAllowable": 10.12,
      |        "interestOnBankOtherLoansAllowable": 11.12,
      |        "financeChargesAllowable": 12.12,
      |        "irrecoverableDebtsAllowable": 13.12,
      |        "professionalFeesAllowable": 14.12,
      |        "depreciationAllowable": 15.12,
      |        "otherExpensesAllowable": 16.12
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

  val model: PeriodAllowableExpenses = PeriodAllowableExpenses(
    consolidatedExpenses = Some(1.12),
    costOfGoodsAllowable = Some(2.12),
    paymentsToSubcontractorsAllowable = Some(3.12),
    wagesAndStaffCostsAllowable = Some(4.12),
    carVanTravelExpensesAllowable = Some(5.12),
    premisesRunningCostsAllowable = Some(6.12),
    maintenanceCostsAllowable = Some(7.12),
    adminCostsAllowable = Some(8.12),
    businessEntertainmentCostsAllowable = Some(9.12),
    advertisingCostsAllowable = Some(10.12),
    interestOnBankOtherLoansAllowable = Some(11.12),
    financeChargesAllowable = Some(12.12),
    irrecoverableDebtsAllowable = Some(13.12),
    professionalFeesAllowable = Some(14.12),
    depreciationAllowable = Some(15.12),
    otherExpensesAllowable = Some(16.12)
  )

  "reads" should {
    "return a model" when {
      "passed a valid json" in {
        json.as[PeriodAllowableExpenses] shouldBe model
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
