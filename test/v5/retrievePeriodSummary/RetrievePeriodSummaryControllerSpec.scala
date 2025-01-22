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

package v5.retrievePeriodSummary

import api.models.domain.PeriodId
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import v5.retrievePeriodSummary.def1.model.request.Def1_RetrievePeriodSummaryRequestData
import v5.retrievePeriodSummary.def1.model.response.Def1_Retrieve_PeriodDates
import v5.retrievePeriodSummary.def2.model.request.Def2_RetrievePeriodSummaryRequestData
import v5.retrievePeriodSummary.def2.model.response.Def2_Retrieve_PeriodDates
import v5.retrievePeriodSummary.model.response.{Def1_RetrievePeriodSummaryResponse, Def2_RetrievePeriodSummaryResponse, RetrievePeriodSummaryResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrievePeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockRetrievePeriodSummaryService
    with MockRetrievePeriodSummaryValidatorFactory {

  private val businessId = "XAIS12345678910"
  private val tysTaxYear = "2023-24"
  private val taxYear    = "2019-20"

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "given a valid non-TYS request" in new NonTysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrievePeriodSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseJson)
        )
      }
      "given a valid TYS request" in new TysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrievePeriodSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseJson)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new NonTysTest {
        willUseValidator(returning(NinoFormatError))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new NonTysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrievePeriodSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait NonTysTest extends ControllerTest {
    val periodId = "2019-01-01_2020-01-01"

    val requestData: Def1_RetrievePeriodSummaryRequestData =
      Def1_RetrievePeriodSummaryRequestData(Nino(validNino), BusinessId(businessId), PeriodId(periodId), TaxYear.fromMtd(taxYear))

    val responseBody: RetrievePeriodSummaryResponse =
      Def1_RetrievePeriodSummaryResponse(Def1_Retrieve_PeriodDates("2019-01-01", "2020-01-01"), None, None, None)

    val responseJson: JsValue = Json.parse(
      s"""
         |{
         |  "periodDates": {
         |    "periodStartDate": "2019-01-01",
         |    "periodEndDate": "2020-01-01"
         |  }
         |}
      """.stripMargin
    )

    val controller = new RetrievePeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrievePeriodSummaryValidatorFactory,
      service = mockRetrievePeriodSummaryService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.handleRequest(validNino, businessId, periodId, tysTaxYear)(fakeGetRequest)
  }

  private trait TysTest extends ControllerTest {
    val periodId = "2024-01-01_2025-01-01"

    val requestData: Def2_RetrievePeriodSummaryRequestData =
      Def2_RetrievePeriodSummaryRequestData(Nino(validNino), BusinessId(businessId), PeriodId(periodId), TaxYear.fromMtd(tysTaxYear))

    val responseBody: RetrievePeriodSummaryResponse = Def2_RetrievePeriodSummaryResponse(
      periodDates = Def2_Retrieve_PeriodDates("2024-01-01", "2025-01-01"),
      periodIncome = None,
      periodExpenses = None,
      periodDisallowableExpenses = None
    )

    val responseJson: JsValue = Json.parse(
      s"""
         |{
         |  "periodDates": {
         |    "periodStartDate": "2024-01-01",
         |    "periodEndDate": "2025-01-01"
         |  }
         |}
      """.stripMargin
    )

    val controller = new RetrievePeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrievePeriodSummaryValidatorFactory,
      service = mockRetrievePeriodSummaryService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.handleRequest(validNino, businessId, periodId, tysTaxYear)(fakeGetRequest)
  }

}
