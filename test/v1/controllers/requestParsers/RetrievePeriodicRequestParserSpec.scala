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

import support.UnitSpec
import v1.mocks.validators.MockRetrievePeriodicValidator
import v1.models.domain.Nino
import v1.models.errors._
import v1.models.request.retrievePeriodic.{RetrievePeriodicRawData, RetrievePeriodicRequest}

class RetrievePeriodicRequestParserSpec extends UnitSpec {

  val nino: String = "AA123456B"
  val businessId: String = "XAIS12345678910"
  val periodId: String = "2017-01-25_2017-02-25"
  implicit val correlationId: String = "X-123"

  val rawData: RetrievePeriodicRawData = RetrievePeriodicRawData(
    nino = nino,
    businessId = businessId,
    periodId = periodId
  )

  trait Test extends MockRetrievePeriodicValidator {
    lazy val parser: RetrievePeriodicRequestParser = new RetrievePeriodicRequestParser(
      validator = mockRetrievePeriodicValidator
    )
  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockRetrievePeriodicValidator.validate(rawData).returns(Nil)

        parser.parseRequest(rawData) shouldBe
          Right(RetrievePeriodicRequest(Nino(nino), businessId, periodId))
      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockRetrievePeriodicValidator.validate(rawData)
          .returns(List(NinoFormatError))

        parser.parseRequest(rawData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockRetrievePeriodicValidator.validate(rawData)
          .returns(List(NinoFormatError, BusinessIdFormatError, PeriodIdFormatError))

        parser.parseRequest(rawData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BusinessIdFormatError, PeriodIdFormatError))))
      }
    }
  }
}