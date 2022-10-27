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

package v1.models.response.retrievePeriodSummary

import mocks.MockAppConfig
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.hateoas.{Link, Method}

class RetrievePeriodSummaryResponseSpec extends UnitSpec with MockAppConfig {

  val downstreamFullJson: JsValue = Json.parse("""
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
      |         "turnover": 3100.00,
      |         "other": 3200.00
      |      }
      |   }
      |}
      |""".stripMargin)

  val mtdFullJson: JsValue = Json.parse("""
      |{
      |   "periodDates":{
      |      "periodStartDate":"2019-08-24",
      |      "periodEndDate":"2020-08-24"
      |   },
      |   "periodIncome":{
      |      "turnover":3100.00,
      |      "other":3200.00
      |   },
      |   "periodAllowableExpenses":{
      |      "costOfGoodsAllowable":900.00,
      |      "paymentsToSubcontractorsAllowable":700.00,
      |      "wagesAndStaffCostsAllowable":2500.00,
      |      "carVanTravelExpensesAllowable":2700.00,
      |      "premisesRunningCostsAllowable":2300.00,
      |      "maintenanceCostsAllowable":1700.00,
      |      "adminCostsAllowable":100.00,
      |      "businessEntertainmentCostsAllowable":2900.00,
      |      "advertisingCostsAllowable":300.00,
      |      "interestOnBankOtherLoansAllowable":1500.00,
      |      "financeChargesAllowable":1300.00,
      |      "irrecoverableDebtsAllowable":500.00,
      |      "professionalFeesAllowable":2100.00,
      |      "depreciationAllowable":1100.00,
      |      "otherExpensesAllowable":1900.00
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
      |""".stripMargin)

  val downstreamConsolidatedJson: JsValue = Json.parse("""
      |{
      |   "from": "2019-08-24",
      |   "to": "2020-08-24",
      |   "financials": {
      |      "deductions": {
      |         "simplifiedExpenses": 666.66
      |      },
      |      "incomes": {
      |         "turnover": 100.00,
      |         "other": 200.00
      |      }
      |   }
      |}
      |""".stripMargin)

  val mtdConsolidatedJson: JsValue = Json.parse("""
      |{
      |   "periodDates":{
      |      "periodStartDate":"2019-08-24",
      |      "periodEndDate":"2020-08-24"
      |   },
      |   "periodIncome":{
      |      "turnover":100.00,
      |      "other":200.00
      |   },
      |   "periodAllowableExpenses":{
      |      "consolidatedExpenses":666.66
      |   }
      |}
      |""".stripMargin)

  val downstreamMinimalJson: JsValue = Json.parse("""
      |{
      |   "from": "2019-08-24",
      |   "to": "2020-08-24",
      |   "financials": {
      |     "incomes": {}
      |   }
      |}
      |""".stripMargin)

  val mtdMinimalJson: JsValue = Json.parse("""
      |{
      |   "periodDates":{
      |      "periodStartDate":"2019-08-24",
      |      "periodEndDate":"2020-08-24"
      |   }
      |}
      |""".stripMargin)

  "round trip" should {
    "return mtd json" when {
      "passed valid full downstream json" in {
        Json.toJson(downstreamFullJson.as[RetrievePeriodSummaryResponse]) shouldBe mtdFullJson
      }
      "passed valid consolidated downstream json" in {
        Json.toJson(downstreamConsolidatedJson.as[RetrievePeriodSummaryResponse]) shouldBe mtdConsolidatedJson
      }
      "passed valid minimal downstream json" in {
        Json.toJson(downstreamMinimalJson.as[RetrievePeriodSummaryResponse]) shouldBe mtdMinimalJson
      }
    }
  }

  "LinksFactory" should {
    val nino       = "AA111111A"
    val businessId = "id"
    val periodId   = "periodId"

    "produce the correct links with TYS disabled" when {
      "called" in {
        val data: RetrievePeriodSummaryHateoasData = RetrievePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, None)

        MockAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()
        MockAppConfig.featureSwitches.returns(Configuration("tys-api.enabled" -> false)).anyNumberOfTimes()

        RetrievePeriodSummaryResponse.RetrieveAnnualSubmissionLinksFactory.links(mockAppConfig, data) shouldBe Seq(
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.PUT, rel = "amend-self-employment-period-summary"),
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.GET, rel = "self"),
          Link(href = s"/my/context/$nino/$businessId/period", method = Method.GET, rel = "list-self-employment-period-summaries")
        )
      }
    }

    "produce the correct links with TYS enabled and the tax year is TYS" when {
      "called" in {
        val data: RetrievePeriodSummaryHateoasData =
          RetrievePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(TaxYear.fromMtd("2023-24")))

        MockAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()
        MockAppConfig.featureSwitches.returns(Configuration("tys-api.enabled" -> true)).anyNumberOfTimes()

        RetrievePeriodSummaryResponse.RetrieveAnnualSubmissionLinksFactory.links(mockAppConfig, data) shouldBe Seq(
          Link(href = s"/my/context/$nino/$businessId/period/$periodId", method = Method.PUT, rel = "amend-self-employment-period-summary"),
          Link(href = s"/my/context/$nino/$businessId/period/$periodId?taxYear=2023-24", method = Method.GET, rel = "self"),
          Link(href = s"/my/context/$nino/$businessId/period?taxYear=2023-24", method = Method.GET, rel = "list-self-employment-period-summaries")
        )
      }
    }
  }

}
