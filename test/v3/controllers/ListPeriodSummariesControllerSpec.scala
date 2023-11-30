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

package v3.controllers

import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.hateoas.Method.GET
import api.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import play.api.libs.json.Json
import play.api.mvc.Result
import v3.controllers.validators.MockListPeriodSummariesValidatorFactory
import v3.models.request.listPeriodSummaries.ListPeriodSummariesRequestData
import v3.models.response.listPeriodSummaries._
import v3.services.MockListPeriodSummariesService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListPeriodSummariesControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockListPeriodSummariesService
    with MockListPeriodSummariesValidatorFactory
    with MockHateoasFactory {

  private val businessId = "XAIS12345678910"
  private val from       = "2019-01-01"
  private val to         = "2020-01-01"
//  private val creationDate = "2020-01-02" // To be reinstated, see MTDSA-15595
  private val periodId = s"${from}_$to"
  private val taxYear  = "2024-25"

  private val periodDetails = PeriodDetails(
    periodId,
    from,
    to
//    Some(creationDate) // To be reinstated, see MTDSA-15595
  )

  private val response = ListPeriodSummariesResponse(List(periodDetails))

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
        willUseValidator(returningSuccess(requestData))

        MockedListPeriodSummariesService
          .listPeriodSummaries(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        MockHateoasFactory
          .wrapList(response, ListPeriodSummariesHateoasData(Nino(nino), BusinessId(businessId), None))
          .returns(HateoasWrapper(hateoasResponse, List(testHateoasLink)))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseBody)
        )
      }

      "the Tys request received is valid" in new TysTest {
        willUseValidator(returningSuccess(tysRequestData))

        MockedListPeriodSummariesService
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
        willUseValidator(returning(NinoFormatError))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockedListPeriodSummariesService
          .listPeriodSummaries(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val requestData: ListPeriodSummariesRequestData = ListPeriodSummariesRequestData(Nino(nino), BusinessId(businessId), None)

    val testHateoasLink: Link = Link(href = "test/href", method = GET, rel = "self")

    private val testInnerHateoasLink = Link(href = s"test/href/$periodId", method = GET, rel = "self")

    val hateoasResponse: ListPeriodSummariesResponse[HateoasWrapper[PeriodDetails]] = ListPeriodSummariesResponse(
      List(HateoasWrapper(periodDetails, List(testInnerHateoasLink))))

    private val controller = new ListPeriodSummariesController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockListPeriodSummariesValidatorFactory,
      service = mockListPeriodSummariesService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId, None)(fakeGetRequest)
  }

  private trait TysTest extends ControllerTest {

    val tysRequestData: ListPeriodSummariesRequestData =
      ListPeriodSummariesRequestData(Nino(nino), BusinessId(businessId), Some(TaxYear.fromMtd(taxYear)))

    val testTysHateoasLink: Link = Link(href = s"test/href?taxYear=$taxYear", method = GET, rel = "self")

    private val testTysInnerHateoasLink = Link(href = s"test/href/$periodId?taxYear=$taxYear", method = GET, rel = "self")

    val tysHateoasResponse: ListPeriodSummariesResponse[HateoasWrapper[PeriodDetails]] =
      ListPeriodSummariesResponse(List(HateoasWrapper(periodDetails, List(testTysInnerHateoasLink))))

    private val controller = new ListPeriodSummariesController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockListPeriodSummariesValidatorFactory,
      service = mockListPeriodSummariesService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId, Some(taxYear))(fakeGetRequest)
  }

}
