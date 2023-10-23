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

package v2.controllers.requestParsers.validators

import api.models.errors._
import api.models.utils.JsonErrorValidators
import play.api.libs.json._
import support.UnitSpec
import v2.fixtures.CreatePeriodSummaryFixture
import v2.models.request.createPeriodSummary.CreatePeriodSummaryRawData

class CreatePeriodSummaryValidatorSpec extends UnitSpec with CreatePeriodSummaryFixture with JsonErrorValidators {

  val validator               = new CreatePeriodSummaryValidator()
  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"

  "running a validation" should {
    "return no errors" when {
      "a valid expenses request is supplied" in {
        validator.validate(CreatePeriodSummaryRawData(validNino, validBusinessId, requestMtdBodyJson)) shouldBe Nil
      }

      "a valid consolidated expenses request is supplied" in {
        validator.validate(CreatePeriodSummaryRawData(validNino, validBusinessId, requestConsolidatedMtdJson)) shouldBe Nil
      }

      "a valid expenses request is supplied with negative expenses and includeNegatives is true" in {
        validator.validate(
          CreatePeriodSummaryRawData(validNino, validBusinessId, requestMtdBodyWithNegativesJson, includeNegatives = true)) shouldBe Nil
      }

      "the minimum fields are supplied" in {
        validator.validate(CreatePeriodSummaryRawData(validNino, validBusinessId, mtdMimimumFieldsJson)) shouldBe Nil
      }

      "only incomes are supplied" in {
        validator.validate(CreatePeriodSummaryRawData(validNino, validBusinessId, mtdIncomeOnlyJson)) shouldBe Nil
      }

      "a valid request with only disallowable expenses is supplied" in {
        validator.validate(CreatePeriodSummaryRawData(validNino, validBusinessId, mtdDisallowableExpensesOnlyJson)) shouldBe Nil
      }

      "a valid request with only expenses is supplied" in {
        validator.validate(CreatePeriodSummaryRawData(validNino, validBusinessId, mtdExpensesOnlyJson)) shouldBe Nil
      }

      "return a path parameter error" when {
        "an invalid nino is supplied" in {
          validator.validate(CreatePeriodSummaryRawData("walrus", validBusinessId, requestMtdBodyJson)) shouldBe List(NinoFormatError)
        }

        "an invalid businessId is supplied" in {
          validator.validate(CreatePeriodSummaryRawData(validNino, "beans", requestMtdBodyJson)) shouldBe List(BusinessIdFormatError)
        }
      }
    }

    "return RuleIncorrectOrEmptyBodyError" when {
      "an object is empty or field is missing" when {
        Seq(
          "/periodIncome",
          "/periodIncome/turnover",
          "/periodIncome/other"
        ).foreach(path => testWith(requestMtdBodyJson.replaceWithEmptyObject(path), path))

        Seq(
          "/periodExpenses",
          "/periodExpenses/costOfGoods",
          "/periodExpenses/paymentsToSubcontractors",
          "/periodExpenses/wagesAndStaffCosts"
        ).foreach(path => testWith(requestMtdBodyJson.replaceWithEmptyObject(path), path))

        Seq(
          "/periodDisallowableExpenses",
          "/periodDisallowableExpenses/costOfGoodsDisallowable",
          "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable"
        ).foreach(path => testWith(requestMtdBodyJson.replaceWithEmptyObject(path), path))

        def testWith(body: JsValue, expectedPath: String): Unit =
          s"for $expectedPath" in {
            validator.validate(
              CreatePeriodSummaryRawData(
                validNino,
                validBusinessId,
                body
              )) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq(expectedPath))))
          }

