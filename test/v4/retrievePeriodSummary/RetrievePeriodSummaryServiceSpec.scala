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

package v4.retrievePeriodSummary

import api.models.domain.PeriodId
import api.models.errors.PeriodIdFormatError
import play.api.Configuration
import shared.config.MockAppConfig
import shared.controllers.EndpointLogContext
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.{ServiceOutcome, ServiceSpec}
import v4.retrievePeriodSummary.def1.model.response.{Def1_RetrievePeriodSummaryResponse, Retrieve_PeriodDates, Retrieve_PeriodIncome}
import v4.retrievePeriodSummary.model.request.Def1_RetrievePeriodSummaryRequestData
import v4.retrievePeriodSummary.model.response.RetrievePeriodSummaryResponse

import scala.concurrent.Future

class RetrievePeriodSummaryServiceSpec extends ServiceSpec {

  private val nino                            = Nino("AA123456A")
  private val businessId                      = BusinessId("XAIS12345678910")
  private val periodId                        = PeriodId("2019-01-25_2020-01-25")
  private val taxYear                         = TaxYear.fromMtd("2024-25")
  override implicit val correlationId: String = "X-123"

  private val requestData = Def1_RetrievePeriodSummaryRequestData(
    nino = nino,
    businessId = businessId,
    periodId = periodId,
    taxYear = taxYear
  )

  private val def1PeriodIncomeWithCl290Disabled = Retrieve_PeriodIncome(turnover = Some(2000.00), None, taxTakenOffTradingIncome = None)
  private val def1PeriodIncomeWithCl290Enabled  = Retrieve_PeriodIncome(turnover = Some(2000.00), None, taxTakenOffTradingIncome = Some(2000.00))

  private val def1ResponseWithCl290Disabled = Def1_RetrievePeriodSummaryResponse(
    Retrieve_PeriodDates("2019-01-25", "2020-01-25"),
    Some(def1PeriodIncomeWithCl290Disabled),
    None,
    None
  )

  private val def1ResponseWithCl290Enabled = Def1_RetrievePeriodSummaryResponse(
    Retrieve_PeriodDates("2019-01-25", "2020-01-25"),
    Some(def1PeriodIncomeWithCl290Enabled),
    None,
    None
  )

  trait Test extends MockRetrievePeriodSummaryConnector with MockAppConfig {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")
    val service                                 = new RetrievePeriodSummaryService(connector = mockRetrievePeriodSummaryConnector)
  }

  "retrievePeriodSummary()" when {

    "given a def1 (TYS) request and CL290 feature is disabled" should {
      "return a def1 response without taxTakenOffTradingIncome" in new Test {
        MockRetrievePeriodSummaryConnector
          .retrievePeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, def1ResponseWithCl290Disabled))))

        MockedAppConfig.featureSwitchConfig.returns(Configuration("cl290.enabled" -> false))

        val result: ServiceOutcome[RetrievePeriodSummaryResponse] = await(service.retrievePeriodSummary(requestData))
        result shouldBe Right(ResponseWrapper(correlationId, def1ResponseWithCl290Disabled))
      }
    }

    "given a def1 (TYS) request and CL290 feature is enabled" should {
      "return a def1 response with taxTakenOffTradingIncome" in new Test {
        MockRetrievePeriodSummaryConnector
          .retrievePeriodSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, def1ResponseWithCl290Enabled))))

        MockedAppConfig.featureSwitchConfig.returns(Configuration("cl290.enabled" -> true))

        val result: ServiceOutcome[RetrievePeriodSummaryResponse] = await(service.retrievePeriodSummary(requestData))
        result shouldBe Right(ResponseWrapper(correlationId, def1ResponseWithCl290Enabled))
      }
    }
  }

  "retrievePeriodSummary()" should {
    "map errors according to spec" when {
      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockRetrievePeriodSummaryConnector
            .retrievePeriodSummary(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(desErrorCode))))))

          await(service.retrievePeriodSummary(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input: Seq[(String, MtdError)] = {
        val errors: Seq[(String, MtdError)] = List(
          "INVALID_NINO"             -> NinoFormatError,
          "INVALID_INCOMESOURCEID"   -> BusinessIdFormatError,
          "INVALID_INCOME_SOURCE_ID" -> BusinessIdFormatError,
          "INVALID_DATE_FROM"        -> PeriodIdFormatError,
          "INVALID_DATE_TO"          -> PeriodIdFormatError,
          "NOT_FOUND_INCOME_SOURCE"  -> NotFoundError,
          "NOT_FOUND_PERIOD"         -> NotFoundError,
          "SERVER_ERROR"             -> InternalError,
          "SERVICE_UNAVAILABLE"      -> InternalError
        )
        val extraTysErrors: Seq[(String, MtdError)] = List(
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
