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

import api.controllers.ControllerBaseSpec
import api.mocks.MockIdGenerator
import api.mocks.hateoas.MockHateoasFactory
import api.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import api.models.domain.{BusinessId, Nino, PeriodId, TaxYear}
import api.models.errors._
import api.models.hateoas.{HateoasWrapper, Link}
import api.models.hateoas.Method.GET
import api.models.outcomes.ResponseWrapper
import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.requestParsers.MockRetrievePeriodSummaryRequestParser
import v1.mocks.services.MockRetrievePeriodSummaryService
import v1.models.request.retrievePeriodSummary.{RetrievePeriodSummaryRawData, RetrievePeriodSummaryRequest}
import v1.models.response.retrievePeriodSummary._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrievePeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockRetrievePeriodSummaryService
    with MockRetrievePeriodSummaryRequestParser
    with MockHateoasFactory
    with MockAuditService
    with MockIdGenerator {

  private val nino          = "AA123456A"
  private val businessId    = "XAIS12345678910"
  private val periodId      = "2019-01-01_2020-01-01"
  private val correlationId = "X-123"
  private val taxYear       = "2023-24"

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new RetrievePeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRetrievePeriodSummaryRequestParser,
      service = mockRetrievePeriodSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)
  }

  private val rawData        = RetrievePeriodSummaryRawData(nino, businessId, periodId, None)
  private val tysRawData     = RetrievePeriodSummaryRawData(nino, businessId, periodId, Some(taxYear))
  private val requestData    = RetrievePeriodSummaryRequest(Nino(nino), BusinessId(businessId), PeriodId(periodId), None)
  private val tysRequestData = RetrievePeriodSummaryRequest(Nino(nino), BusinessId(businessId), PeriodId(periodId), Some(TaxYear.fromMtd(taxYear)))

  private val testHateoasLink = Link(href = s"individuals/business/self-employment/$nino/$businessId/period/$periodId", method = GET, rel = "self")

  val responseBody: RetrievePeriodSummaryResponse = RetrievePeriodSummaryResponse(
    periodDates = PeriodDates("2019-01-01", "2020-01-01"),
    periodIncome = None,
    periodAllowableExpenses = None,
    periodDisallowableExpenses = None
  )

  "handleRequest" should {
    "return Ok" when {
      "the request received is valid" in new Test {
        MockRetrievePeriodSummaryRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockRetrievePeriodSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, RetrievePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, None))
          .returns(HateoasWrapper(responseBody, Seq(testHateoasLink)))

        val result: Future[Result] = controller.handleRequest(nino, businessId, periodId, None)(fakeRequest)
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
      "the TYS request received is valid" in new Test {
        MockRetrievePeriodSummaryRequestParser
          .parse(tysRawData)
          .returns(Right(tysRequestData))

        MockRetrievePeriodSummaryService
          .retrieve(tysRequestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, RetrievePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(TaxYear.fromMtd(taxYear))))
          .returns(HateoasWrapper(responseBody, Seq(testHateoasLink)))

        val result: Future[Result] = controller.handleRequest(nino, businessId, periodId, Some(taxYear))(fakeRequest)
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return an error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockRetrievePeriodSummaryRequestParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.handleRequest(nino, businessId, periodId, None)(fakeRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = {
          val errors = Seq(
            (BadRequestError, BAD_REQUEST),
            (NinoFormatError, BAD_REQUEST),
            (BusinessIdFormatError, BAD_REQUEST),
            (PeriodIdFormatError, BAD_REQUEST)
          )
          val tysErrors = Seq(
            (TaxYearFormatError, BAD_REQUEST),
            (RuleTaxYearRangeInvalidError, BAD_REQUEST),
            (InvalidTaxYearParameterError, BAD_REQUEST)
          )
          errors ++ tysErrors
        }

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" should {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockRetrievePeriodSummaryRequestParser
              .parse(rawData)
              .returns(Right(requestData))

            MockRetrievePeriodSummaryService
              .retrieve(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.handleRequest(nino, businessId, periodId, None)(fakeRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = {
          val errors = Seq(
            (NinoFormatError, BAD_REQUEST),
            (BusinessIdFormatError, BAD_REQUEST),
            (PeriodIdFormatError, BAD_REQUEST),
            (NotFoundError, NOT_FOUND),
            (InternalError, INTERNAL_SERVER_ERROR)
          )
          val extraTysErrors = Seq(
            (TaxYearFormatError, BAD_REQUEST),
            (RuleTaxYearNotSupportedError, BAD_REQUEST),
            (RuleTaxYearRangeInvalidError, BAD_REQUEST)
          )
          errors ++ extraTysErrors
        }

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }

}
