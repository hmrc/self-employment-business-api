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

package v5.createAmendCumulativePeriodSummary.def1.model.request

import play.api.libs.json.Json
import shared.utils.UnitSpec
import v5.createAmendCumulativePeriodSummary.model.request.Def1_CreateAmendCumulativePeriodSummaryRequestBody
import v5.createAmendCumulativePeriodSummary.model.request.Def1_CreateAmendCumulativePeriodSummaryRequestBody._

class CumulativePeriodSummaryBodySpec extends UnitSpec with CumulativePeriodSummaryFixture {

  val someOptionalFieldsMtdBody: Def1_CreateAmendCumulativePeriodSummaryRequestBody =
    Def1_CreateAmendCumulativePeriodSummaryRequestBody(
      Some(PeriodDates("2025-08-24", "2026-08-24")),
      Some(PeriodIncome(Some(1000.99), Some(2000.99), Some(3000.99))),
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

  val noOptionalFieldsMtdBody: Def1_CreateAmendCumulativePeriodSummaryRequestBody =
    Def1_CreateAmendCumulativePeriodSummaryRequestBody(Some(PeriodDates("2025-08-24", "2026-08-24")), None, None, None)

  "reads" should {
    "read from a JSON" when {
      val someOptionalRequestJson = Json.parse("""
          |{
          |     "periodDates": {
          |           "periodStartDate": "2025-08-24",
          |           "periodEndDate": "2026-08-24"
          |     },
          |     "periodIncome": {
          |          "turnover": 1000.99,
          |          "other": 2000.99,
          |          "taxTakenOffTradingIncome": 3000.99
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
          |           "periodStartDate": "2025-08-24",
          |           "periodEndDate": "2026-08-24"
          |     }
          |}
          |""".stripMargin)

      "a valid request with all optional fields is made" in {
        requestMtdBodyJson.as[Def1_CreateAmendCumulativePeriodSummaryRequestBody] shouldBe fullMTDRequest
      }

      "a valid request with some optional fields is made" in {
        someOptionalRequestJson.as[Def1_CreateAmendCumulativePeriodSummaryRequestBody] shouldBe someOptionalFieldsMtdBody
      }

      "a valid request with no optional fields is made" in {
        noOptionalRequestJson.as[Def1_CreateAmendCumulativePeriodSummaryRequestBody] shouldBe noOptionalFieldsMtdBody
      }
    }
  }

  "writes" should {
    "write to downstream" when {
      val someOptionalDesJson = Json.parse("""
          |{
          |   "selfEmploymentPeriodDates":{
          |      "periodStartDate":"2025-08-24",
          |      "periodEndDate":"2026-08-24"
          |   },
          |   "selfEmploymentPeriodIncome":{
          |      "turnover":1000.99,
          |      "other":2000.99,
          |      "taxTakenOffTradingIncome":3000.99
          |   },
          |   "selfEmploymentPeriodDeductions":{
          |      "costOfGoods":{
          |         "amount":1000.99
          |      },
          |      "constructionIndustryScheme":{
          |         "amount":1000.99
          |      },
          |      "staffCosts":{
          |         "amount":1000.99
          |      },
          |      "travelCosts":{
          |         "amount":1000.99
          |      },
          |      "premisesRunningCosts":{
          |         "amount":-99999.99
          |      },
          |      "maintenanceCosts":{
          |         "amount":-1000.99
          |      },
          |      "adminCosts":{
          |         "amount":1000.99
          |      },
          |      "businessEntertainmentCosts":{
          |         "amount":1000.99
          |      },
          |      "advertisingCosts":{
          |         "amount":1000.99
          |      },
          |      "interest":{
          |         "amount":-1000.99
          |      },
          |      "financialCharges":{
          |         "amount":-1000.99
          |      },
          |      "badDebt":{
          |         "amount":-1000.99
          |      },
          |      "professionalFees":{
          |         "amount":-99999999999.99
          |      },
          |      "depreciation":{
          |         "amount":-1000.99
          |      },
          |      "other":{
          |         "amount":1000.99
          |      }
          |   }
          |}
          |""".stripMargin)

      val noOptionalDesJson = Json.parse("""
          |{
          |   "selfEmploymentPeriodDates":{
          |      "periodStartDate":"2025-08-24",
          |      "periodEndDate":"2026-08-24"
          |   }
          |}
          |""".stripMargin)

      "a valid request with all optional fields is made" in {
        Json.toJson(fullMTDRequest) shouldBe requestDownstreamBodyJson
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
