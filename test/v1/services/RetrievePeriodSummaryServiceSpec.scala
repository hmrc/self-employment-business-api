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

package v1.services

import v1.controllers.EndpointLogContext
import v1.mocks.connectors.MockRetrievePeriodSummaryConnector
import v1.models.domain.{BusinessId, Nino, PeriodId}
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.retrievePeriodSummary.RetrievePeriodSummaryRequest
import v1.models.response.retrievePeriodSummary.{PeriodDates, RetrievePeriodSummaryResponse}

import scala.concurrent.Future

class RetrievePeriodSummaryServiceSpec extends ServiceSpec {

  val nino: String                   = "AA123456A"
  val businessId: String             = "XAIS12345678910"
  val periodId: String               = "2019-01-25_2020-01-25"
  val tysTaxYear: String             = "23-24"
  implicit val correlationId: String = "X-123"

  val response: RetrievePeriodSummaryResponse = RetrievePeriodSummaryResponse(
    PeriodDates("2019-01-25", "2020-01-25"),
    None,
    None,
    None
  )

  private val requestData = RetrievePeriodSummaryRequest(
    nino = Nino(nino),
    businessId = BusinessId(businessId),
    periodId = PeriodId(periodId),
    taxYear = None
  )

  trait Test extends MockRetrievePeriodSummaryConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new RetrievePeriodSummaryService(
      connector = mockRetrievePeriodSummaryConnector
    )

  }

  "service" should {
    "service call successful" when {
      "return mapped result" in new Test {
        MockRetrievePeriodSummaryConnector
          .retrievePeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrievePeriodSummary(requestData)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {
      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockRetrievePeriodSummaryConnector
            .retrievePeriodSummary(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(desErrorCode))))))

          await(service.retrievePeriodSummary(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input: Seq[(String, MtdError)] = {
        val errors: Seq[(String, MtdError)] = Seq(
          "INVALID_NINO"            -> NinoFormatError,
          "INVALID_INCOMESOURCEID"  -> BusinessIdFormatError,
          "INVALID_DATE_FROM"       -> PeriodIdFormatError,
          "INVALID_DATE_TO"         -> PeriodIdFormatError,
          "NOT_FOUND_INCOME_SOURCE" -> NotFoundError,
          "NOT_FOUND_PERIOD"        -> NotFoundError,
          "SERVER_ERROR"            -> InternalError,
          "SERVICE_UNAVAILABLE"     -> InternalError
        )
        val extraTysErrors: Seq[(String, MtdError)] = Seq(
          "INVALID_TAX_YEAR"             -> TaxYearFormatError,
          "INVALID_INCOMESOURCE_ID"      -> BusinessIdFormatError,
          "INVALID_CORRELATION_ID"       -> InternalError,
          "INCOME_DATA_SOURCE_NOT_FOUND" -> NotFoundError,
          "SUBMISSION_DATA_NOT_FOUND"    -> NotFoundError,
          "TAX_YEAR_NOT_SUPPORTED"       -> RuleTaxYearNotSupportedError
        )
        errors ++ extraTysErrors
      }
      input.foreach(args => (serviceError _).tupled(args))
    }
  }

}
