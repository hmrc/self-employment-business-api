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

package fixtures

import play.api.libs.json.{JsValue, Json}
import v1.models.request.createPeriodSummary._

trait CreatePeriodSummaryFixture {

  val requestMtdBodyJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |    "periodIncome": {
      |      "turnover": 1000.99,
      |      "other": 1000.99
      |    },
      |    "periodAllowableExpenses": {
      |      "costOfGoodsAllowable": 1000.99,
      |      "paymentsToSubcontractorsAllowable": 1000.99,
      |      "wagesAndStaffCostsAllowable": 1000.99,
      |      "carVanTravelExpensesAllowable": 1000.99,
      |      "premisesRunningCostsAllowable": -99999.99,
      |      "maintenanceCostsAllowable": -1000.99,
      |      "adminCostsAllowable": 1000.99,
      |      "businessEntertainmentCostsAllowable": 1000.99,
      |      "advertisingCostsAllowable": 1000.99,
      |      "interestOnBankOtherLoansAllowable": -1000.99,
      |      "financeChargesAllowable": -1000.99,
      |      "irrecoverableDebtsAllowable": -1000.99,
      |      "professionalFeesAllowable": -99999999999.99,
      |      "depreciationAllowable": -1000.99,
      |      "otherExpensesAllowable": 1000.99
      |    },
      |    "periodDisallowableExpenses": {
      |      "costOfGoodsDisallowable": 1000.99,
      |      "paymentsToSubcontractorsDisallowable": 1000.99,
      |      "wagesAndStaffCostsDisallowable": 1000.99,
      |      "carVanTravelExpensesDisallowable": 1000.99,
      |      "premisesRunningCostsDisallowable": -1000.99,
      |      "maintenanceCostsDisallowable": -999.99,
      |      "adminCostsDisallowable": 1000.99,
      |      "businessEntertainmentCostsDisallowable": 1000.99,
      |      "advertisingCostsDisallowable": 1000.99,
      |      "interestOnBankOtherLoansDisallowable": -1000.99,
      |      "financeChargesDisallowable": -9999.99,
      |      "irrecoverableDebtsDisallowable": 1000.99,
      |      "professionalFeesDisallowable": 9999999999.99,
      |      "depreciationDisallowable": -99999999999.99,
      |      "otherExpensesDisallowable": 1000.99
      |     }
      |}
    """.stripMargin
  )

  val requestMtdFullBodyJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |    "periodIncome": {
      |      "turnover": 1000.99,
      |      "other": 1000.99
      |    },
      |    "periodAllowableExpenses": {
      |      "consolidatedExpenses": 1000.99,
      |      "costOfGoodsAllowable": 1000.99,
      |      "paymentsToSubcontractorsAllowable": 1000.99,
      |      "wagesAndStaffCostsAllowable": 1000.99,
      |      "carVanTravelExpensesAllowable": 1000.99,
      |      "premisesRunningCostsAllowable": -99999.99,
      |      "maintenanceCostsAllowable": -1000.99,
      |      "adminCostsAllowable": 1000.99,
      |      "businessEntertainmentCostsAllowable": 1000.99,
      |      "advertisingCostsAllowable": 1000.99,
      |      "interestOnBankOtherLoansAllowable": -1000.99,
      |      "financeChargesAllowable": -1000.99,
      |      "irrecoverableDebtsAllowable": -1000.99,
      |      "professionalFeesAllowable": -99999999999.99,
      |      "depreciationAllowable": -1000.99,
      |      "otherExpensesAllowable": 1000.99
      |    },
      |    "periodDisallowableExpenses": {
      |      "costOfGoodsDisallowable": 1000.99,
      |      "paymentsToSubcontractorsDisallowable": 1000.99,
      |      "wagesAndStaffCostsDisallowable": 1000.99,
      |      "carVanTravelExpensesDisallowable": 1000.99,
      |      "premisesRunningCostsDisallowable": -1000.99,
      |      "maintenanceCostsDisallowable": -999.99,
      |      "adminCostsDisallowable": 1000.99,
      |      "businessEntertainmentCostsDisallowable": 1000.99,
      |      "advertisingCostsDisallowable": 1000.99,
      |      "interestOnBankOtherLoansDisallowable": -1000.99,
      |      "financeChargesDisallowable": -9999.99,
      |      "irrecoverableDebtsDisallowable": 1000.99,
      |      "professionalFeesDisallowable": 9999999999.99,
      |      "depreciationDisallowable": -99999999999.99,
      |      "otherExpensesDisallowable": 1000.99
      |     }
      |}
    """.stripMargin
  )

  val mtdMimimumFieldsJson: JsValue = Json.parse(
    """
        |{
        |   "periodDates": {
        |     "periodStartDate": "2019-08-24",
        |     "periodEndDate": "2019-08-24"
        |   }
        |}
      """.stripMargin
  )

  val mtdIncomeOnlyJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |    "periodIncome": {
      |      "turnover": 1000.99,
      |      "other": 1000.09
      |    }
      |}
    """.stripMargin
  )

  val mtdExpensesValidBodyJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |    "periodIncome": {
      |      "turnover": 1000.99,
      |      "other": 1000.99
      |    },
      |    "periodAllowableExpenses": {
      |      "costOfGoodsAllowable": 1000.99,
      |      "paymentsToSubcontractorsAllowable": 1000.99,
      |      "wagesAndStaffCostsAllowable": 1000.99,
      |      "carVanTravelExpensesAllowable": 1000.99,
      |      "premisesRunningCostsAllowable": -99999.99,
      |      "maintenanceCostsAllowable": -1000.99,
      |      "adminCostsAllowable": 1000.99,
      |      "businessEntertainmentCostsAllowable": 1000.99,
      |      "advertisingCostsAllowable": 1000.99,
      |      "interestOnBankOtherLoansAllowable": -1000.99,
      |      "financeChargesAllowable": -1000.99,
      |      "irrecoverableDebtsAllowable": -1000.99,
      |      "professionalFeesAllowable": -99999999999.99,
      |      "depreciationAllowable": -1000.99,
      |      "otherExpensesAllowable": 1000.99
      |    },
      |    "periodDisallowableExpenses": {
      |      "costOfGoodsDisallowable": 1000.99,
      |      "paymentsToSubcontractorsDisallowable": 1000.99,
      |      "wagesAndStaffCostsDisallowable": 1000.99,
      |      "carVanTravelExpensesDisallowable": 1000.99,
      |      "premisesRunningCostsDisallowable": -1000.99,
      |      "maintenanceCostsDisallowable": -999.99,
      |      "adminCostsDisallowable": 1000.99,
      |      "businessEntertainmentCostsDisallowable": 1000.99,
      |      "advertisingCostsDisallowable": 1000.99,
      |      "interestOnBankOtherLoansDisallowable": -1000.99,
      |      "financeChargesDisallowable": -9999.99,
      |      "irrecoverableDebtsDisallowable": 1000.99,
      |      "professionalFeesDisallowable": 9999999999.99,
      |      "depreciationDisallowable": -99999999999.99,
      |      "otherExpensesDisallowable": 1000.99
      |     }
      |}
    """.stripMargin
  )

  val mtdAllowableConsolidatedExpensesOnlyJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |   "periodIncome": {
      |      "turnover": 1000.99,
      |      "other": 1000.09
      |    },
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |    "periodAllowableExpenses": {
      |      "consolidatedExpenses": 1000.99,
      |      "costOfGoodsAllowable": 1000.99,
      |      "paymentsToSubcontractorsAllowable": 1000.99,
      |      "wagesAndStaffCostsAllowable": 1000.99,
      |      "carVanTravelExpensesAllowable": 1000.99,
      |      "premisesRunningCostsAllowable": -99999.99,
      |      "maintenanceCostsAllowable": -1000.99,
      |      "adminCostsAllowable": 1000.99,
      |      "businessEntertainmentCostsAllowable": 1000.99,
      |      "advertisingCostsAllowable": 1000.99,
      |      "interestOnBankOtherLoansAllowable": -1000.99,
      |      "financeChargesAllowable": -1000.99,
      |      "irrecoverableDebtsAllowable": -1000.99,
      |      "professionalFeesAllowable": -99999999999.99,
      |      "depreciationAllowable": -1000.99,
      |      "otherExpensesAllowable": 1000.99
      |    }
      |}
    """.stripMargin
  )

  val mtdDisallowableConsolidatedExpensesOnlyJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |   "periodIncome": {
      |      "turnover": 1000.99,
      |      "other": 1000.09
      |    },
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |    "periodAllowableExpenses": {
      |      "consolidatedExpenses": 1000.99
      |    },
      |    "periodDisallowableExpenses": {
      |      "costOfGoodsDisallowable": 1000.99,
      |      "paymentsToSubcontractorsDisallowable": 1000.99,
      |      "wagesAndStaffCostsDisallowable": 1000.99,
      |      "carVanTravelExpensesDisallowable": 1000.99,
      |      "premisesRunningCostsDisallowable": -1000.99,
      |      "maintenanceCostsDisallowable": -999.99,
      |      "adminCostsDisallowable": 1000.99,
      |      "businessEntertainmentCostsDisallowable": 1000.99,
      |      "advertisingCostsDisallowable": 1000.99,
      |      "interestOnBankOtherLoansDisallowable": -1000.99,
      |      "financeChargesDisallowable": -9999.99,
      |      "irrecoverableDebtsDisallowable": 1000.99,
      |      "professionalFeesDisallowable": 9999999999.99,
      |      "depreciationDisallowable": -99999999999.99,
      |      "otherExpensesDisallowable": 1000.99
      |     }
      |}
    """.stripMargin
  )

  val mtdAllowableExpensesOnlyJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |   "periodIncome": {
      |      "turnover": 1000.99,
      |      "other": 1000.09
      |    },
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |    "periodAllowableExpenses": {
      |      "costOfGoodsAllowable": 1000.99,
      |      "paymentsToSubcontractorsAllowable": 1000.99,
      |      "wagesAndStaffCostsAllowable": 1000.99,
      |      "carVanTravelExpensesAllowable": 1000.99,
      |      "premisesRunningCostsAllowable": -99999.99,
      |      "maintenanceCostsAllowable": -1000.99,
      |      "adminCostsAllowable": 1000.99,
      |      "businessEntertainmentCostsAllowable": 1000.99,
      |      "advertisingCostsAllowable": 1000.99,
      |      "interestOnBankOtherLoansAllowable": -1000.99,
      |      "financeChargesAllowable": -1000.99,
      |      "irrecoverableDebtsAllowable": -1000.99,
      |      "professionalFeesAllowable": -99999999999.99,
      |      "depreciationAllowable": -1000.99,
      |      "otherExpensesAllowable": 1000.99
      |    }
      |}
    """.stripMargin
  )

  val mtdDisallowableExpensesOnlyJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |   "periodIncome": {
      |      "turnover": 1000.99,
      |      "other": 1000.09
      |    },
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |    "periodDisallowableExpenses": {
      |      "costOfGoodsDisallowable": 1000.99,
      |      "paymentsToSubcontractorsDisallowable": 1000.99,
      |      "wagesAndStaffCostsDisallowable": 1000.99,
      |      "carVanTravelExpensesDisallowable": 1000.99,
      |      "premisesRunningCostsDisallowable": -1000.99,
      |      "maintenanceCostsDisallowable": -999.99,
      |      "adminCostsDisallowable": 1000.99,
      |      "businessEntertainmentCostsDisallowable": 1000.99,
      |      "advertisingCostsDisallowable": 1000.99,
      |      "interestOnBankOtherLoansDisallowable": -1000.99,
      |      "financeChargesDisallowable": -9999.99,
      |      "irrecoverableDebtsDisallowable": 1000.99,
      |      "professionalFeesDisallowable": 9999999999.99,
      |      "depreciationDisallowable": -99999999999.99,
      |      "otherExpensesDisallowable": 1000.99
      |     }
      |}
    """.stripMargin
  )

  val requestConsolidatedMtdJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2019-08-24"
      |    },
      |    "periodIncome": {
      |      "turnover": 1000.99,
      |      "other": 1000.09
      |    },
      |    "periodAllowableExpenses": {
      |      "consolidatedExpenses": 999999999.99
      |    }
      |}
    """.stripMargin
  )

  val requestDownstreamBodyJson: JsValue = Json.parse(
    """
      |{
      |   "from": "2019-08-24",
      |   "to": "2019-08-24",
      |   "financials": {
      |      "incomes": {
      |         "turnover": 200.00,
      |         "other": 200.00
      |      },
      |      "deductions": {
      |         "costOfGoods": {
      |            "amount": -200.00,
      |            "disallowableAmount": -200.00
      |         },
      |         "constructionIndustryScheme": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |         },
      |         "staffCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |         },
      |         "travelCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |         },
      |         "premisesRunningCosts": {
      |            "amount": -200.00,
      |            "disallowableAmount": -200.00
      |         },
      |         "maintenanceCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |         },
      |         "adminCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |         },
      |        "businessEntertainmentCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |         "advertisingCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |         },
      |         "interest": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |         },
      |         "financialCharges": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |         },
      |         "badDebt": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |         },
      |         "professionalFees": {
      |            "amount": -200.00,
      |            "disallowableAmount": 200.00
      |         },
      |         "depreciation": {
      |            "amount": -200.00,
      |            "disallowableAmount": -200.00
      |         },
      |         "other": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |         }
      |      }
      |   }
      |}
    """.stripMargin
  )

  val requestConsolidatedDownstreamJson: JsValue = Json.parse(
    """
      |{
      |   "from": "2019-08-24",
      |   "to": "2019-08-24",
      |   "financials": {
      |      "incomes": {
      |         "turnover": 200.00,
      |         "other": 200.00
      |      },
      |      "deductions": {
      |         "simplifiedExpenses": 199.00
      |      }
      |   }
      |}
    """.stripMargin
  )

  val periodDatesMTDModel: PeriodDates = PeriodDates(
    periodStartDate = "2019-08-24",
    periodEndDate = "2019-08-24"
  )

  val periodIncomeMTDModel: PeriodIncome = PeriodIncome(
    turnover = Some(1000.99),
    other = Some(1000.99)
  )

  val periodAllowableExpensesModel: PeriodAllowableExpenses = PeriodAllowableExpenses(
    consolidatedExpenses = None,
    costOfGoodsAllowable = Some(1000.99),
    paymentsToSubcontractorsAllowable = Some(1000.99),
    wagesAndStaffCostsAllowable = Some(1000.99),
    carVanTravelExpensesAllowable = Some(1000.99),
    premisesRunningCostsAllowable = Some(-99999.99),
    maintenanceCostsAllowable = Some(-1000.99),
    adminCostsAllowable = Some(1000.99),
    businessEntertainmentCostsAllowable = Some(1000.99),
    advertisingCostsAllowable = Some(1000.99),
    interestOnBankOtherLoansAllowable = Some(-1000.99),
    financeChargesAllowable = Some(-1000.99),
    irrecoverableDebtsAllowable = Some(-1000.99),
    professionalFeesAllowable = Some(-99999999999.99),
    depreciationAllowable = Some(-1000.99),
    otherExpensesAllowable = Some(1000.99)
  )

  val periodDisallowableExpensesModel: PeriodDisallowableExpenses = PeriodDisallowableExpenses(
    costOfGoodsDisallowable = Some(1000.99),
    paymentsToSubcontractorsDisallowable = Some(1000.99),
    wagesAndStaffCostsDisallowable = Some(1000.99),
    carVanTravelExpensesDisallowable = Some(1000.99),
    premisesRunningCostsDisallowable = Some(-1000.99),
    maintenanceCostsDisallowable = Some(-999.99),
    adminCostsDisallowable = Some(1000.99),
    businessEntertainmentCostsDisallowable = Some(1000.99),
    advertisingCostsDisallowable = Some(1000.99),
    interestOnBankOtherLoansDisallowable = Some(-1000.99),
    financeChargesDisallowable = Some(-9999.99),
    irrecoverableDebtsDisallowable = Some(1000.99),
    professionalFeesDisallowable = Some(9999999999.99),
    depreciationDisallowable = Some(-99999999999.99),
    otherExpensesDisallowable = Some(1000.99)
  )

  val fullMTDResponseModel: CreatePeriodSummaryBody = CreatePeriodSummaryBody(
    periodDates = periodDatesMTDModel,
    periodIncome = Some(periodIncomeMTDModel),
    periodAllowableExpenses = Some(periodAllowableExpensesModel),
    periodDisallowableExpenses = Some(periodDisallowableExpensesModel)
  )

}
