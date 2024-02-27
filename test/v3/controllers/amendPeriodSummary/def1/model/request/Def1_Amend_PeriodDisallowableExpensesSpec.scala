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

package v3.controllers.amendPeriodSummary.def1.model.request

import play.api.libs.json.{JsValue, Json}
import support.UnitSpec

class Def1_Amend_PeriodDisallowableExpensesSpec extends UnitSpec {

  val json: JsValue = Json.parse(
    """
      |{
      |        "costOfGoodsDisallowable": 102.12,
      |        "paymentsToSubcontractorsDisallowable": 103.12,
      |        "wagesAndStaffCostsDisallowable": 104.12,
      |        "carVanTravelExpensesDisallowable": 105.12,
      |        "premisesRunningCostsDisallowable": 106.12,
      |        "maintenanceCostsDisallowable": 107.12,
      |        "adminCostsDisallowable": 108.12,
      |        "businessEntertainmentCostsDisallowable": 109.12,
      |        "advertisingCostsDisallowable": 110.12,
      |        "interestOnBankOtherLoansDisallowable": 111.12,
      |        "financeChargesDisallowable": 112.12,
      |        "irrecoverableDebtsDisallowable": 113.12,
      |        "professionalFeesDisallowable": 114.12,
      |        "depreciationDisallowable": 115.12,
      |        "otherExpensesDisallowable": 116.12
      |}
    """.stripMargin
  )

  val desJson: JsValue = Json.parse(
    """
      |{
      |        "costOfGoods": {
      |            "disallowableAmount": 102.12
      |        },
      |        "constructionIndustryScheme": {
      |            "disallowableAmount": 103.12
      |        },
      |        "staffCosts": {
      |            "disallowableAmount": 104.12
      |        },
      |        "travelCosts": {
      |            "disallowableAmount": 105.12
      |        },
      |        "premisesRunningCosts": {
      |            "disallowableAmount": 106.12
      |        },
      |        "maintenanceCosts": {
      |            "disallowableAmount": 107.12
      |        },
      |        "adminCosts": {
      |            "disallowableAmount": 108.12
      |        },
      |        "businessEntertainmentCosts": {
      |            "disallowableAmount": 109.12
      |        },
      |        "advertisingCosts": {
      |            "disallowableAmount": 110.12
      |        },
      |        "interest": {
      |            "disallowableAmount": 111.12
      |        },
      |        "financialCharges": {
      |            "disallowableAmount": 112.12
      |        },
      |        "badDebt": {
      |            "disallowableAmount": 113.12
      |        },
      |        "professionalFees": {
      |            "disallowableAmount": 114.12
      |        },
      |        "depreciation": {
      |            "disallowableAmount": 115.12
      |        },
      |        "other": {
      |            "disallowableAmount": 116.12
      |        }
      |}
    """.stripMargin
  )

  val model: Def1_Amend_PeriodDisallowableExpenses = Def1_Amend_PeriodDisallowableExpenses(
    costOfGoodsDisallowable = Some(102.12),
    paymentsToSubcontractorsDisallowable = Some(103.12),
    wagesAndStaffCostsDisallowable = Some(104.12),
    carVanTravelExpensesDisallowable = Some(105.12),
    premisesRunningCostsDisallowable = Some(106.12),
    maintenanceCostsDisallowable = Some(107.12),
    adminCostsDisallowable = Some(108.12),
    businessEntertainmentCostsDisallowable = Some(109.12),
    advertisingCostsDisallowable = Some(110.12),
    interestOnBankOtherLoansDisallowable = Some(111.12),
    financeChargesDisallowable = Some(112.12),
    irrecoverableDebtsDisallowable = Some(113.12),
    professionalFeesDisallowable = Some(114.12),
    depreciationDisallowable = Some(115.12),
    otherExpensesDisallowable = Some(116.12)
  )

  "reads" should {
    "return a model" when {
      "passed a valid json" in {
        json.as[Def1_Amend_PeriodDisallowableExpenses] shouldBe model
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
