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

package v1.support

import play.api.libs.json.{Format, Json}
import support.UnitSpec
import utils.Logging
import v1.controllers.EndpointLogContext
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.response.createPeriodSummary.CreatePeriodSummaryResponse

class DesResponseMappingSupportSpec extends UnitSpec {

  implicit val logContext: EndpointLogContext         = EndpointLogContext("ctrl", "ep")
  val mapping: DesResponseMappingSupport with Logging = new DesResponseMappingSupport with Logging {}

  val correlationId = "someCorrelationId"

  val errorCodeMap: PartialFunction[String, MtdError] = {
    case "ERR1" => NinoFormatError
    case "ERR2" => TaxYearFormatError
    case "DS"   => DownstreamError
  }

  case class TestClass(field: Option[String])

  object TestClass {
    implicit val format: Format[TestClass] = Json.format[TestClass]
  }

  "validateRetrieveResponse" when {
    "passed an empty response" should {
      "return a NotFoundError error" in {
        mapping.validateRetrieveResponse(ResponseWrapper(correlationId, TestClass(None))) shouldBe
          Left(ErrorWrapper(correlationId, NotFoundError))
      }
    }
    "passed anything else" should {
      "pass it through" in {
        mapping.validateRetrieveResponse(ResponseWrapper(correlationId, NotFoundError)) shouldBe
          Right(ResponseWrapper(correlationId, NotFoundError))
      }
    }
  }

  "mapping Des errors" when {
    "single error" when {
      "the error code is in the map provided" must {
        "use the mapping and wrap" in {
          mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode("ERR1")))) shouldBe
            ErrorWrapper(correlationId, NinoFormatError)
        }
      }

      "the error code is not in the map provided" must {
        "default to DownstreamError and wrap" in {
          mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode("UNKNOWN")))) shouldBe
            ErrorWrapper(correlationId, DownstreamError)
        }
      }
    }

    "multiple errors" when {
      "the error codes is in the map provided" must {
        "use the mapping and wrap with main error type of BadRequest" in {
          mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, DesErrors(List(DesErrorCode("ERR1"), DesErrorCode("ERR2"))))) shouldBe
            ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError)))
        }
      }

      "the error code is not in the map provided" must {
        "default main error to DownstreamError ignore other errors" in {
          mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, DesErrors(List(DesErrorCode("ERR1"), DesErrorCode("UNKNOWN"))))) shouldBe
            ErrorWrapper(correlationId, DownstreamError)
        }
      }

      "one of the mapped errors is DownstreamError" must {
        "wrap the errors with main error type of DownstreamError" in {
          mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, DesErrors(List(DesErrorCode("ERR1"), DesErrorCode("DS"))))) shouldBe
            ErrorWrapper(correlationId, DownstreamError)
        }
      }
    }

    "the error code is an OutboundError" must {
      "return the error as is (in an ErrorWrapper)" in {
        mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, OutboundError(BusinessIdFormatError))) shouldBe
          ErrorWrapper(correlationId, BusinessIdFormatError)
      }
    }

    "the error code is an OutboundError with multiple errors" must {
      "return the error as is (in an ErrorWrapper)" in {
        mapping.mapDesErrors(errorCodeMap)(ResponseWrapper(correlationId, OutboundError(BusinessIdFormatError, Some(Seq(BVRError))))) shouldBe
          ErrorWrapper(correlationId, BusinessIdFormatError, Some(Seq(BVRError)))
      }
    }
  }

  "createPeriodId" should {
    "return a valid periodId" when {
      "given two valid dates" in {
        val responseModel = CreatePeriodSummaryResponse("2017-09-09_2017-09-09")
        mapping.createPeriodId(ResponseWrapper(correlationId, ()), "2017-09-09", "2017-09-09") shouldBe
          Right(ResponseWrapper(correlationId, responseModel))
      }
    }
  }

}
