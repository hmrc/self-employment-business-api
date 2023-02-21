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
import api.models.hateoas
import api.models.hateoas.HateoasWrapper
import api.models.hateoas.Method.{GET, PUT}
import api.models.outcomes.ResponseWrapper
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import v1.mocks.requestParsers.MockAmendPeriodSummaryRequestParser
import v1.mocks.services.MockAmendPeriodSummaryService
import v1.models.request.amendPeriodSummary._
import v1.models.response.amendPeriodSummary.AmendPeriodSummaryHateoasData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendPeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockAmendPeriodSummaryService
    with MockAmendPeriodSummaryRequestParser
    with MockHateoasFactory
    with AmendPeriodSummaryFixture {

  private val businessId = "XAIS12345678910"
  private val periodId   = "2019-01-01_2020-01-01"
  private val taxYear    = "2023-24"

  private val testHateoasLinks = Seq(
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

  trait Test extends ControllerTest {

    val controller = new AmendPeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockAmendPeriodSummaryRequestParser,
      service = mockAmendPeriodSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] =
      controller.handleRequest(nino, businessId, periodId, Some(taxYear))(fakePutRequest(requestBodyJson))

    MockMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockEnrolmentsAuthService.authoriseUser()
    MockIdGenerator.generateCorrelationId.returns(correlationId)
  }

  private val requestBodyJson = amendPeriodSummaryBodyMtdJson
  private val requestBody     = amendPeriodSummaryBody

  private val rawData        = AmendPeriodSummaryRawData(nino, businessId, periodId, requestBodyJson, None)
  private val tysRawData     = AmendPeriodSummaryRawData(nino, businessId, periodId, requestBodyJson, Some(taxYear))
  private val requestData    = AmendPeriodSummaryRequest(Nino(nino), BusinessId(businessId), periodId, requestBody, None)
  private val tysRequestData = AmendPeriodSummaryRequest(Nino(nino), BusinessId(businessId), periodId, requestBody, Some(TaxYear.fromMtd(taxYear)))

  val responseJson: JsValue = Json.parse(
    s"""
       |{
       |    "links": [
       |    {
       |      "href": "/individuals/business/self-employment/TC663795B/XAIS12345678910/period/2019-06-12_2020-06-12",
       |      "rel": "amend-self-employment-period-summary",
       |      "method": "PUT"
       |    },
       |    {
       |      "href": "/individuals/business/self-employment/TC663795B/XAIS12345678910/period/2019-06-12_2020-06-12",
       |      "rel": "self",
       |      "method": "GET"
       |    },
       |    {
       |      "href": "/individuals/business/self-employment/TC663795B/XAIS12345678910/period",
       |      "rel": "list-self-employment-period-summaries",
       |      "method": "GET"
       |    }
       |    ]
       |  }
    """.stripMargin
  )

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        MockAmendPeriodSummaryRequestParser
          .requestFor(rawData)
          .returns(Right(requestData))

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

      "the TYS request received is valid" in new Test {
        MockAmendPeriodSummaryRequestParser
          .requestFor(tysRawData)
          .returns(Right(tysRequestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(tysRequestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendPeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(TaxYear.fromMtd(taxYear))))
          .returns(HateoasWrapper((), testHateoasLinks))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseJson)
        )
      }

      "return the error as per spec" when {
        "parser errors occur" should {
          "the parser validation fails" in new Test {

            MockAmendPeriodSummaryRequestParser
              .requestFor(rawData)
              .returns(Left(ErrorWrapper(correlationId, NinoFormatError)))
          }
        }
      }

      "the service returns an error" in new Test {

        MockAmendPeriodSummaryRequestParser
          .requestFor(rawData)
          .returns(Right(requestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

      }
    }
  }

}
