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

package v2.controllers

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.hateoas.Method.{GET, PUT}
import api.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import api.models.domain.{BusinessId, Nino, PeriodId, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.MockAuditService
import mocks.MockAppConfig
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import v2.controllers.validators.MockAmendPeriodSummaryValidatorFactory
import v2.models.request.amendPeriodSummary._
import v2.models.response.amendPeriodSummary.AmendPeriodSummaryHateoasData
import v2.services.MockAmendPeriodSummaryService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendPeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockAmendPeriodSummaryService
    with MockAmendPeriodSummaryValidatorFactory
    with MockHateoasFactory
    with MockAppConfig
    with AmendPeriodSummaryFixture
    with MockAuditService {

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new PreTysTest {
        willUseValidator(returningSuccess(requestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendPeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, None))
          .returns(HateoasWrapper((), testHateoasLinks))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestBodyJson),
          maybeExpectedResponseBody = Some(responseJson),
          maybeAuditResponseBody = None
        )
      }

      "the TYS request received is valid" in new TysTest {
        willUseValidator(returningSuccess(requestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendPeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(TaxYear.fromMtd(taxYear))))
          .returns(HateoasWrapper((), testHateoasLinks))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestBodyJson),
          maybeExpectedResponseBody = Some(responseJson),
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
    MockAppConfig.featureSwitches.returns(Configuration("allowNegativeExpenses.enabled" -> false)).anyNumberOfTimes()

    val businessId: String = "XAIS12345678910"
    val periodId: String

    val requestData: AmendPeriodSummaryRequestData

    val requestBodyJson: JsValue            = amendPeriodSummaryBodyMtdJson
    val requestBody: AmendPeriodSummaryBody = amendPeriodSummaryBody

    val responseJson: JsValue

    val testHateoasLinks: Seq[Link]

    val controller = new AmendPeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockAmendPeriodSummaryValidatorFactory,
      service = mockAmendPeriodSummaryService,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "AmendPeriodicEmployment",
        transactionName = "self-employment-periodic-amend",
        detail = GenericAuditDetail(
          versionNumber = "2.0",
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> nino, "businessId" -> businessId, "periodId" -> periodId),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

  private trait PreTysTest extends Test {
    val periodId: String = "2019-01-01_2020-01-01"

    val requestData: AmendPeriodSummaryRequestData =
      AmendPeriodSummaryRequestData(Nino(nino), BusinessId(businessId), PeriodId(periodId), None, requestBody)

    val responseJson: JsValue = Json.parse(
      s"""
         |{
         |    "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "method": "PUT",
         |      "rel": "amend-self-employment-period-summary"
         |     
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "method": "GET",
         |      "rel": "self"
         |      
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period",
         |      "method": "GET",
         |      "rel": "list-self-employment-period-summaries"
         |      
         |    }
         |    ]
         |  }
    """.stripMargin
    )

    val testHateoasLinks: Seq[Link] = Seq(
      Link(
        href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId",
        method = PUT,
        rel = "amend-self-employment-period-summary"
      ),
      Link(href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", method = GET, rel = "self"),
      Link(
        href = s"/individuals/business/self-employment/$nino/$businessId/period",
        method = GET,
        rel = "list-self-employment-period-summaries"
      )
    )

    protected def callController(): Future[Result] =
      controller.handleRequest(nino, businessId, periodId, None)(fakePutRequest(requestBodyJson))

    override protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      super
        .event(auditResponse, requestBody)
        .copy(
          detail = super
            .event(auditResponse, requestBody)
            .detail
            .copy(
              params = Map("nino" -> nino, "businessId" -> businessId, "periodId" -> periodId)
            )
        )

  }

  private trait TysTest extends Test {
    val periodId: String = "2024-01-01_2025-01-01"
    val taxYear: String  = "2023-24"

    val requestData: AmendPeriodSummaryRequestData =
      AmendPeriodSummaryRequestData(Nino(nino), BusinessId(businessId), PeriodId(periodId), Some(TaxYear.fromMtd(taxYear)), requestBody)

    val responseJson: JsValue = Json.parse(
      s"""
         |{
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
         |      "method": "PUT",
         |      "rel": "amend-self-employment-period-summary"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
         |      "method": "GET",
         |      "rel": "self"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
         |      "method": "GET",
         |      "rel": "list-self-employment-period-summaries"
         |    }
         |  ]
         |}
    """.stripMargin
    )

    val testHateoasLinks: Seq[Link] = Seq(
      Link(
        href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
        method = PUT,
        rel = "amend-self-employment-period-summary"
      ),
      Link(href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]", method = GET, rel = "self"),
      Link(
        href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
        method = GET,
        rel = "list-self-employment-period-summaries"
      )
    )

    protected def callController(): Future[Result] =
      controller.handleRequest(nino, businessId, periodId, Some(taxYear))(fakePutRequest(requestBodyJson))

    override protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      super
        .event(auditResponse, requestBody)
        .copy(
          detail = super
            .event(auditResponse, requestBody)
            .detail
            .copy(
              params = Map("nino" -> nino, "businessId" -> businessId, "periodId" -> periodId, "taxYear" -> taxYear)
            )
        )

  }

}
