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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.Method.{DELETE, GET, PUT}
import shared.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.routing.{Version, Version2}
import shared.services.MockAuditService
import v2.controllers.validators.MockAmendAnnualSubmissionValidatorFactory
import v2.fixtures.AmendAnnualSubmissionFixture
import v2.models.request.amendSEAnnual.{AmendAnnualSubmissionBody, AmendAnnualSubmissionRequestData}
import v2.models.response.amendSEAnnual.AmendAnnualSubmissionHateoasData
import v2.services.MockAmendAnnualSubmissionService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendAnnualSubmissionControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockAmendAnnualSubmissionService
    with MockAmendAnnualSubmissionValidatorFactory
    with MockHateoasFactory
    with AmendAnnualSubmissionFixture
    with MockAuditService {

  private val businessId: String   = "XAIS12345678910"
  private val taxYear: String      = "2019-20"
  override val apiVersion: Version = Version2

  private val testHateoasLinks: Seq[Link] = Seq(
    Link(
      href = s"/individuals/business/self-employment/$validNino/$businessId/annual/$taxYear",
      method = PUT,
      rel = "create-and-amend-self-employment-annual-submission"
    ),
    Link(href = s"/individuals/business/self-employment/$validNino/$businessId/annual/$taxYear", method = GET, rel = "self"),
    Link(
      href = s"/individuals/business/self-employment/$validNino/$businessId/annual/$taxYear",
      method = DELETE,
      rel = "delete-self-employment-annual-submission")
  )

  private val requestJson = amendAnnualSubmissionBodyMtdJson(None, None, None)

  private val requestBody = AmendAnnualSubmissionBody(None, None, None)

  private val responseJson: JsValue = Json.parse(
    s"""
       |{
       |  "links": [
       |    {
       |      "href": "/individuals/business/self-employment/$validNino/$businessId/annual/$taxYear",
       |      "rel": "create-and-amend-self-employment-annual-submission",
       |      "method": "PUT"
       |    },
       |    {
       |      "href": "/individuals/business/self-employment/$validNino/$businessId/annual/$taxYear",
       |      "rel": "self",
       |      "method": "GET"
       |    },
       |    {
       |      "href": "/individuals/business/self-employment/$validNino/$businessId/annual/$taxYear",
       |      "rel": "delete-self-employment-annual-submission",
       |      "method": "DELETE"
       |    }
       |  ]
       |}
    """.stripMargin
  )

  private val requestData = AmendAnnualSubmissionRequestData(Nino(validNino), BusinessId(businessId), TaxYear.fromMtd(taxYear), requestBody)

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockAmendAnnualSubmissionService
          .amendAnnualSubmission(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendAnnualSubmissionHateoasData(Nino(validNino), BusinessId(businessId), taxYear))
          .returns(HateoasWrapper((), testHateoasLinks))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestJson),
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

        MockAmendAnnualSubmissionService
          .amendAnnualSubmission(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking {

    private val controller = new AmendAnnualSubmissionController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockAmendAnnualSubmissionValidatorFactory,
      service = mockAmendAnnualSubmissionService,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "UpdateAnnualEmployment",
        transactionName = "self-employment-annual-summary-update",
        detail = GenericAuditDetail(
          versionNumber = "2.0",
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> validNino, "businessId" -> businessId, "taxYear" -> taxYear),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

    protected def callController(): Future[Result] =
      controller.handleRequest(validNino, businessId, taxYear)(fakeRequestWithBody(requestJson))

  }

}
