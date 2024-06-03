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

package v2.services

import shared.controllers.EndpointLogContext
import shared.models.domain.{Nino, PeriodId}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.{ServiceOutcome, ServiceSpec}
import v2.connectors.MockAmendPeriodSummaryConnector
import v2.models.request.amendPeriodSummary.{AmendPeriodSummaryBody, AmendPeriodSummaryRequestData}

import scala.concurrent.Future

class AmendPeriodSummaryServiceSpec extends ServiceSpec {

  implicit private val correlationId: String = "X-123"

  private val nino       = "AA123456A"
  private val businessId = "XAIS12345678910"
  private val periodId   = "2019-01-25_2020-01-25"

  private val requestData =
    AmendPeriodSummaryRequestData(Nino(nino), BusinessId(businessId), PeriodId(periodId), None, AmendPeriodSummaryBody(None, None, None))

  trait Test extends MockAmendPeriodSummaryConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new AmendPeriodSummaryService(
      connector = mockAmendPeriodSummaryConnector
    )

  }

  "service" should {
    "service call successful" when {
      "return mapped result" in new Test {
        MockAmendPeriodSummaryConnector
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        val result: ServiceOutcome[Unit] = await(service.amendPeriodSummary(requestData))

        result shouldBe Right(ResponseWrapper(correlationId, ()))
      }
    }
  }

  "service" should {
    "map errors according to spec" when {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

          MockAmendPeriodSummaryConnector
            .amendPeriodSummary(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          val result: ServiceOutcome[Unit] = await(service.amendPeriodSummary(requestData))

          result shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        "INVALID_NINO"                    -> NinoFormatError,
        "INVALID_INCOME_SOURCE"           -> BusinessIdFormatError,
        "INVALID_DATE_FROM"               -> PeriodIdFormatError,
        "INVALID_DATE_TO"                 -> PeriodIdFormatError,
        "INVALID_PAYLOAD"                 -> InternalError,
        "NOT_FOUND_INCOME_SOURCE"         -> NotFoundError,
        "NOT_FOUND_PERIOD"                -> NotFoundError,
        "BOTH_EXPENSES_SUPPLIED"          -> RuleBothExpensesSuppliedError,
        "NOT_ALLOWED_SIMPLIFIED_EXPENSES" -> RuleNotAllowedConsolidatedExpenses,
        "SERVER_ERROR"                    -> InternalError,
        "SERVICE_UNAVAILABLE"             -> InternalError
      )

      val extraTysErrors = List(
        "INVALID_TAX_YEAR"                      -> TaxYearFormatError,
        "TAX_YEAR_NOT_SUPPORTED"                -> RuleTaxYearNotSupportedError,
        "INVALID_CORRELATION_ID"                -> InternalError,
        "INVALID_INCOMESOURCE_ID"               -> BusinessIdFormatError,
        "PERIOD_NOT_FOUND"                      -> NotFoundError,
        "INCOME_SOURCE_NOT_FOUND"               -> NotFoundError,
        "INCOME_SOURCE_DATA_NOT_FOUND"          -> NotFoundError,
        "BOTH_CONS_BREAKDOWN_EXPENSES_SUPPLIED" -> RuleBothExpensesSuppliedError
      )

      (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
    }
  }

}
