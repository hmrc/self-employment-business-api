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

package v5.createAmendCumulativePeriodSummary

import cats.data.Validated
import shared.models.domain.TaxYear
import shared.models.errors.{RuleTaxYearNotSupportedError, TaxYearFormatError}
import shared.utils.UnitSpec

class CreateAmendCumulativePeriodSummarySchemaSpec extends UnitSpec {

  "schemaFor()" when {

    "given a minimum valid tax year" should {
      "return a Valid TaxYear" in {
        val validTaxYear = "2025-26"

        val result = CreateAmendCumulativePeriodSummarySchema.schemaFor(validTaxYear)

        result shouldBe a[Validated.Valid[_]]
        result.map(_.year) shouldBe Validated.Valid(TaxYear.fromMtd(validTaxYear).year)
      }
    }
    "given a valid tax year" should {
      "return a Valid TaxYear" in {
        val validTaxYear = "2026-27"

        val result = CreateAmendCumulativePeriodSummarySchema.schemaFor(validTaxYear)

        result shouldBe a[Validated.Valid[_]]
        result.map(_.year) shouldBe Validated.Valid(TaxYear.fromMtd(validTaxYear).year)
      }
    }

    "given a tax year 2 years before the minimum " should {
      "return an Invalid with MtdError" in {
        val invalidTaxYear = "2023-24"

        val result = CreateAmendCumulativePeriodSummarySchema.schemaFor(invalidTaxYear)

        result shouldBe a[Validated.Invalid[_]]
        result shouldBe Validated.Invalid(Seq(RuleTaxYearNotSupportedError))
      }
    }
    "given a tax year before the minimum" should {
      "return an Invalid with MtdError" in {
        val invalidTaxYear = "2024-25"

        val result = CreateAmendCumulativePeriodSummarySchema.schemaFor(invalidTaxYear)

        result shouldBe a[Validated.Invalid[_]]
        result shouldBe Validated.Invalid(Seq(RuleTaxYearNotSupportedError))
      }
    }

    "given a malformed tax year" should {
      "return an Invalid with TaxYearFormatError" in {
        val malformedTaxYear = "invalid-tax-year"

        val result = CreateAmendCumulativePeriodSummarySchema.schemaFor(malformedTaxYear)

        result shouldBe a[Validated.Invalid[_]]
        result shouldBe Validated.Invalid(Seq(TaxYearFormatError))
      }
    }

  }

}
