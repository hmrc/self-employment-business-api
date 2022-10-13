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
import v1.mocks.validators.MockListPeriodSummariesValidator
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.errors._
import v1.models.request.listPeriodSummaries.{ListPeriodSummariesRawData, ListPeriodSummariesRequest}

class ListPeriodSummariesRequestParserSpec extends UnitSpec {

  val nino: String                   = "AA123456B"
  val businessId: String             = "XAIS12345678910"
  implicit val correlationId: String = "X-123"
  val taxYear: String                = "2024-25"

  val rawData: ListPeriodSummariesRawData = ListPeriodSummariesRawData(
    nino = nino,
    businessId = businessId,
    None
  )

  val rawTysData = rawData.copy(taxYear = Some(taxYear))

  trait Test extends MockListPeriodSummariesValidator {

    lazy val parser: ListPeriodSummariesRequestParser = new ListPeriodSummariesRequestParser(
      validator = mockListPeriodSummariesValidator
    )

  }

  "parse" should {
    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockListPeriodSummariesValidator.validate(rawData).returns(Nil)

        parser.parseRequest(rawData) shouldBe
          Right(ListPeriodSummariesRequest(Nino(nino), BusinessId(businessId), None))
      }
      "valid request data with Tys data is supplied" in new Test {
        MockListPeriodSummariesValidator.validate(rawTysData).returns(Nil)

        parser.parseRequest(rawTysData) shouldBe
          Right(ListPeriodSummariesRequest(Nino(nino), BusinessId(businessId), Some(TaxYear.fromMtd(taxYear))))
      }
    }

    "return an ErrorWrapper" when {
      "a single validation error occurs" in new Test {
        MockListPeriodSummariesValidator
          .validate(rawData)
          .returns(List(NinoFormatError))

        parser.parseRequest(rawData) shouldBe
          Left(ErrorWrapper(correlationId, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockListPeriodSummariesValidator
          .validate(rawData)
          .returns(List(NinoFormatError, BusinessIdFormatError))

        parser.parseRequest(rawData) shouldBe
          Left(ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, BusinessIdFormatError))))
      }
    }
  }

}
