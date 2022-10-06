/*
 * Copyright 2022 HM Revenue & Customs
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

import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.MockIdGenerator
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockRetrieveAnnualSubmissionRequestParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockRetrieveAnnualSubmissionService}
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.models.errors._
import v1.models.hateoas.HateoasWrapper
import v1.models.outcomes.ResponseWrapper
import v1.models.request.retrieveAnnual.{RetrieveAnnualSubmissionRawData, RetrieveAnnualSubmissionRequest}
import v1.models.response.retrieveAnnual._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveAnnualSubmissionControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrieveAnnualSubmissionService
    with MockRetrieveAnnualSubmissionRequestParser
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator
    with RetrieveAnnualSubmissionFixture {

  private val nino          = "AA123456A"
  private val businessId    = "XAIS12345678910"
  private val taxYear       = "2020-21"
  private val correlationId = "X-123"

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new RetrieveAnnualSubmissionController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRetrieveAnnualSubmissionRequestParser,
      service = mockRetrieveAnnualSubmissionService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  private val rawData     = RetrieveAnnualSubmissionRawData(nino, businessId, taxYear)
  private val requestData = RetrieveAnnualSubmissionRequest(Nino(nino), BusinessId(businessId), TaxYear.fromMtd(taxYear))

  private val responseBody: RetrieveAnnualSubmissionResponse = retrieveResponseModel

  "handleRequest" should {
    "return Ok" when {
      "the request received is valid" in new Test {
        MockRetrieveAnnualSubmissionRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockRetrieveAnnualSubmissionService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, RetrieveAnnualSubmissionHateoasData(Nino(nino), BusinessId(businessId), TaxYear.fromMtd(taxYear)))
          .returns(HateoasWrapper(responseBody, testHateoasLinks))

        val result: Future[Result] = controller.handleRequest(nino, businessId, taxYear)(fakeRequest)

        contentAsJson(result) shouldBe Json.toJson(responseBody).as[JsObject] ++ testHateoasLinksJson
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return an error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockRetrieveAnnualSubmissionRequestParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.handleRequest(nino, businessId, taxYear)(fakeRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (RuleTaxYearRangeInvalidError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" should {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockRetrieveAnnualSubmissionRequestParser
              .parse(rawData)
              .returns(Right(requestData))

            MockRetrieveAnnualSubmissionService
              .retrieve(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.handleRequest(nino, businessId, taxYear)(fakeRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (InternalError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }

}
