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

import shared.controllers.validators.Validator
import shared.utils.UnitSpec
import v4.retrievePeriodSummary.def1.Def1_RetrievePeriodSummaryValidator
import v4.retrievePeriodSummary.model.request.RetrievePeriodSummaryRequestData

class RetrievePeriodSummaryValidatorFactorySpec extends UnitSpec {

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validPeriodId   = "2017-01-25_2017-02-28"

  private val validatorFactory = new RetrievePeriodSummaryValidatorFactory

  "validator()" when {
    "given a valid tax year parameter (2023-24, TYS)" should {
      "return the Validator for schema definition 2" in {
        val result: Validator[RetrievePeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, validPeriodId, taxYear = "2023-24")

        result shouldBe a[Def1_RetrievePeriodSummaryValidator]
      }
    }

    "given a tax year parameter after 2023-24" should {
      "return the Validator for schema definition 2" in {
        val result: Validator[RetrievePeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, validPeriodId, taxYear = "2024-25")

        result shouldBe a[Def1_RetrievePeriodSummaryValidator]
      }
    }

    "given a pre-TYS tax year param" should {
      "return the Validator for schema definition 2 (non-TYS ty param will then be validated and rejected)" in {
        val result: Validator[RetrievePeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, validPeriodId, taxYear = "2022-23")

        result shouldBe a[Def1_RetrievePeriodSummaryValidator]
      }
    }

    "given an invalid tax year param" should {
      "return the Validator for schema definition 2 (ty param will then be validated and rejected)" in {
        val result: Validator[RetrievePeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, validPeriodId, taxYear = "not-a-tax-year")

        result shouldBe a[Def1_RetrievePeriodSummaryValidator]
      }
    }
  }

}