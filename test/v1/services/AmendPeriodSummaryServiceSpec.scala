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

package v1.services

import api.controllers.EndpointLogContext
import api.models.domain.{BusinessId, Nino, PeriodId}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import v1.connectors.MockAmendPeriodSummaryConnector
import v1.models.request.amendPeriodSummary.{AmendPeriodSummaryBody, AmendPeriodSummaryRequestData}

import scala.concurrent.Future

class AmendPeriodSummaryServiceSpec extends ServiceSpec {

  val nino: String                   = "AA123456A"
  val businessId: String             = "XAIS12345678910"
  val periodId: String               = "2019-01-25_2020-01-25"
  implicit val correlationId: String = "X-123"

  private val requestData = AmendPeriodSummaryRequestData(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    periodId = PeriodId(periodId),
    body = AmendPeriodSummaryBody(None, None, None),
    taxYear = None
  )

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

        await(service.amendPeriodSummary(requestData)) shouldBe Right(ResponseWrapper(correlationId, ()))
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

          await(service.amendPeriodSummary(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = Seq(
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

      val extraTysErrors = Seq(
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
