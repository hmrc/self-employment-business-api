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
import v1.mocks.validators.MockAmendAnnualSubmissionValidator
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.errors._
import v1.models.request.amendSEAnnual._

class AmendAnnualSubmissionRequestParserSpec extends UnitSpec with AmendAnnualSubmissionFixture {

  val nino: String                   = "AA123456B"
  val businessId: String             = "XAIS12345678910"
  val taxYear: String                = "2019-20"
  implicit val correlationId: String = "X-123"

  private val requestBodyJson = amendAnnualSubmissionBodyMtdJson()

  val inputData: AmendAnnualSubmissionRawData = AmendAnnualSubmissionRawData(
    nino = nino,
    businessId = businessId,
    taxYear = taxYear,
    body = requestBodyJson
  )

  trait Test extends MockAmendAnnualSubmissionValidator {
    lazy val parser = new AmendAnnualSubmissionRequestParser(mockValidator)
  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockAmendAnnualSummaryValidator.validate(inputData).returns(Nil)

        val amendAnnualSummaryRequestBody: AmendAnnualSubmissionBody = amendAnnualSubmissionBody()

        parser.parseRequest(inputData) shouldBe
          Right(AmendAnnualSubmissionRequest(Nino(nino), BusinessId(businessId), TaxYear.fromMtd(taxYear), amendAnnualSummaryRequestBody))
      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockAmendAnnualSummaryValidator
          .validate(inputData)
          .returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockAmendAnnualSummaryValidator
          .validate(inputData)
          .returns(List(NinoFormatError, BusinessIdFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BusinessIdFormatError))))
      }
    }
  }

}
