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

package config.controllers.requestparsers

import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors.{BadRequestError, BusinessIdFormatError, ErrorWrapper, NinoFormatError, TaxYearFormatError}
import support.UnitSpec
import v1.mocks.validators.MockRetrieveAnnualSubmissionValidator
import v1.controllers.requestParsers.RetrieveAnnualSubmissionRequestParser
import v1.models.request.retrieveAnnual.{RetrieveAnnualSubmissionRawData, RetrieveAnnualSubmissionRequest}

class RetrieveAnnualSubmissionRequestParserSpec extends UnitSpec {

  val nino: String                   = "AA123456B"
  val businessId: String             = "XAIS12345678910"
  val taxYear: String                = "2017-18"
  implicit val correlationId: String = "X-123"

  val rawData: RetrieveAnnualSubmissionRawData = RetrieveAnnualSubmissionRawData(
    nino = nino,
    businessId = businessId,
    taxYear = taxYear
  )

  trait Test extends MockRetrieveAnnualSubmissionValidator {

    lazy val parser: RetrieveAnnualSubmissionRequestParser = new RetrieveAnnualSubmissionRequestParser(
      validator = mockRetrieveAnnualSubmissionValidator
    )

  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockRetrieveAnnualSubmissionValidator.validate(rawData).returns(Nil)

        parser.parseRequest(rawData) shouldBe
          Right(RetrieveAnnualSubmissionRequest(Nino(nino), BusinessId(businessId), TaxYear.fromMtd(taxYear)))
      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockRetrieveAnnualSubmissionValidator
          .validate(rawData)
          .returns(List(NinoFormatError))

        parser.parseRequest(rawData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockRetrieveAnnualSubmissionValidator
          .validate(rawData)
          .returns(List(NinoFormatError, BusinessIdFormatError, TaxYearFormatError))

        parser.parseRequest(rawData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BusinessIdFormatError, TaxYearFormatError))))
      }
    }
  }

}