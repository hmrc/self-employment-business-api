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
import shared.controllers.validators.Validator
import shared.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import shared.utils.UnitSpec
import v4.retrieveAnnualSubmission.RetrieveAnnualSubmissionSchema.{Def1, Def2}
import v4.retrieveAnnualSubmission.def1.Def1_RetrieveAnnualSubmissionValidator
import v4.retrieveAnnualSubmission.def2.Def2_RetrieveAnnualSubmissionValidator
import v4.retrieveAnnualSubmission.model.request.RetrieveAnnualSubmissionRequestData

class RetrieveAnnualSubmissionValidatorFactorySpec
    extends UnitSpec
    with ScalaCheckDrivenPropertyChecks
    with TaxYearPropertyCheckSupport
    with MockSeBusinessConfig {

  private val nino       = "AA123456A"
  private val businessId = "XAIS12345678910"

  private val validatorFactory = new RetrieveAnnualSubmissionValidatorFactory

  "ValidatorFactory" when {

    "given a tax year" should {
      "return the Validator corresponding to the schema for that tax year" in forTaxYearsFrom(TaxYear.starting(2020)) { taxYear: TaxYear =>
        val result: Validator[RetrieveAnnualSubmissionRequestData] =
          validatorFactory.validator(nino, businessId, taxYear = taxYear.asMtd)

        RetrieveAnnualSubmissionSchema.schemaFor(taxYear) match {
          case Def1 => result shouldBe a[Def1_RetrieveAnnualSubmissionValidator]
          case Def2 => result shouldBe a[Def2_RetrieveAnnualSubmissionValidator]
        }
      }
    }

  }

}
