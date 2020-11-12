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
import v1.mocks.requestParsers.MockCreateSelfEmploymentPeriodicRequestParser
import v1.mocks.services.{MockCreateSelfEmploymentPeriodicService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import v1.models.errors._
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.hateoas.Method._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.createSEPeriodic._
import v1.models.response.createSEPeriodic.{CreateSelfEmploymentPeriodicHateoasData, CreateSelfEmploymentPeriodicResponse}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class CreateSelfEmploymentPeriodicControllerSpec
  extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockCreateSelfEmploymentPeriodicService
    with MockCreateSelfEmploymentPeriodicRequestParser
    with MockHateoasFactory
    with MockIdGenerator {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new CreateSelfEmploymentPeriodicController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockCreateSelfEmploymentPeriodicRequestParser,
      service = mockCreateSelfEmploymentPeriodicService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  private val nino = "AA123456A"
  private val businessId = "XAIS12345678910"
  private val periodId = "2017-01-25_2017-01-25"
  private val correlationId = "X-123"

  private val testHateoasLinks = Seq(
    Link(href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", method = GET, rel = "self")
  )

  private val requestJson = Json.parse(
    """|{
       |   "periodFromDate": "2017-01-25",
       |   "periodToDate": "2018-01-24",
       |   "incomes": {
       |      "turnover": {
       |         "amount": 500.25
       |      },
       |      "other": {
       |         "amount": 500.25
       |      }
       |   },
       |   "expenses": {
       |      "costOfGoodsBought": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "cisPaymentsTo": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "staffCosts": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "travelCosts": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "premisesRunningCosts": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "maintenanceCosts": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "adminCosts": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "advertisingCosts": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "businessEntertainmentCosts": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "interestOnLoans": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "financialCharges": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "badDebt": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "professionalFees": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "depreciation": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      },
       |      "other": {
       |         "amount": 500.25,
       |         "disallowableAmount": 500.25
       |      }
       |   }
       |}
       |""".stripMargin
  )

  private val requestBody = CreateSelfEmploymentPeriodicBody(
    "2017-01-25",
    "2018-01-24",
    Some(Incomes(Some(IncomesAmountObject(500.25)), Some(IncomesAmountObject(500.25)))),
    None,
    Some(Expenses(
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25))),
      Some(ExpensesAmountObject(500.25, Some(500.25)))
    )))

  val responseJson: JsValue = Json.parse(
    s"""
       |{
       |  "periodId": "2017-01-25_2018-01-24",
       |  "links": [
       |    {
       |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
       |      "method": "GET",
       |      "rel": "self"
       |    }
       |  ]
       |}
       |""".stripMargin)

  private val rawData = CreateSelfEmploymentPeriodicRawData(nino, businessId, requestJson)
  private val requestData = CreateSelfEmploymentPeriodicRequest(Nino(nino), businessId, requestBody)

  "handleRequest" should {
    "return Ok" when {
      "the request received is valid" in new Test {

        MockCreateSelfEmploymentPeriodicRequestParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockCreateSelfEmploymentPeriodicService
          .createPeriodic(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, CreateSelfEmploymentPeriodicResponse(periodId)))))

        MockHateoasFactory
          .wrap(CreateSelfEmploymentPeriodicResponse(periodId), CreateSelfEmploymentPeriodicHateoasData(nino, businessId, periodId))
          .returns(HateoasWrapper(CreateSelfEmploymentPeriodicResponse(periodId), testHateoasLinks))

        val result: Future[Result] = controller.handleRequest(nino, businessId)(fakePostRequest(requestJson))
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }
    "return the error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockCreateSelfEmploymentPeriodicRequestParser
              .parseRequest(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.handleRequest(nino, businessId)(fakePostRequest(requestJson))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (FromDateFormatError, BAD_REQUEST),
          (ToDateFormatError, BAD_REQUEST),
          (ValueFormatError.copy(paths = Some(Seq("/foreignTaxCreditRelief/amount"))), BAD_REQUEST),
          (RuleIncorrectOrEmptyBodyError, BAD_REQUEST),
          (RuleBothExpensesSuppliedError, BAD_REQUEST),
          (RuleToDateBeforeFromDateError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" should {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockCreateSelfEmploymentPeriodicRequestParser
              .parseRequest(rawData)
              .returns(Right(requestData))

            MockCreateSelfEmploymentPeriodicService
              .createPeriodic(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.handleRequest(nino, businessId)(fakePostRequest(requestJson))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (RuleOverlappingPeriod, BAD_REQUEST),
          (RuleMisalignedPeriod, BAD_REQUEST),
          (RuleNotContiguousPeriod, BAD_REQUEST),
          (RuleNotAllowedConsolidatedExpenses, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}