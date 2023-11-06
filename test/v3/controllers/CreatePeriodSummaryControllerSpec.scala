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
import api.hateoas.Method.{GET, PUT}
import api.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import mocks.MockAppConfig
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import v3.fixtures.CreatePeriodSummaryFixture
import v3.mocks.requestParsers.MockCreatePeriodSummaryRequestParser
import v3.mocks.services.MockCreatePeriodSummaryService
import v3.models.request.createPeriodSummary._
import v3.models.response.createPeriodSummary.{CreatePeriodSummaryHateoasData, CreatePeriodSummaryResponse}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreatePeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockCreatePeriodSummaryService
    with MockCreatePeriodSummaryRequestParser
    with MockHateoasFactory
    with MockAppConfig
    with CreatePeriodSummaryFixture {

  private val businessId = "XAIS12345678910"
  private val periodId   = "2017-01-25_2017-01-25"

  private val testHateoasLinks: Seq[Link] = Seq(
    Link(
      href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId",
      method = PUT,
      rel = "amend-self-employment-period-summary"
    ),
    Link(href = s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", method = GET, rel = "self"),
    Link(
      href = s"/individuals/business/self-employment/$nino/$businessId/period",
      method = GET,
      rel = "list-self-employment-period-summaries"
    )
  )

  val responseJson: JsValue = Json.parse(
    s"""
       |{
       |  "periodId": "$periodId",
       |  "links": [
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
       |  ]
       |}
    """.stripMargin
  )

  private val rawData     = CreatePeriodSummaryRawData(nino, businessId, requestMtdBodyJson)
  private val requestData = CreatePeriodSummaryRequestData(Nino(nino), BusinessId(businessId), fullMTDRequestModel)

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        MockCreatePeriodSummaryRequestParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockCreatePeriodicService
          .createPeriodic(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, CreatePeriodSummaryResponse(periodId)))))

        MockHateoasFactory
          .wrap(
            CreatePeriodSummaryResponse(periodId),
            CreatePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(TaxYear.fromMtd("2019-20"))))
          .returns(HateoasWrapper(CreatePeriodSummaryResponse(periodId), testHateoasLinks))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseJson)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        MockCreatePeriodSummaryRequestParser
          .parseRequest(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError)))

        runErrorTest(NinoFormatError)

      }

      "the service returns an error" in new Test {

        MockCreatePeriodSummaryRequestParser
          .parseRequest(rawData)
          .returns(Right(requestData))

        MockCreatePeriodicService
          .createPeriodic(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {
    MockAppConfig.featureSwitches.returns(Configuration("allowNegativeExpenses.enabled" -> false)).anyNumberOfTimes()

    val controller = new CreatePeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockCreatePeriodicRequestParser,
      service = mockCreatePeriodicService,
      appConfig = mockAppConfig,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId)(fakePostRequest(requestMtdBodyJson))
  }

}
