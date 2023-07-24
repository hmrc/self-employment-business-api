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

package v3.fixtures

import anyVersion.models.request.amendPeriodSummary.PeriodDisallowableExpenses
import play.api.libs.json.{JsValue, Json}
import v3.models.request.amendPeriodSummary.{AmendPeriodSummaryBody, PeriodExpenses, PeriodIncome}

trait AmendPeriodSummaryFixture {

  val amendPeriodSummaryBodyMtdJson: JsValue = Json.parse(
    """
      |{
      |    "periodIncome": {
      |        "turnover": 1.12,
      |        "other": 2.12,
      |        "taxTakenOffTradingIncome": 3.12
      |    },
      |    "periodExpenses": {
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
      |    },
      |    "periodDisallowableExpenses": {
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
      |    }
      |}
    """.stripMargin
  )

  val amendPeriodSummaryBody: AmendPeriodSummaryBody = AmendPeriodSummaryBody(
    periodIncome = Some(PeriodIncome(turnover = Some(1.12), other = Some(2.12), taxTakenOffTradingIncome = Some(3.12))),
    periodExpenses = Some(
      PeriodExpenses(
        consolidatedExpenses = None,
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
      )),
    periodDisallowableExpenses = Some(
      PeriodDisallowableExpenses(
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
      ))
  )

  val amendPeriodSummaryBodyDownstreamJson: JsValue = Json.parse(
    """
      |{
      |    "incomes": {
      |        "turnover": 1.12,
      |        "other": 2.12,
      |        "taxTakenOffTradingIncome": 3.12
      |    },
      |    "deductions": {
      |        "costOfGoods": {
      |            "amount": 2.12,
      |            "disallowableAmount": 102.12
      |        },
      |        "constructionIndustryScheme": {
      |            "amount": 3.12,
      |            "disallowableAmount": 103.12
      |        },
      |        "staffCosts": {
      |            "amount": 4.12,
      |            "disallowableAmount": 104.12
      |        },
      |        "travelCosts": {
      |            "amount": 5.12,
      |            "disallowableAmount": 105.12
      |        },
      |        "premisesRunningCosts": {
      |            "amount": 6.12,
      |            "disallowableAmount": 106.12
      |        },
      |        "maintenanceCosts": {
      |            "amount": 7.12,
      |            "disallowableAmount": 107.12
      |        },
      |        "adminCosts": {
      |            "amount": 8.12,
      |            "disallowableAmount": 108.12
      |        },
      |        "businessEntertainmentCosts": {
      |            "amount": 9.12,
      |            "disallowableAmount": 109.12
      |        },
      |        "advertisingCosts": {
      |            "amount": 10.12,
      |            "disallowableAmount": 110.12
      |        },
      |        "interest": {
      |            "amount": 11.12,
      |            "disallowableAmount": 111.12
      |        },
      |        "financialCharges": {
      |            "amount": 12.12,
      |            "disallowableAmount": 112.12
      |        },
      |        "badDebt": {
      |            "amount": 13.12,
      |            "disallowableAmount": 113.12
      |        },
      |        "professionalFees": {
      |            "amount": 14.12,
      |            "disallowableAmount": 114.12
      |        },
      |        "depreciation": {
      |            "amount": 15.12,
      |            "disallowableAmount": 115.12
      |        },
      |        "other": {
      |            "amount": 16.12,
      |            "disallowableAmount": 116.12
      |        }
      |    }
      |}
    """.stripMargin
  )

  val amendPeriodSummaryConsolidatedBodyMtdJson: JsValue = Json.parse(
    """
      |{
      |    "periodIncome": {
      |        "turnover": 1.12,
      |        "other": 2.12,
      |        "taxTakenOffTradingIncome": 3.12
      |    },
      |    "periodExpenses": {
      |        "consolidatedExpenses": 1.12
      |    }
      |}
    """.stripMargin
  )

  val amendPeriodSummaryConsolidatedBody: AmendPeriodSummaryBody = AmendPeriodSummaryBody(
    periodIncome = Some(PeriodIncome(turnover = Some(1.12), other = Some(2.12), taxTakenOffTradingIncome = Some(3.12))),
    periodExpenses = Some(
      PeriodExpenses(
        consolidatedExpenses = Some(1.12),
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None,
        None
      )),
    periodDisallowableExpenses = None
  )

  val amendPeriodSummaryConsolidatedBodyDownstreamJson: JsValue = Json.parse(
    """
      |{
      |    "incomes": {
      |        "turnover": 1.12,
      |        "other": 2.12,
      |        "taxTakenOffTradingIncome": 3.12
      |    },
      |    "deductions": {
      |        "simplifiedExpenses": 1.12
      |    }
      |}
    """.stripMargin
  )

}
