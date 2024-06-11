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

package v3.retrieveAnnualSubmission

import config.MockSeBusinessConfig
import shared.controllers.validators.Validator
import shared.UnitSpec
import v3.retrieveAnnualSubmission.def1.Def1_RetrieveAnnualSubmissionValidator
import v3.retrieveAnnualSubmission.model.request.RetrieveAnnualSubmissionRequestData

class RetrieveAnnualSubmissionValidatorFactorySpec extends UnitSpec with MockSeBusinessConfig {

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678910"
  private val validTaxYear    = "2017-18"

  private val validatorFactory = new RetrieveAnnualSubmissionValidatorFactory

  "validator()" when {

    "given any request regardless of tax year" should {
      "return the Validator for schema definition 1" in {
        val result: Validator[RetrieveAnnualSubmissionRequestData] =
          validatorFactory.validator(validNino, validBusinessId, taxYear = validTaxYear)

        result shouldBe a[Def1_RetrieveAnnualSubmissionValidator]
      }
    }

  }

}
