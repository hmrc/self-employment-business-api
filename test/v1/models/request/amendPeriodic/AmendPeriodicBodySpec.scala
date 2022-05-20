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

class AmendPeriodicBodySpec extends UnitSpec {

  val fullRequestJson: JsValue = Json.parse(
    """
      |{
      |    "periodIncome": {
      |        "turnover": 200.00,
      |        "other": 200.00
      |    },
      |    "periodAllowableExpenses": {
      |        "consolidatedExpenses": 200.00,
      |        "costOfGoodsAllowable": 200.00,
      |        "paymentsToSubcontractorsAllowable": 200.00,
      |        "wagesAndStaffCostsAllowable": 200.00,
      |        "carVanTravelExpensesAllowable": 200.00,
      |        "premisesRunningCostsAllowable": 200.00,
      |        "maintenanceCostsAllowable": 200.00,
      |        "adminCostsAllowable": 200.00,
      |        "businessEntertainmentCostsAllowable": 200.00,
      |        "advertisingCostsAllowable": 200.00,
      |        "interestOnBankOtherLoansAllowable": 200.00,
      |        "financeChargesAllowable": 200.00,
      |        "irrecoverableDebtsAllowable": 200.00,
      |        "professionalFeesAllowable": 200.00,
      |        "depreciationAllowable": 200.00,
      |        "otherExpensesAllowable": 200.00
      |    },
      |    "periodDisallowableExpenses": {
      |        "costOfGoodsDisallowable": 200.00,
      |        "paymentsToSubcontractorsDisallowable": 200.00,
      |        "wagesAndStaffCostsDisallowable": 200.00,
      |        "carVanTravelExpensesDisallowable": 200.00,
      |        "premisesRunningCostsDisallowable": 200.00,
      |        "maintenanceCostsDisallowable": 200.00,
      |        "adminCostsDisallowable": 200.00,
      |        "businessEntertainmentCostsDisallowable": 200.00,
      |        "advertisingCostsDisallowable": 200.00,
      |        "interestOnBankOtherLoansDisallowable": 200.00,
      |        "financeChargesDisallowable": 200.00,
      |        "irrecoverableDebtsDisallowable": 200.00,
      |        "professionalFeesDisallowable": 200.00,
      |        "depreciationDisallowable": 200.00,
      |        "otherExpensesDisallowable": 200.00
      |    }
      |}
    """.stripMargin
  )

  val partialRequestJson: JsValue = Json.parse(
    """
      |{
      |    "periodAllowableExpenses": {
      |        "consolidatedExpenses": 200.00,
      |        "costOfGoodsAllowable": 200.00,
      |        "paymentsToSubcontractorsAllowable": 200.00,
      |        "wagesAndStaffCostsAllowable": 200.00,
      |        "carVanTravelExpensesAllowable": 200.00,
      |        "premisesRunningCostsAllowable": 200.00,
      |        "maintenanceCostsAllowable": 200.00,
      |        "adminCostsAllowable": 200.00,
      |        "businessEntertainmentCostsAllowable": 200.00,
      |        "advertisingCostsAllowable": 200.00,
      |        "interestOnBankOtherLoansAllowable": 200.00,
      |        "financeChargesAllowable": 200.00,
      |        "irrecoverableDebtsAllowable": 200.00,
      |        "professionalFeesAllowable": 200.00,
      |        "depreciationAllowable": 200.00,
      |        "otherExpensesAllowable": 200.00
      |    },
      |    "periodDisallowableExpenses": {
      |        "costOfGoodsDisallowable": 200.00,
      |        "paymentsToSubcontractorsDisallowable": 200.00,
      |        "wagesAndStaffCostsDisallowable": 200.00,
      |        "carVanTravelExpensesDisallowable": 200.00,
      |        "premisesRunningCostsDisallowable": 200.00,
      |        "maintenanceCostsDisallowable": 200.00,
      |        "adminCostsDisallowable": 200.00,
      |        "businessEntertainmentCostsDisallowable": 200.00,
      |        "advertisingCostsDisallowable": 200.00,
      |        "interestOnBankOtherLoansDisallowable": 200.00,
      |        "financeChargesDisallowable": 200.00,
      |        "irrecoverableDebtsDisallowable": 200.00,
      |        "professionalFeesDisallowable": 200.00,
      |        "depreciationDisallowable": 200.00,
      |        "otherExpensesDisallowable": 200.00
      |    }
      |}
    """.stripMargin
  )

  val emptyJson: JsValue = Json.parse(
    """
      |{}
    """.stripMargin
  )

  val fullDesJson: JsValue = Json.parse(
    """
      |{
      |    "incomes": {
      |        "turnover": 200.00,
      |        "other": 200.00
      |    },
      |    "deductions": {
      |        "costOfGoods": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "constructionIndustryScheme": {
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
      |        "businessEntertainmentCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "advertisingCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "interest": {
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
      |        },
      |        "simplifiedExpenses": 200.00
      |    }
      |}
    """.stripMargin
  )

  val partialDesJson: JsValue = Json.parse(
    """
      |{
      |   "deductions": {
      |        "costOfGoods": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "constructionIndustryScheme": {
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
      |        "businessEntertainmentCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "advertisingCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "interest": {
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
      |        },
      |        "simplifiedExpenses": 200.00
      |    }
      |}
    """.stripMargin
  )

  val fullMtdModel: AmendPeriodicBody = AmendPeriodicBody(
    Some(
      PeriodIncome(
        Some(200.00),
        Some(200.00)
      )),
    Some(
      PeriodAllowableExpenses(
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00)
      )),
    Some(
      PeriodDisallowableExpenses(
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00)
      ))
  )

  val partialMtdModel: AmendPeriodicBody = AmendPeriodicBody(
    None,
    Some(
      PeriodAllowableExpenses(
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00)
      )),
    Some(
      PeriodDisallowableExpenses(
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00),
        Some(200.00)
      ))
  )

  val emptyMtdModel: AmendPeriodicBody = AmendPeriodicBody(
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
        emptyJson.as[AmendPeriodicBody] shouldBe emptyMtdModel
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
        Json.toJson(emptyMtdModel) shouldBe emptyJson
      }
    }
  }

}
