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

package v2.services

import shared.controllers.EndpointLogContext
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v2.connectors.MockListPeriodSummariesConnector
import v2.models.request.listPeriodSummaries.ListPeriodSummariesRequestData
import v2.models.response.listPeriodSummaries.{ListPeriodSummariesResponse, PeriodDetails}

import scala.concurrent.Future

class ListPeriodSummariesServiceSpec extends ServiceSpec {

  val nino: String                   = "AA123456A"
  val businessId: String             = "XAIS12345678910"
  val taxYear: String                = "2024-25"
  override implicit val correlationId: String = "X-123"

  val response: ListPeriodSummariesResponse[PeriodDetails] = ListPeriodSummariesResponse(
    Seq(
      PeriodDetails(
        "2020-01-01_2020-01-01",
        "2020-01-01",
        "2020-01-01"
//        Some("2020-01-01") // To be reinstated, see MTDSA-15595
      ))
  )

  private val requestData = ListPeriodSummariesRequestData(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    None
  )

  private val requestDataForTys = ListPeriodSummariesRequestData(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    taxYear = Some(TaxYear.fromMtd(taxYear))
  )

  trait Test extends MockListPeriodSummariesConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new ListPeriodSummariesService(
      connector = mockListPeriodSummariesConnector
    )

  }

  "service" should {
    "service call successful for default request" when {
      "return mapped result" in new Test {
        MockListPeriodSummariesConnector
          .listPeriodSummaries(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.listPeriodSummaries(requestData)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }

    "service call successful for TYS request" when {
      "return mapped result" in new Test {
        MockListPeriodSummariesConnector
          .listPeriodSummaries(requestDataForTys)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.listPeriodSummaries(requestDataForTys)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {
      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockListPeriodSummariesConnector
            .listPeriodSummaries(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(desErrorCode))))))

          await(service.listPeriodSummaries(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input: Seq[(String, MtdError)] = {
        val errors: Seq[(String, MtdError)] =
          Seq(
            "INVALID_NINO"             -> NinoFormatError,
            "INVALID_INCOME_SOURCEID"  -> BusinessIdFormatError,
            "INVALID_INCOME_SOURCE_ID" -> BusinessIdFormatError,
            "NOT_FOUND_INCOME_SOURCE"  -> NotFoundError,
            "SERVER_ERROR"             -> InternalError,
            "SERVICE_UNAVAILABLE"      -> InternalError
          )
        val tysSpecificErrors: Seq[(String, MtdError)] =
          Seq(
            "INVALID_INCOMESOURCE_ID" -> BusinessIdFormatError,
            "INVALID_TAX_YEAR"        -> TaxYearFormatError,
            "INVALID_CORRELATION_ID"  -> InternalError,
            "TAX_YEAR_NOT_SUPPORTED"  -> RuleTaxYearNotSupportedError
          )
        errors ++ tysSpecificErrors
      }

      input.foreach(args => (serviceError _).tupled(args))
    }
  }

}
