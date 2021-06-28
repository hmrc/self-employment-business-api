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
import v1.models.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.mocks.MockIdGenerator
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockListSelfEmploymentPeriodicRequestParser
import v1.mocks.services.{MockAuditService, MockEnrolmentsAuthService, MockListSelfEmploymentPeriodicService, MockMtdIdLookupService}
import v1.models.errors._
import v1.models.hateoas.Method.GET
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.listSEPeriodic.{ListSelfEmploymentPeriodicRawData, ListSelfEmploymentPeriodicRequest}
import v1.models.response.listSEPeriodic._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListSelfEmploymentPeriodicControllerSpec extends ControllerBaseSpec
  with MockEnrolmentsAuthService
  with MockMtdIdLookupService
  with MockListSelfEmploymentPeriodicService
  with MockListSelfEmploymentPeriodicRequestParser
  with MockHateoasFactory
  with MockAuditService
  with MockIdGenerator {

  trait Test {
    val hc = HeaderCarrier()

    val controller = new ListSelfEmploymentPeriodicController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockListSelfEmploymentPeriodicRequestParser,
      service = mockListSelfEmploymentPeriodicService,
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
  private val from = "2019-01-01"
  private val to = "2020-01-01"
  private val periodId = s"${from}_$to"
  private val correlationId = "X-123"

  private val rawData = ListSelfEmploymentPeriodicRawData(nino, businessId)
  private val requestData = ListSelfEmploymentPeriodicRequest(Nino(nino), businessId)

  private val testHateoasLink = Link(href = s"test/href", method = GET, rel = "self")
  private val testInnerHateoasLink = Link(href = s"test/href/$periodId", method = GET, rel = "self")

  private val periodDetails: PeriodDetails = PeriodDetails(periodId, from, to)

  private val response: ListSelfEmploymentPeriodicResponse[PeriodDetails] = ListSelfEmploymentPeriodicResponse(Seq(periodDetails))

  private val hateoasResponse = ListSelfEmploymentPeriodicResponse(Seq(HateoasWrapper(periodDetails, Seq(testInnerHateoasLink))))

  private val responseBody = Json.parse(
    s"""
      |{
      |  "periods": [
      |    {
      |      "periodId": "$periodId",
      |      "from": "$from",
      |      "to": "$to",
      |      "links": [
      |        {
      |          "href": "test/href/$periodId",
      |          "method": "GET",
      |          "rel": "self"
      |        }
      |      ]
      |    }
      |  ],
      |  "links": [
      |    {
      |      "href": "test/href",
      |      "method": "GET",
      |      "rel": "self"
      |    }
      |  ]
      |}
      |""".stripMargin
  )

  "handleRequest" should {
    "return Ok" when {
      "the request received is valid" in new Test {

        MockListSelfEmploymentPeriodicRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockListSelfEmploymentPeriodicService
          .listSelfEmploymentUpdatePeriods(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrapList(response, ListSelfEmploymentPeriodicHateoasData(nino, businessId))
          .returns(HateoasWrapper(hateoasResponse, Seq(testHateoasLink)))

        val result: Future[Result] = controller.handleRequest(nino, businessId)(fakeRequest)
        status(result) shouldBe OK
        contentAsJson(result) shouldBe responseBody
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }
    "return an error as per spec" when {
      "parser errors occur" should {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockListSelfEmploymentPeriodicRequestParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(correlationId, error, None)))

            val result: Future[Result] = controller.handleRequest(nino, businessId)(fakeRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (BusinessIdFormatError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }
      "service errors occur" should {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockListSelfEmploymentPeriodicRequestParser
              .parse(rawData)
              .returns(Right(requestData))

            MockListSelfEmploymentPeriodicService
              .listSelfEmploymentUpdatePeriods(requestData)
              .returns(Future.successful(Left(ErrorWrapper(correlationId, mtdError))))

            val result: Future[Result] = controller.handleRequest(nino, businessId)(fakeRequest)

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