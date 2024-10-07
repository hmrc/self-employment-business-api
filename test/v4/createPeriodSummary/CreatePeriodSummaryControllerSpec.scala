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

package v4.createPeriodSummary

import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.config.MockSharedAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.MockHateoasFactory
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{BusinessId, Nino}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.routing.{Version, Version3}
import shared.services.MockAuditService
import v4.createPeriodSummary.def1.model.request.Def1_CreatePeriodSummaryFixture
import v4.createPeriodSummary.model.request.{CreatePeriodSummaryRequestData, Def1_CreatePeriodSummaryRequestData}
import v4.createPeriodSummary.model.response.CreatePeriodSummaryResponse

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreatePeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockCreatePeriodSummaryService
    with MockCreatePeriodSummaryValidatorFactory
    with MockHateoasFactory
    with MockSharedAppConfig
    with MockAuditService
    with Def1_CreatePeriodSummaryFixture {

  private val businessId           = "XAIS12345678910"
  private val periodId             = "2017-01-25_2017-01-25"
  override val apiVersion: Version = Version3

  private val responseJson = Json.parse(
    s"""
       |{
       |  "periodId": "$periodId"
       |}
    """.stripMargin
  )

  private val requestData: CreatePeriodSummaryRequestData =
    Def1_CreatePeriodSummaryRequestData(Nino(validNino), BusinessId(businessId), fullMTDRequest)

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockedCreatePeriodicService
          .createPeriodic(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, CreatePeriodSummaryResponse(periodId)))))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestMtdBodyJson),
          maybeExpectedResponseBody = Some(responseJson),
          maybeAuditResponseBody = None
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTest(NinoFormatError)

      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockedCreatePeriodicService
          .createPeriodic(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {
    MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("allowNegativeExpenses.enabled" -> false)).anyNumberOfTimes()

    val controller = new CreatePeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockCreatePeriodSummaryValidatorFactory,
      service = mockCreatePeriodicService,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.handleRequest(validNino, businessId)(fakePostRequest(requestMtdBodyJson))

    protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "CreatePeriodicEmployment",
        transactionName = "self-employment-periodic-create",
        detail = GenericAuditDetail(
          versionNumber = apiVersion.name,
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "businessId" -> businessId),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
