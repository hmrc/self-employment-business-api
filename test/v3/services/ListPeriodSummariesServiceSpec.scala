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
import v3.connectors.MockListPeriodSummariesConnector
import v3.models.request.listPeriodSummaries.ListPeriodSummariesRequestData
import v3.models.response.listPeriodSummaries.{ListPeriodSummariesResponse, PeriodDetails}

import scala.concurrent.Future

class ListPeriodSummariesServiceSpec extends ServiceSpec {

  private val nino                           = "AA123456A"
  private val businessId                     = "XAIS12345678910"
  private val taxYear                        = "2024-25"
  implicit private val correlationId: String = "X-123"

  private val response = ListPeriodSummariesResponse(
    List(
      PeriodDetails(
        "2020-01-01_2020-01-01",
        "2020-01-01",
        "2020-01-01"
//        Some("2020-01-01") // To be reinstated, see MTDSA-15595
      ))
  )

  private val requestData = ListPeriodSummariesRequestData(Nino(nino), BusinessId(businessId), None)

  private val requestDataForTys = ListPeriodSummariesRequestData(Nino(nino), BusinessId(businessId), Some(TaxYear.fromMtd(taxYear)))

  "service" should {
    "service call successful for default request" when {
      "return mapped result" in new Test {
        MockedListPeriodSummariesConnector
          .listPeriodSummaries(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: ServiceOutcome[ListPeriodSummariesResponse[PeriodDetails]] = await(service.listPeriodSummaries(requestData))
        result shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }

    "service call successful for TYS request" when {
      "return mapped result" in new Test {
        MockedListPeriodSummariesConnector
          .listPeriodSummaries(requestDataForTys)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        val result: ServiceOutcome[ListPeriodSummariesResponse[PeriodDetails]] = await(service.listPeriodSummaries(requestDataForTys))
        result shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Test {

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

}
