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

package v4.retrievePeriodSummary

import api.models.domain.PeriodId
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import v4.retrievePeriodSummary.def1.model.response.{Def1_RetrievePeriodSummaryResponse, Retrieve_PeriodDates}
import v4.retrievePeriodSummary.model.request.Def1_RetrievePeriodSummaryRequestData
import v4.retrievePeriodSummary.model.response.RetrievePeriodSummaryResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrievePeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockRetrievePeriodSummaryService
    with MockRetrievePeriodSummaryValidatorFactory {

  private val businessId = "XAIS12345678910"
  private val taxYear    = "2023-24"

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
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
  }

  private trait TysTest extends ControllerTest {
    val periodId = "2024-01-01_2025-01-01"

    val requestData: Def1_RetrievePeriodSummaryRequestData =
      Def1_RetrievePeriodSummaryRequestData(Nino(validNino), BusinessId(businessId), PeriodId(periodId), TaxYear.fromMtd(taxYear))

    val responseBody: RetrievePeriodSummaryResponse = Def1_RetrievePeriodSummaryResponse(
      periodDates = Retrieve_PeriodDates("2024-01-01", "2025-01-01"),
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

    protected def callController(): Future[Result] = controller.handleRequest(validNino, businessId, periodId, taxYear)(fakeGetRequest)
  }

}