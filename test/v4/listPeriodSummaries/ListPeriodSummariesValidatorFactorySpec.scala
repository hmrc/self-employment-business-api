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

package v4.listPeriodSummaries

import config.MockSeBusinessConfig
import shared.config.MockSharedAppConfig
import shared.controllers.validators.{AlwaysErrorsValidator, Validator}
import shared.utils.UnitSpec
import v4.listPeriodSummaries.def1.Def1_ListPeriodSummariesValidator
import v4.listPeriodSummaries.def2.Def2_ListPeriodSummariesValidator
import v4.listPeriodSummaries.model.request.ListPeriodSummariesRequestData

class ListPeriodSummariesValidatorFactorySpec extends UnitSpec with MockSharedAppConfig with MockSeBusinessConfig {

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678910"

  private val validatorFactory = new ListPeriodSummariesValidatorFactory

  private def validatorFor(taxYear: String) =
    new ListPeriodSummariesValidatorFactory().validator(validNino, validBusinessId, taxYear)

  "validator()" when {

    "given a pre-TYS tax year param" should {
      "return the Validator for schema definition 1 (non-TYS)" in {
        val result: Validator[ListPeriodSummariesRequestData] =
          validatorFactory.validator(validNino, validBusinessId, taxYear = "2018-19")

        result shouldBe a[Def1_ListPeriodSummariesValidator]
      }
    }

    "given a valid tax year parameter (2023-24, TYS)" should {
      "return the Validator for schema definition 2" in {
        val result: Validator[ListPeriodSummariesRequestData] =
          validatorFactory.validator(validNino, validBusinessId, taxYear = "2023-24")

        result shouldBe a[Def2_ListPeriodSummariesValidator]
      }
    }

    "given a request where no valid schema could be determined" should {
      "return a validator returning the errors" in {
        validatorFor("BAD_TAX_YEAR") shouldBe an[AlwaysErrorsValidator]
      }
    }
  }

}
