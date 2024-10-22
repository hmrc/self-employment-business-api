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

package v4.amendPeriodSummary

import api.models.domain.PeriodId
import play.api.Configuration
import play.api.libs.json.JsValue
import play.api.mvc.Result
import shared.config.MockSharedAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.routing.{Version, Version3}
import shared.services.MockAuditService
import v4.amendPeriodSummary.def1.model.Def1_AmendPeriodSummaryFixture
import v4.amendPeriodSummary.def1.model.request.Def1_AmendPeriodSummaryRequestData
import v4.amendPeriodSummary.def2.model.Def2_AmendPeriodSummaryFixture
import v4.amendPeriodSummary.def2.model.request.Def2_AmendPeriodSummaryRequestData
import v4.amendPeriodSummary.model.request.AmendPeriodSummaryRequestData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendPeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockAmendPeriodSummaryService
    with MockAmendPeriodSummaryValidatorFactory
    with MockAuditService
    with MockSharedAppConfig
    with Def1_AmendPeriodSummaryFixture
    with Def2_AmendPeriodSummaryFixture {

  override val apiVersion: Version = Version3

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "given a valid non-TYS request" in new PreTysTest {
        willUseValidator(returningSuccess(requestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestBodyJson),
          maybeExpectedResponseBody = None,
          maybeAuditResponseBody = None
        )
      }

      "given a valid TYS request" in new TysTest {
        willUseValidator(returningSuccess(requestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestBodyJson),
          maybeExpectedResponseBody = None,
          maybeAuditResponseBody = None
        )
      }

      "return the error as per spec" when {
        "the parser validation fails" in new PreTysTest {
          willUseValidator(returning(NinoFormatError))
          runErrorTest(NinoFormatError)
        }

        "the service returns an error" in new PreTysTest {
          willUseValidator(returningSuccess(requestData))

          MockAmendPeriodSummaryService
            .amendPeriodSummary(requestData)
            .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

          runErrorTest(RuleTaxYearNotSupportedError)
        }
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {
    MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("allowNegativeExpenses.enabled" -> false)).anyNumberOfTimes()

    val businessId = "XAIS12345678910"

    val periodId: String
    val taxYear: String
    val requestData: AmendPeriodSummaryRequestData
    val requestBodyJson: JsValue

    val controller = new AmendPeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockAmendPeriodSummaryValidatorFactory,
      service = mockAmendPeriodSummaryService,
      auditService = mockAuditService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "AmendPeriodicEmployment",
        transactionName = "self-employment-periodic-amend",
        detail = GenericAuditDetail(
          versionNumber = apiVersion.name,
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "businessId" -> businessId, "periodId" -> periodId, "taxYear" -> taxYear),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

  private trait PreTysTest extends Test {
    val periodId                         = "2019-01-01_2020-01-01"
    val taxYear: String                  = "2019-20"
    protected val parsedTaxYear: TaxYear = TaxYear.fromMtd(taxYear)

    val requestBodyJson: JsValue = def1_AmendPeriodSummaryBodyMtdJson

    val requestData: AmendPeriodSummaryRequestData =
      Def1_AmendPeriodSummaryRequestData(Nino(validNino), BusinessId(businessId), PeriodId(periodId), parsedTaxYear, def1_AmendPeriodSummaryBody)

    protected def callController(): Future[Result] =
      controller.handleRequest(validNino, businessId, periodId, taxYear)(fakePostRequest(requestBodyJson))

    override protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      super
        .event(auditResponse, requestBody)
        .copy(
          detail = super
            .event(auditResponse, requestBody)
            .detail
            .copy(
              params = Map("nino" -> validNino, "businessId" -> businessId, "periodId" -> periodId, "taxYear" -> taxYear)
            )
        )

  }

  private trait TysTest extends Test {
    val periodId: String                 = "2024-01-01_2025-01-01"
    val taxYear: String                  = "2023-24"
    protected val parsedTaxYear: TaxYear = TaxYear.fromMtd(taxYear)

    val requestBodyJson: JsValue = def2_AmendPeriodSummaryBodyMtdJson

    val requestData: Def2_AmendPeriodSummaryRequestData =
      Def2_AmendPeriodSummaryRequestData(Nino(validNino), BusinessId(businessId), PeriodId(periodId), parsedTaxYear, def2_AmendPeriodSummaryBody)

    protected def callController(): Future[Result] =
      controller.handleRequest(validNino, businessId, periodId, taxYear)(fakePostRequest(requestBodyJson))

    override protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      super
        .event(auditResponse, requestBody)
        .copy(
          detail = super
            .event(auditResponse, requestBody)
            .detail
            .copy(
              params = Map("nino" -> validNino, "businessId" -> businessId, "periodId" -> periodId, "taxYear" -> taxYear)
            )
        )

  }

}
