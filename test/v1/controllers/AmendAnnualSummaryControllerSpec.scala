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

import play.api.libs.json.{ JsValue, Json }
import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.MockIdGenerator
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockAmendSelfEmploymentAnnualSummaryRequestParser
import v1.mocks.services.{ MockAmendAnnualSummaryService, MockEnrolmentsAuthService, MockMtdIdLookupService }
import v1.models.domain.Nino
import v1.models.domain.ex.MtdEx
import v1.models.errors._
import v1.models.hateoas.{ HateoasWrapper, Link }
import v1.models.hateoas.Method.{ DELETE, GET, PUT }
import v1.models.hateoas.RelType.{ AMEND_ANNUAL_SUMMARY_REL, DELETE_ANNUAL_SUMMARY_REL }
import v1.models.outcomes.ResponseWrapper
import v1.models.request.amendSEAnnual._
import v1.models.response.amendSEAnnual.{ AmendAnnualSummaryHateoasData, AmendAnnualSummaryResponse }

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendAnnualSummaryControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockAmendAnnualSummaryService
    with MockAmendSelfEmploymentAnnualSummaryRequestParser
    with MockHateoasFactory
    with MockIdGenerator {

  private val nino          = "AA123456A"
  private val businessId    = "XAIS12345678910"
  private val taxYear       = "2019-20"
  private val correlationId = "X-123"

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new AmendAnnualSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockAmendSelfEmploymentAnnualSummaryRequestParser,
      service = mockAmendAnnualSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      new StandardControllerFactory(mockIdGenerator, ApiCommonErrorHandling)
    )

    MockMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  private val testHateoasLinks = Seq(
    Link(href = s"/individuals/business/self-employment/$nino/$businessId/annual/$taxYear", method = GET, rel = "self"),
    Link(href = s"/individuals/business/self-employment/$nino/$businessId/annual/$taxYear", method = PUT, rel = AMEND_ANNUAL_SUMMARY_REL),
    Link(href = s"/individuals/business/self-employment/$nino/$businessId/annual/$taxYear", method = DELETE, rel = DELETE_ANNUAL_SUMMARY_REL)
  )

  private val requestJson = Json.parse(
    """
      |{
      |   "adjustments": {
      |        "includedNonTaxableProfits": 1.11,
      |        "basisAdjustment": 2.22,
      |        "overlapReliefUsed": 3.33,
      |        "accountingAdjustment": 4.44,
      |        "averagingAdjustment": 5.55,
      |        "lossBroughtForward": 6.66,
      |        "outstandingBusinessIncome": 7.77,
      |        "balancingChargeBPRA": 8.88,
      |        "balancingChargeOther": 9.99,
      |        "goodsAndServicesOwnUse": 10.10
      |    },
      |    "allowances": {
      |        "annualInvestmentAllowance": 1.11,
      |        "businessPremisesRenovationAllowance": 2.22,
      |        "capitalAllowanceMainPool": 3.33,
      |        "capitalAllowanceSpecialRatePool": 4.44,
      |        "zeroEmissionGoodsVehicleAllowance": 5.55,
      |        "enhancedCapitalAllowance": 6.66,
      |        "allowanceOnSales": 7.77,
      |        "capitalAllowanceSingleAssetPool": 8.88,
      |        "tradingAllowance": 9.99,
      |        "electricChargePointAllowance": "11.11"
      |    },
      |    "nonFinancials": {
      |        "class4NicInfo":{
      |            "isExempt": true,
      |            "exemptionCode": "001 - Non Resident"
      |        }
      |    }
      |}
    """.stripMargin
  )

  private val requestBody = AmendAnnualSummaryBody(
    Some(Adjustments(Some(1.11), Some(2.22), Some(3.33), Some(4.44), Some(5.55), Some(6.66), Some(7.77), Some(8.88), Some(9.99), Some(10.10))),
    Some(Allowances(Some(1.11), Some(2.22), Some(3.33), Some(4.44), Some(5.55), Some(6.66), Some(7.77), Some(8.88), Some(9.99), Some(11.11))),
    Some(NonFinancials(Some(Class4NicInfo(Some(MtdEx.`001 - Non Resident`)))))
  )

  val responseJson: JsValue = Json.parse(
    s"""
      |{
      |  "links": [
      |    {
      |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
      |      "method": "GET",
      |      "rel": "self"
      |    },
      |    {
      |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
      |      "method": "PUT",
      |      "rel": "create-and-amend-self-employment-annual-summary"
      |    },
      |    {
      |      "href": "/individuals/business/self-employment/$nino/$businessId/annual/$taxYear",
      |      "method": "DELETE",
      |      "rel": "delete-self-employment-annual-summary"
      |    }
      |  ]
      |}
    """.stripMargin
  )

  val responseBody: AmendAnnualSummaryResponse = AmendAnnualSummaryResponse(
    transactionReference = "2017090920170909"
  )

  private val rawData     = AmendAnnualSummaryRawData(nino, businessId, taxYear, requestJson)
  private val requestData = AmendAnnualSummaryRequest(Nino(nino), businessId, taxYear, requestBody)

  "handleRequest" should {
    "return Ok" when {
      "the request received is valid" in new Test {
        MockAmendSelfEmploymentAnnualSummaryRequestParser
          .requestFor(rawData)
          .returns(Right(requestData))

        MockAmendAnnualSummaryService
          .amendAnnualSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, AmendAnnualSummaryHateoasData(nino, businessId, taxYear))
          .returns(HateoasWrapper(responseBody, testHateoasLinks))

        val result: Future[Result] = controller.handleRequest(nino, businessId, taxYear)(fakePostRequest(requestJson))
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockAmendSelfEmploymentAnnualSummaryRequestParser
              .requestFor(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.handleRequest(nino, businessId, taxYear)(fakePostRequest(requestJson))

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
          (ValueFormatError.copy(paths = Some(Seq("/foreignTaxCreditRelief/amount"))), BAD_REQUEST),
          (RuleIncorrectOrEmptyBodyError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST),
          (RuleTaxYearRangeInvalidError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" should {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockAmendSelfEmploymentAnnualSummaryRequestParser
              .requestFor(rawData)
              .returns(Right(requestData))

            MockAmendAnnualSummaryService
              .amendAnnualSummary(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.handleRequest(nino, businessId, taxYear)(fakePostRequest(requestJson))

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
