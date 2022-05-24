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

package v1.services

import v1.controllers.EndpointLogContext
import v1.mocks.connectors.MockListPeriodicConnector
import v1.models.domain.{BusinessId, Nino}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.listPeriodic.ListPeriodicRequest
import v1.models.response.listPeriodic.{ListPeriodicResponse, PeriodDetails}

import scala.concurrent.Future

class ListPeriodicServiceSpec extends ServiceSpec {

  val nino: String                   = "AA123456A"
  val businessId: String             = "XAIS12345678910"
  implicit val correlationId: String = "X-123"

  val response: ListPeriodicResponse[PeriodDetails] = ListPeriodicResponse(
    Seq(
      PeriodDetails(
        "2020-01-01_2020-01-01",
        "2020-01-01",
        "2020-01-01"
      ))
  )

  private val requestData = ListPeriodicRequest(
    nino = Nino(nino),
    businessId = BusinessId(businessId)
  )

  trait Test extends MockListPeriodicConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new ListPeriodicService(
      connector = mockListPeriodicConnector
    )

  }

  "service" should {
    "service call successful" when {
      "return mapped result" in new Test {
        MockListPeriodicConnector
          .listPeriods(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.listPeriods(requestData)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {
      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockListPeriodicConnector
            .listPeriods(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.listPeriods(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(
        "INVALID_NINO"            -> NinoFormatError,
        "INVALID_INCOME_SOURCEID" -> BusinessIdFormatError,
        "NOT_FOUND_INCOME_SOURCE" -> NotFoundError,
        "SERVER_ERROR"            -> DownstreamError,
        "SERVICE_UNAVAILABLE"     -> DownstreamError
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }

}
