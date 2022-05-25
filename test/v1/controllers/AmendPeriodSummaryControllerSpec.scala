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

import play.api.libs.json.Json
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.MockIdGenerator
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockAmendPeriodSummaryRequestParser
import v1.mocks.services.{MockAmendPeriodSummaryService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import v1.models.domain.{BusinessId, Nino}
import v1.models.errors._
import v1.models.hateoas.HateoasWrapper
import v1.models.outcomes.ResponseWrapper
import v1.models.request.amendPeriodic._
import v1.models.response.amendPeriodic.AmendPeriodSumaryHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendPeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockAmendPeriodSummaryService
    with MockAmendPeriodSummaryRequestParser
    with MockHateoasFactory
    with MockIdGenerator
    with AmendPeriodSummaryFixture {

  private val nino          = "AA123456A"
  private val businessId    = "XAIS12345678910"
  private val periodId      = "2019-01-01_2020-01-01"
  private val correlationId = "X-123"

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new AmendPeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockAmendPeriodSummaryRequestParser,
      service = mockAmendPeriodSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  private val requestBodyJson = amendPeriodSummaryBodyMtdJson
  private val requestBody     = amendPeriodSummaryBody

  private val rawData     = AmendPeriodSummaryRawData(nino, businessId, periodId, requestBodyJson)
  private val requestData = AmendPeriodSummaryRequest(Nino(nino), BusinessId(businessId), periodId, requestBody)

  "handleRequest" should {
    "return OK" when {
      "the request received is valid" in new Test {
        MockAmendPeriodSummaryRequestParser
          .requestFor(rawData)
          .returns(Right(requestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendPeriodSumaryHateoasData(Nino(nino), BusinessId(businessId), periodId))
          .returns(HateoasWrapper((), testHateoasLinks))

        val result: Future[Result] = controller.handleRequest(nino, businessId, periodId)(fakePostRequest(requestBodyJson))
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockAmendPeriodSummaryRequestParser
              .requestFor(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.handleRequest(nino, businessId, periodId)(fakePostRequest(requestBodyJson))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (PeriodIdFormatError, BAD_REQUEST),
          (ValueFormatError.copy(paths = Some(Seq("/incomes/turnover"))), BAD_REQUEST),
          (RuleBothExpensesSuppliedError, BAD_REQUEST),
          (RuleIncorrectOrEmptyBodyError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" should {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockAmendPeriodSummaryRequestParser
              .requestFor(rawData)
              .returns(Right(requestData))

            MockAmendPeriodSummaryService
              .amendPeriodSummary(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.handleRequest(nino, businessId, periodId)(fakePostRequest(requestBodyJson))

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
          (RuleBothExpensesSuppliedError, BAD_REQUEST),
          (RuleNotAllowedConsolidatedExpenses, BAD_REQUEST),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }

}
