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

package v3.services

import api.controllers.EndpointLogContext
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.{ServiceOutcome, ServiceSpec}
import mocks.MockFeatureSwitches
import v3.connectors.MockListPeriodSummariesConnector
import v3.models.request.listPeriodSummaries.ListPeriodSummariesRequestData
import v3.models.response.listPeriodSummaries.{ListPeriodSummariesResponse, PeriodDetails}

import scala.concurrent.Future

class ListPeriodSummariesServiceSpec extends ServiceSpec with MockFeatureSwitches {

  implicit private val correlationId: String = "X-123"

  private val nino       = "AA123456A"
  private val businessId = "XAIS12345678910"
  private val taxYear    = "2024-25"

  private val periodId           = "2020-01-01_2020-01-01"
  private val periodStartDate    = "2020-01-01"
  private val periodEndDate      = "2020-01-01"
  private val periodCreationDate = "2023-02-31T11:08:17.488Z"

  private val periodDetails = PeriodDetails(periodId, periodStartDate, periodEndDate, Some(periodCreationDate))

  private val response                          = ListPeriodSummariesResponse(List(periodDetails))
  private val responseWithoutPeriodCreationDate = ListPeriodSummariesResponse(List(periodDetails.copy(periodCreationDate = None)))

  private val requestData    = ListPeriodSummariesRequestData(Nino(nino), BusinessId(businessId), None)
  private val requestDataTys = ListPeriodSummariesRequestData(Nino(nino), BusinessId(businessId), Some(TaxYear.fromMtd(taxYear)))

  "service" should {
    "service call successful for default request" when {
      "return mapped result" in new Test with WIS008Enabled {
        MockedListPeriodSummariesConnector
          .listPeriodSummaries(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: ServiceOutcome[ListPeriodSummariesResponse[PeriodDetails]] = await(service.listPeriodSummaries(requestData))
        result shouldBe Right(ResponseWrapper(correlationId, response))
      }

      "return mapped result without periodCreationDate" in new Test with WIS008Disabled {
        MockedListPeriodSummariesConnector
          .listPeriodSummaries(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: ServiceOutcome[ListPeriodSummariesResponse[PeriodDetails]] = await(service.listPeriodSummaries(requestData))
        result shouldBe Right(ResponseWrapper(correlationId, responseWithoutPeriodCreationDate))
      }
    }

    "service call successful for TYS request" when {
      "return mapped result" in new Test with WIS008Enabled {
        MockedListPeriodSummariesConnector
          .listPeriodSummaries(requestDataTys)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: ServiceOutcome[ListPeriodSummariesResponse[PeriodDetails]] = await(service.listPeriodSummaries(requestDataTys))
        result shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test with WIS008Enabled {

          MockedListPeriodSummariesConnector
            .listPeriodSummaries(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          val result: ServiceOutcome[ListPeriodSummariesResponse[PeriodDetails]] = await(service.listPeriodSummaries(requestData))
          result shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors =
        List(
          "INVALID_NINO"            -> NinoFormatError,
          "INVALID_INCOME_SOURCEID" -> BusinessIdFormatError,
          "NOT_FOUND_INCOME_SOURCE" -> NotFoundError,
          "SERVER_ERROR"            -> InternalError,
          "SERVICE_UNAVAILABLE"     -> InternalError
        )

      val tysSpecificErrors =
        List(
          "INVALID_INCOMESOURCE_ID" -> BusinessIdFormatError,
          "INVALID_TAX_YEAR"        -> TaxYearFormatError,
          "INVALID_CORRELATION_ID"  -> InternalError,
          "TAX_YEAR_NOT_SUPPORTED"  -> RuleTaxYearNotSupportedError
        )

      (errors ++ tysSpecificErrors).foreach((serviceError _).tupled)
    }
  }

  private trait Test extends MockListPeriodSummariesConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new ListPeriodSummariesService(mockListPeriodSummariesConnector)
  }

  private trait WIS008Enabled {
    MockFeatureSwitches.isWIS008Enabled.returns(true).anyNumberOfTimes()
  }

  private trait WIS008Disabled {
    MockFeatureSwitches.isWIS008Enabled.returns(false)
  }

}
