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

import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.MockIdGenerator
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockAmendSelfEmploymentPeriodicRequestParser
import v1.mocks.services.{MockAmendSelfEmploymentPeriodicService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import v1.models.errors._
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.hateoas.Method.{GET, PUT}
import v1.models.hateoas.RelType.AMEND_PERIODIC_UPDATE_REL
import v1.models.outcomes.ResponseWrapper
import v1.models.request.amendSEPeriodic._
import v1.models.response.amendSEPeriodic.AmendSelfEmploymentPeriodicHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendSelfEmploymentPeriodicControllerSpec
  extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockAmendSelfEmploymentPeriodicService
    with MockAmendSelfEmploymentPeriodicRequestParser
    with MockHateoasFactory
    with MockIdGenerator {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new AmendSelfEmploymentPeriodicController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockAmendSelfEmploymentPeriodicRequestParser,
      service = mockAmendSelfEmploymentPeriodicService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  private val nino: String = "AA123456A"
  private val businessId: String = "XAIS12345678910"
  private val periodId: String = "2019-01-01_2020-01-01"
  private val correlationId: String = "X-123"

  private val testHateoasLinks = Seq(
    Link(href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", method = PUT, rel = AMEND_PERIODIC_UPDATE_REL),
    Link(href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", method = GET, rel = "self")
  )

  private val requestJson = Json.parse(
    """
      |{
      |    "incomes": {
      |        "turnover": {
      |            "amount": 172.89
      |        },
      |        "other": {
      |            "amount": 634.14
      |        }
      |    },
      |    "consolidatedExpenses": {
      |        "consolidatedExpenses": 647.89
      |    }
      |}
      |""".stripMargin)

  private val requestBody = AmendPeriodicBody(
    Some(Incomes(Some(IncomesAmountObject(500.25)),Some(IncomesAmountObject(500.15)))),
    Some(ConsolidatedExpenses(500.25)),
    None
  )

  val responseJson: JsValue = Json.parse(
    s"""
      |{
      |  "links": [
      |    {
      |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
      |      "method": "PUT",
      |      "rel": "amend-periodic-update"
      |    },
      |    {
      |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
      |      "method": "GET",
      |      "rel": "self"
      |    }
      |  ]
      |}
      |""".stripMargin)

  private val rawData = AmendPeriodicRawData(nino, businessId, periodId, requestJson)
  private val requestData = AmendPeriodicRequest(Nino(nino), businessId, periodId, requestBody)

  "handleRequest" should {
    "return OK" when {
      "the request received is valid" in new Test {

        MockAmendSelfEmploymentPeriodicRequestParser
          .requestFor(rawData)
          .returns(Right(requestData))

        MockAmendSelfEmploymentPeriodicService
          .amendPeriodic(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendSelfEmploymentPeriodicHateoasData(nino, businessId, periodId))
          .returns(HateoasWrapper((), testHateoasLinks))

        val result: Future[Result] = controller.handleRequest(nino, businessId, periodId)(fakePostRequest(requestJson))
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockAmendSelfEmploymentPeriodicRequestParser
              .requestFor(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.handleRequest(nino, businessId, periodId)(fakePostRequest(requestJson))

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
          (ValueFormatError.copy(paths = Some(Seq("/incomes/turnover/amount"))), BAD_REQUEST),
          (RuleIncorrectOrEmptyBodyError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" should {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockAmendSelfEmploymentPeriodicRequestParser
              .requestFor(rawData)
              .returns(Right(requestData))

            MockAmendSelfEmploymentPeriodicService
              .amendPeriodic(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.handleRequest(nino, businessId, periodId)(fakePostRequest(requestJson))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (PeriodIdFormatError, BAD_REQUEST),
          (RuleBothExpensesSuppliedError, BAD_REQUEST),
          (RuleNotAllowedConsolidatedExpenses, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}