/*
 * Copyright 2024 HM Revenue & Customs
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

import cats.data.Validated.{Invalid, Valid}
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import shared.models.domain.{TaxYear, TaxYearPropertyCheckSupport}
import shared.models.errors.{RuleTaxYearNotSupportedError, RuleTaxYearRangeInvalidError, TaxYearFormatError}
import shared.utils.UnitSpec
import v4.retrieveAnnualSubmission.RetrieveAnnualSubmissionSchema._

class RetrieveAnnualSubmissionSchemaSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks with TaxYearPropertyCheckSupport {

  "RetrieveAnnualSubmissionSchema" when {
    "a correctly formatted tax year is supplied" must {
      "use Def1 for tax years between 2017-18 and 2023-24" in {
        forTaxYearsInRange(TaxYear.fromMtd("2017-18"), TaxYear.fromMtd("2023-24")) { taxYear =>
          schemaFor(taxYear.asMtd) shouldBe Valid(Def1)
        }
      }

      "use Def2 for tax year 2024-25" in {
        schemaFor(TaxYear.fromMtd("2024-25")) shouldBe Valid(Def2)
      }

      "use Def3 for tax years from 2025-26 onwards" in {
        forTaxYearsFrom(TaxYear.fromMtd("2025-26")) { taxYear =>
          schemaFor(taxYear.asMtd) shouldBe Valid(Def3)
        }
      }

      "disallow tax years prior to 2017-18 and return RuleTaxYearNotSupportedError" in {
        forTaxYearsBefore(TaxYear.fromMtd("2016-17")) { taxYear =>
          schemaFor(taxYear.asMtd) shouldBe Invalid(Seq(RuleTaxYearNotSupportedError))
        }
      }
    }

    "a badly formatted tax year is supplied" must {
      "the tax year format is invalid" must {
        "return a TaxYearFormatError" in {
          schemaFor("NotATaxYear") shouldBe Invalid(Seq(TaxYearFormatError))
        }
      }

      "the tax year range is invalid" must {
        "return a RuleTaxYearRangeInvalidError" in {
          schemaFor("2020-99") shouldBe Invalid(Seq(RuleTaxYearRangeInvalidError))
        }
      }
    }
  }

}
