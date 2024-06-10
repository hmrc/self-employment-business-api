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

package v3.amendPeriodSummary

import api.models.domain.PeriodId
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.Method.{GET, PUT}
import shared.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.MockAuditService
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.config.MockAppConfig
import v3.amendPeriodSummary.def1.model.Def1_AmendPeriodSummaryFixture
import v3.amendPeriodSummary.def2.model.Def2_AmendPeriodSummaryFixture
import v3.amendPeriodSummary.model.request.{AmendPeriodSummaryRequestData, Def1_AmendPeriodSummaryRequestData, Def2_AmendPeriodSummaryRequestData}
import v3.amendPeriodSummary.model.response.AmendPeriodSummaryHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendPeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockAmendPeriodSummaryService
    with MockAmendPeriodSummaryValidatorFactory
    with MockHateoasFactory
    with MockAuditService
    with MockAppConfig
    with Def1_AmendPeriodSummaryFixture
    with Def2_AmendPeriodSummaryFixture {

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "given a valid non-TYS request" in new PreTysTest {
        willUseValidator(returningSuccess(requestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendPeriodSummaryHateoasData(Nino(validNino), BusinessId(businessId), periodId, taxYear = None))
          .returns(HateoasWrapper((), testHateoasLinks))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestBodyJson),
          maybeExpectedResponseBody = Some(responseJson),
          maybeAuditResponseBody = None
        )
      }

      "given a valid TYS request" in new TysTest {
        willUseValidator(returningSuccess(requestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendPeriodSummaryHateoasData(Nino(validNino), BusinessId(businessId), periodId, taxYear = Some(parsedTaxYear)))
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

  private trait Test extends ControllerTest with AuditEventChecking {
    MockAppConfig.featureSwitchConfig.returns(Configuration("allowNegativeExpenses.enabled" -> false)).anyNumberOfTimes()

    val businessId = "XAIS12345678910"

    val periodId: String
    val requestData: AmendPeriodSummaryRequestData
    val requestBodyJson: JsValue
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
          versionNumber = "3.0",
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "businessId" -> businessId, "periodId" -> periodId),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

  private trait PreTysTest extends Test {
    val periodId = "2019-01-01_2020-01-01"

    val requestBodyJson: JsValue = def1_AmendPeriodSummaryBodyMtdJson

    val requestData: AmendPeriodSummaryRequestData =
      Def1_AmendPeriodSummaryRequestData(Nino(validNino), BusinessId(businessId), PeriodId(periodId), def1_AmendPeriodSummaryBody)

    val responseJson: JsValue = Json.parse(
      s"""
         |{
         |    "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$validNino/$businessId/period/$periodId",
         |      "method": "PUT",
         |      "rel": "amend-self-employment-period-summary"
         |     
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$validNino/$businessId/period/$periodId",
         |      "method": "GET",
         |      "rel": "self"
         |      
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$validNino/$businessId/period",
         |      "method": "GET",
         |      "rel": "list-self-employment-period-summaries"
         |      
         |    }
         |    ]
         |  }
    """.stripMargin
    )

    val testHateoasLinks: Seq[Link] = List(
      Link(s"/individuals/business/self-employment/$validNino/$businessId/period/$periodId", PUT, "amend-self-employment-period-summary"),
      Link(s"/individuals/business/self-employment/$validNino/$businessId/period/$periodId", GET, "self"),
      Link(s"/individuals/business/self-employment/$validNino/$businessId/period", GET, "list-self-employment-period-summaries")
    )

    protected def callController(): Future[Result] =
      controller.handleRequest(validNino, businessId, periodId, None)(fakePutRequest(requestBodyJson))

    override protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      super
        .event(auditResponse, requestBody)
        .copy(
          detail = super
            .event(auditResponse, requestBody)
            .detail
            .copy(
              params = Map("nino" -> validNino, "businessId" -> businessId, "periodId" -> periodId)
            )
        )

  }

  private trait TysTest extends Test {
    val periodId: String                 = "2024-01-01_2025-01-01"
    protected val taxYear: String        = "2023-24"
    protected val parsedTaxYear: TaxYear = TaxYear.fromMtd(taxYear)

    val requestBodyJson: JsValue = def2_AmendPeriodSummaryBodyMtdJson

    val requestData: Def2_AmendPeriodSummaryRequestData =
      Def2_AmendPeriodSummaryRequestData(Nino(validNino), BusinessId(businessId), PeriodId(periodId), parsedTaxYear, def2_AmendPeriodSummaryBody)

    val responseJson: JsValue = Json.parse(
      s"""
         |{
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$validNino/$businessId/period/$periodId[?taxYear=$taxYear]",
         |      "method": "PUT",
         |      "rel": "amend-self-employment-period-summary"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$validNino/$businessId/period/$periodId[?taxYear=$taxYear]",
         |      "method": "GET",
         |      "rel": "self"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$validNino/$businessId/period/$periodId[?taxYear=$taxYear]",
         |      "method": "GET",
         |      "rel": "list-self-employment-period-summaries"
         |    }
         |  ]
         |}
    """.stripMargin
    )

    val testHateoasLinks: Seq[Link] = List(
      Link(
        s"/individuals/business/self-employment/$validNino/$businessId/period/$periodId[?taxYear=$taxYear]",
        PUT,
        "amend-self-employment-period-summary"
      ),
      Link(s"/individuals/business/self-employment/$validNino/$businessId/period/$periodId[?taxYear=$taxYear]", GET, "self"),
      Link(
        s"/individuals/business/self-employment/$validNino/$businessId/period/$periodId[?taxYear=$taxYear]",
        GET,
        "list-self-employment-period-summaries")
    )

    protected def callController(): Future[Result] =
      controller.handleRequest(validNino, businessId, periodId, Some(taxYear))(fakePutRequest(requestBodyJson))

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
