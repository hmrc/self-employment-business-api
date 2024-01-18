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
import api.services.{ServiceOutcome, ServiceSpec}
import mocks.MockAppConfig
import play.api.Configuration
import v3.connectors.MockCreatePeriodSummaryConnector
import v3.models.request.createPeriodSummary._
import v3.models.request.createPeriodSummary.def2.{Def2_CreatePeriodSummaryRequestBody, Def2_Create_PeriodDates, Def2_Create_PeriodIncome}
import v3.models.response.createPeriodSummary.CreatePeriodSummaryResponse

import scala.concurrent.Future

class CreatePeriodSummaryServiceSpec extends ServiceSpec {

  private val nino                           = Nino("AA123456A")
  private val businessId                     = BusinessId("XAIS12345678910")
  private implicit val correlationId: String = "X-123"

  private val periodIncome = Def2_Create_PeriodIncome(turnover = Some(2000.00), None, taxTakenOffTradingIncome = Some(2000.00))

  private val parsedRequestBody = Def2_CreatePeriodSummaryRequestBody(Def2_Create_PeriodDates("2019-08-24", "2019-08-24"), None, None, None)

  private val requestData = Def2_CreatePeriodSummaryRequestData(
    nino = nino,
    businessId = businessId,
    body = parsedRequestBody.copy(periodIncome = Some(periodIncome))
  )

  private val response = CreatePeriodSummaryResponse(periodId = "2019-08-24_2019-08-24")

  "CreatePeriodSummaryService" should {
    "return a valid response" when {
      "given a valid request" in new Test {
        MockedCreatePeriodSummaryConnector
          .createPeriodicSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        val result: ServiceOutcome[CreatePeriodSummaryResponse] = await(service.createPeriodSummary(requestData))
        result shouldBe Right(ResponseWrapper(correlationId, response))
      }

      "map errors according to spec" when {
        def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service" in new Test {

            MockedCreatePeriodSummaryConnector
              .createPeriodicSummary(requestData)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            val result: ServiceOutcome[CreatePeriodSummaryResponse] = await(service.createPeriodSummary(requestData))
            result shouldBe Left(ErrorWrapper(correlationId, error))
          }

        val errors = List(
          ("INVALID_NINO", NinoFormatError),
          ("INVALID_INCOME_SOURCE", BusinessIdFormatError),
          ("INVALID_PAYLOAD", InternalError),
          ("INVALID_PERIOD", RuleEndBeforeStartDateError),
          ("OVERLAPS_IN_PERIOD", RuleOverlappingPeriod),
          ("NOT_ALIGN_PERIOD", RuleMisalignedPeriod),
          ("BOTH_EXPENSES_SUPPLIED", RuleBothExpensesSuppliedError),
          ("NOT_CONTIGUOUS_PERIOD", RuleNotContiguousPeriod),
          ("NOT_ALLOWED_SIMPLIFIED_EXPENSES", RuleNotAllowedConsolidatedExpenses),
          ("NOT_FOUND_INCOME_SOURCE", NotFoundError),
          ("SERVER_ERROR", InternalError),
          ("SERVICE_UNAVAILABLE", InternalError)
        )

        val extraTysErrors = List(
          ("TAX_YEAR_NOT_SUPPORTED", RuleTaxYearNotSupportedError),
          ("INVALID_CORRELATIONID", InternalError),
          ("INVALID_INCOME_SOURCE_ID", BusinessIdFormatError),
          ("PERIOD_EXISTS", RuleDuplicateSubmissionError),
          ("PERIOD_OVERLAP", RuleOverlappingPeriod),
          ("PERIOD_ALIGNMENT", RuleMisalignedPeriod),
          ("END_BEFORE_START", RuleEndBeforeStartDateError),
          ("PERIOD_HAS_GAPS", RuleNotContiguousPeriod),
          ("INCOME_SOURCE_NOT_FOUND", NotFoundError),
          ("INVALID_TAX_YEAR", InternalError),
          ("BUSINESS_INCOME_PERIOD_RESTRICTION", RuleBusinessIncomePeriodRestriction),
          ("SUBMISSION_DATE_ISSUE", RuleMisalignedPeriod)
          //          ("INVALID_SUBMISSION_PERIOD", RuleInvalidSubmissionPeriodError), // To be reinstated, see MTDSA-15595
//          ("INVALID_SUBMISSION_END_DATE", RuleInvalidSubmissionEndDateError) // To be reinstated, see MTDSA-15595
        )

        (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
      }
    }
  }

  trait Test extends MockCreatePeriodSummaryConnector with MockAppConfig {
    MockAppConfig.featureSwitches.returns(Configuration("cl290.enabled" -> true)).anyNumberOfTimes()

    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new CreatePeriodSummaryService(connector = mockCreatePeriodSummaryConnector)
  }

}
