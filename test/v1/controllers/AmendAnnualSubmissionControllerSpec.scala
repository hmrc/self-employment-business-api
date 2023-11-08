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

package v1.controllers

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.hateoas.Method.{DELETE, GET, PUT}
import api.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import v1.mocks.requestParsers.MockAmendAnnualSubmissionRequestParser
import v1.mocks.services.MockAmendAnnualSubmissionService
import v1.models.request.amendSEAnnual._
import v1.models.response.amendSEAnnual.AmendAnnualSubmissionHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendAnnualSubmissionControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockAmendAnnualSubmissionService
    with MockAmendAnnualSubmissionRequestParser
    with MockHateoasFactory
    with AmendAnnualSubmissionFixture {

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

  private val requestJson = amendAnnualSubmissionBodyMtdJson(None, None, None)

  private val requestBody = AmendAnnualSubmissionBody(None, None, None)

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

  private val rawData     = AmendAnnualSubmissionRawData(nino, businessId, taxYear, requestJson)
  private val requestData = AmendAnnualSubmissionRequestData(Nino(nino), BusinessId(businessId), TaxYear.fromMtd(taxYear), requestBody)

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        MockAmendAnnualSummaryRequestParser
          .requestFor(rawData)
          .returns(Right(requestData))

        MockAmendAnnualSubmissionService
          .amendAnnualSubmission(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendAnnualSubmissionHateoasData(Nino(nino), BusinessId(businessId), taxYear))
          .returns(HateoasWrapper((), testHateoasLinks))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseJson)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {

        MockAmendAnnualSummaryRequestParser
          .requestFor(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError)))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
        MockAmendAnnualSummaryRequestParser
          .requestFor(rawData)
          .returns(Right(requestData))

        MockAmendAnnualSubmissionService
          .amendAnnualSubmission(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val controller = new AmendAnnualSubmissionController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockAmendAnnualSummaryRequestParser,
      service = mockAmendAnnualSubmissionService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId, taxYear)(fakePutRequest(requestJson))

  }

}
