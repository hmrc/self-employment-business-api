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

package v3.controllers.requestParsers

import api.models.domain.{BusinessId, Nino}
import api.models.errors.{BadRequestError, BusinessIdFormatError, ErrorWrapper, NinoFormatError}
import support.UnitSpec
import v3.fixtures.CreatePeriodSummaryFixture
import v3.mocks.validators.MockCreatePeriodSummaryValidator
import v3.models.request.createPeriodSummary.{CreatePeriodSummaryRawData, CreatePeriodSummaryRequest}

class CreatePeriodSummaryRequestParserSpec extends UnitSpec with CreatePeriodSummaryFixture {

  val nino: String                   = "AA123456B"
  val businessId: String             = "XAIS12345678910"
  implicit val correlationId: String = "X-123"

  val inputData: CreatePeriodSummaryRawData = CreatePeriodSummaryRawData(
    nino = nino,
    businessId = businessId,
    body = requestMtdBodyJson
  )

  trait Test extends MockCreatePeriodSummaryValidator {
    lazy val parser = new CreatePeriodSummaryRequestParser(mockValidator)
  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockCreatePeriodSummaryValidator.validate(inputData).returns(Nil)
        parser.parseRequest(inputData) shouldBe
          Right(CreatePeriodSummaryRequest(Nino(nino), BusinessId(businessId), fullMTDRequestModel))
      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockCreatePeriodSummaryValidator
          .validate(inputData)
          .returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockCreatePeriodSummaryValidator
          .validate(inputData)
          .returns(List(NinoFormatError, BusinessIdFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BusinessIdFormatError))))
      }
    }
  }

}
