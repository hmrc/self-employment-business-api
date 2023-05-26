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

import anyVersion.models.request.amendPeriodSummary.AmendPeriodSummaryRawData
import anyVersion.models.response.amendPeriodSummary.AmendPeriodSummaryHateoasData
import api.controllers.{ControllerBaseSpec, ControllerTestRunner}
import api.mocks.hateoas.MockHateoasFactory
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import api.models.hateoas
import api.models.hateoas.Method.{GET, PUT}
import api.models.hateoas.{HateoasWrapper, Link}
import api.models.outcomes.ResponseWrapper
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import v1.mocks.requestParsers.MockAmendPeriodSummaryRequestParser
import v1.mocks.services.MockAmendPeriodSummaryService
import v1.models.request.amendPeriodSummary._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AmendPeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockAmendPeriodSummaryService
    with MockAmendPeriodSummaryRequestParser
    with MockHateoasFactory
    with AmendPeriodSummaryFixture {

  private val businessId: String  = "XAIS12345678910"
  private val periodId: String    = "2019-01-01_2020-01-01"
  private val tysPeriodId: String = "2024-01-01_2025-01-01"
  private val taxYear: String     = "2023-24"

  val testHateoasLinks: Seq[Link] = Seq(
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

  private val testTysHateoasLink = Seq(
    hateoas.Link(
      href = s"/individuals/business/self-employment/$nino/$businessId/period/$tysPeriodId[?taxYear=$taxYear]",
      method = PUT,
      rel = "amend-self-employment-period-summary"
    ),
    hateoas.Link(
      href = s"/individuals/business/self-employment/$nino/$businessId/period/$tysPeriodId[?taxYear=$taxYear]",
      method = GET,
      rel = "self"),
    hateoas.Link(
      href = s"/individuals/business/self-employment/$nino/$businessId/period/$tysPeriodId[?taxYear=$taxYear]",
      method = GET,
      rel = "list-self-employment-period-summaries"
    )
  )

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

  val tysResponseJson: JsValue = Json.parse(
    s"""
       |{"links":[{"href":"/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2024-01-01_2025-01-01[?taxYear=2023-24]","method":"PUT","rel":"amend-self-employment-period-summary"},{"href":"/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2024-01-01_2025-01-01[?taxYear=2023-24]","method":"GET","rel":"self"},{"href":"/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2024-01-01_2025-01-01[?taxYear=2023-24]","method":"GET","rel":"list-self-employment-period-summaries"}]}
    """.stripMargin
  )

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new PreTysTest {
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

      "the TYS request received is valid" in new TysTest {

        MockAmendPeriodSummaryRequestParser
          .requestFor(tysRawData)
          .returns(Right(tysRequestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(tysRequestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        MockHateoasFactory
          .wrap((), AmendPeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(TaxYear.fromMtd(taxYear))))
          .returns(HateoasWrapper((), testTysHateoasLink))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(tysResponseJson)
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new PreTysTest {

        MockAmendPeriodSummaryRequestParser
          .requestFor(rawData)
          .returns(Left(ErrorWrapper(correlationId, NinoFormatError)))

        runErrorTest(NinoFormatError)
      }

      "the service returns an error" in new PreTysTest {

        MockAmendPeriodSummaryRequestParser
          .requestFor(rawData)
          .returns(Right(requestData))

        MockAmendPeriodSummaryService
          .amendPeriodSummary(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val controller = new AmendPeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      parser = mockAmendPeriodSummaryRequestParser,
      service = mockAmendPeriodSummaryService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

  }

  private trait PreTysTest extends Test {

    protected def callController(): Future[Result] =
      controller.handleRequest(nino, businessId, periodId, None)(fakePutRequest(requestBodyJson))

  }

  private trait TysTest extends Test {

    protected def callController(): Future[Result] =
      controller.handleRequest(nino, businessId, periodId, Some(taxYear))(fakePutRequest(requestBodyJson))

  }

}
