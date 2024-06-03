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

package v3.retrievePeriodSummary

import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.hateoas.Method.{GET, PUT}
import api.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.models.domain.{Nino, PeriodId, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import v3.retrievePeriodSummary.def1.model.response.Def1_Retrieve_PeriodDates
import v3.retrievePeriodSummary.def2.model.response.Def2_Retrieve_PeriodDates
import v3.retrievePeriodSummary.model.request.{Def1_RetrievePeriodSummaryRequestData, Def2_RetrievePeriodSummaryRequestData}
import v3.retrievePeriodSummary.model.response.{Def1_RetrievePeriodSummaryResponse, Def2_RetrievePeriodSummaryResponse, RetrievePeriodSummaryHateoasData, RetrievePeriodSummaryResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrievePeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockRetrievePeriodSummaryService
    with MockRetrievePeriodSummaryValidatorFactory
    with MockHateoasFactory {

  private val businessId    = "XAIS12345678910"
  private val taxYear       = "2023-24"
  private val parsedTaxYear = TaxYear.fromMtd(taxYear)

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "given a valid non-TYS request" in new NonTysTest {
        willUseValidator(returningSuccess(requestData))

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
      "given a valid TYS request" in new TysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrievePeriodSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseBody))))

        MockHateoasFactory
          .wrap(responseBody, RetrievePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(parsedTaxYear)))
          .returns(HateoasWrapper(responseBody, testHateoasLink))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseJson)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new NonTysTest {
        willUseValidator(returning(NinoFormatError))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new NonTysTest {
        willUseValidator(returningSuccess(requestData))

        MockRetrievePeriodSummaryService
          .retrieve(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait NonTysTest extends ControllerTest {
    val periodId = "2019-01-01_2020-01-01"

    val requestData: Def1_RetrievePeriodSummaryRequestData =
      Def1_RetrievePeriodSummaryRequestData(Nino(nino), BusinessId(businessId), PeriodId(periodId))

    val responseBody: RetrievePeriodSummaryResponse =
      Def1_RetrievePeriodSummaryResponse(Def1_Retrieve_PeriodDates("2019-01-01", "2020-01-01"), None, None, None)

    val testHateoasLink: Seq[Link] = List(
      Link(s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", PUT, "amend-self-employment-period-summary"),
      Link(s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", GET, "self"),
      Link(s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", GET, "list-self-employment-period-summaries")
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
      validatorFactory = mockRetrievePeriodSummaryValidatorFactory,
      service = mockRetrievePeriodSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId, periodId, None)(fakeGetRequest)
  }

  private trait TysTest extends ControllerTest {
    val periodId = "2024-01-01_2025-01-01"

    val requestData: Def2_RetrievePeriodSummaryRequestData =
      Def2_RetrievePeriodSummaryRequestData(Nino(nino), BusinessId(businessId), PeriodId(periodId), TaxYear.fromMtd(taxYear))

    val responseBody: RetrievePeriodSummaryResponse = Def2_RetrievePeriodSummaryResponse(
      periodDates = Def2_Retrieve_PeriodDates("2024-01-01", "2025-01-01"),
      periodIncome = None,
      periodExpenses = None,
      periodDisallowableExpenses = None
    )

    val testHateoasLink: Seq[Link] = Seq(
      Link(
        href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
        method = PUT,
        rel = "amend-self-employment-period-summary"
      ),
      Link(href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]", method = GET, rel = "self"),
      Link(
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
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
         |      "method": "PUT",
         |      "rel": "amend-self-employment-period-summary"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
         |      "method": "GET",
         |      "rel": "self"
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
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
      validatorFactory = mockRetrievePeriodSummaryValidatorFactory,
      service = mockRetrievePeriodSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId, periodId, Some(taxYear))(fakeGetRequest)
  }

}
