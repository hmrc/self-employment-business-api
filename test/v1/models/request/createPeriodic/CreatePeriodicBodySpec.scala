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

package v1.models.request.createPeriodic

import play.api.libs.json.Json
import support.UnitSpec

class CreatePeriodicBodySpec extends UnitSpec {

  val fullMtdBody: CreatePeriodicBody =
    CreatePeriodicBody(
      PeriodDates(
        "2019-08-24",
        "2019-08-24"),
      Some(PeriodIncome(
        Some(1000.99),
        Some(1000.99)
      )),
      Some(PeriodAllowableExpenses(
        None,
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(-99999.99),
        Some(-1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(-1000.99),
        Some(-1000.99),
        Some(-1000.99),
        Some(-99999999999.99),
        Some(-1000.99),
        Some(1000.99)
      )),
      Some(PeriodDisallowableExpenses(
        None,
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(-1000.99),
        Some(-999.99),
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(-1000.99),
        Some(-9999.99),
        Some(-1000.99),
        Some(-99999999999.99),
        Some(-99999999999.99),
        Some(1000.99)
      ))
    )

  val someOptionalFieldsMtdBody: CreatePeriodicBody =
    CreatePeriodicBody(
      PeriodDates(
        "2019-08-24",
        "2019-08-24"),
      Some(PeriodIncome(
        Some(1000.99),
        Some(1000.99)
      )),
      Some(PeriodAllowableExpenses(
        None,
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(-99999.99),
        Some(-1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(-1000.99),
        Some(-1000.99),
        Some(-1000.99),
        Some(-99999999999.99),
        Some(-1000.99),
        Some(1000.99)
      )),
      Some(PeriodDisallowableExpenses(
        None,
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(-1000.99),
        Some(-999.99),
        Some(1000.99),
        Some(1000.99),
        Some(1000.99),
        Some(-1000.99),
        Some(-9999.99),
        Some(-1000.99),
        Some(-99999999999.99),
        Some(-99999999999.99),
        Some(1000.99)
      ))
    )

  val noOptionalFieldsMtdBody: CreatePeriodicBody =
    CreatePeriodicBody(
      PeriodDates(
        "2019-08-24",
        "2019-08-24"),
      None,
      None,
      None
    )

  "reads" should {
    "read from a JSON" when {
      val fullRequestJson = Json.parse(
        """
          |{
          |     "periodDates": {
          |           "periodStartDate": "2019-08-24",
          |           "periodEndDate": "2019-08-24"
          |     },
          |     "periodIncome": {
          |          "turnover": 1000.99,
          |          "other": 1000.99
          |     },
          |     "periodAllowableExpenses": {
          |          "costOfGoodsAllowable": 1000.99,
          |          "paymentsToSubcontractorsAllowable": 1000.99,
          |          "wagesAndStaffCostsAllowable": 1000.99,
          |          "carVanTravelExpensesAllowable": 1000.99,
          |          "premisesRunningCostsAllowable": -99999.99,
          |          "maintenanceCostsAllowable": -1000.99,
          |          "adminCostsAllowable": 1000.99,
          |          "businessEntertainmentCostsAllowable": 1000.99,
          |          "advertisingCostsAllowable": 1000.99,
          |          "interestOnBankOtherLoansAllowable": -1000.99,
          |          "financeChargesAllowable": -1000.99,
          |          "irrecoverableDebtsAllowable": -1000.99,
          |          "professionalFeesAllowable": -99999999999.99,
          |          "depreciationAllowable": -1000.99,
          |          "otherExpensesAllowable": 1000.99
          |      },
          |     "periodDisallowableExpenses": {
          |          "costOfGoodsDisallowable": 1000.99,
          |          "paymentsToSubcontractorsDisallowable": 1000.99,
          |          "wagesAndStaffCostsDisallowable": 1000.99,
          |          "carVanTravelExpensesDisallowable": 1000.99,
          |          "premisesRunningCostsDisallowable": -1000.99,
          |          "maintenanceCostsDisallowable": -999.99,
          |          "adminCostsDisallowable": 1000.99,
          |          "businessEntertainmentCostsDisallowable": 1000.99,
          |          "advertisingCostsDisallowable": 1000.99,
          |          "interestOnBankOtherLoansDisallowable": -1000.99,
          |          "financeChargesDisallowable": -9999.99,
          |          "irrecoverableDebtsDisallowable": -1000.99,
          |          "professionalFeesDisallowable": -99999999999.99,
          |          "depreciationDisallowable": -99999999999.99,
          |          "otherExpensesDisallowable": 1000.99
          |      }
          |}
          |""".stripMargin)

      val someOptionalRequestJson = Json.parse(
        """
          |{
          |     "periodDates": {
          |           "periodStartDate": "2019-08-24",
          |           "periodEndDate": "2019-08-24"
          |     },
          |     "periodIncome": {
          |          "turnover": 1000.99,
          |          "other": 1000.99
          |     },
          |     "periodAllowableExpenses": {
          |          "costOfGoodsAllowable": 1000.99,
          |          "paymentsToSubcontractorsAllowable": 1000.99,
          |          "wagesAndStaffCostsAllowable": 1000.99,
          |          "carVanTravelExpensesAllowable": 1000.99,
          |          "premisesRunningCostsAllowable": -99999.99,
          |          "maintenanceCostsAllowable": -1000.99,
          |          "adminCostsAllowable": 1000.99,
          |          "businessEntertainmentCostsAllowable": 1000.99,
          |          "advertisingCostsAllowable": 1000.99,
          |          "interestOnBankOtherLoansAllowable": -1000.99,
          |          "financeChargesAllowable": -1000.99,
          |          "irrecoverableDebtsAllowable": -1000.99,
          |          "professionalFeesAllowable": -99999999999.99,
          |          "depreciationAllowable": -1000.99,
          |          "otherExpensesAllowable": 1000.99
          |      },
          |     "periodDisallowableExpenses": {
          |          "costOfGoodsDisallowable": 1000.99,
          |          "paymentsToSubcontractorsDisallowable": 1000.99,
          |          "wagesAndStaffCostsDisallowable": 1000.99,
          |          "carVanTravelExpensesDisallowable": 1000.99,
          |          "premisesRunningCostsDisallowable": -1000.99,
          |          "maintenanceCostsDisallowable": -999.99,
          |          "adminCostsDisallowable": 1000.99,
          |          "businessEntertainmentCostsDisallowable": 1000.99,
          |          "advertisingCostsDisallowable": 1000.99,
          |          "interestOnBankOtherLoansDisallowable": -1000.99,
          |          "financeChargesDisallowable": -9999.99,
          |          "irrecoverableDebtsDisallowable": -1000.99,
          |          "professionalFeesDisallowable": -99999999999.99,
          |          "depreciationDisallowable": -99999999999.99,
          |          "otherExpensesDisallowable": 1000.99
          |      }
          |}
          |""".stripMargin)



      "a valid request with all optional fields is made" in {
        fullRequestJson.as[CreatePeriodicBody] shouldBe fullMtdBody
      }

      "a valid request with some optional fields is made" in {
        someOptionalRequestJson.as[CreatePeriodicBody] shouldBe someOptionalFieldsMtdBody
      }
    }
  }

  "writes" should {
    "write to des" when {
      val fullDesJson = Json.parse(
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
          |""".stripMargin)

      "a valid request with all optional fields is made" in {
        Json.toJson(fullMtdBody) shouldBe fullDesJson
      }
    }
  }
}
