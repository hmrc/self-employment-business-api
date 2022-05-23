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

package v1.controllers.requestParsers

import play.api.libs.json.Json
import support.UnitSpec
import v1.mocks.validators.MockAmendPeriodicValidator
import v1.models.domain.Nino
import v1.models.errors._
import v1.models.request.amendPeriodic._

class AmendPeriodSummaryRequestParserSpec extends UnitSpec {

  val nino: String                   = "AA123456B"
  val businessId: String             = "XAIS12345678910"
  val periodId: String               = "2019-01-01_2019-02-02"
  implicit val correlationId: String = "X-123"

  private val requestBodyJson = Json.parse(
    """
      |{
      |    "periodIncome": {
      |        "turnover": 200.00,
      |        "other": 200.00
      |    },
      |    "periodAllowableExpenses": {
      |        "consolidatedExpenses": 200.00,
      |        "costOfGoodsAllowable": 200.00,
      |        "paymentsToSubcontractorsAllowable": 200.00,
      |        "wagesAndStaffCostsAllowable": 200.00,
      |        "carVanTravelExpensesAllowable": 200.00,
      |        "premisesRunningCostsAllowable": 200.00,
      |        "maintenanceCostsAllowable": 200.00,
      |        "adminCostsAllowable": 200.00,
      |        "businessEntertainmentCostsAllowable": 200.00,
      |        "advertisingCostsAllowable": 200.00,
      |        "interestOnBankOtherLoansAllowable": 200.00,
      |        "financeChargesAllowable": 200.00,
      |        "irrecoverableDebtsAllowable": 200.00,
      |        "professionalFeesAllowable": 200.00,
      |        "depreciationAllowable": 200.00,
      |        "otherExpensesAllowable": 200.00
      |    },
      |    "periodDisallowableExpenses": {
      |        "costOfGoodsDisallowable": 200.00,
      |        "paymentsToSubcontractorsDisallowable": 200.00,
      |        "wagesAndStaffCostsDisallowable": 200.00,
      |        "carVanTravelExpensesDisallowable": 200.00,
      |        "premisesRunningCostsDisallowable": 200.00,
      |        "maintenanceCostsDisallowable": 200.00,
      |        "adminCostsDisallowable": 200.00,
      |        "businessEntertainmentCostsDisallowable": 200.00,
      |        "advertisingCostsDisallowable": 200.00,
      |        "interestOnBankOtherLoansDisallowable": 200.00,
      |        "financeChargesDisallowable": 200.00,
      |        "irrecoverableDebtsDisallowable": 200.00,
      |        "professionalFeesDisallowable": 200.00,
      |        "depreciationDisallowable": 200.00,
      |        "otherExpensesDisallowable": 200.00
      |    }
      |}
    """.stripMargin
  )

  val inputData: AmendPeriodicRawData = AmendPeriodicRawData(
    nino = nino,
    businessId = businessId,
    periodId = periodId,
    body = requestBodyJson
  )

  trait Test extends MockAmendPeriodicValidator {
    lazy val parser = new AmendPeriodicRequestParser(mockValidator)
  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockAmendPeriodicValidator.validate(inputData).returns(Nil)

        val amendPeriodicRequestBody: AmendPeriodicBody = AmendPeriodicBody(
          Some(
            PeriodIncome(
              Some(200.00),
              Some(200.00)
            )),
          Some(
            PeriodAllowableExpenses(
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00)
            )),
          Some(
            PeriodDisallowableExpenses(
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00),
              Some(200.00)
            ))
        )

        parser.parseRequest(inputData) shouldBe
          Right(AmendPeriodicRequest(Nino(nino), businessId, periodId, amendPeriodicRequestBody))
      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockAmendPeriodicValidator
          .validate(inputData)
          .returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockAmendPeriodicValidator
          .validate(inputData)
          .returns(List(NinoFormatError, BusinessIdFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BusinessIdFormatError))))
      }
    }
  }

}
