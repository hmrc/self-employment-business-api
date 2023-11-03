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
import api.hateoas.Method.{GET, PUT}
import api.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import api.models.domain.{BusinessId, Nino, PeriodId, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import v1.controllers.validators.MockAmendPeriodSummaryValidatorFactory
import v1.mocks.services.MockAmendPeriodSummaryService
import v1.models.request.amendPeriodSummary._
import v1.models.response.amendPeriodSummary.AmendPeriodSummaryHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendPeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockAmendPeriodSummaryService
    with MockAmendPeriodSummaryValidatorFactory
    with MockHateoasFactory
    with AmendPeriodSummaryFixture {

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new PreTysTest {
        willUseValidator(returningSuccess(requestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendPeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, None))
          .returns(HateoasWrapper((), testHateoasLinks))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseJson)
        )
      }

      "the TYS request received is valid" in new TysTest {
        willUseValidator(returningSuccess(requestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendPeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(TaxYear.fromMtd(taxYear))))
          .returns(HateoasWrapper((), testHateoasLinks))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseJson)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new PreTysTest {
        willUseValidator(returning(NinoFormatError))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new PreTysTest {
        willUseValidator(returningSuccess(requestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {
    val businessId: String = "XAIS12345678910"
    val periodId: String

    val requestData: AmendPeriodSummaryRequestData

    val requestBodyJson: JsValue            = amendPeriodSummaryBodyMtdJson
    val requestBody: AmendPeriodSummaryBody = amendPeriodSummaryBody

    val responseJson: JsValue

    val testHateoasLinks: Seq[Link]

    val controller = new AmendPeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockAmendPeriodSummaryValidatorFactory,
      service = mockAmendPeriodSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

  }

  private trait PreTysTest extends Test {
    val periodId: String = "2019-01-01_2020-01-01"

    val requestData: AmendPeriodSummaryRequestData =
      AmendPeriodSummaryRequestData(Nino(nino), BusinessId(businessId), PeriodId(periodId), None, requestBody)

    val responseJson: JsValue = Json.parse(
      s"""
         |{
         |    "links": [
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "method": "PUT",
         |      "rel": "amend-self-employment-period-summary"
         |
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
         |      "method": "GET",
         |      "rel": "self"
         |
         |    },
         |    {
         |      "href": "/individuals/business/self-employment/$nino/$businessId/period",
         |      "method": "GET",
         |      "rel": "list-self-employment-period-summaries"
         |
         |    }
         |    ]
         |  }
    """.stripMargin
    )

    val testHateoasLinks: Seq[Link] = List(
      Link(s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", PUT, "amend-self-employment-period-summary"),
      Link(s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", GET, "self"),
      Link(s"/individuals/business/self-employment/$nino/$businessId/period", GET, "list-self-employment-period-summaries")
    )

    protected def callController(): Future[Result] =
      controller.handleRequest(nino, businessId, periodId, None)(fakePutRequest(requestBodyJson))

  }

  private trait TysTest extends Test {
    val periodId: String = "2024-01-01_2025-01-01"
    val taxYear: String  = "2023-24"

    val requestData: AmendPeriodSummaryRequestData =
      AmendPeriodSummaryRequestData(Nino(nino), BusinessId(businessId), PeriodId(periodId), Some(TaxYear.fromMtd(taxYear)), requestBody)

    val responseJson: JsValue = Json.parse(
      s"""
         |{
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

    val testHateoasLinks: Seq[Link] = List(
      Link(
        s"/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
        PUT,
        "amend-self-employment-period-summary"),
      Link(s"/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]", GET, "self"),
      Link(
        s"/individuals/business/self-employment/$nino/$businessId/period/$periodId[?taxYear=$taxYear]",
        GET,
        "list-self-employment-period-summaries")
    )

    protected def callController(): Future[Result] =
      controller.handleRequest(nino, businessId, periodId, Some(taxYear))(fakePutRequest(requestBodyJson))

  }

}
