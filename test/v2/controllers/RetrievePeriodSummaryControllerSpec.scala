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

package v2.controllers

import api.models.domain.PeriodId
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.Method.{GET, PUT}
import shared.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import v2.controllers.validators.MockRetrievePeriodSummaryValidatorFactory
import v2.models.request.retrievePeriodSummary.RetrievePeriodSummaryRequestData
import v2.models.response.retrievePeriodSummary._
import v2.services.MockRetrievePeriodSummaryService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrievePeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockRetrievePeriodSummaryService
    with MockRetrievePeriodSummaryValidatorFactory
    with MockHateoasFactory {

  private val businessId = "XAIS12345678910"
  private val taxYear    = "2023-24"

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockRetrievePeriodSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, RetrievePeriodSummaryHateoasData(Nino(validNino), BusinessId(businessId), periodId, None))
          .returns(HateoasWrapper(responseBody, testHateoasLink))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseJson)
        )
      }

      "the TYS request received is valid" in new TysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrievePeriodSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, RetrievePeriodSummaryHateoasData(Nino(validNino), BusinessId(businessId), periodId, Some(TaxYear.fromMtd(taxYear))))
          .returns(HateoasWrapper(responseBody, testHateoasLink))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseJson)
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

        MockRetrievePeriodSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {
    val periodId: String = "2019-01-01_2020-01-01"

    val requestData: RetrievePeriodSummaryRequestData = RetrievePeriodSummaryRequestData(Nino(validNino), BusinessId(businessId), PeriodId(periodId), None)

    val responseBody: RetrievePeriodSummaryResponse = RetrievePeriodSummaryResponse(
      periodDates = PeriodDates("2019-01-01", "2020-01-01"),
      periodIncome = None,
      periodExpenses = None,
      periodDisallowableExpenses = None
    )

    val testHateoasLink: Seq[Link] = Seq(
      Link(s"/individuals/business/self-employment/$validNino/$businessId/period/$periodId", PUT, "amend-self-employment-period-summary"),
      Link(s"/individuals/business/self-employment/$validNino/$businessId/period/$periodId", GET, "self"),
      Link(s"/individuals/business/self-employment/$validNino/$businessId/period/$periodId", GET, "list-self-employment-period-summaries")
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
         |      "href": "/individuals/business/self-employment/$validNino/$businessId/period/$periodId",
         |      "method": "PUT",
         |      "rel": "amend-self-employment-period-summary"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$validNino/$businessId/period/$periodId",
         |      "method": "GET",
         |      "rel": "self"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$validNino/$businessId/period/$periodId",
         |      "method": "GET",
         |      "rel": "list-self-employment-period-summaries"
         |    }
         |  ]
         |}
      """.stripMargin
    )

    private val controller = new RetrievePeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrievePeriodSummaryValidatorFactory,
      service = mockRetrievePeriodSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(validNino, businessId, periodId, None)(fakeGetRequest)
  }

  private trait TysTest extends ControllerTest {
    val periodId: String = "2024-01-01_2025-01-01"

    val requestData: RetrievePeriodSummaryRequestData =
      RetrievePeriodSummaryRequestData(Nino(validNino), BusinessId(businessId), PeriodId(periodId), Some(TaxYear.fromMtd(taxYear)))

    val responseBody: RetrievePeriodSummaryResponse =
      RetrievePeriodSummaryResponse(PeriodDates("2024-01-01", "2025-01-01"), None, None, None)

    val testHateoasLink: Seq[Link] = List(
      Link(
        s"/individuals/business/self-employment/$validNino/$businessId/period/$periodId[?taxYear=$taxYear]",
        PUT,
        "amend-self-employment-period-summary"),
      Link(s"/individuals/business/self-employment/$validNino/$businessId/period/$periodId[?taxYear=$taxYear]", GET, "self"),
      Link(
        s"/individuals/business/self-employment/$validNino/$businessId/period/$periodId[?taxYear=$taxYear]",
        GET,
        "list-self-employment-period-summaries")
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
         |      "href": "/individuals/business/self-employment/$validNino/$businessId/period/$periodId[?taxYear=$taxYear]",
         |      "method": "PUT",
         |      "rel": "amend-self-employment-period-summary"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$validNino/$businessId/period/$periodId[?taxYear=$taxYear]",
         |      "method": "GET",
         |      "rel": "self"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$validNino/$businessId/period/$periodId[?taxYear=$taxYear]",
         |      "method": "GET",
         |      "rel": "list-self-employment-period-summaries"
         |    }
         |  ]
         |}
      """.stripMargin
    )

    private val controller = new RetrievePeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockRetrievePeriodSummaryValidatorFactory,
      service = mockRetrievePeriodSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(validNino, businessId, periodId, Some(taxYear))(fakeGetRequest)
  }

}
