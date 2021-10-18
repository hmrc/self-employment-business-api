/*
 * Copyright 2021 HM Revenue & Customs
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
import v1.mocks.requestParsers.MockRetrieveSelfEmploymentAnnualSummaryRequestParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockMtdIdLookupService, MockRetrieveSelfEmploymentAnnualSummaryService}
import v1.models.domain.Nino
import v1.models.domain.ex.MtdEx
import v1.models.errors._
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.hateoas.Method.GET
import v1.models.outcomes.ResponseWrapper
import v1.models.request.retrieveSEAnnual.{RetrieveSelfEmploymentAnnualSummaryRawData, RetrieveSelfEmploymentAnnualSummaryRequest}
import v1.models.response.retrieveSEAnnual._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveSelfEmploymentAnnualSummaryControllerSpec extends ControllerBaseSpec
  with MockEnrolmentsAuthService
  with MockMtdIdLookupService
  with MockRetrieveSelfEmploymentAnnualSummaryService
  with MockRetrieveSelfEmploymentAnnualSummaryRequestParser
  with MockHateoasFactory
  with MockAuditService
  with MockIdGenerator {

  private val nino = "AA123456A"
  private val businessId = "XAIS12345678910"
  private val taxYear = "2020-21"
  private val correlationId = "X-123"

  trait Test {
    val hc: HeaderCarrier = HeaderCarrier()

    val controller = new RetrieveSelfEmploymentAnnualSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRetrieveSelfEmploymentAnnualSummaryRequestParser,
      service = mockRetrieveSelfEmploymentAnnualSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.getCorrelationId.returns(correlationId)
  }

  private val rawData = RetrieveSelfEmploymentAnnualSummaryRawData(nino, businessId, taxYear)
  private val requestData = RetrieveSelfEmploymentAnnualSummaryRequest(Nino(nino), businessId, taxYear)

  private val testHateoasLink = Link(href = s"Individuals/business/property/$nino/$businessId/annual/$taxYear", method = GET, rel = "self")

  private val adjustments = Adjustments(
    Some(1000), Some(1000), Some(1000), Some(1000), Some(1000),
    Some(1000), Some(1000), Some(1000), Some(1000), Some(1000)
  )
  private val allowances = Allowances(
    Some(1000), Some(1000), Some(1000), Some(1000), Some(1000),
    Some(1000), Some(1000), Some(1000), Some(1000)
  )
  private val nonFinancials = NonFinancials(Some(Class4NicInfo(Some(MtdEx.`002 - Trustee`))))

  val responseBody: RetrieveSelfEmploymentAnnualSummaryResponse = RetrieveSelfEmploymentAnnualSummaryResponse(
    adjustments = Some(adjustments),
    allowances = Some(allowances),
    nonFinancials = Some(nonFinancials)
  )

  "handleRequest" should {
    "return Ok" when {
      "the request received is valid" in new Test {
        MockRetrieveSelfEmploymentAnnualSummaryRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockRetrieveSelfEmploymentAnnualSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, RetrieveSelfEmploymentAnnualSummaryHateoasData(nino, businessId, taxYear))
          .returns(HateoasWrapper(responseBody, Seq(testHateoasLink)))

        val result: Future[Result] = controller.handleRequest(nino, businessId, taxYear)(fakeRequest)
        status(result) shouldBe OK
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return an error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockRetrieveSelfEmploymentAnnualSummaryRequestParser
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

            MockRetrieveSelfEmploymentAnnualSummaryRequestParser
              .parse(rawData)
              .returns(Right(requestData))

            MockRetrieveSelfEmploymentAnnualSummaryService
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
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}