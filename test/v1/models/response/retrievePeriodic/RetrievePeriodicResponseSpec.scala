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

package v1.models.response.retrievePeriodic

import mocks.MockAppConfig
import play.api.libs.json.{JsValue, Json}
import support.UnitSpec
import v1.models.hateoas.{Link, Method}

class RetrievePeriodicResponseSpec extends UnitSpec with MockAppConfig {

  val mtdJson: JsValue = Json.parse(
    """
      |{
      |   "periodFromDate": "2017-01-25",
      |   "periodToDate": "2017-01-25",
      |   "incomes": {
      |      "turnover": {
      |         "amount": 500.25
      |      },
      |      "other": {
      |         "amount": 500.25
      |      }
      |   },
      |   "consolidatedExpenses": {
      |      "consolidatedExpenses": 500.25
      |   },
      |   "expenses": {
      |      "costOfGoodsBought": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "cisPaymentsTo": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "staffCosts": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "travelCosts": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "premisesRunningCosts": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "maintenanceCosts": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "adminCosts": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "advertisingCosts": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "interestOnLoans": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "financialCharges": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "badDebt": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "professionalFees": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "depreciation": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      },
      |      "other": {
      |         "amount": 500.25,
      |         "disallowableAmount": 500.25
      |      }
      |   }
      |}
    """.stripMargin
  )

  val desJson: JsValue = Json.parse(
    """
      |{
      |   "from": "2017-01-25",
      |   "to": "2017-01-25",
      |   "financials": {
      |      "deductions": {
      |         "adminCosts": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "advertisingCosts": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "badDebt": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "constructionIndustryScheme": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "costOfGoods": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "depreciation": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "financialCharges": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "interest": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "maintenanceCosts": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "other": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "professionalFees": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "premisesRunningCosts": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "staffCosts": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "travelCosts": {
      |            "amount": 500.25,
      |            "disallowableAmount": 500.25
      |         },
      |         "simplifiedExpenses": 500.25
      |      },
      |      "incomes": {
      |         "turnover": 500.25,
      |         "other": 500.25
      |      }
      |   }
      |}
    """.stripMargin
  )

  val model: RetrievePeriodicResponse = RetrievePeriodicResponse(
    "2017-01-25",
    "2017-01-25",
    Some(
      Incomes(
        Some(
          IncomesAmountObject(
            500.25
          )),
        Some(
          IncomesAmountObject(
            500.25
          ))
      )),
    Some(ConsolidatedExpenses(500.25)),
    Some(
      Expenses(
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        None,
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25))),
        Some(ExpensesAmountObject(500.25, Some(500.25)))
      ))
  )

  "reads" should {
    "return a model" when {
      "passed a valid json" in {
        desJson.as[RetrievePeriodicResponse] shouldBe model
      }
    }
  }

  "writes" should {
    "return json" when {
      "passed a valid model" in {
        Json.toJson(model) shouldBe mtdJson
      }
    }
  }

  "LinksFactory" should {
    "produce the correct links" when {
      "called" in {
        val data: RetrievePeriodicHateoasData = RetrievePeriodicHateoasData("mynino", "myBusinessId", "myPeriodId")

        MockAppConfig.apiGatewayContext.returns("my/context").anyNumberOfTimes()

        RetrievePeriodicResponse.RetrieveAnnualSubmissionLinksFactory.links(mockAppConfig, data) shouldBe Seq(
          Link(href = s"/my/context/${data.nino}/${data.businessId}/period/${data.periodId}", method = Method.PUT, rel = "amend-self-employment-period-summary"),
          Link(href = s"/my/context/${data.nino}/${data.businessId}/period/${data.periodId}", method = Method.GET, rel = "self")
        )
      }
    }
  }

}
