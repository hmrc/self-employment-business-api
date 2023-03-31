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

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.mocks.hateoas.MockHateoasFactory
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import api.models.hateoas.Method.GET
import api.models.hateoas._
import api.models.outcomes.ResponseWrapper
import play.api.libs.json.Json
import play.api.mvc.Result
import v1.mocks.requestParsers.MockListPeriodSummariesRequestParser
import v1.mocks.services.MockListPeriodSummariesService
import v1.models.request.listPeriodSummaries.{ListPeriodSummariesRawData, ListPeriodSummariesRequest}
import v1.models.response.listPeriodSummaries._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListPeriodSummariesControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockListPeriodSummariesService
    with MockListPeriodSummariesRequestParser
    with MockHateoasFactory {

  private val businessId: String = "XAIS12345678910"
  private val from: String       = "2019-01-01"
  private val to: String         = "2020-01-01"
//  private val creationDate: String = "2020-01-02"
  private val periodId: String = s"${from}_$to"
  private val taxYear: String  = "2024-25"

  private val periodDetails: PeriodDetails = PeriodDetails(
    periodId,
    from,
    to
//    Some(creationDate)
  )

  private val response: ListPeriodSummariesResponse[PeriodDetails] = ListPeriodSummariesResponse(Seq(periodDetails))

  private val responseBody = Json.parse(s"""
      |{
      |  "periods": [
      |    {
      |      "periodId": "$periodId",
      |      "periodStartDate": "$from",
      |      "periodEndDate": "$to",
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
    """.stripMargin)

  private val responseBodyTys = Json.parse(s"""
                                           |{
                                           |  "periods": [
                                           |    {
                                           |      "periodId": "$periodId",
                                           |      "periodStartDate": "$from",
                                           |      "periodEndDate": "$to",
                                           |      "links": [
                                           |        {
                                           |          "href": "test/href/$periodId?taxYear=$taxYear",
                                           |          "method": "GET",
                                           |          "rel": "self"
                                           |        }
                                           |      ]
                                           |    }
                                           |  ],
                                           |  "links": [
                                           |    {
                                           |      "href": "test/href?taxYear=$taxYear",
                                           |      "method": "GET",
                                           |      "rel": "self"
                                           |    }
                                           |  ]
                                           |}
    """.stripMargin)

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        MockListPeriodSummariesRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockListPeriodSummariesService
          .listPeriodSummaries(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrapList(response, ListPeriodSummariesHateoasData(Nino(nino), BusinessId(businessId), None))
          .returns(HateoasWrapper(hateoasResponse, Seq(testHateoasLink)))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseBody)
        )
      }

      "the Tys request received is valid" in new TysTest {

        MockListPeriodSummariesRequestParser
          .parse(rawTysData)
          .returns(Right(tysRequestData))

        MockListPeriodSummariesService
          .listPeriodSummaries(tysRequestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrapList(response, ListPeriodSummariesHateoasData(Nino(nino), BusinessId(businessId), Some(TaxYear.fromMtd(taxYear))))
          .returns(HateoasWrapper(tysHateoasResponse, Seq(testTysHateoasLink)))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseBodyTys)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {

        MockListPeriodSummariesRequestParser
          .parse(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError)))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {

        MockListPeriodSummariesRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockListPeriodSummariesService
          .listPeriodSummaries(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  trait Test extends ControllerTest {

    val rawData: ListPeriodSummariesRawData     = ListPeriodSummariesRawData(nino, businessId, None)
    val requestData: ListPeriodSummariesRequest = ListPeriodSummariesRequest(Nino(nino), BusinessId(businessId), None)

    val testHateoasLink: Link      = Link(href = "test/href", method = GET, rel = "self")
    val testInnerHateoasLink: Link = Link(href = s"test/href/$periodId", method = GET, rel = "self")

    val hateoasResponse: ListPeriodSummariesResponse[HateoasWrapper[PeriodDetails]] = ListPeriodSummariesResponse(
      Seq(HateoasWrapper(periodDetails, Seq(testInnerHateoasLink))))

    val controller = new ListPeriodSummariesController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockListPeriodSummariesRequestParser,
      service = mockListPeriodSummariesService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId, None)(fakeGetRequest)
  }

  trait TysTest extends ControllerTest {

    val rawTysData: ListPeriodSummariesRawData     = ListPeriodSummariesRawData(nino, businessId, Some(taxYear))
    val tysRequestData: ListPeriodSummariesRequest = ListPeriodSummariesRequest(Nino(nino), BusinessId(businessId), Some(TaxYear.fromMtd(taxYear)))
    val testTysHateoasLink: Link                   = Link(href = s"test/href?taxYear=$taxYear", method = GET, rel = "self")
    val testTysInnerHateoasLink: Link              = Link(href = s"test/href/$periodId?taxYear=$taxYear", method = GET, rel = "self")

    val tysHateoasResponse: ListPeriodSummariesResponse[HateoasWrapper[PeriodDetails]] = ListPeriodSummariesResponse(
      Seq(HateoasWrapper(periodDetails, Seq(testTysInnerHateoasLink))))

    val controller = new ListPeriodSummariesController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockListPeriodSummariesRequestParser,
      service = mockListPeriodSummariesService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId, Some(taxYear))(fakeGetRequest)
  }

}