        "return ValueFormatError" when {
          "single fields are invalid" when {
            Seq(
              "/periodIncome/turnover",
              "/periodIncome/other",
              "/periodExpenses/consolidatedExpenses",
              "/periodExpenses/costOfGoods",
              "/periodExpenses/paymentsToSubcontractors",
              "/periodExpenses/wagesAndStaffCosts",
              "/periodExpenses/carVanTravelExpenses",
              "/periodExpenses/premisesRunningCosts",
              "/periodExpenses/maintenanceCosts",
              "/periodExpenses/adminCosts",
              "/periodExpenses/businessEntertainmentCosts",
              "/periodExpenses/advertisingCosts",
              "/periodExpenses/interestOnBankOtherLoans",
              "/periodExpenses/financeCharges",
              "/periodExpenses/irrecoverableDebts",
              "/periodExpenses/professionalFees",
              "/periodExpenses/depreciation",
              "/periodExpenses/otherExpenses",
              "/periodDisallowableExpenses/costOfGoodsDisallowable",
              "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable",
              "/periodDisallowableExpenses/wagesAndStaffCostsDisallowable",
              "/periodDisallowableExpenses/carVanTravelExpensesDisallowable",
              "/periodDisallowableExpenses/premisesRunningCostsDisallowable",
              "/periodDisallowableExpenses/maintenanceCostsDisallowable",
              "/periodDisallowableExpenses/adminCostsDisallowable",
              "/periodDisallowableExpenses/businessEntertainmentCostsDisallowable",
              "/periodDisallowableExpenses/advertisingCostsDisallowable",
              "/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable",
              "/periodDisallowableExpenses/financeChargesDisallowable",
              "/periodDisallowableExpenses/irrecoverableDebtsDisallowable",
              "/periodDisallowableExpenses/professionalFeesDisallowable",
              "/periodDisallowableExpenses/depreciationDisallowable",
              "/periodDisallowableExpenses/otherExpensesDisallowable"
            ).foreach(path => testWith(requestMtdFullBodyJson.update(path, _), path))
          }

          "consolidated expenses is invalid" when {
            Seq(
              "/periodExpenses/consolidatedExpenses"
            ).foreach(path => testWith(requestMtdFullBodyJson.update(path, _), path))
          }

          "multiple fields are invalid" in {
            val path1 = "/periodIncome/turnover"
            val path2 = "/periodExpenses/consolidatedExpenses"
            val path3 = "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable"

            val json: JsValue = Json.parse(
              s"""{
                 |    "periodDates": {
                 |      "periodStartDate": "2019-08-24",
                 |      "periodEndDate": "2019-08-24"
                 |    },
                 |	  "periodIncome": {
                 |		"turnover": -1000
                 |	  },
                 |	  "periodExpenses": {
                 |		"consolidatedExpenses": 123.123
                 |	  },
                 |    "periodDisallowableExpenses": {
                 |      "paymentsToSubcontractorsDisallowable": 999999999999.99
                 |    }
                 |}
                 |""".stripMargin
            )

            validator.validate(
              CreatePeriodSummaryRawData(
                validNino,
                validBusinessId,
                json
              )) shouldBe List(
              ValueFormatError.copy(paths = Some(Seq(path1, path2, path3)), message = "The value must be between 0 and 99999999999.99"))
          }

          "negative field in periodExpenses and periodDisallowableExpenses are provided and includeNegatives is false" in {
            validator.validate(CreatePeriodSummaryRawData(validNino, validBusinessId, requestMtdBodyWithNegativesJson)) shouldBe List(
              ValueFormatError.copy(paths = Some(Seq(
                "/periodExpenses/paymentsToSubcontractors",
                "/periodExpenses/wagesAndStaffCosts",
                "/periodExpenses/carVanTravelExpenses",
                "/periodExpenses/adminCosts",
                "/periodExpenses/businessEntertainmentCosts",
                "/periodExpenses/advertisingCosts",
                "/periodExpenses/otherExpenses",
                "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable",
                "/periodDisallowableExpenses/wagesAndStaffCostsDisallowable",
                "/periodDisallowableExpenses/carVanTravelExpensesDisallowable",
                "/periodDisallowableExpenses/adminCostsDisallowable",
                "/periodDisallowableExpenses/businessEntertainmentCostsDisallowable",
                "/periodDisallowableExpenses/advertisingCostsDisallowable",
                "/periodDisallowableExpenses/professionalFeesDisallowable",
                "/periodDisallowableExpenses/otherExpensesDisallowable"
              ))))
          }

          def testWith(body: JsNumber => JsValue, expectedPath: String): Unit = s"for $expectedPath" when {
            def doTest(value: JsNumber) =
              validator.validate(
                CreatePeriodSummaryRawData(
                  validNino,
                  validBusinessId,
                  body(value)
                )) shouldBe List(ValueFormatError.forPathAndRange(expectedPath, "0", "99999999999.99"))

            "value is out of range" in doTest(JsNumber(99999999999.99 + 0.01))
          }
        }

        "an empty body is submitted" in {
          validator.validate(
            CreatePeriodSummaryRawData(validNino, validBusinessId, Json.parse("""{}"""))
          ) shouldBe List(RuleIncorrectOrEmptyBodyError)
        }

