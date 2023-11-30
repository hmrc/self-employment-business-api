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
import mocks.MockFeatureSwitches
import v3.connectors.MockCreatePeriodSummaryConnector
import v3.models.request.createPeriodSummary._
import v3.models.response.createPeriodSummary.CreatePeriodSummaryResponse

import scala.concurrent.Future

class CreatePeriodSummaryServiceSpec extends ServiceSpec with MockFeatureSwitches {

  implicit private val correlationId: String = "X-123"

  private val nino       = Nino("AA123456A")
  private val businessId = BusinessId("XAIS12345678910")

  private val periodIncomeWithCl290Enabled  = PeriodIncome(Some(2000.00), None, taxTakenOffTradingIncome = Some(2000.00))
  private val periodIncomeWithCl290Disabled = PeriodIncome(Some(2000.00), None, taxTakenOffTradingIncome = None)

  private val parsedRequestBody = CreatePeriodSummaryRequestBody(PeriodDates("2019-08-24", "2019-08-24"), None, None, None)

  private val requestDataWithCl290Enabled =
    CreatePeriodSummaryRequestData(nino, businessId, parsedRequestBody.copy(periodIncome = Some(periodIncomeWithCl290Enabled)))

  private val requestDataWithCl290Disabled =
    CreatePeriodSummaryRequestData(nino, businessId, parsedRequestBody.copy(periodIncome = Some(periodIncomeWithCl290Disabled)))

  "CreatePeriodSummaryServiceSpec" should {
    "return a valid response" when {
      "a valid request is supplied with cl290 feature switch enabled" in new Test with Cl290Enabled with WIS008Enabled {
        val connectorOutcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))
        val outcome: Right[Nothing, ResponseWrapper[CreatePeriodSummaryResponse]] =
          Right(ResponseWrapper(correlationId, CreatePeriodSummaryResponse("2019-08-24_2019-08-24")))

        MockCreatePeriodicConnector
          .createPeriodicSummary(requestDataWithCl290Enabled)
          .returns(Future.successful(connectorOutcome))

        val result: ServiceOutcome[CreatePeriodSummaryResponse] = await(service.createPeriodSummary(requestDataWithCl290Enabled))
        result shouldBe outcome
      }

      "a valid request is supplied with cl290 feature switch disabled" in new Test with Cl290Disabled with WIS008Enabled {
        val connectorOutcome: Right[Nothing, ResponseWrapper[Unit]] = Right(ResponseWrapper(correlationId, ()))
        val outcome: Right[Nothing, ResponseWrapper[CreatePeriodSummaryResponse]] =
          Right(ResponseWrapper(correlationId, CreatePeriodSummaryResponse("2019-08-24_2019-08-24")))

        MockCreatePeriodicConnector
          .createPeriodicSummary(requestDataWithCl290Disabled)
          .returns(Future.successful(connectorOutcome))

        val result: ServiceOutcome[CreatePeriodSummaryResponse] = await(service.createPeriodSummary(requestDataWithCl290Disabled))
        result shouldBe outcome
      }

      "map errors according to spec" when {
        def serviceError(wis008Enabled: Boolean)(downstreamErrorCode: String, error: MtdError): Unit =
          s"a $downstreamErrorCode error is returned from the service and WIS008 is $wis008Enabled" in new Test with Cl290Disabled {
            withWIS008Enabled(wis008Enabled)

            MockCreatePeriodicConnector
              .createPeriodicSummary(requestDataWithCl290Disabled)
              .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

            val result: ServiceOutcome[CreatePeriodSummaryResponse] = await(service.createPeriodSummary(requestDataWithCl290Disabled))
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
        )

        val wis008Errors = List(
          ("INVALID_SUBMISSION_PERIOD", RuleInvalidSubmissionPeriodError),
          ("INVALID_SUBMISSION_END_DATE", RuleInvalidSubmissionEndDateError)
        )

        (errors ++ extraTysErrors ++ wis008Errors).foreach((serviceError(wis008Enabled = true) _).tupled)

        List(
          "INVALID_SUBMISSION_PERIOD",
          "INVALID_SUBMISSION_END_DATE"
        ).foreach(serviceError(wis008Enabled = false)(_, InternalError))
      }
    }
  }

  private trait Test extends MockCreatePeriodSummaryConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new CreatePeriodSummaryService(mockCreatePeriodicConnector)
  }

  private trait Cl290Enabled {
    MockFeatureSwitches.isCl290Enabled.returns(true)
  }

  private trait Cl290Disabled {
    MockFeatureSwitches.isCl290Enabled.returns(false)
  }

  private def withWIS008Enabled(enabled: Boolean): Unit = {
    MockFeatureSwitches.isWIS008Enabled.returns(enabled)
  }

  private trait WIS008Enabled {
    withWIS008Enabled(true)
  }

}
