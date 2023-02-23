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

package v2.models.request.createPeriodSummary

import anyVersion.models.request.createPeriodSummary.{PeriodDates, PeriodDisallowableExpenses, PeriodIncome}
import play.api.libs.json.Json
import support.UnitSpec

class CreatePeriodSummaryBodySpec extends UnitSpec {

  val fullMtdBody: CreatePeriodSummaryBody =
    CreatePeriodSummaryBody(
      PeriodDates("2019-08-24", "2019-08-24"),
      Some(
        PeriodIncome(
          Some(1000.99),
          Some(1000.99)
        )),
      Some(
        PeriodExpenses(
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
      Some(
        PeriodDisallowableExpenses(
          Some(1000.99),
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

  val someOptionalFieldsMtdBody: CreatePeriodSummaryBody =
    CreatePeriodSummaryBody(
      PeriodDates("2019-08-24", "2019-08-24"),
      Some(
        PeriodIncome(
          Some(1000.99),
          Some(1000.99)
        )),
      Some(
        PeriodExpenses(
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
      None
    )

  val noOptionalFieldsMtdBody: CreatePeriodSummaryBody =
    CreatePeriodSummaryBody(
      PeriodDates("2019-08-24", "2019-08-24"),
      None,
      None,
      None
    )

  "reads" should {
    "read from a JSON" when {
      val fullRequestJson = Json.parse("""
          |{
          |     "periodDates": {
          |           "periodStartDate": "2019-08-24",
          |           "periodEndDate": "2019-08-24"
          |     },
          |     "periodIncome": {
          |          "turnover": 1000.99,
          |          "other": 1000.99
          |     },
          |     "periodExpenses": {
          |          "costOfGoods": 1000.99,
          |          "paymentsToSubcontractors": 1000.99,
          |          "wagesAndStaffCosts": 1000.99,
          |          "carVanTravelExpenses": 1000.99,
          |          "premisesRunningCosts": -99999.99,
          |          "maintenanceCosts": -1000.99,
          |          "adminCosts": 1000.99,
          |          "businessEntertainmentCosts": 1000.99,
          |          "advertisingCosts": 1000.99,
          |          "interestOnBankOtherLoans": -1000.99,
          |          "financeCharges": -1000.99,
          |          "irrecoverableDebts": -1000.99,
          |          "professionalFees": -99999999999.99,
          |          "depreciation": -1000.99,
          |          "otherExpenses": 1000.99
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

      val someOptionalRequestJson = Json.parse("""
          |{
          |     "periodDates": {
          |           "periodStartDate": "2019-08-24",
          |           "periodEndDate": "2019-08-24"
          |     },
          |     "periodIncome": {
          |          "turnover": 1000.99,
          |          "other": 1000.99
          |     },
          |     "periodExpenses": {
          |          "costOfGoods": 1000.99,
          |          "paymentsToSubcontractors": 1000.99,
          |          "wagesAndStaffCosts": 1000.99,
          |          "carVanTravelExpenses": 1000.99,
          |          "premisesRunningCosts": -99999.99,
          |          "maintenanceCosts": -1000.99,
          |          "adminCosts": 1000.99,
          |          "businessEntertainmentCosts": 1000.99,
          |          "advertisingCosts": 1000.99,
          |          "interestOnBankOtherLoans": -1000.99,
          |          "financeCharges": -1000.99,
          |          "irrecoverableDebts": -1000.99,
          |          "professionalFees": -99999999999.99,
          |          "depreciation": -1000.99,
          |          "otherExpenses": 1000.99
          |      }
          |}
          |""".stripMargin)

      val noOptionalRequestJson = Json.parse("""
          |{
          |     "periodDates": {
          |           "periodStartDate": "2019-08-24",
          |           "periodEndDate": "2019-08-24"
          |     }
          |}
          |""".stripMargin)

      "a valid request with all optional fields is made" in {
        fullRequestJson.as[CreatePeriodSummaryBody] shouldBe fullMtdBody
      }

      "a valid request with some optional fields is made" in {
        someOptionalRequestJson.as[CreatePeriodSummaryBody] shouldBe someOptionalFieldsMtdBody
      }

      "a valid request with no optional fields is made" in {
        noOptionalRequestJson.as[CreatePeriodSummaryBody] shouldBe noOptionalFieldsMtdBody
      }
    }
  }

  "writes" should {
    "write to downstream" when {
      val fullDownstreamJson = Json.parse("""
          |{
          |   "from": "2019-08-24",
          |   "to": "2019-08-24",
          |   "financials": {
          |      "incomes": {
          |         "turnover": 1000.99,
          |         "other": 1000.99
          |      },
          |      "deductions": {
          |         "adminCosts": {
          |            "amount": 1000.99,
          |            "disallowableAmount": 1000.99
          |         },
          |         "other": {
          |            "amount": 1000.99,
          |            "disallowableAmount": 1000.99
          |         },
          |         "professionalFees": {
          |            "amount": -99999999999.99,
          |            "disallowableAmount": -99999999999.99
          |         },
          |         "staffCosts": {
          |            "amount": 1000.99,
          |            "disallowableAmount": 1000.99
          |         },
          |         "premisesRunningCosts": {
          |            "amount": -99999.99,
          |            "disallowableAmount": -1000.99
          |         },
          |         "financialCharges": {
          |            "amount": -1000.99,
          |            "disallowableAmount": -9999.99
          |         },
          |         "maintenanceCosts": {
          |            "amount": -1000.99,
          |            "disallowableAmount": -999.99
          |         },
          |         "badDebt": {
          |            "amount": -1000.99,
          |            "disallowableAmount": -1000.99
          |         },
          |         "advertisingCosts": {
          |            "amount": 1000.99,
          |            "disallowableAmount": 1000.99
          |         },
          |         "costOfGoods": {
          |            "amount": 1000.99,
          |            "disallowableAmount": 1000.99
          |         },
          |         "constructionIndustryScheme": {
          |            "amount": 1000.99,
          |            "disallowableAmount": 1000.99
          |         },
          |         "interest": {
          |            "amount": -1000.99,
          |            "disallowableAmount": -1000.99
          |         },
          |         "depreciation": {
          |            "amount": -1000.99,
          |            "disallowableAmount": -99999999999.99
          |         },
          |         "travelCosts": {
          |            "amount": 1000.99,
          |            "disallowableAmount": 1000.99
          |         },
          |        "businessEntertainmentCosts": {
          |            "amount": 1000.99,
          |            "disallowableAmount": 1000.99
          |        }
          |      }
          |   }
          |}
          |""".stripMargin)

      val someOptionalDesJson = Json.parse("""
          |{
          |   "from": "2019-08-24",
          |   "to": "2019-08-24",
          |   "financials": {
          |      "incomes": {
          |         "turnover": 1000.99,
          |         "other": 1000.99
          |      },
          |      "deductions": {
          |         "adminCosts": {
          |            "amount": 1000.99
          |         },
          |         "other": {
          |            "amount": 1000.99
          |         },
          |         "professionalFees": {
          |            "amount": -99999999999.99
          |         },
          |         "staffCosts": {
          |            "amount": 1000.99
          |         },
          |         "premisesRunningCosts": {
          |            "amount": -99999.99
          |         },
          |         "financialCharges": {
          |            "amount": -1000.99
          |         },
          |         "maintenanceCosts": {
          |            "amount": -1000.99
          |         },
          |         "badDebt": {
          |            "amount": -1000.99
          |         },
          |         "advertisingCosts": {
          |            "amount": 1000.99
          |         },
          |         "costOfGoods": {
          |            "amount": 1000.99
          |         },
          |         "constructionIndustryScheme": {
          |            "amount": 1000.99
          |         },
          |         "interest": {
          |            "amount": -1000.99
          |         },
          |         "depreciation": {
          |            "amount": -1000.99
          |         },
          |         "travelCosts": {
          |            "amount": 1000.99
          |         },
          |        "businessEntertainmentCosts": {
          |            "amount": 1000.99
          |        }
          |      }
          |   }
          |}
          |""".stripMargin)

      val noOptionalDesJson = Json.parse("""
          |{
          |   "from": "2019-08-24",
          |   "to": "2019-08-24"
          |}
          |""".stripMargin)

      "a valid request with all optional fields is made" in {
        Json.toJson(fullMtdBody) shouldBe fullDownstreamJson
      }

      "a valid request with some optional fields is made" in {
        Json.toJson(someOptionalFieldsMtdBody) shouldBe someOptionalDesJson
      }

      "a valid request with no optional fields is made" in {
        Json.toJson(noOptionalFieldsMtdBody) shouldBe noOptionalDesJson
      }
    }
  }

}
