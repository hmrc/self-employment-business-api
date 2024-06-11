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

package v3.createAmendAnnualSubmission

import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.Method.{DELETE, GET, PUT}
import shared.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.MockAuditService
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import v3.createAmendAnnualSubmission.def1.model.request.{Def1_CreateAmendAnnualSubmissionFixture, Def1_CreateAmendAnnualSubmissionRequestBody}
import v3.createAmendAnnualSubmission.model.request.Def1_CreateAmendAnnualSubmissionRequestData
import v3.createAmendAnnualSubmission.model.response.CreateAmendAnnualSubmissionHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreateAmendAnnualSubmissionControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockCreateAmendAnnualSubmissionService
    with MockCreateAmendAnnualSubmissionValidatorFactory
    with MockHateoasFactory
    with MockAuditService
    with Def1_CreateAmendAnnualSubmissionFixture {

  private val businessId: String = "XAIS12345678910"
  private val taxYear: String    = "2019-20"

  private val testHateoasLinks: Seq[Link] = Seq(
    Link(
      href = s"/individuals/business/self-employment/$validNino/$businessId/annual/$taxYear",
      method = PUT,
      rel = "create-and-amend-self-employment-annual-submission"),
    Link(href = s"/individuals/business/self-employment/$validNino/$businessId/annual/$taxYear", method = GET, rel = "self"),
    Link(
      href = s"/individuals/business/self-employment/$validNino/$businessId/annual/$taxYear",
      method = DELETE,
      rel = "delete-self-employment-annual-submission")
  )

  private val requestJson = createAmendAnnualSubmissionRequestBodyMtdJson(None, None, None)

  private val requestBody = Def1_CreateAmendAnnualSubmissionRequestBody(None, None, None)

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

  private val requestData = Def1_CreateAmendAnnualSubmissionRequestData(Nino(validNino), BusinessId(businessId), TaxYear.fromMtd(taxYear), requestBody)

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockedCreateAmendAnnualSubmissionService
          .createAmendAnnualSubmission(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), CreateAmendAnnualSubmissionHateoasData(Nino(validNino), BusinessId(businessId), taxYear))
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

        MockedCreateAmendAnnualSubmissionService
          .createAmendAnnualSubmission(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking {

    val controller = new CreateAmendAnnualSubmissionController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockAmendAnnualSubmissionValidatorFactory,
      service = mockAmendAnnualSubmissionService,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(validNino, businessId, taxYear)(fakeRequestWithBody(requestJson))

    protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "UpdateAnnualEmployment",
        transactionName = "self-employment-annual-summary-update",
        detail = GenericAuditDetail(
          versionNumber = "3.0",
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
