/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockRetrieveSelfEmploymentPeriodicRequestParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockRetrieveSelfEmploymentPeriodicService}
import v1.models.errors.{BadRequestError, BusinessIdFormatError, DownstreamError, ErrorWrapper, MtdError, NinoFormatError, NotFoundError, PeriodIdFormatError}
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.hateoas.Method.GET
import v1.models.outcomes.ResponseWrapper
import v1.models.request.retrieveSEPeriodic.{RetrieveSelfEmploymentPeriodicRawData, RetrieveSelfEmploymentPeriodicRequest}
import v1.models.response.retrieveSEPeriodic._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveSelfEmploymentPeriodicControllerSpec extends ControllerBaseSpec
  with MockEnrolmentsAuthService
  with MockMtdIdLookupService
  with MockRetrieveSelfEmploymentPeriodicService
  with MockRetrieveSelfEmploymentPeriodicRequestParser
  with MockHateoasFactory
  with MockAuditService {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new RetrieveSelfEmploymentPeriodicController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRetrieveSelfEmploymentPeriodicRequestParser,
      service = mockRetrieveSelfEmploymentPeriodicService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  private val nino = "AA123456A"
  private val businessId = "XAIS12345678910"
  private val periodId = "2019-01-01_2020-01-01"
  private val correlationId = "X-123"

  private val rawData = RetrieveSelfEmploymentPeriodicRawData(nino, businessId, periodId)
  private val requestData = RetrieveSelfEmploymentPeriodicRequest(Nino(nino), businessId, periodId)

  private val testHateoasLink = Link(href = s"Individuals/business/property/$nino/$businessId/period/$periodId", method = GET, rel = "self")

  val responseBody: RetrieveSelfEmploymentPeriodicResponse = RetrieveSelfEmploymentPeriodicResponse(
    "2019-01-01",
    "2020-01-01",
    Some(Incomes(Some(IncomesAmountObject(100.99)), Some(IncomesAmountObject(100.99)))),
    Some(ConsolidatedExpenses(100.99)),
    None
  )

  "handleRequest" should {
    "return Ok" when {
      "the request received is valid" in new Test {

        MockRetrieveSelfEmploymentPeriodicRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockRetrieveSelfEmploymentPeriodicService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, RetrieveSelfEmploymentPeriodicHateoasData(nino, businessId, periodId))
          .returns(HateoasWrapper(responseBody, Seq(testHateoasLink)))

        val result: Future[Result] = controller.handleRequest(nino, businessId, periodId)(fakeRequest)
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }
    "return an error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockRetrieveSelfEmploymentPeriodicRequestParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(Some(correlationId), error, None)))

            val result: Future[Result] = controller.handleRequest(nino, businessId, periodId)(fakeRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (PeriodIdFormatError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }
      "service errors occur" should {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockRetrieveSelfEmploymentPeriodicRequestParser
              .parse(rawData)
              .returns(Right(requestData))

            MockRetrieveSelfEmploymentPeriodicService
              .retrieve(requestData)
              .returns(Future.successful(Left(ErrorWrapper(Some(correlationId), mtdError))))

            val result: Future[Result] = controller.handleRequest(nino, businessId, periodId)(fakeRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (PeriodIdFormatError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}

