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

package v3.listPeriodSummaries

import play.api.Configuration
import play.api.libs.json.Json
import play.api.mvc.Result
import shared.config.MockSharedAppConfig
import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors.*
import shared.models.outcomes.ResponseWrapper
import v3.listPeriodSummaries.def1.model.request.Def1_ListPeriodSummariesRequestData
import v3.listPeriodSummaries.def1.model.response.{Def1_ListPeriodSummariesResponse, Def1_PeriodDetails}
import v3.listPeriodSummaries.model.request.ListPeriodSummariesRequestData
import v3.listPeriodSummaries.model.response.{ListPeriodSummariesResponse, PeriodDetails}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ListPeriodSummariesControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockListPeriodSummariesService
    with MockListPeriodSummariesValidatorFactory
    with MockSharedAppConfig {

  private val businessId: String = "XAIS12345678910"
  private val from: String       = "2019-01-01"
  private val to: String         = "2020-01-01"
  //  private val creationDate: String = "2020-01-02" // To be reinstated, see MTDSA-15595
  private val periodId: String = s"${from}_$to"
  private val taxYear: String  = "2024-25"

  private val periodDetails: PeriodDetails = Def1_PeriodDetails(
    periodId,
    from,
    to
    //    Some(creationDate) // To be reinstated, see MTDSA-15595
  )

  private val response: ListPeriodSummariesResponse[PeriodDetails] = Def1_ListPeriodSummariesResponse(Seq(periodDetails))

  private val responseBody = Json.parse(s"""
                                           |{
                                           |  "periods": [
                                           |    {
                                           |      "periodId": "$periodId",
                                           |      "periodStartDate": "$from",
                                           |      "periodEndDate": "$to"
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
                                              |      "periodEndDate": "$to"
                                              |    }
                                              |  ]
                                              |}
    """.stripMargin)

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockListPeriodSummariesService
          .listPeriodSummaries(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        runOkTest(
          expectedStatus = OK,
          maybeExpectedResponseBody = Some(responseBody)
        )
      }

      "the Tys request received is valid" in new TysTest {
        willUseValidator(returningSuccess(tysRequestData))

        MockListPeriodSummariesService
          .listPeriodSummaries(tysRequestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

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

        MockListPeriodSummariesService
          .listPeriodSummaries(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest {

    val requestData: ListPeriodSummariesRequestData = Def1_ListPeriodSummariesRequestData(Nino(validNino), BusinessId(businessId), None)

    val controller: ListPeriodSummariesController = new ListPeriodSummariesController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockListPeriodSummariesValidatorFactory,
      service = mockListPeriodSummariesService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.handleRequest(validNino, businessId, None)(fakeGetRequest)
  }

  private trait TysTest extends ControllerTest {

    val tysRequestData: ListPeriodSummariesRequestData =
      Def1_ListPeriodSummariesRequestData(Nino(validNino), BusinessId(businessId), Some(TaxYear.fromMtd(taxYear)))

    val controller: ListPeriodSummariesController = new ListPeriodSummariesController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockListPeriodSummariesValidatorFactory,
      service = mockListPeriodSummariesService,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    MockedSharedAppConfig.featureSwitchConfig.anyNumberOfTimes() returns Configuration(
      "supporting-agents-access-control.enabled" -> true
    )

    MockedSharedAppConfig.endpointAllowsSupportingAgents(controller.endpointName).anyNumberOfTimes() returns false

    protected def callController(): Future[Result] = controller.handleRequest(validNino, businessId, Some(taxYear))(fakeGetRequest)
  }

}
