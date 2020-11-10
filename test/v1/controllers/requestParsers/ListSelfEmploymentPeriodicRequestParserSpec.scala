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

import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v1.mocks.validators.MockListSelfEmploymentPeriodicValidator
import v1.models.errors._
import v1.models.request.listSEPeriodic.{ListSelfEmploymentPeriodicRawData, ListSelfEmploymentPeriodicRequest}

class ListSelfEmploymentPeriodicRequestParserSpec extends UnitSpec {

  val nino: String = "AA123456B"
  val businessId: String = "XAIS12345678910"
  implicit val correlationId = "X-123"

  val rawData = ListSelfEmploymentPeriodicRawData(
    nino = nino,
    businessId = businessId
  )


  trait Test extends MockListSelfEmploymentPeriodicValidator {
    lazy val parser: ListSelfEmploymentPeriodicRequestParser = new ListSelfEmploymentPeriodicRequestParser(
      validator = mockListSelfEmploymentPeriodicValidator
    )
  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockListSelfEmploymentPeriodicValidator.validate(rawData).returns(Nil)

        parser.parseRequest(rawData) shouldBe
          Right(ListSelfEmploymentPeriodicRequest(Nino(nino), businessId))
      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockListSelfEmploymentPeriodicValidator.validate(rawData)
          .returns(List(NinoFormatError))

        parser.parseRequest(rawData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockListSelfEmploymentPeriodicValidator.validate(rawData)
          .returns(List(NinoFormatError, BusinessIdFormatError))

        parser.parseRequest(rawData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BusinessIdFormatError))))
      }
    }
  }
}
