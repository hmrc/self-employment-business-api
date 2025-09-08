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

package v4.retrieveAnnualSubmission

import config.MockSeBusinessConfig
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import shared.controllers.validators.{AlwaysErrorsValidator, Validator}
import shared.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import shared.utils.UnitSpec
import v4.retrieveAnnualSubmission.def1.Def1_RetrieveAnnualSubmissionValidator
import v4.retrieveAnnualSubmission.def2.Def2_RetrieveAnnualSubmissionValidator
import v4.retrieveAnnualSubmission.def3.Def3_RetrieveAnnualSubmissionValidator
import v4.retrieveAnnualSubmission.model.request.RetrieveAnnualSubmissionRequestData

class RetrieveAnnualSubmissionValidatorFactorySpec
    extends UnitSpec
    with ScalaCheckDrivenPropertyChecks
    with TaxYearPropertyCheckSupport
    with MockSeBusinessConfig {

  private def validatorFor(taxYear: String): Validator[RetrieveAnnualSubmissionRequestData] =
    new RetrieveAnnualSubmissionValidatorFactory()
      .validator(nino = "ignoredNino", businessId = "ignored", taxYear = taxYear)

  "RetrieveAnnualSubmissionValidatorFactory" when {
    "given a request corresponding to a Def1 schema" should {
      "return a Def1 validator" in forTaxYearsInRange(TaxYear.fromMtd("2017-18"), TaxYear.fromMtd("2023-24")) { (taxYear: TaxYear) =>
        validatorFor(taxYear.asMtd) shouldBe a[Def1_RetrieveAnnualSubmissionValidator]
      }
    }

    "given a request corresponding to a Def2 schema" should {
      "return a Def2 validator" in {
        validatorFor("2024-25") shouldBe a[Def2_RetrieveAnnualSubmissionValidator]
      }
    }

    "given a request corresponding to a Def3 schema" should {
      "return a Def3 validator" in {
        validatorFor("2025-26") shouldBe a[Def3_RetrieveAnnualSubmissionValidator]
      }
    }

    "given a request where no valid schema could be determined" should {
      "return a validator returning the errors" in {
        validatorFor("BAD_TAX_YEAR") shouldBe an[AlwaysErrorsValidator]
      }
    }
  }

}
