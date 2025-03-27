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

package v3.amendPeriodSummary

import api.models.domain.PeriodId
import play.api.Configuration
import shared.config.MockSharedAppConfig
import shared.controllers.EndpointLogContext
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.outcomes.ResponseWrapper
import shared.services.ServiceSpec
import v3.amendPeriodSummary.def2.model.request.Def2_Amend_PeriodIncome
import v3.amendPeriodSummary.model.request.{Def2_AmendPeriodSummaryRequestBody, Def2_AmendPeriodSummaryRequestData}

import scala.concurrent.Future

class AmendPeriodSummaryServiceSpec extends ServiceSpec {

  private val nino                            = Nino("AA123456A")
  private val businessId                      = BusinessId("XAIS12345678910")
  private val periodId                        = PeriodId("2019-01-25_2020-01-25")
  private val taxYear                         = TaxYear.fromMtd("2023-24")
  override implicit val correlationId: String = "X-123"

  private val periodIncomeWithCl290Enabled = Def2_Amend_PeriodIncome(turnover = Some(2000.00), None, taxTakenOffTradingIncome = Some(2000.00))

  private val requestDataWithCl290Enabled = Def2_AmendPeriodSummaryRequestData(
    nino = nino,
    businessId = businessId,
    periodId = periodId,
    body = Def2_AmendPeriodSummaryRequestBody(Some(periodIncomeWithCl290Enabled), None, None),
    taxYear = taxYear
  )

  trait Test extends MockAmendPeriodSummaryConnector with MockSharedAppConfig {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new AmendPeriodSummaryService(connector = mockAmendPeriodSummaryConnector)

  }

  trait Cl290Enabled extends Test {
    MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("cl290.enabled" -> true)).anyNumberOfTimes()
  }

  trait Cl290Disabled extends Test {
    MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("cl290.enabled" -> false)).anyNumberOfTimes()
  }

  "AmendPeriodSummaryService" should {
    "return a valid response" when {
      "a valid request is supplied with cl290 feature switch enabled" in new Cl290Enabled {
        MockAmendPeriodSummaryConnector
          .amendPeriodSummary(requestDataWithCl290Enabled)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        await(service.amendPeriodSummary(requestDataWithCl290Enabled)) shouldBe Right(ResponseWrapper(correlationId, ()))
      }
    }
  }

}
