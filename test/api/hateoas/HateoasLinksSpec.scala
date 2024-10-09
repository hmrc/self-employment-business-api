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

package api.hateoas

import shared.utils.UnitSpec
import shared.hateoas
import shared.config.MockSharedAppConfig
import shared.hateoas.Method.{DELETE, GET, POST, PUT}
import shared.models.domain.{BusinessId, Nino, TaxYear}

class HateoasLinksSpec extends UnitSpec with MockSharedAppConfig with HateoasLinks {

  private val nino        = Nino("AA123456A")
  private val businessId  = BusinessId("XAIS12345678910")
  private val taxYear2023 = TaxYear.fromMtd("2022-23")
  private val taxYear2024 = TaxYear.fromMtd("2022-24")
  private val periodId    = "2019-01-01_2020-01-01"

  class Test {
    MockedSharedAppConfig.apiGatewayContext.returns("individuals/business/self-employment")
  }

  "retrieveAnnualSubmission" should {
    "generate the correct link" in new Test {
      val link         = retrieveAnnualSubmission(mockSharedAppConfig, nino, businessId, taxYear2023)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/annual/2022-23"

      link shouldBe hateoas.Link(expectedHref, GET, "self")
    }
  }

  "amendAnnualSubmission" should {
    "generate the correct link" in new Test {
      val link         = amendAnnualSubmission(mockSharedAppConfig, nino, businessId, taxYear2023)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/annual/2022-23"

      link shouldBe hateoas.Link(expectedHref, PUT, "create-and-amend-self-employment-annual-submission")
    }
  }

  "deleteAnnualSubmission" should {
    "generate the correct link" in new Test {
      val link         = deleteAnnualSubmission(mockSharedAppConfig, nino, businessId, taxYear2023)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/annual/2022-23"

      link shouldBe hateoas.Link(expectedHref, DELETE, "delete-self-employment-annual-submission")
    }
  }

  "listPeriodSummaries" when {
    "generate the correct link with isSelf set to true" in new Test {
      val link         = listPeriodSummaries(mockSharedAppConfig, nino, businessId, None, true)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period"

      link shouldBe hateoas.Link(expectedHref, GET, "self")
    }

    "generate the correct link with isSelf set to false" in new Test {
      val link         = listPeriodSummaries(mockSharedAppConfig, nino, businessId, None, false)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period"

      link shouldBe hateoas.Link(expectedHref, GET, "list-self-employment-period-summaries")
    }

    "not include tax year query parameter given a non-TYS tax year" in new Test {
      val link         = listPeriodSummaries(mockSharedAppConfig, nino, businessId, Some(taxYear2023), true)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period"

      link shouldBe hateoas.Link(expectedHref, GET, "self")
    }

    "include tax year query parameter given a TYS tax year" in new Test {
      val link         = listPeriodSummaries(mockSharedAppConfig, nino, businessId, Some(taxYear2024), true)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period?taxYear=2023-24"

      link shouldBe hateoas.Link(expectedHref, GET, "self")
    }
  }

  "createPeriodSummary" should {
    "generate the correct link" in new Test {
      val link         = createPeriodSummary(mockSharedAppConfig, nino, businessId)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period"

      link shouldBe hateoas.Link(expectedHref, POST, "create-self-employment-period-summary")
    }
  }

  "retrievePeriodSummary" should {
    "generate the correct link" in new Test {
      val link         = retrievePeriodSummary(mockSharedAppConfig, nino, businessId, periodId, None)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01"

      link shouldBe hateoas.Link(expectedHref, GET, "self")
    }

    "not include tax year query parameter given a non-TYS tax year" in new Test {
      val link         = retrievePeriodSummary(mockSharedAppConfig, nino, businessId, periodId, Some(taxYear2023))
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01"

      link shouldBe hateoas.Link(expectedHref, GET, "self")
    }

    "include tax year query parameter given a TYS tax year" in new Test {
      val link         = retrievePeriodSummary(mockSharedAppConfig, nino, businessId, periodId, Some(taxYear2024))
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01?taxYear=2023-24"

      link shouldBe hateoas.Link(expectedHref, GET, "self")
    }
  }

  "amendPeriodSummary" should {
    "generate the correct link" in new Test {
      val link         = amendPeriodSummary(mockSharedAppConfig, nino, businessId, periodId, None)
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01"

      link shouldBe hateoas.Link(expectedHref, PUT, "amend-self-employment-period-summary")
    }

    "not include tax year query parameter given a non-TYS tax year" in new Test {
      val link         = amendPeriodSummary(mockSharedAppConfig, nino, businessId, periodId, Some(taxYear2023))
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01"

      link shouldBe hateoas.Link(expectedHref, PUT, "amend-self-employment-period-summary")
    }

    "include tax year query parameter given a TYS tax year" in new Test {
      val link         = amendPeriodSummary(mockSharedAppConfig, nino, businessId, periodId, Some(taxYear2024))
      val expectedHref = "/individuals/business/self-employment/AA123456A/XAIS12345678910/period/2019-01-01_2020-01-01?taxYear=2023-24"

      link shouldBe hateoas.Link(expectedHref, PUT, "amend-self-employment-period-summary")
    }
  }

}
