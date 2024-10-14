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

package v4.createAmendCumulativeSubmission

import config.MockSeBusinessFeatureSwitches
import play.api.libs.json._
import shared.config.MockSharedAppConfig
import shared.controllers.validators.{AlwaysErrorsValidator, Validator}
import shared.utils.UnitSpec
import v4.createAmendCumulativeSubmission.def1.Def1_CreateAmendCumulativeSubmissionValidator
import v4.createAmendCumulativeSubmission.model.request.CreateAmendCumulativeSubmissionRequestData

class CreateAmendCumulativeSubmissionValidatorFactorySpec extends UnitSpec with MockSharedAppConfig with MockSeBusinessFeatureSwitches {

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"

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

  private val validatorFactory = new CreateAmendCumulativeSubmissionValidatorFactory

  "min tax year validator" when {
    "given min tax 25-26 year " should {
      "return the Validator for schema definition 1" in {
        val requestBody = validBody("2025-08-24", "2026-08-24")
        val result: Validator[CreateAmendCumulativeSubmissionRequestData] =
          validatorFactory.validator(validNino, validBusinessId, "2025-26", requestBody)
        result shouldBe a[Def1_CreateAmendCumulativeSubmissionValidator]
      }
    }
  }

  "tax year before 25-26" when {
    "given tax year 24-25 year " should {
      "return error" in {
        val requestBody = validBody("2024-08-24", "2025-08-24")
        val result: Validator[CreateAmendCumulativeSubmissionRequestData] =
          validatorFactory.validator(validNino, validBusinessId, "2024-25", requestBody)
        result shouldBe a[AlwaysErrorsValidator]
      }
    }
  }

}
