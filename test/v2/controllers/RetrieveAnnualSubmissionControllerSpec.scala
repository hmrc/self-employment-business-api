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

import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.Method.{DELETE, GET, PUT}
import shared.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import play.api.mvc.Result
import v2.controllers.validators.MockRetrieveAnnualSubmissionValidatorFactory
import v2.fixtures.RetrieveAnnualSubmissionFixture
import v2.models.request.retrieveAnnual.RetrieveAnnualSubmissionRequestData
import v2.models.response.retrieveAnnual.{RetrieveAnnualSubmissionHateoasData, RetrieveAnnualSubmissionResponse}
import v2.services.MockRetrieveAnnualSubmissionService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveAnnualSubmissionControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockRetrieveAnnualSubmissionService
    with MockRetrieveAnnualSubmissionValidatorFactory
    with MockHateoasFactory
    with RetrieveAnnualSubmissionFixture {

  private val businessId: String = "XAIS12345678910"
  private val taxYear: String    = "2020-21"

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

  private val requestData = RetrieveAnnualSubmissionRequestData(Nino(validNino), BusinessId(businessId), TaxYear.fromMtd(taxYear))

  private val responseBody: RetrieveAnnualSubmissionResponse = retrieveResponseModel

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrieveAnnualSubmissionService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, RetrieveAnnualSubmissionHateoasData(Nino(validNino), BusinessId(businessId), taxYear))
          .returns(HateoasWrapper(responseBody, testHateoasLinks))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(mtdRetrieveAnnualSubmissionJsonWithHateoas(validNino, businessId, taxYear))
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

        MockRetrieveAnnualSubmissionService
          .retrieve(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {

    private val controller = new RetrieveAnnualSubmissionController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrieveAnnualSubmissionValidatorFactory,
      service = mockRetrieveAnnualSubmissionService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(validNino, businessId, taxYear)(fakeGetRequest)
  }

}
