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

package v1.services

import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.controllers.EndpointLogContext
import v1.mocks.connectors.MockRetrieveSelfEmploymentPeriodicConnector
import v1.models.errors.{BusinessIdFormatError, DesErrorCode, DesErrors, DownstreamError, ErrorWrapper, MtdError, NinoFormatError, NotFoundError, PeriodIdFormatError}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.retrieveSEPeriodic.RetrieveSelfEmploymentPeriodicRequest
import v1.models.response.retrieveSEPeriodic.{ConsolidatedExpenses, Incomes, IncomesAmountObject, RetrieveSelfEmploymentPeriodicResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveSelfEmploymentPeriodicServiceSpec extends UnitSpec {

  val nino = Nino("AA123456A")
  val businessId = "XAIS12345678910"
  val periodId = "2019-01-25_2020-01-25"
  implicit val correlationId = "X-123"

  val response = RetrieveSelfEmploymentPeriodicResponse(
    "2019-01-25",
    "2020-01-25",
    Some(Incomes(
      Some(IncomesAmountObject(
        1000.20
      )),
      Some(IncomesAmountObject(
        1000.20
      ))
    )),
    Some(ConsolidatedExpenses(
      1000.20
    )),
    None
  )

  private val requestData = RetrieveSelfEmploymentPeriodicRequest(nino, businessId, periodId)

  trait Test extends MockRetrieveSelfEmploymentPeriodicConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new RetrieveSelfEmploymentPeriodicService(
      retrieveSelfEmploymentPeriodicUpdateConnector = mockRetrieveSelfEmploymentPeriodicUpdateConnector
    )
  }

  "service" should {
    "service call successful" when {
      "return mapped result" in new Test {
        MockRetrieveSelfEmploymentPeriodicUpdateConnector.retrieveSelfEmployment(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieveSelfEmploymentPeriodicUpdate(requestData)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockRetrieveSelfEmploymentPeriodicUpdateConnector.retrieveSelfEmployment(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.retrieveSelfEmploymentPeriodicUpdate(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(
        "INVALID_NINO" -> NinoFormatError,
        "INVALID_INCOME_SOURCE_ID" -> BusinessIdFormatError,
        "INVALID_DATE_FROM" -> PeriodIdFormatError,
        "INVALID_DATE_TO" -> PeriodIdFormatError,
        "NOT_FOUND_INCOME_SOURCE" -> NotFoundError,
        "NOT_FOUND_PERIOD" -> NotFoundError,
        "SERVER_ERROR" -> DownstreamError,
        "SERVICE_UNAVAILABLE" -> DownstreamError
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}