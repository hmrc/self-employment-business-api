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
import api.models.domain.{BusinessId, Nino, PeriodId}
import api.models.errors._
import api.models.outcomes.ResponseWrapper
import api.services.ServiceSpec
import mocks.MockAppConfig
import play.api.Configuration
import v3.connectors.MockRetrievePeriodSummaryConnector
import v3.models.request.retrievePeriodSummary.RetrievePeriodSummaryRequestData
import v3.models.response.retrievePeriodSummary.{PeriodDates, PeriodIncome, RetrievePeriodSummaryResponse}

import scala.concurrent.Future

class RetrievePeriodSummaryServiceSpec extends ServiceSpec {

  private val nino                           = Nino("AA123456A")
  private val businessId                     = BusinessId("XAIS12345678910")
  private val periodId                       = PeriodId("2019-01-25_2020-01-25")
  private implicit val correlationId: String = "X-123"

  private val requestData = RetrievePeriodSummaryRequestData(
    nino = nino,
    businessId = businessId,
    periodId = periodId,
    taxYear = None
  )

  private val periodIncomeWithCl290Enabled = PeriodIncome(turnover = Some(2000.00), None, taxTakenOffTradingIncome = Some(2000.00))

  private val periodIncomeWithCl290Disabled = PeriodIncome(turnover = Some(2000.00), None, taxTakenOffTradingIncome = None)

  private val responseWithCl290Disabled = RetrievePeriodSummaryResponse(
    PeriodDates("2019-01-25", "2020-01-25"),
    Some(periodIncomeWithCl290Disabled),
    None,
    None
  )

  private val responseWithCl290Enabled = RetrievePeriodSummaryResponse(
    PeriodDates("2019-01-25", "2020-01-25"),
    Some(periodIncomeWithCl290Enabled),
    None,
    None
  )

  trait Test extends MockRetrievePeriodSummaryConnector with MockAppConfig {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new RetrievePeriodSummaryService(
      connector = mockRetrievePeriodSummaryConnector,
      appConfig = mockAppConfig
    )

  }

  "RetrievePeriodSummaryService" should {
    "return a valid response without cl290 field taxTakenOffTradingIncome" when {
      "downstream response includes taxTakenOffTradingIncome and cl290.enabled = false" in new Test {
        MockRetrievePeriodSummaryConnector
          .retrievePeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithCl290Enabled))))

        MockAppConfig.featureSwitches.returns(Configuration("cl290.enabled" -> false))

        await(service.retrievePeriodSummary(requestData)) shouldBe Right(ResponseWrapper(correlationId, responseWithCl290Disabled))
      }

      "downstream response does not include taxTakenOffTradingIncome and cl290.enabled = false" in new Test {
        MockRetrievePeriodSummaryConnector
          .retrievePeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithCl290Disabled))))

        MockAppConfig.featureSwitches.returns(Configuration("cl290.enabled" -> false))

        await(service.retrievePeriodSummary(requestData)) shouldBe Right(ResponseWrapper(correlationId, responseWithCl290Disabled))
      }
    }

    "return a valid response with cl290 field taxTakenOffTradingIncome" when {
      "downstream response includes taxTakenOffTradingIncome and cl290.enabled = true" in new Test {
        MockRetrievePeriodSummaryConnector
          .retrievePeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, responseWithCl290Enabled))))

        MockAppConfig.featureSwitches.returns(Configuration("cl290.enabled" -> true))

        await(service.retrievePeriodSummary(requestData)) shouldBe Right(ResponseWrapper(correlationId, responseWithCl290Enabled))
      }
    }

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
