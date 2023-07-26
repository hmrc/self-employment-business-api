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

package v3.services

import api.controllers.EndpointLogContext
import api.models.domain.{BusinessId, Nino}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import v3.mocks.connectors.MockCreatePeriodSummaryConnector
import v3.models.request.createPeriodSummary._
import v3.models.response.createPeriodSummary.CreatePeriodSummaryResponse

import scala.concurrent.Future

class CreatePeriodSummaryServiceSpec extends ServiceSpec {

  val nino: String                   = "AA123456A"
  val businessId: String             = "XAIS12345678910"
  implicit val correlationId: String = "X-123"

  private val requestBody: CreatePeriodSummaryBody =
    CreatePeriodSummaryBody(
      PeriodDates("2019-08-24", "2019-08-24"),
      None,
      None,
      None
    )

  private val requestData = CreatePeriodSummaryRequest(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    body = requestBody
  )

  trait Test extends MockCreatePeriodSummaryConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new CreatePeriodSummaryService(
      connector = mockCreatePeriodicConnector
    )

  }

  "CreatePeriodSummaryServiceSpec" when {
    "createPeriodSummary" must {
      "return correct result for a success" in new Test {
        val connectorOutcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))
        val outcome: Right[Nothing, ResponseWrapper[CreatePeriodSummaryResponse]] =
          Right(ResponseWrapper(correlationId, CreatePeriodSummaryResponse("2019-08-24_2019-08-24")))

        MockCreatePeriodicConnector
          .createPeriodicSummary(requestData)
          .returns(Future.successful(connectorOutcome))

        await(service.createPeriodSummary(requestData)) shouldBe outcome
      }

      "map errors according to spec" when {
        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockCreatePeriodicConnector
              .createPeriodicSummary(requestData)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            await(service.createPeriodSummary(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = Seq(
          ("INVALID_NINO", NinoFormatError),
          ("INVALID_INCOME_SOURCE", BusinessIdFormatError),
          ("INVALID_PAYLOAD", InternalError),
          ("INVALID_PERIOD", RuleEndDateBeforeStartDateError),
          ("OVERLAPS_IN_PERIOD", RuleOverlappingPeriod),
          ("NOT_ALIGN_PERIOD", RuleMisalignedPeriod),
          ("BOTH_EXPENSES_SUPPLIED", RuleBothExpensesSuppliedError),
          ("NOT_CONTIGUOUS_PERIOD", RuleNotContiguousPeriod),
          ("NOT_ALLOWED_SIMPLIFIED_EXPENSES", RuleNotAllowedConsolidatedExpenses),
          ("NOT_FOUND_INCOME_SOURCE", NotFoundError),
          ("SERVER_ERROR", InternalError),
          ("SERVICE_UNAVAILABLE", InternalError)
        )

        val extraTysErrors = Seq(
          ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError),
          ("INVALID_CORRELATIONID", InternalError),
          ("INVALID_INCOME_SOURCE_ID", BusinessIdFormatError),
          ("PERIOD_EXISTS", RuleDuplicateSubmissionError),
          ("PERIOD_OVERLAP", RuleOverlappingPeriod),
          ("PERIOD_ALIGNMENT", RuleMisalignedPeriod),
          ("END_BEFORE_START", RuleEndDateBeforeStartDateError),
          ("PERIOD_HAS_GAPS", RuleNotContiguousPeriod),
          ("INCOME_SOURCE_NOT_FOUND", NotFoundError),
          ("INVALID_TAX_YEAR", InternalError),
          ("BUSINESS_INCOME_PERIOD_RESTRICTION", RuleBusinessIncomePeriodRestriction)
//          ("INVALID_SUBMISSION_PERIOD", RuleInvalidSubmissionPeriodError), // To be reinstated, see MTDSA-15595
//          ("INVALID_SUBMISSION_END_DATE", RuleInvalidSubmissionEndDateError) // To be reinstated, see MTDSA-15595
        )

        (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
      }
    }
  }

}
