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

package api.models.domain

import mocks.MockAppConfig
import support.UnitSpec

import java.time.{LocalDate, ZoneId}

class TaxYearSpec extends UnitSpec with MockAppConfig {

  "TaxYear" when {

    val taxYear = TaxYear.fromMtd("2023-24")

    "constructed from an MTD tax year" should {
      "return the downstream tax value" in {
        taxYear.asDownstream shouldBe "2024"
      }

      "return the MTD tax year" in {
        taxYear.asMtd shouldBe "2023-24"
      }

      "return the tax year in the 'Tax Year Specific API' format" in {
        taxYear.asTysDownstream shouldBe "23-24"
      }
    }

    "constructed from a downstream tax year" should {
      "return the downstream tax value" in {
        TaxYear.fromDownstream("2019").asDownstream shouldBe "2019"
      }

      "allow the MTD tax year to be extracted" in {
        TaxYear.fromDownstream("2019").asMtd shouldBe "2018-19"
      }
    }

    "constructed directly" should {
      "not compile" in {
        """new TaxYear("2021-22")""" shouldNot compile
      }
    }

    "compared with equals" should {
      "have equality based on content" in {
        val taxYear = TaxYear.fromMtd("2021-22")
        taxYear shouldBe TaxYear.fromDownstream("2022")
        taxYear should not be TaxYear.fromDownstream("2021")
      }
    }

    "isTys" should {
      "return false for None" in {
        TaxYear.isTys(None) shouldBe false
      }

      "return false for a tax year before 2023-24" in {
        TaxYear.isTys(Some(TaxYear.fromMtd("2022-23"))) shouldBe false
      }

      "return true for the 2023-24 tax year" in {
        TaxYear.isTys(Some(TaxYear.fromMtd("2023-24"))) shouldBe true
      }
    }

    "constructed from an ISO date" should {
      "be the expected year, taking into account the UK tax year start date" in {

        def test(datesAndExpectedYears: Seq[(String, Int)]): Unit = {
          datesAndExpectedYears.foreach { case (date, expectedYear) =>
            withClue(s"Given $date:") {
              val result = TaxYear.fromIso(date)
              result.year shouldBe expectedYear
            }
          }
        }

        val input = List(
          "2023-10-05" -> 2024,
          "2025-01-01" -> 2025,
          "2025-04-01" -> 2025,
          "2025-04-06" -> 2026,
          "2023-06-01" -> 2024,
          "2026-01-01" -> 2026,
          "2021-12-31" -> 2022
        )

        test(input)
      }
    }
  }

  "TaxYear.currentTaxYear()" should {
    "return the current tax year" in {
      val today = LocalDate.now(ZoneId.of("UTC"))
      val year  = today.getYear

      val expectedYear = {
        val taxYearStartDate = LocalDate.of(year, 4, 6)
        if (today.isBefore(taxYearStartDate)) year else year + 1
      }

      val result = TaxYear.currentTaxYear()
      result.year shouldBe expectedYear
    }
  }

}
