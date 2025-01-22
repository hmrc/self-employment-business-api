/*
 * Copyright 2024 HM Revenue & Customs
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

package v5.retrieveCumulativePeriodSummary.def1.model

import play.api.libs.json.{JsValue, Json}

trait Def1_RetrieveCumulativePeriodSummaryFixture {

  val nino       = "AA111111A"
  val businessId = "id"

  val def1_DownstreamFullJson: JsValue = Json.parse(
    """
      |{
      |  "selfEmploymentPeriodDates": {
      |    "periodStartDate": "2025-06-07",
      |    "periodEndDate": "2025-08-10"
      |  },
      |  "selfEmploymentPeriodIncome": {
      |    "turnover": 3100.00,
      |    "other": 3200.00,
      |    "taxTakenOffTradingIncome": 3000.99
      |  },
      |  "selfEmploymentPeriodDeductions": {
      |    "costOfGoods": {
      |      "amount": -900.00,
      |      "disallowableAmount": -1000.00
      |    },
      |    "constructionIndustryScheme": {
      |      "amount": -700.00,
      |      "disallowableAmount": -800.00
      |    },
      |    "staffCosts": {
      |      "amount": -2500.00,
      |      "disallowableAmount": -2600.00
      |    },
      |    "travelCosts": {
      |      "amount": -2700.00,
      |      "disallowableAmount": -2800.00
      |    },
      |    "premisesRunningCosts": {
      |      "amount": -2300.00,
      |      "disallowableAmount": -2400.00
      |    },
      |    "maintenanceCosts": {
      |      "amount": -1700.00,
      |      "disallowableAmount": -1800.00
      |    },
      |    "adminCosts": {
      |      "amount": 100.00,
      |      "disallowableAmount": 200.00
      |    },
      |    "businessEntertainmentCosts": {
      |      "amount": 2900.00,
      |      "disallowableAmount": 3000.00
      |    },
      |    "advertisingCosts": {
      |      "amount": 300.00,
      |      "disallowableAmount": 400.00
      |    },
      |    "interest": {
      |      "amount": 1500.00,
      |      "disallowableAmount": 1600.00
      |    },
      |    "financialCharges": {
      |      "amount": 1300.00,
      |      "disallowableAmount": 1400.00
      |    },
      |    "badDebt": {
      |      "amount": 500.00,
      |      "disallowableAmount": 600.00
      |    },
      |    "professionalFees": {
      |      "amount": 2100.00,
      |      "disallowableAmount": 2200.00
      |    },
      |    "depreciation": {
      |      "amount": 1100.00,
      |      "disallowableAmount": 1200.00
      |    },
      |    "other": {
      |      "amount": 1900.00,
      |      "disallowableAmount": 2000.00
      |    }
      |  }
      |}
      |""".stripMargin
  )

  val def1_MtdFullJson: JsValue = Json.parse(
    """
      |{
      |  "periodDates":{
      |      "periodStartDate": "2025-06-07",
      |      "periodEndDate": "2025-08-10"
      |   },
      |   "periodIncome":{
      |      "turnover":3100.00,
      |      "other":3200.00,
      |      "taxTakenOffTradingIncome":3000.99
      |   },
      |   "periodExpenses":{
      |      "costOfGoods":-900.00,
      |      "paymentsToSubcontractors":-700.00,
      |      "wagesAndStaffCosts":-2500.00,
      |      "carVanTravelExpenses":-2700.00,
      |      "premisesRunningCosts":-2300.00,
      |      "maintenanceCosts":-1700.00,
      |      "adminCosts":100.00,
      |      "businessEntertainmentCosts":2900.00,
      |      "advertisingCosts":300.00,
      |      "interestOnBankOtherLoans":1500.00,
      |      "financeCharges":1300.00,
      |      "irrecoverableDebts":500.00,
      |      "professionalFees":2100.00,
      |      "depreciation":1100.00,
      |      "otherExpenses":1900.00
      |   },
      |   "periodDisallowableExpenses":{
      |      "costOfGoodsDisallowable":-1000.00,
      |      "paymentsToSubcontractorsDisallowable":-800.00,
      |      "wagesAndStaffCostsDisallowable":-2600.00,
      |      "carVanTravelExpensesDisallowable":-2800.00,
      |      "premisesRunningCostsDisallowable":-2400.00,
      |      "maintenanceCostsDisallowable":-1800.00,
      |      "adminCostsDisallowable":200.00,
      |      "businessEntertainmentCostsDisallowable":3000.00,
      |      "advertisingCostsDisallowable":400.00,
      |      "interestOnBankOtherLoansDisallowable":1600.00,
      |      "financeChargesDisallowable":1400.00,
      |      "irrecoverableDebtsDisallowable":600.00,
      |      "professionalFeesDisallowable":2200.00,
      |      "depreciationDisallowable":1200.00,
      |      "otherExpensesDisallowable":2000.00
      |   }
      |}
      |""".stripMargin
  )

  val def1_DownstreamConsolidatedJson: JsValue = Json.parse(
    """
      |{
      |  "selfEmploymentPeriodDates": {
      |    "periodStartDate": "2025-06-07",
      |    "periodEndDate": "2025-08-10"
      |  },
      |  "selfEmploymentPeriodIncome": {
      |    "turnover": 100.00,
      |    "other": 200.00,
      |    "taxTakenOffTradingIncome": 300.99
      |  },
      |  "selfEmploymentPeriodDeductions": {
      |    "consolidatedExpenses": 666.66
      |  }
      |}
      |""".stripMargin
  )

  val def1_MtdConsolidatedJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates":{
      |      "periodStartDate": "2025-06-07",
      |      "periodEndDate": "2025-08-10"
      |   },
      |   "periodIncome":{
      |      "turnover":100.00,
      |      "other":200.00,
      |      "taxTakenOffTradingIncome": 300.99
      |   },
      |   "periodExpenses":{
      |      "consolidatedExpenses":666.66
      |   }
      |}
      |""".stripMargin
  )

  val def1_DownstreamMinimalJson: JsValue = Json.parse(
    """
      |{
      |  "selfEmploymentPeriodDates": {
      |    "periodStartDate": "2025-06-07",
      |    "periodEndDate": "2025-08-10"
      |  }
      |}
      |""".stripMargin
  )

  val def1_MtdMinimalJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates":{
      |      "periodStartDate": "2025-06-07",
      |      "periodEndDate": "2025-08-10"
      |   }
      |}
      |""".stripMargin
  )

}
