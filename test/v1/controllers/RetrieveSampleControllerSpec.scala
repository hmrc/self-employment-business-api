/*
 * Copyright 2020 HM Revenue & Customs
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

import play.api.libs.json.{JsObject, Json}
import play.api.mvc.Result
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.fixtures.RetrieveSampleControllerFixture
import v1.hateoas.HateoasLinks
import v1.mocks.hateoas.MockHateoasFactory
import v1.mocks.requestParsers.MockDeleteRetrieveRequestParser
import v1.mocks.services.{MockDeleteRetrieveService, MockEnrolmentsAuthService, MockMtdIdLookupService}
import v1.models.domain.{DesTaxYear, SampleMtdEnum}
import v1.models.errors._
import v1.models.hateoas.Method.{DELETE, GET, PUT}
import v1.models.hateoas.RelType.{AMEND_SAMPLE_REL, DELETE_SAMPLE_REL, SELF}
import v1.models.hateoas.{HateoasWrapper, Link}
import v1.models.outcomes.ResponseWrapper
import v1.models.request.{DeleteRetrieveRawData, DeleteRetrieveRequest}
import v1.models.response.retrieveSample._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class RetrieveSampleControllerSpec
    extends ControllerBaseSpec
    with MockEnrolmentsAuthService
    with MockMtdIdLookupService
    with MockDeleteRetrieveService
    with MockHateoasFactory
    with MockDeleteRetrieveRequestParser
    with HateoasLinks {

  val nino: String          = "AA123456A"
  val taxYear: String       = "2017-18"
  val correlationId: String = "X-123"

  val rawData: DeleteRetrieveRawData = DeleteRetrieveRawData(
    nino = nino,
    taxYear = taxYear
  )

  val requestData: DeleteRetrieveRequest = DeleteRetrieveRequest(
    nino = Nino(nino),
    taxYear = DesTaxYear.fromMtd(taxYear)
  )

  val amendSampleLink: Link =
    Link(
      href = s"/mtd/template/sample/$nino/$taxYear",
      method = PUT,
      rel = AMEND_SAMPLE_REL
    )

  val retrieveSampleLink: Link =
    Link(
      href = s"/mtd/template/sample/$nino/$taxYear",
      method = GET,
      rel = SELF
    )

  val deleteSampleLink: Link =
    Link(
      href = s"/mtd/template/sample/$nino/$taxYear",
      method = DELETE,
      rel = DELETE_SAMPLE_REL
    )

  val arrayItemModel1: SampleArrayItem = SampleArrayItem(
    id = "AAA123",
    declaredAmount = Some(200.11),
    taxableAmount = Some(100.14),
    itemType = SampleMtdEnum.One,
    taxYear = "2018-19",
    finalised = true
  )

  val arrayItemModel2: SampleArrayItem = SampleArrayItem(
    id = "AAA123",
    declaredAmount = Some(200.11),
    taxableAmount = Some(100.14),
    itemType = SampleMtdEnum.One,
    taxYear = "2018-19",
    finalised = true
  )

  val sampleObjectModel: SampleObject = SampleObject(
    dateSubmitted = "01-01-2019",
    submissionItem = Some(arrayItemModel2)
  )

  val sampleOptionalModel1: SampleOptionalObject = SampleOptionalObject(
    itemIdentifier = Some("anId1"),
    itemType = Some(SampleMtdEnum.One),
    deductibleAmount = Some(300.54)
  )

  val sampleOptionalModel2: SampleOptionalObject = SampleOptionalObject(
    itemIdentifier = Some("anId2"),
    itemType = Some(SampleMtdEnum.Four),
    deductibleAmount = Some(400.54)
  )

  val retrieveSampleResponseModel: RetrieveSampleResponse = RetrieveSampleResponse(
    Some(Seq(arrayItemModel1)),
    Some(sampleObjectModel),
    Some(sampleOptionalModel1),
    Some(Seq(sampleOptionalModel2))
  )

  private val mtdResponse = RetrieveSampleControllerFixture.mtdResponseWithHateoas(nino, taxYear)

  trait Test {
    val hc = HeaderCarrier()

    val controller = new RetrieveSampleController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      requestParser = mockDeleteRetrieveRequestParser,
      service = mockDeleteRetrieveService,
      hateoasFactory = mockHateoasFactory,
      cc = cc
    )

    MockedMtdIdLookupService.lookup(nino).returns(Future.successful(Right("test-mtd-id")))
    MockedEnrolmentsAuthService.authoriseUser()
  }

  "RetrieveSampleController" should {
    "return OK" when {
      "happy path" in new Test {

        val wrappedResponse: HateoasWrapper[RetrieveSampleResponse] = HateoasWrapper(
          payload = retrieveSampleResponseModel,
          links = Seq(
            amendSampleLink,
            retrieveSampleLink,
            deleteSampleLink
          )
        )

        MockDeleteRetrieveRequestParser
          .parse(rawData)
          .returns(Right(requestData))

        MockDeleteRetrieveService
          .retrieve[RetrieveSampleResponse]()
          .returns(Future.successful(Right(ResponseWrapper(correlationId, retrieveSampleResponseModel))))

        MockHateoasFactory
          .wrap(retrieveSampleResponseModel, RetrieveSampleHateoasData(nino, taxYear))
          .returns(wrappedResponse)

        val result: Future[Result] = controller.retrieveSample(nino, taxYear)(fakeGetRequest)

        status(result) shouldBe OK
        contentAsJson(result) shouldBe mtdResponse.as[JsObject]
        header("X-CorrelationId", result) shouldBe Some(correlationId)
      }
    }

    "return the error as per spec" when {
      "parser errors occur" must {
        def errorsFromParserTester(error: MtdError, expectedStatus: Int): Unit = {
          s"a ${error.code} error is returned from the parser" in new Test {

            MockDeleteRetrieveRequestParser
              .parse(rawData)
              .returns(Left(ErrorWrapper(Some(correlationId), error, None)))

            val result: Future[Result] = controller.retrieveSample(nino, taxYear)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(error)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (BadRequestError, BAD_REQUEST),
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (RuleTaxYearRangeInvalidError, BAD_REQUEST),
          (RuleTaxYearNotSupportedError, BAD_REQUEST)
        )

        input.foreach(args => (errorsFromParserTester _).tupled(args))
      }

      "service errors occur" must {
        def serviceErrors(mtdError: MtdError, expectedStatus: Int): Unit = {
          s"a $mtdError error is returned from the service" in new Test {

            MockDeleteRetrieveRequestParser
              .parse(rawData)
              .returns(Right(requestData))

            MockDeleteRetrieveService
              .retrieve[RetrieveSampleResponse]()
              .returns(Future.successful(Left(ErrorWrapper(Some(correlationId), mtdError))))

            val result: Future[Result] = controller.retrieveSample(nino, taxYear)(fakeGetRequest)

            status(result) shouldBe expectedStatus
            contentAsJson(result) shouldBe Json.toJson(mtdError)
            header("X-CorrelationId", result) shouldBe Some(correlationId)
          }
        }

        val input = Seq(
          (NinoFormatError, BAD_REQUEST),
          (TaxYearFormatError, BAD_REQUEST),
          (NotFoundError, NOT_FOUND),
          (DownstreamError, INTERNAL_SERVER_ERROR)
        )

        input.foreach(args => (serviceErrors _).tupled(args))
      }
    }
  }
}
