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

package v2.controllers.requestParsers

import anyVersion.models.request.amendPeriodSummary.{AmendPeriodSummaryRawData, PeriodDisallowableExpenses, PeriodIncome}
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import play.api.libs.json.Json
import support.UnitSpec
import v2.mocks.validators.MockAmendPeriodSummaryValidator
import v2.models.request.amendPeriodSummary._

class AmendPeriodSummaryRequestParserSpec extends UnitSpec {

  val nino: String                   = "AA123456B"
  val businessId: String             = "XAIS12345678910"
  val periodId: String               = "2019-01-01_2019-02-02"
  implicit val correlationId: String = "X-123"
  val tysTaxYear: String             = "2023-24"

  private val requestBodyJson = Json.parse(
    """
      |{
      |    "periodIncome": {
      |        "turnover": 200.00,
      |        "other": 200.00
      |    },
      |    "periodExpenses": {
      |        "consolidatedExpenses": 200.00,
      |        "costOfGoods": 200.00,
      |        "paymentsToSubcontractors": 200.00,
      |        "wagesAndStaffCosts": 200.00,
      |        "carVanTravelExpenses": 200.00,
      |        "premisesRunningCosts": 200.00,
      |        "maintenanceCosts": 200.00,
      |        "adminCosts": 200.00,
      |        "businessEntertainmentCosts": 200.00,
      |        "advertisingCosts": 200.00,
      |        "interestOnBankOtherLoans": 200.00,
      |        "financeCharges": 200.00,
      |        "irrecoverableDebts": 200.00,
      |        "professionalFees": 200.00,
      |        "depreciation": 200.00,
      |        "otherExpenses": 200.00
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

  val requestBody: AmendPeriodSummaryBody = AmendPeriodSummaryBody(
    periodIncome = Some(
      PeriodIncome(
        Some(200.00),
        Some(200.00)
      )),
    periodExpenses = Some(
      PeriodExpenses(
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
    periodDisallowableExpenses = Some(
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

  val inputData: AmendPeriodSummaryRawData = AmendPeriodSummaryRawData(
    nino = nino,
    businessId = businessId,
    periodId = periodId,
    body = requestBodyJson,
    taxYear = None
  )

  val tysInputData: AmendPeriodSummaryRawData = AmendPeriodSummaryRawData(
    nino = nino,
    businessId = businessId,
    periodId = periodId,
    body = requestBodyJson,
    taxYear = Some(tysTaxYear)
  )

  trait Test extends MockAmendPeriodSummaryValidator {
    lazy val parser = new AmendPeriodSummaryRequestParser(mockValidator)
  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockAmendPeriodSummaryValidator.validate(inputData).returns(Nil)

        parser.parseRequest(inputData) shouldBe
          Right(AmendPeriodSummaryRequest(Nino(nino), BusinessId(businessId), periodId, requestBody, None))
      }

      "valid TYS request data is supplied" in new Test {
        MockAmendPeriodSummaryValidator.validate(tysInputData).returns(Nil)

        parser.parseRequest(tysInputData) shouldBe
          Right(AmendPeriodSummaryRequest(Nino(nino), BusinessId(businessId), periodId, requestBody, Some(TaxYear.fromMtd(tysTaxYear))))
      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockAmendPeriodSummaryValidator
          .validate(inputData)
          .returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockAmendPeriodSummaryValidator
          .validate(inputData)
          .returns(List(NinoFormatError, BusinessIdFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BusinessIdFormatError))))
      }
    }
  }

}
