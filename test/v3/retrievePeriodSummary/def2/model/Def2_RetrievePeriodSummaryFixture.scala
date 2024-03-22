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

package v3.retrievePeriodSummary.def2.model

import api.models.domain.TaxYear
import play.api.libs.json.{JsValue, Json}

trait Def2_RetrievePeriodSummaryFixture {

  val nino       = "AA111111A"
  val businessId = "id"
  val periodId   = "periodId"
  val taxYear    = TaxYear.fromMtd("2023-24")

  val def2_DownstreamFullJson: JsValue = Json.parse(
    """
      |{
      |   "from": "2019-08-24",
      |   "to": "2020-08-24",
      |   "financials": {
      |      "deductions": {
      |         "adminCosts": {
      |            "amount": 100.00,
      |            "disallowableAmount": 200.00
      |         },
      |         "advertisingCosts": {
      |            "amount": 300.00,
      |            "disallowableAmount": 400.00
      |         },
      |         "badDebt": {
      |            "amount": 500.00,
      |            "disallowableAmount": 600.00
      |         },
      |         "constructionIndustryScheme": {
      |            "amount": 700.00,
      |            "disallowableAmount": 800.00
      |         },
      |         "costOfGoods": {
      |            "amount": 900.00,
      |            "disallowableAmount": 1000.00
      |         },
      |         "depreciation": {
      |            "amount": 1100.00,
      |            "disallowableAmount": 1200.00
      |         },
      |         "financialCharges": {
      |            "amount": 1300.00,
      |            "disallowableAmount": 1400.00
      |         },
      |         "interest": {
      |            "amount": 1500.00,
      |            "disallowableAmount": 1600.00
      |         },
      |         "maintenanceCosts": {
      |            "amount": 1700.00,
      |            "disallowableAmount": 1800.00
      |         },
      |         "other": {
      |            "amount": 1900.00,
      |            "disallowableAmount": 2000.00
      |         },
      |         "professionalFees": {
      |            "amount": 2100.00,
      |            "disallowableAmount": 2200.00
      |         },
      |         "premisesRunningCosts": {
      |            "amount": 2300.00,
      |            "disallowableAmount": 2400.00
      |         },
      |         "staffCosts": {
      |            "amount": 2500.00,
      |            "disallowableAmount": 2600.00
      |         },
      |         "travelCosts": {
      |            "amount": 2700.00,
      |            "disallowableAmount": 2800.00
      |         },
      |         "businessEntertainmentCosts": {
      |            "amount": 2900.00,
      |            "disallowableAmount": 3000.00
      |         }
      |      },
      |      "incomes": {
      |         "turnover": 1000.00,
      |         "other": 2000.00,
      |         "taxTakenOffTradingIncome": 3000.00
      |      }
      |   }
      |}
      |""".stripMargin
  )

  val def2_MtdFullJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates":{
      |      "periodStartDate":"2019-08-24",
      |      "periodEndDate":"2020-08-24"
      |   },
      |   "periodIncome":{
      |      "turnover":1000.00,
      |      "other":2000.00,
      |      "taxTakenOffTradingIncome": 3000.00
      |   },
      |   "periodExpenses":{
      |      "costOfGoods":900.00,
      |      "paymentsToSubcontractors":700.00,
      |      "wagesAndStaffCosts":2500.00,
      |      "carVanTravelExpenses":2700.00,
      |      "premisesRunningCosts":2300.00,
      |      "maintenanceCosts":1700.00,
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
      |      "costOfGoodsDisallowable":1000.00,
      |      "paymentsToSubcontractorsDisallowable":800.00,
      |      "wagesAndStaffCostsDisallowable":2600.00,
      |      "carVanTravelExpensesDisallowable":2800.00,
      |      "premisesRunningCostsDisallowable":2400.00,
      |      "maintenanceCostsDisallowable":1800.00,
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

  val def2_DownstreamConsolidatedJson: JsValue = Json.parse(
    """
      |{
      |   "from": "2019-08-24",
      |   "to": "2020-08-24",
      |   "financials": {
      |      "deductions": {
      |         "simplifiedExpenses": 666.66
      |      },
      |      "incomes": {
      |         "turnover": 100.00,
      |         "other": 200.00,
      |         "taxTakenOffTradingIncome": 300.00
      |      }
      |   }
      |}
      |""".stripMargin
  )

  val def2_MtdConsolidatedJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates":{
      |      "periodStartDate":"2019-08-24",
      |      "periodEndDate":"2020-08-24"
      |   },
      |   "periodIncome":{
      |      "turnover":100.00,
      |      "other":200.00,
      |      "taxTakenOffTradingIncome": 300.00
      |   },
      |   "periodExpenses":{
      |      "consolidatedExpenses":666.66
      |   }
      |}
      |""".stripMargin
  )

  val def2_DownstreamMinimalJson: JsValue = Json.parse(
    """
      |{
      |   "from": "2019-08-24",
      |   "to": "2020-08-24",
      |   "financials": {
      |     "incomes": {}
      |   }
      |}
      |""".stripMargin
  )

  val def2_MtdMinimalJson: JsValue = Json.parse(
    """
      |{
      |   "periodDates":{
      |      "periodStartDate":"2019-08-24",
      |      "periodEndDate":"2020-08-24"
      |   }
      |}
      |""".stripMargin
  )

}
