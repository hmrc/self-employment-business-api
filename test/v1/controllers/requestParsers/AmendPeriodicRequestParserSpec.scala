/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.controllers.requestParsers

import play.api.libs.json.Json
import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v1.mocks.validators.MockAmendPeriodicValidator
import v1.models.errors._
import v1.models.request.amendSEPeriodic._

class AmendPeriodicRequestParserSpec extends UnitSpec {
  val nino = "AA123456B"
  val businessId = "XAIS12345678910"
  val periodId = "2019-01-01_2019-02-02"

  private val requestBodyJson = Json.parse(
    """
      |{
      |    "incomes": {
      |        "turnover": {
      |            "amount": 200.00
      |        },
      |        "other": {
      |            "amount": 200.00
      |        }
      |    },
      |    "expenses": {
      |        "costOfGoodsBought": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "cisPaymentsTo": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "staffCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "travelCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "premisesRunningCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "maintenanceCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "adminCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "advertisingCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "businessEntertainmentCosts": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "interestOnLoans": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "financialCharges": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "badDebt": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "professionalFees": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "depreciation": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        },
      |        "other": {
      |            "amount": 200.00,
      |            "disallowableAmount": 200.00
      |        }
      |    }
      |}
        """.stripMargin)

  val inputData =
    AmendPeriodicRawData(nino, businessId, periodId, requestBodyJson)

  trait Test extends MockAmendPeriodicValidator {
    lazy val parser = new AmendPeriodicRequestParser(mockValidator)
  }

  "parse" should {

    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockAmendPeriodicValidator.validate(inputData).returns(Nil)

        val amendPeriodicRequestBody =
          AmendPeriodicBody(
            Some(Incomes(Some(IncomesAmountObject(200.00)), Some(IncomesAmountObject(200.00)))),
            None,
            Some(Expenses(
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00))),
              Some(ExpensesAmountObject(200.00, Some(200.00)))))
          )

        parser.parseRequest(inputData) shouldBe
          Right(AmendPeriodicRequest(Nino(nino), businessId, periodId, amendPeriodicRequestBody))
      }
    }
    "return an ErrroWrapper" when {
      "a single validation error occurs" in new Test {
        MockAmendPeriodicValidator.validate(inputData)
          .returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(None, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockAmendPeriodicValidator.validate(inputData)
          .returns(List(NinoFormatError, BusinessIdFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(None, BadRequestError, Some(Seq(NinoFormatError, BusinessIdFormatError))))
      }
    }
  }
}
