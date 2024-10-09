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

package v4.amendPeriodSummary

import play.api.libs.json.Json
import shared.config.MockSharedAppConfig
import shared.controllers.validators.Validator
import shared.utils.UnitSpec
import v4.amendPeriodSummary.def1.Def1_AmendPeriodSummaryValidator
import v4.amendPeriodSummary.def2.Def2_AmendPeriodSummaryValidator
import v4.amendPeriodSummary.model.request.AmendPeriodSummaryRequestData

class AmendPeriodSummaryValidatorFactorySpec extends UnitSpec with MockSharedAppConfig {

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validPeriodId   = "2017-01-25_2017-02-28"

  private def validBody(startDate: String, endDate: String) =
    Json.parse(s"""
                  |{
                  |   "periodDates": {
                  |     "periodStartDate": "$startDate",
                  |     "periodEndDate": "$endDate"
                  |   },
                  |   "periodIncome": {},
                  |   "periodExpenses": {},
                  |   "periodDisallowableExpenses": {}
                  |}
                  |""".stripMargin)

  private val validatorFactory = new AmendPeriodSummaryValidatorFactory

  "validator()" when {

    "given no taxYear parameter" should {
      "return the Validator for schema definition 1 (non-TYS)" in {
        val requestBody = validBody("2019-08-24", "2020-08-24")
        val result: Validator[AmendPeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, validPeriodId, maybeTaxYear = None, requestBody, includeNegatives = true)
        result shouldBe a[Def1_AmendPeriodSummaryValidator]
      }

      "given a valid tax year parameter (2023-24, TYS)" should {
        "return the Validator for schema definition 2" in {
          val requestBody = validBody("2023-08-24", "2024-08-24")
          val result: Validator[AmendPeriodSummaryRequestData] =
            validatorFactory.validator(
              validNino,
              validBusinessId,
              validPeriodId,
              maybeTaxYear = Some("2023-24"),
              requestBody,
              includeNegatives = true)
          result shouldBe a[Def2_AmendPeriodSummaryValidator]
        }
      }

      "given a tax year parameter after 2023-24" should {
        "return the Validator for schema definition 2" in {
          val requestBody = validBody("2025-08-24", "2026-08-24")
          val result: Validator[AmendPeriodSummaryRequestData] =
            validatorFactory.validator(
              validNino,
              validBusinessId,
              validPeriodId,
              maybeTaxYear = Some("2025-26"),
              requestBody,
              includeNegatives = true)
          result shouldBe a[Def2_AmendPeriodSummaryValidator]
        }
      }

      "given a pre-TYS tax year param" should {
        "return the Validator for schema definition 2 (non-TYS ty param will then be validated and rejected)" in {
          val requestBody = validBody("2025-08-24", "2026-08-24")
          val result: Validator[AmendPeriodSummaryRequestData] =
            validatorFactory.validator(
              validNino,
              validBusinessId,
              validPeriodId,
              maybeTaxYear = Some("2022-23"),
              requestBody,
              includeNegatives = true)
          result shouldBe a[Def2_AmendPeriodSummaryValidator]
        }
      }

      "given an invalid tax year param" should {
        "return the Validator for schema definition 2 (ty param will then be validated and rejected)" in {
          val requestBody = validBody("2025-08-24", "2026-08-24")
          val result: Validator[AmendPeriodSummaryRequestData] =
            validatorFactory.validator(
              validNino,
              validBusinessId,
              validPeriodId,
              maybeTaxYear = Some("not-a-tax-year"),
              requestBody,
              includeNegatives = true)
          result shouldBe a[Def2_AmendPeriodSummaryValidator]
        }
      }

    }

  }

}