        "an empty PeriodIncome object is supplied" in {
          validator.validate(
            CreatePeriodSummaryRawData(
              validNino,
              validBusinessId,
              Json.parse("""
                  |{
                  |   "periodDates": {
                  |     "periodStartDate": "2019-08-24",
                  |     "periodEndDate": "2019-08-24"
                  |    },
                  |    "periodIncome": {}
                  |}
                       """.stripMargin)
            )
          ) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/periodIncome"))))
        }
      }

      "an empty PeriodExpenses object is supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse("""
                |{
                |   "periodDates": {
                |     "periodStartDate": "2019-08-24",
                |     "periodEndDate": "2019-08-24"
                |    },
                |    "periodIncome": {
                |      "turnover": 1000.99,
                |      "other": 1000.99
                |    },
                |    "periodExpenses": {},
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
              """.stripMargin)
          )
        ) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/periodExpenses"))))
      }

      "an empty PeriodDisallowableExpenses object is supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse("""
                |{
                |   "periodDates": {
                |     "periodStartDate": "2019-08-24",
                |     "periodEndDate": "2019-08-24"
                |    },
                |    "periodIncome": {
                |      "turnover": 1000.99,
                |      "other": 1000.99
                |    },
                |    "periodExpenses": {
                |      "consolidatedExpenses": 1000.99,
                |      "costOfGoods": 1000.99,
                |      "paymentsToSubcontractors": 1000.99,
                |      "wagesAndStaffCosts": 1000.99,
                |      "carVanTravelExpenses": 1000.99,
                |      "premisesRunningCosts": -99999.99,
                |      "maintenanceCosts": -1000.99,
                |      "adminCosts": 1000.99,
                |      "businessEntertainmentCosts": 1000.99,
                |      "advertisingCosts": 1000.99,
                |      "interestOnBankOtherLoans": -1000.99,
                |      "financeCharges": -1000.99,
                |      "irrecoverableDebts": -1000.99,
                |      "professionalFees": -99999999999.99,
                |      "depreciation": -1000.99,
                |      "otherExpenses": 1000.99
                |    },
                |    "periodDisallowableExpenses": {}
                |}
              """.stripMargin)
          )
        ) shouldBe List(RuleIncorrectOrEmptyBodyError.copy(paths = Some(Seq("/periodDisallowableExpenses"))))
      }
    }

    "return date errors" when {
      "an invalid periodStartDate is supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
                |{
                |   "periodDates": {
                |     "periodStartDate": "2019-08-025",
                |     "periodEndDate": "2019-08-24"
                |    }
                |}
              """.stripMargin
            )
          )) shouldBe List(StartDateFormatError)
      }
      "an out of range periodStartDate is supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
                |{
                |   "periodDates": {
                |     "periodStartDate": "0010-01-01",
                |     "periodEndDate": "2019-08-24"
                |    }
                |}
              """.stripMargin
            )
          )) shouldBe List(StartDateFormatError)
      }

      "an invalid periodEndDate is supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
                |{
                |   "periodDates": {
                |     "periodStartDate": "2019-08-24",
                |     "periodEndDate": "30"
                |    }
                |}
              """.stripMargin
            )
          )) shouldBe List(EndDateFormatError)
      }

      "an out of range periodEndDate is supplied" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
                |{
                |   "periodDates": {
                |     "periodStartDate": "2019-08-24",
                |     "periodEndDate": "0010-01-01"
                |    }
                |}
              """.stripMargin
            )
          )) shouldBe List(EndDateFormatError)
      }

      "periodStartDate is after periodEndDate" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
                |{
                |   "periodDates": {
                |     "periodStartDate": "2019-08-25",
                |     "periodEndDate": "2019-08-24"
                |    }
                |}
              """.stripMargin
            )
          )) shouldBe List(RuleEndDateBeforeStartDateError)
      }

      "both dates are invalid" in {
        validator.validate(
          CreatePeriodSummaryRawData(
            validNino,
            validBusinessId,
            Json.parse(
              """
                |{
                |   "periodDates": {
                |     "periodStartDate": "3020",
                |     "periodEndDate": "300"
                |    }
                |}
          """.stripMargin
            )
          )) shouldBe List(StartDateFormatError, EndDateFormatError)
      }
    }

    "return all errors" when {
      "all path parameters are invalid" in {
        validator.validate(CreatePeriodSummaryRawData("walrus", "beans", requestMtdBodyJson)) shouldBe
          List(NinoFormatError, BusinessIdFormatError)
      }
    }
  }

}
