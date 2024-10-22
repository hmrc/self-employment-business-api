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

package v4.createAmendCumulativePeriodSummary

import play.api.Configuration
import play.api.libs.json.JsValue
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.MockHateoasFactory
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.routing.{Version, Version3}
import shared.services.MockAuditService
import v4.createAmendCumulativePeriodSummary.def1.model.request.CumulativePeriodSummaryFixture
import v4.createAmendCumulativePeriodSummary.model.request.{
  Def1_CreateAmendCumulativePeriodSummaryRequestBody,
  Def1_CreateAmendCumulativePeriodSummaryRequestData
}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateAmendCumulativePeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockCreateAmendCumulativePeriodSummaryService
    with MockCreateAmendCumulativePeriodSummaryValidatorFactory
    with MockHateoasFactory
    with MockAuditService
    with CumulativePeriodSummaryFixture {

  private val businessId: String   = "XAIS12345678910"
  private val taxYear: String      = "2025-26"
  override val apiVersion: Version = Version3

  private val requestBody: Def1_CreateAmendCumulativePeriodSummaryRequestBody =
    requestMtdBodyJson.as[Def1_CreateAmendCumulativePeriodSummaryRequestBody]

  private val requestData =
    Def1_CreateAmendCumulativePeriodSummaryRequestData(Nino(validNino), BusinessId(businessId), TaxYear.fromMtd(taxYear), requestBody)

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockedCreateAmendCumulativePeriodSummaryService
          .createAmendCumulativePeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestMtdBodyJson),
          maybeExpectedResponseBody = None,
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

        MockedCreateAmendCumulativePeriodSummaryService
          .createAmendCumulativePeriodSummary(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

    val controller = new CreateAmendCumulativePeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockAmendCumulativePeriodSummaryValidatorFactory,
      service = mockAmendCumulativePeriodSummaryService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.handleRequest(validNino, businessId, taxYear)(fakePostRequest(requestMtdBodyJson))

    protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "UpdateCumulativeEmployment",
        transactionName = "self-employment-cumulative-summary-update",
        detail = GenericAuditDetail(
          versionNumber = apiVersion.name,
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "businessId" -> businessId, "taxYear" -> taxYear),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
