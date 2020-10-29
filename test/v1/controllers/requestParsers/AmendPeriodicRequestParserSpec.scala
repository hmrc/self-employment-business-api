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
import v1.models.domain.ex.MtdEx._
import v1.models.errors._
import v1.models.request.amendSEPeriodic._

class AmendPeriodicRequestParserSpec extends UnitSpec {
  val nino = "AA123456B"
  val businessId = "XAIS12345678910"
  val periodId = ""

  private val requestBodyJson = Json.parse(
    """
      |{
      |   "costOfGoodsBought": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "cisPaymentsTo": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "staffCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "travelCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "premisesRunningCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "maintenanceCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "adminCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "advertisingCosts": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "interestOnLoans": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "financialCharges": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "badDebt": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "professionalFees": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "depreciation": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   },
      |   "other": {
      |      "amount": 500.25,
      |      "disallowableAmount": 500.25
      |   }
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
