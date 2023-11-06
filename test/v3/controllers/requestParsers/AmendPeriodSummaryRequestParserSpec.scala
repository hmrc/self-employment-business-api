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

import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import support.UnitSpec
import v3.fixtures.AmendPeriodSummaryFixture
import v3.mocks.validators.MockAmendPeriodSummaryValidator
import v3.models.request.amendPeriodSummary._

class AmendPeriodSummaryRequestParserSpec extends UnitSpec with AmendPeriodSummaryFixture {

  val nino: String                   = "AA123456B"
  val businessId: String             = "XAIS12345678910"
  val periodId: String               = "2019-01-01_2019-02-02"
  implicit val correlationId: String = "X-123"
  val tysTaxYear: String             = "2023-24"

  val inputData: AmendPeriodSummaryRawData = AmendPeriodSummaryRawData(
    nino = nino,
    businessId = businessId,
    periodId = periodId,
    body = amendPeriodSummaryBodyMtdJson,
    taxYear = None
  )

  val tysInputData: AmendPeriodSummaryRawData = AmendPeriodSummaryRawData(
    nino = nino,
    businessId = businessId,
    periodId = periodId,
    body = amendPeriodSummaryBodyMtdJson,
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
          Right(AmendPeriodSummaryRequestData(Nino(nino), BusinessId(businessId), periodId, amendPeriodSummaryBody, None))
      }

      "valid TYS request data is supplied" in new Test {
        MockAmendPeriodSummaryValidator.validate(tysInputData).returns(Nil)

        parser.parseRequest(tysInputData) shouldBe
          Right(AmendPeriodSummaryRequestData(Nino(nino), BusinessId(businessId), periodId, amendPeriodSummaryBody, Some(TaxYear.fromMtd(tysTaxYear))))
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
