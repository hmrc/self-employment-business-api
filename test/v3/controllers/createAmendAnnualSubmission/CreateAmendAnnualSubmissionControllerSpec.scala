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

package v3.controllers.createAmendAnnualSubmission

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.hateoas.Method.{DELETE, GET, PUT}
import api.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import api.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.MockAuditService
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import v3.controllers.createAmendAnnualSubmission.def1.model.request.{
  Def1_CreateAmendAnnualSubmissionFixture,
  Def1_CreateAmendAnnualSubmissionRequestBody
}
import v3.controllers.createAmendAnnualSubmission.model.request.Def1_CreateAmendAnnualSubmissionRequestData
import v3.controllers.createAmendAnnualSubmission.model.response.CreateAmendAnnualSubmissionHateoasData

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
      href = s"/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
      method = PUT,
      rel = "create-and-amend-self-employment-annual-submission"),
    Link(href = s"/individuals/business/self-employment/$nino/$businessId/annual/$taxYear", method = GET, rel = "self"),
    Link(
      href = s"/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
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
       |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
       |      "rel": "create-and-amend-self-employment-annual-submission",
       |      "method": "PUT"
       |    },
       |    {
       |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
       |      "rel": "self",
       |      "method": "GET"
       |    },
       |    {
       |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
       |      "rel": "delete-self-employment-annual-submission",
       |      "method": "DELETE"
       |    }
       |  ]
       |}
    """.stripMargin
  )

  private val requestData = Def1_CreateAmendAnnualSubmissionRequestData(Nino(nino), BusinessId(businessId), TaxYear.fromMtd(taxYear), requestBody)

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockedCreateAmendAnnualSubmissionService
          .createAmendAnnualSubmission(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), CreateAmendAnnualSubmissionHateoasData(Nino(nino), BusinessId(businessId), taxYear))
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

  private trait Test extends ControllerTest with AuditEventChecking[GenericAuditDetail] {

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

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId, taxYear)(fakePutRequest(requestJson))

    protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "UpdateAnnualEmployment",
        transactionName = "self-employment-annual-summary-update",
        detail = GenericAuditDetail(
          versionNumber = "3.0",
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> nino, "businessId" -> businessId, "taxYear" -> taxYear),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

  }

}
