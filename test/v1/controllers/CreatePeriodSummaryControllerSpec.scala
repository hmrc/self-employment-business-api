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
import api.mocks.services.{MockEnrolmentsAuthService, MockMtdIdLookupService}
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import api.models.hateoas.HateoasWrapper
import api.models.outcomes.ResponseWrapper
import api.models.request.createPeriodSummary.{CreatePeriodSummaryRawData, PeriodDates, PeriodIncome}
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.requestParsers.MockCreatePeriodSummaryRequestParser
import v1.mocks.services.MockCreatePeriodSummaryService
import v1.models.request.createPeriodSummary
import v1.models.request.createPeriodSummary._
import v1.models.response.createPeriodSummary.{CreatePeriodSummaryHateoasData, CreatePeriodSummaryResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreatePeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockCreatePeriodSummaryService
    with MockCreatePeriodSummaryRequestParser
    with MockHateoasFactory
    with MockIdGenerator {

  private val nino          = "AA123456A"
  private val businessId    = "XAIS12345678910"
  private val periodId      = "2017-01-25_2017-01-25"
  private val correlationId = "X-123"

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new CreatePeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockCreatePeriodicRequestParser,
      service = mockCreatePeriodicService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  private val requestJson = Json.parse(
    """
      |{
      |     "periodDates": {
      |           "periodStartDate": "2019-08-24",
      |           "periodEndDate": "2019-08-24"
      |     },
      |     "periodIncome": {
      |          "turnover": 1000.99,
      |          "other": 1000.99
      |     },
      |     "periodAllowableExpenses": {
      |          "costOfGoodsAllowable": 1000.99,
      |          "paymentsToSubcontractorsAllowable": 1000.99,
      |          "wagesAndStaffCostsAllowable": 1000.99,
      |          "carVanTravelExpensesAllowable": 1000.99,
      |          "premisesRunningCostsAllowable": -99999.99,
      |          "maintenanceCostsAllowable": -1000.99,
      |          "adminCostsAllowable": 1000.99,
      |          "businessEntertainmentCostsAllowable": 1000.99,
      |          "advertisingCostsAllowable": 1000.99,
      |          "interestOnBankOtherLoansAllowable": -1000.99,
      |          "financeChargesAllowable": -1000.99,
      |          "irrecoverableDebtsAllowable": -1000.99,
      |          "professionalFeesAllowable": -99999999999.99,
      |          "depreciationAllowable": -1000.99,
      |          "otherExpensesAllowable": 1000.99
      |      },
      |     "periodDisallowableExpenses": {
      |          "costOfGoodsDisallowable": 1000.99,
      |          "paymentsToSubcontractorsDisallowable": 1000.99,
      |          "wagesAndStaffCostsDisallowable": 1000.99,
      |          "carVanTravelExpensesDisallowable": 1000.99,
      |          "premisesRunningCostsDisallowable": -1000.99,
      |          "maintenanceCostsDisallowable": -999.99,
      |          "adminCostsDisallowable": 1000.99,
      |          "businessEntertainmentCostsDisallowable": 1000.99,
      |          "advertisingCostsDisallowable": 1000.99,
      |          "interestOnBankOtherLoansDisallowable": -1000.99,
      |          "financeChargesDisallowable": -9999.99,
      |          "irrecoverableDebtsDisallowable": -1000.99,
      |          "professionalFeesDisallowable": -99999999999.99,
      |          "depreciationDisallowable": -99999999999.99,
      |          "otherExpensesDisallowable": 1000.99
      |      }
      |}
    """.stripMargin
  )

  private val requestBody: CreatePeriodSummaryBody =
    CreatePeriodSummaryBody(
      PeriodDates("2019-08-24", "2019-08-24"),
      Some(
        PeriodIncome(
          Some(1000.99),
          Some(1000.99)
        )),
      Some(
        PeriodAllowableExpenses(
          None,
          Some(1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(-99999.99),
          Some(-1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(-1000.99),
          Some(-1000.99),
          Some(-1000.99),
          Some(-99999999999.99),
          Some(-1000.99),
          Some(1000.99)
        )),
      Some(
        PeriodDisallowableExpenses(
          Some(1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(-1000.99),
          Some(-999.99),
          Some(1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(-1000.99),
          Some(-9999.99),
          Some(-1000.99),
          Some(-99999999999.99),
          Some(-99999999999.99),
          Some(1000.99)
        ))
    )

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
    """.stripMargin
  )

  private val rawData     = CreatePeriodSummaryRawData(nino, businessId, requestJson)
  private val requestData = createPeriodSummary.CreatePeriodSummaryRequest(Nino(nino), BusinessId(businessId), requestBody)

  "handleRequest" should {
    "return Ok" when {
      "the request received is valid" in new Test {
        MockCreatePeriodSummaryRequestParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockCreatePeriodicService
          .createPeriodic(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, CreatePeriodSummaryResponse(periodId)))))

        MockHateoasFactory
          .wrap(
            CreatePeriodSummaryResponse(periodId),
            CreatePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(TaxYear.fromMtd("2019-20"))))
          .returns(HateoasWrapper(CreatePeriodSummaryResponse(periodId), testHateoasLinks))

        val result: Future[Result] = controller.handleRequest(nino, businessId)(fakePostRequest(requestJson))
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockCreatePeriodSummaryRequestParser
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
          (StartDateFormatError, BAD_REQUEST),
          (EndDateFormatError, BAD_REQUEST),
          (ValueFormatError.copy(paths = Some(Seq("/foreignTaxCreditRelief/amount"))), BAD_REQUEST),
          (RuleIncorrectOrEmptyBodyError, BAD_REQUEST),
          (RuleBothExpensesSuppliedError, BAD_REQUEST),
          (RuleEndDateBeforeStartDateError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" should {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockCreatePeriodSummaryRequestParser
              .parseRequest(rawData)
              .returns(Right(requestData))

            MockCreatePeriodicService
              .createPeriodic(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.handleRequest(nino, businessId)(fakePostRequest(requestJson))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val errors = Seq(
          (NinoFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (RuleOverlappingPeriod, BAD_REQUEST),
          (RuleMisalignedPeriod, BAD_REQUEST),
          (RuleNotContiguousPeriod, BAD_REQUEST),
          (RuleNotAllowedConsolidatedExpenses, BAD_REQUEST),
          (RuleInvalidSubmissionPeriodError, BAD_REQUEST),
          (RuleInvalidSubmissionEndDateError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (InternalError, INTERNAL_SERVER_ERROR)
        )
        val extraTysErrors = Seq(
          (RuleDuplicateSubmissionError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST)
        )

        (errors ++ extraTysErrors).foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }

}
