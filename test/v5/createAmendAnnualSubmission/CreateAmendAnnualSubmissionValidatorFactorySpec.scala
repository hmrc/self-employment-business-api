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

package v5.createAmendAnnualSubmission

import play.api.Configuration
import play.api.libs.json._
import shared.config.MockSharedAppConfig
import shared.controllers.validators.Validator
import shared.utils.UnitSpec
import v5.createAmendAnnualSubmission.def1.Def1_CreateAmendAnnualSubmissionValidator
import v5.createAmendAnnualSubmission.def2.Def2_CreateAmendAnnualSubmissionValidator
import v5.createAmendAnnualSubmission.def3.Def3_CreateAmendAnnualSubmissionValidator
import v5.createAmendAnnualSubmission.model.request.CreateAmendAnnualSubmissionRequestData

class CreateAmendAnnualSubmissionValidatorFactorySpec extends UnitSpec with MockSharedAppConfig {

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

  private val validatorFactory = new CreateAmendAnnualSubmissionValidatorFactory

  private def setupMocks(): Unit =
    MockedSharedAppConfig.featureSwitchConfig.returns(Configuration("cl290.enabled" -> true)).anyNumberOfTimes()

  "validator()" when {
    "given a tax year before 2024-25" should {
      "return the Validator for schema definition 1" in {
        setupMocks()
        val requestBody = validBody("2019-08-24", "2020-08-24")
        val result: Validator[CreateAmendAnnualSubmissionRequestData] =
          validatorFactory.validator(validNino, validBusinessId, "2022-23", requestBody)
        result shouldBe a[Def1_CreateAmendAnnualSubmissionValidator]
      }
    }

    "given the tax year is 2024-25" should {
      "return the Validator for schema definition 2" in {
        setupMocks()
        val requestBody = validBody("2019-08-24", "2020-08-24")
        val result: Validator[CreateAmendAnnualSubmissionRequestData] =
          validatorFactory.validator(validNino, validBusinessId, "2024-25", requestBody)
        result shouldBe a[Def2_CreateAmendAnnualSubmissionValidator]
      }
    }

    "given the tax year is after 2024-25" should {
      "return the Validator for schema definition 3" in {
        setupMocks()
        val requestBody = validBody("2019-08-24", "2020-08-24")
        val result: Validator[CreateAmendAnnualSubmissionRequestData] =
          validatorFactory.validator(validNino, validBusinessId, "2025-26", requestBody)
        result shouldBe a[Def3_CreateAmendAnnualSubmissionValidator]
      }
    }
  }

}
