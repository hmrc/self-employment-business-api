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

package v4.amendPeriodSummaryOld

import api.models.domain.PeriodId
import api.models.errors.{PeriodIdFormatError, RuleBothExpensesSuppliedError, RuleNotAllowedConsolidatedExpenses}
import play.api.Configuration
import shared.config.MockAppConfig
import shared.controllers.EndpointLogContext
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v4.amendPeriodSummaryOld.def1.model.request.Def1_Amend_PeriodIncome
import v4.amendPeriodSummaryOld.def2.model.request.Def2_Amend_PeriodIncome
import v4.amendPeriodSummaryOld.model.request.{
  Def1_AmendPeriodSummaryRequestBody,
  Def1_AmendPeriodSummaryRequestData,
  Def2_AmendPeriodSummaryRequestBody,
  Def2_AmendPeriodSummaryRequestData
}

import scala.concurrent.Future

class AmendPeriodSummaryServiceSpec extends ServiceSpec {

  private val nino                            = Nino("AA123456A")
  private val businessId                      = BusinessId("XAIS12345678910")
  private val periodId                        = PeriodId("2019-01-25_2020-01-25")
  private val taxYear                         = TaxYear.fromMtd("2023-24")
  override implicit val correlationId: String = "X-123"

  private val periodIncomeWithCl290Enabled = Def2_Amend_PeriodIncome(turnover = Some(2000.00), None, taxTakenOffTradingIncome = Some(2000.00))

  private val periodIncomeWithCl290Disabled = Def1_Amend_PeriodIncome(turnover = Some(2000.00), None)

  private val requestDataWithCl290Enabled = Def2_AmendPeriodSummaryRequestData(
    nino = nino,
    businessId = businessId,
    periodId = periodId,
    body = Def2_AmendPeriodSummaryRequestBody(Some(periodIncomeWithCl290Enabled), None, None),
    taxYear = taxYear
  )

  private val requestDataWithCl290Disabled = Def1_AmendPeriodSummaryRequestData(
    nino = nino,
    businessId = businessId,
    periodId = periodId,
    body = Def1_AmendPeriodSummaryRequestBody(Some(periodIncomeWithCl290Disabled), None, None)
  )

  trait Test extends MockAmendPeriodSummaryConnector with MockAppConfig {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new AmendPeriodSummaryService(connector = mockAmendPeriodSummaryConnector)

  }

  trait Cl290Enabled extends Test {
    MockedAppConfig.featureSwitchConfig.returns(Configuration("cl290.enabled" -> true)).anyNumberOfTimes()
  }

  trait Cl290Disabled extends Test {
    MockedAppConfig.featureSwitchConfig.returns(Configuration("cl290.enabled" -> false)).anyNumberOfTimes()
  }

  "AmendPeriodSummaryService" should {
    "return a valid response" when {
      "a valid request is supplied with cl290 feature switch enabled" in new Cl290Enabled {
        MockAmendPeriodSummaryConnector
          .amendPeriodSummary(requestDataWithCl290Enabled)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        await(service.amendPeriodSummary(requestDataWithCl290Enabled)) shouldBe Right(ResponseWrapper(correlationId, ()))
      }

      "a valid request is supplied with cl290 feature switch disabled" in new Cl290Disabled {
        MockAmendPeriodSummaryConnector
          .amendPeriodSummary(requestDataWithCl290Disabled)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        await(service.amendPeriodSummary(requestDataWithCl290Disabled)) shouldBe Right(ResponseWrapper(correlationId, ()))
      }
    }

    "map errors according to spec" when {
      def serviceError(downstreamErrorCode: String, error: MtdError): Unit =
        s"a $downstreamErrorCode error is returned from the service" in new Cl290Disabled {

          MockAmendPeriodSummaryConnector
            .amendPeriodSummary(requestDataWithCl290Disabled)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DownstreamErrors.single(DownstreamErrorCode(downstreamErrorCode))))))

          await(service.amendPeriodSummary(requestDataWithCl290Disabled)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val errors = List(
        "INVALID_NINO"                    -> NinoFormatError,
        "INVALID_INCOME_SOURCE"           -> BusinessIdFormatError,
        "INVALID_DATE_FROM"               -> PeriodIdFormatError,
        "INVALID_DATE_TO"                 -> PeriodIdFormatError,
        "INVALID_PAYLOAD"                 -> InternalError,
        "NOT_FOUND_INCOME_SOURCE"         -> NotFoundError,
        "NOT_FOUND_PERIOD"                -> NotFoundError,
        "BOTH_EXPENSES_SUPPLIED"          -> RuleBothExpensesSuppliedError,
        "NOT_ALLOWED_SIMPLIFIED_EXPENSES" -> RuleNotAllowedConsolidatedExpenses,
        "SERVER_ERROR"                    -> InternalError,
        "SERVICE_UNAVAILABLE"             -> InternalError
      )

      val extraTysErrors = List(
        "INVALID_TAX_YEAR"                      -> TaxYearFormatError,
        "TAX_YEAR_NOT_SUPPORTED"                -> RuleTaxYearNotSupportedError,
        "INVALID_CORRELATION_ID"                -> InternalError,
        "INVALID_INCOMESOURCE_ID"               -> BusinessIdFormatError,
        "PERIOD_NOT_FOUND"                      -> NotFoundError,
        "INCOME_SOURCE_NOT_FOUND"               -> NotFoundError,
        "INCOME_SOURCE_DATA_NOT_FOUND"          -> NotFoundError,
        "BOTH_CONS_BREAKDOWN_EXPENSES_SUPPLIED" -> RuleBothExpensesSuppliedError
      )

      (errors ++ extraTysErrors).foreach(args => (serviceError _).tupled(args))
    }
  }

}
