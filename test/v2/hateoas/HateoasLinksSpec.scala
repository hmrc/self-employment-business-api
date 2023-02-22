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

package v2.hateoas

import api.hateoas.HateoasLinks
import api.models.domain.{BusinessId, Nino, TaxYear}
import api.models.hateoas.Link
import api.models.hateoas.Method.{GET, POST, PUT}
import mocks.MockAppConfig
import play.api.Configuration
import support.UnitSpec

class HateoasLinksSpec extends UnitSpec with MockAppConfig with HateoasLinks {

  private val nino        = Nino("AA123456A")
  private val businessId  = BusinessId("XAIS12345678910")
  private val taxYear2023 = TaxYear.fromMtd("2022-23")
  private val taxYear2024 = TaxYear.fromMtd("2022-24")
  private val periodId    = "2019-01-01_2020-01-01"

  class Test {
    MockAppConfig.apiGatewayContext.returns("individuals/business/self-employment")
  }

  class TysDisabledTest extends Test {
    MockAppConfig.featureSwitches returns Configuration("tys-api.enabled" -> false)
  }

  class TysEnabledTest extends Test {
    MockAppConfig.featureSwitches returns Configuration("tys-api.enabled" -> true)
  }

  "createPeriodSummary" should {
    "generate the correct link" in new TysDisabledTest {
      val link         = createPeriodSummary(mockAppConfig, nino, businessId)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period"

      link shouldBe Link(expectedHref, POST, "create-self-employment-period-summary")
    }
  }

  "retrievePeriodSummary" should {
    "generate the correct link" in new TysDisabledTest {
      val link         = retrievePeriodSummary(mockAppConfig, nino, businessId, periodId, None)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01"

      link shouldBe Link(expectedHref, GET, "self")
    }

    "TYS feature switch is disabled" should {
      "not include tax year query parameter given a TYS tax year" in new TysDisabledTest {
        val link         = retrievePeriodSummary(mockAppConfig, nino, businessId, periodId, Some(taxYear2024))
        val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01"

        link shouldBe Link(expectedHref, GET, "self")
      }
    }

    "TYS feature switch is enabled" should {
      "not include tax year query parameter given a non-TYS tax year" in new TysEnabledTest {
        val link         = retrievePeriodSummary(mockAppConfig, nino, businessId, periodId, Some(taxYear2023))
        val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01"

        link shouldBe Link(expectedHref, GET, "self")
      }

      "include tax year query parameter given a TYS tax year" in new TysEnabledTest {
        val link         = retrievePeriodSummary(mockAppConfig, nino, businessId, periodId, Some(taxYear2024))
        val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01?taxYear=2023-24"

        link shouldBe Link(expectedHref, GET, "self")
      }
    }
  }

  "amendPeriodSummary" should {
    "generate the correct link" in new TysDisabledTest {
      val link         = amendPeriodSummary(mockAppConfig, nino, businessId, periodId, None)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01"

      link shouldBe Link(expectedHref, PUT, "amend-self-employment-period-summary")
    }

    "TYS feature switch is disabled" should {
      "not include tax year query parameter given a TYS tax year" in new TysDisabledTest {
        val link         = amendPeriodSummary(mockAppConfig, nino, businessId, periodId, Some(taxYear2024))
        val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01"

        link shouldBe Link(expectedHref, PUT, "amend-self-employment-period-summary")
      }
    }

    "TYS feature switch is enabled" should {
      "not include tax year query parameter given a non-TYS tax year" in new TysEnabledTest {
        val link         = amendPeriodSummary(mockAppConfig, nino, businessId, periodId, Some(taxYear2023))
        val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01"

        link shouldBe Link(expectedHref, PUT, "amend-self-employment-period-summary")
      }

      "include tax year query parameter given a TYS tax year" in new TysEnabledTest {
        val link         = amendPeriodSummary(mockAppConfig, nino, businessId, periodId, Some(taxYear2024))
        val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01?taxYear=2023-24"

        link shouldBe Link(expectedHref, PUT, "amend-self-employment-period-summary")
      }
    }
  }

}