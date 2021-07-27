/*
 * Copyright 2021 HM Revenue & Customs
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
import v1.mocks.validators.MockCreateSelfEmploymentPeriodicValidator
import v1.models.domain.Nino
import v1.models.errors.{BadRequestError, BusinessIdFormatError, ErrorWrapper, NinoFormatError}
import v1.models.request.createSEPeriodic._

class CreateSelfEmploymentPeriodicRequestParserSpec extends UnitSpec {

  val nino: String = "AA123456B"
  val businessId: String = "XAIS12345678910"
  implicit val correlationId: String = "X-123"

  private val requestBodyJson = Json.parse(
    """
       {
       |   "periodFromDate": "2017-01-25",
       |   "periodToDate": "2018-01-24",
       |   "incomes": {
       |     "turnover": {
       |       "amount": 500.25
       |     },
       |     "other": {
       |       "amount": 500.25
       |     }
       |   },
       |   "consolidatedExpenses": {
       |     "consolidatedExpenses": 500.25
       |   }
       |}
     """.stripMargin
  )

  val inputData: CreateSelfEmploymentPeriodicRawData = CreateSelfEmploymentPeriodicRawData(
    nino = nino,
    businessId = businessId,
    body = requestBodyJson
  )

  trait Test extends MockCreateSelfEmploymentPeriodicValidator {
    lazy val parser = new CreateSelfEmploymentPeriodicRequestParser(mockValidator)
  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockCreateSelfEmploymentPeriodicValidator.validate(inputData).returns(Nil)

        val createPeriodicUpdateRequestBody: CreateSelfEmploymentPeriodicBody = CreateSelfEmploymentPeriodicBody(
          "2017-01-25",
          "2018-01-24",
          Some(Incomes(Some(IncomesAmountObject(500.25)), Some(IncomesAmountObject(500.25)))),
          Some(ConsolidatedExpenses(500.25)),
          None
        )

        parser.parseRequest(inputData) shouldBe
          Right(CreateSelfEmploymentPeriodicRequest(Nino(nino), businessId, createPeriodicUpdateRequestBody))
      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockCreateSelfEmploymentPeriodicValidator.validate(inputData)
          .returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockCreateSelfEmploymentPeriodicValidator.validate(inputData)
          .returns(List(NinoFormatError, BusinessIdFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BusinessIdFormatError))))
      }
    }
  }
}