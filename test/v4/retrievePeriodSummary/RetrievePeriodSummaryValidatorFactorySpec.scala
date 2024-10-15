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

import shared.controllers.validators.{AlwaysErrorsValidator, Validator}
import shared.utils.UnitSpec
import v4.retrievePeriodSummary.def1.Def1_RetrievePeriodSummaryValidator
import v4.retrievePeriodSummary.def2.Def2_RetrievePeriodSummaryValidator
import v4.retrievePeriodSummary.model.request.RetrievePeriodSummaryRequestData

class RetrievePeriodSummaryValidatorFactorySpec extends UnitSpec {

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validPeriodId   = "2017-01-25_2017-02-28"

  private val validatorFactory = new RetrievePeriodSummaryValidatorFactory

  private def validatorFor(taxYear: String) =
    new RetrievePeriodSummaryValidatorFactory().validator(validNino, validBusinessId, validPeriodId, taxYear)

  "validator()" when {

    "given a pre-TYS tax year param" should {
      "return the Validator for schema definition 1 (non-TYS)" in {
        val result: Validator[RetrievePeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, validPeriodId, taxYear = "2018-19")

        result shouldBe a[Def1_RetrievePeriodSummaryValidator]
      }
    }

    "given a valid tax year parameter (2023-24, TYS)" should {
      "return the Validator for schema definition 2" in {
        val result: Validator[RetrievePeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, validPeriodId, taxYear = "2023-24")

        result shouldBe a[Def2_RetrievePeriodSummaryValidator]
      }
    }

    "given a tax year parameter after 2023-24" should {
      "return the Validator for schema definition 2" in {
        val result: Validator[RetrievePeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, validPeriodId, taxYear = "2024-25")

        result shouldBe a[Def2_RetrievePeriodSummaryValidator]
      }
    }

    "given a request where no valid schema could be determined" should {
      "return a validator returning the errors" in {
        validatorFor("BAD_TAX_YEAR") shouldBe an[AlwaysErrorsValidator]
      }
    }
  }

}
