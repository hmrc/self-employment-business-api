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
import api.hateoas
import api.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import api.models.domain.{BusinessId, Nino, PeriodId, TaxYear}
import api.models.errors._
import api.hateoas.Method.{GET, PUT}
import api.models.outcomes.ResponseWrapper
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import v1.mocks.requestParsers.MockRetrievePeriodSummaryRequestParser
import v1.mocks.services.MockRetrievePeriodSummaryService
import v1.models.request.retrievePeriodSummary.{RetrievePeriodSummaryRawData, RetrievePeriodSummaryRequest}
import v1.models.response.retrievePeriodSummary._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrievePeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockRetrievePeriodSummaryService
    with MockRetrievePeriodSummaryRequestParser
    with MockHateoasFactory {

  private val businessId: String  = "XAIS12345678910"
  private val taxYear: String     = "2023-24"
  private val tysPeriodId: String = "2024-01-01_2025-01-01"

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        MockRetrievePeriodSummaryRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockRetrievePeriodSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, RetrievePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, None))
          .returns(HateoasWrapper(responseBody, testHateoasLink))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseJson)
        )
      }
      "the TYS request received is valid" in new TysTest {
        MockRetrievePeriodSummaryRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockRetrievePeriodSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, RetrievePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(TaxYear.fromMtd(taxYear))))
          .returns(HateoasWrapper(responseBody, testHateoasLink))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseJson)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {

        MockRetrievePeriodSummaryRequestParser
          .parse(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError)))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new Test {

        MockRetrievePeriodSummaryRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockRetrievePeriodSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {
    val periodId: String                          = "2019-01-01_2020-01-01"
    val rawData: RetrievePeriodSummaryRawData     = RetrievePeriodSummaryRawData(nino, businessId, periodId, None)
    val requestData: RetrievePeriodSummaryRequest = RetrievePeriodSummaryRequest(Nino(nino), BusinessId(businessId), PeriodId(periodId), None)

    val responseBody: RetrievePeriodSummaryResponse = RetrievePeriodSummaryResponse(
      periodDates = PeriodDates("2019-01-01", "2020-01-01"),
      periodIncome = None,
      periodAllowableExpenses = None,
      periodDisallowableExpenses = None
    )

    val testHateoasLink: Seq[Link] = Seq(
      hateoas.Link(
        href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId",
        method = PUT,
        rel = "amend-self-employment-period-summary"
      ),
      hateoas.Link(href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", method = GET, rel = "self"),
      hateoas.Link(
        href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId",
        method = GET,
        rel = "list-self-employment-period-summaries"
      )
    )

    val responseJson: JsValue = Json.parse(
      s"""
         |{
         |  "periodDates": {
         |    "periodStartDate": "2019-01-01",
         |    "periodEndDate": "2020-01-01"
         |  },
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "method": "PUT",
         |      "rel": "amend-self-employment-period-summary"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "method": "GET",
         |      "rel": "self"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "method": "GET",
         |      "rel": "list-self-employment-period-summaries"
         |    }
         |  ]
         |}
      """.stripMargin
    )

    val controller = new RetrievePeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRetrievePeriodSummaryRequestParser,
      service = mockRetrievePeriodSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId, periodId, None)(fakeGetRequest)
  }

  private trait TysTest extends ControllerTest {
    val periodId: String                      = "2024-01-01_2025-01-01"
    val rawData: RetrievePeriodSummaryRawData = RetrievePeriodSummaryRawData(nino, businessId, periodId, Some(taxYear))

    val requestData: RetrievePeriodSummaryRequest =
      RetrievePeriodSummaryRequest(Nino(nino), BusinessId(businessId), PeriodId(periodId), Some(TaxYear.fromMtd(taxYear)))

    val responseBody: RetrievePeriodSummaryResponse = RetrievePeriodSummaryResponse(
      periodDates = PeriodDates("2024-01-01", "2025-01-01"),
      periodIncome = None,
      periodAllowableExpenses = None,
      periodDisallowableExpenses = None
    )

    val testHateoasLink: Seq[Link] = Seq(
      hateoas.Link(
        href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
        method = PUT,
        rel = "amend-self-employment-period-summary"
      ),
      hateoas.Link(href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]", method = GET, rel = "self"),
      hateoas.Link(
        href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
        method = GET,
        rel = "list-self-employment-period-summaries"
      )
    )

    val responseJson: JsValue = Json.parse(
      s"""
         |{
         |  "periodDates": {
         |    "periodStartDate": "2024-01-01",
         |    "periodEndDate": "2025-01-01"
         |  },
         |  "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$tysPeriodId[?taxYear=$taxYear]",
         |      "method": "PUT",
         |      "rel": "amend-self-employment-period-summary"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$tysPeriodId[?taxYear=$taxYear]",
         |      "method": "GET",
         |      "rel": "self"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$tysPeriodId[?taxYear=$taxYear]",
         |      "method": "GET",
         |      "rel": "list-self-employment-period-summaries"
         |    }
         |  ]
         |}
      """.stripMargin
    )

    val controller = new RetrievePeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockRetrievePeriodSummaryRequestParser,
      service = mockRetrievePeriodSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId, periodId, Some(taxYear))(fakeGetRequest)
  }

}
