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
import v1.mocks.validators.MockRetrieveSelfEmploymentAnnualSummaryValidator
import v1.models.errors.{BadRequestError, BusinessIdFormatError, ErrorWrapper, NinoFormatError, TaxYearFormatError}
import v1.models.request.retrieveSEAnnual.{RetrieveSelfEmploymentAnnualSummaryRawData, RetrieveSelfEmploymentAnnualSummaryRequest}

class RetrieveSelfEmploymentAnnualSummaryRequestParserSpec extends UnitSpec {

  val nino: String = "AA123456B"
  val businessId: String = "XAIS12345678910"
  val taxYear: String = "2017-18"

  val retrieveSelfEmploymentAnnualSummaryRawData: RetrieveSelfEmploymentAnnualSummaryRawData = RetrieveSelfEmploymentAnnualSummaryRawData(
    nino = nino,
    businessId = businessId,
    taxYear = taxYear
  )

  trait Test extends MockRetrieveSelfEmploymentAnnualSummaryValidator {
    lazy val parser: RetrieveSelfEmploymentAnnualSummaryRequestParser = new RetrieveSelfEmploymentAnnualSummaryRequestParser(
      validator = mockRetrieveSelfEmploymentAnnualSummaryValidator
    )
  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockRetrieveSelfEmploymentAnnualSummaryValidator.validate(retrieveSelfEmploymentAnnualSummaryRawData).returns(Nil)

        parser.parseRequest(retrieveSelfEmploymentAnnualSummaryRawData) shouldBe
          Right(RetrieveSelfEmploymentAnnualSummaryRequest(Nino(nino), businessId, taxYear))
      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockRetrieveSelfEmploymentAnnualSummaryValidator.validate(retrieveSelfEmploymentAnnualSummaryRawData)
          .returns(List(NinoFormatError))

        parser.parseRequest(retrieveSelfEmploymentAnnualSummaryRawData) shouldBe
          Left(ErrorWrapper(None, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockRetrieveSelfEmploymentAnnualSummaryValidator.validate(retrieveSelfEmploymentAnnualSummaryRawData)
          .returns(List(NinoFormatError, BusinessIdFormatError, TaxYearFormatError))

        parser.parseRequest(retrieveSelfEmploymentAnnualSummaryRawData) shouldBe
          Left(ErrorWrapper(None, BadRequestError, Some(Seq(NinoFormatError, BusinessIdFormatError, TaxYearFormatError))))
      }
    }
  }
}