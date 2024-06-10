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

package api.support

import shared.controllers.EndpointLogContext
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import play.api.libs.json.{Format, Json}
import shared.UnitSpec
import shared.utils.Logging

class DownstreamResponseMappingSupportSpec extends UnitSpec {

  implicit val logContext: EndpointLogContext                = EndpointLogContext("ctrl", "ep")
  val mapping: DownstreamResponseMappingSupport with Logging = new DownstreamResponseMappingSupport with Logging {}

  val correlationId = "someCorrelationId"

  val errorCodeMap: PartialFunction[String, MtdError] = {
    case "ERR1"                 => NinoFormatError
    case "ERR2"                 => TaxYearFormatError
    case "DS"                   => InternalError
    case "UNMATCHED_STUB_ERROR" => RuleIncorrectGovTestScenarioError
  }

  case class TestClass(field: Option[String])

  object TestClass {
    implicit val format: Format[TestClass] = Json.format[TestClass]
  }

  "mapping downstream errors" when {
    "single error" when {
      "the error code is in the map provided" must {
        "use the mapping and wrap" in {
          mapping.mapDownstreamErrors(errorCodeMap)(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("ERR1")))) shouldBe
            ErrorWrapper(correlationId, NinoFormatError)
        }
      }

      "the error code is not in the map provided" must {
        "default to InternalError and wrap" in {
          mapping.mapDownstreamErrors(errorCodeMap)(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("UNKNOWN")))) shouldBe
            ErrorWrapper(correlationId, InternalError)
        }
      }
    }

    "downstream returns UNMATCHED_STUB_ERROR" must {
      "return an RuleIncorrectGovTestScenario error" in {
        mapping.mapDownstreamErrors(errorCodeMap)(
          ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode("UNMATCHED_STUB_ERROR")))) shouldBe
          ErrorWrapper(correlationId, RuleIncorrectGovTestScenarioError)
      }
    }

    "multiple errors" when {
      "the error codes is in the map provided" must {
        "use the mapping and wrap with main error type of BadRequest" in {
          mapping.mapDownstreamErrors(errorCodeMap)(
            ResponseWrapper(correlationId, DownstreamErrors(List(DownstreamErrorCode("ERR1"), DownstreamErrorCode("ERR2"))))) shouldBe
            ErrorWrapper(correlationId, BadRequestError, Some(Seq(NinoFormatError, TaxYearFormatError)))
        }
      }

      "the error code is not in the map provided" must {
        "default main error to InternalError ignore other errors" in {
          mapping.mapDownstreamErrors(errorCodeMap)(
            ResponseWrapper(correlationId, DownstreamErrors(List(DownstreamErrorCode("ERR1"), DownstreamErrorCode("UNKNOWN"))))) shouldBe
            ErrorWrapper(correlationId, InternalError)
        }
      }

      "one of the mapped errors is InternalError" must {
        "wrap the errors with main error type of InternalError" in {
          mapping.mapDownstreamErrors(errorCodeMap)(
            ResponseWrapper(correlationId, DownstreamErrors(List(DownstreamErrorCode("ERR1"), DownstreamErrorCode("DS"))))) shouldBe
            ErrorWrapper(correlationId, InternalError)
        }
      }
    }

    "the error code is an OutboundError" must {
      "return the error as is (in an ErrorWrapper)" in {
        mapping.mapDownstreamErrors(errorCodeMap)(ResponseWrapper(correlationId, OutboundError(BusinessIdFormatError))) shouldBe
          ErrorWrapper(correlationId, BusinessIdFormatError)
      }
    }

    "the error code is an OutboundError with multiple errors" must {
      "return the error as is (in an ErrorWrapper)" in {
        mapping.mapDownstreamErrors(errorCodeMap)(ResponseWrapper(correlationId, OutboundError(BusinessIdFormatError, Some(Seq(BVRError))))) shouldBe
          ErrorWrapper(correlationId, BusinessIdFormatError, Some(Seq(BVRError)))
      }
    }
  }

}
