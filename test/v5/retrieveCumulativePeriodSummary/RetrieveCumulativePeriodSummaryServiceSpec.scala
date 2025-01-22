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

package v5.retrieveCumulativePeriodSummary

import shared.config.MockSharedAppConfig
import shared.controllers.EndpointLogContext
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.{ServiceOutcome, ServiceSpec}
import v5.retrieveCumulativePeriodSummary.def1.model.request.Def1_RetrieveCumulativePeriodSummaryRequestData
import v5.retrieveCumulativePeriodSummary.def1.model.response.{Def1_RetrieveCumulativePeriodSummaryResponse, Def1_Retrieve_PeriodDates}
import v5.retrieveCumulativePeriodSummary.model.response.RetrieveCumulativePeriodSummaryResponse

import scala.concurrent.Future

class RetrieveCumulativePeriodSummaryServiceSpec extends ServiceSpec {

  private val nino                            = Nino("AA123456A")
  private val businessId                      = BusinessId("XAIS12345678910")
  private val taxYear                         = TaxYear.fromMtd("2025-26")
  override implicit val correlationId: String = "X-123"

  private val requestData = Def1_RetrieveCumulativePeriodSummaryRequestData(
    nino = nino,
    businessId = businessId,
    taxYear = taxYear
  )

  private val def1Response =
    Def1_RetrieveCumulativePeriodSummaryResponse(Def1_Retrieve_PeriodDates("2025-07-08", "2025-09-10"), None, None, None)

  trait Test extends MockRetrieveCumulativePeriodSummaryConnector with MockSharedAppConfig {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")
    val service                                 = new RetrieveCumulativePeriodSummaryService(connector = mockRetrieveCumulativePeriodSummaryConnector)
  }

  "retrievePeriodSummary()" when {
    "given a def1 request" should {

      "return a def1 response" in new Test {
        MockRetrieveCumulativePeriodSummaryConnector
          .retrieveCumulativePeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, def1Response))))

        val result: ServiceOutcome[RetrieveCumulativePeriodSummaryResponse] = await(service.retrieveCumulativePeriodSummary(requestData))
        result shouldBe Right(ResponseWrapper(correlationId, def1Response))
      }
    }
  }

  "retrievePeriodSummary()" should {
    "map errors according to spec" when {
      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockRetrieveCumulativePeriodSummaryConnector
            .retrieveCumulativePeriodSummary(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(desErrorCode))))))

          await(service.retrieveCumulativePeriodSummary(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input: Seq[(String, MtdError)] = {
        val errors: Seq[(String, MtdError)] = List(
          "INVALID_NINO"             -> NinoFormatError,
          "INVALID_TAX_YEAR"         -> TaxYearFormatError,
          "INVALID_INCOME_SOURCE_ID" -> BusinessIdFormatError,
          "INVALID_CORRELATION_ID"   -> InternalError,
          "NOT_FOUND"                -> NotFoundError,
          "TAX_YEAR_NOT_SUPPORTED"   -> RuleTaxYearNotSupportedError,
          "SERVER_ERROR"             -> InternalError,
          "SERVICE_UNAVAILABLE"      -> InternalError
        )
        errors
      }
      input.foreach(args => (serviceError _).tupled(args))
    }
  }

}
