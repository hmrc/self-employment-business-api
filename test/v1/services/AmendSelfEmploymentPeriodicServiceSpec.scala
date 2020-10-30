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
import v1.mocks.connectors.MockAmendSelfEmploymentPeriodicConnector
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.amendSEPeriodic.{AmendPeriodicBody, AmendPeriodicRequest}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendSelfEmploymentPeriodicServiceSpec extends UnitSpec {

  val nino = Nino("AA123456A")
  val businessId = "XAIS12345678910"
  val periodId = "2019-01-25_2020-01-25"
  private val correlationId = "X-123"

  private val requestData = AmendPeriodicRequest(nino, businessId, periodId, AmendPeriodicBody(None, None, None))

  trait Test extends MockAmendSelfEmploymentPeriodicConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new AmendSelfEmploymentPeriodicService(
      connector = mockAmendPeriodicUpdateConnector
    )
  }

  "service" should {
    "service call successful" when {
      "return mapped result" in new Test {
        MockAmendSelfEmploymentPeriodicConnector.amendPeriodicUpdate(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        await(service.amendPeriodicUpdate(requestData)) shouldBe Right(ResponseWrapper(correlationId, ()))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockAmendSelfEmploymentPeriodicConnector.amendPeriodicUpdate(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.amendPeriodicUpdate(requestData)) shouldBe Left(ErrorWrapper(Some(correlationId), error))
        }

      val input = Seq(
        "INVALID_NINO" -> NinoFormatError,
        "INVALID_INCOME_SOURCE" -> BusinessIdFormatError,
        "INVALID_DATE_FROM" -> PeriodIdFormatError,
        "INVALID_DATE_TO" -> PeriodIdFormatError,
        "NOT_FOUND_INCOME_SOURCE" -> NotFoundError,
        "NOT_FOUND_PERIOD" -> NotFoundError,
        "NOT_FOUND_NINO" -> NotFoundError,
        "BOTH_EXPENSES_SUPPLIED" -> RuleBothExpensesSuppliedError,
        "NOT_ALLOWED_SIMPLIFIED_EXPENSES" -> RuleNotAllowedConsolidatedExpenses,
        "SERVER_ERROR" -> DownstreamError,
        "SERVICE_UNAVAILABLE" -> DownstreamError
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}