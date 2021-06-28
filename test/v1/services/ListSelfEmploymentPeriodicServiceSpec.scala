/*
 * Copyright 2021 HM Revenue & Customs
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

import support.UnitSpec
import v1.models.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.controllers.EndpointLogContext
import v1.mocks.connectors.MockListSelfEmploymentPeriodicConnector
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.listSEPeriodic.ListSelfEmploymentPeriodicRequest
import v1.models.response.listSEPeriodic.{ListSelfEmploymentPeriodicResponse, PeriodDetails}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListSelfEmploymentPeriodicServiceSpec extends UnitSpec {

  val nino = Nino("AA123456A")
  val businessId = "XAIS12345678910"
  implicit val correlationId = "X-123"

  val response = ListSelfEmploymentPeriodicResponse(
    Seq(PeriodDetails(
      "2020-01-01_2020-01-01",
      "2020-01-01",
      "2020-01-01"
      )))

  val multipleResponse = ListSelfEmploymentPeriodicResponse(
    Seq(PeriodDetails(
      "2019-04-06_2020-04-05",
      "2019-04-06",
      "2020-04-05"
      ),
      PeriodDetails(
      "2019-04-06_2020-04-05",
      "2020-04-06",
      "2020-04-05"
      )))

  private val requestData = ListSelfEmploymentPeriodicRequest(nino, businessId)

  trait Test extends MockListSelfEmploymentPeriodicConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new ListSelfEmploymentPeriodicService(
      listSelfEmploymentPeriodicConnector = mockListSelfEmploymentPeriodicConnector
    )
  }

  "service" should {
    "service call successful" when {
      "return mapped result" in new Test {
        MockListSelfEmploymentPeriodicConnector.listSelfEmployment(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.listSelfEmploymentUpdatePeriods(requestData)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
      "return multiple responses" in new Test {
        MockListSelfEmploymentPeriodicConnector.listSelfEmployment(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, multipleResponse))))

        await(service.listSelfEmploymentUpdatePeriods(requestData)) shouldBe Right(ResponseWrapper(correlationId, multipleResponse))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockListSelfEmploymentPeriodicConnector.listSelfEmployment(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.listSelfEmploymentUpdatePeriods(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(
        "INVALID_NINO" -> NinoFormatError,
        "INVALID_INCOME_SOURCE_ID" -> BusinessIdFormatError,
        "NOT_FOUND_INCOME_SOURCE" -> NotFoundError,
        "SERVER_ERROR" -> DownstreamError,
        "SERVICE_UNAVAILABLE" -> DownstreamError
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}