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

package v3.createPeriodSummary

import shared.controllers.validators.Validator
import api.controllers.validators.common.InvalidResultValidator
import shared.models.errors.{EndDateFormatError, RuleIncorrectOrEmptyBodyError}
import play.api.Configuration
import play.api.libs.json._
import shared.config.MockAppConfig
import shared.UnitSpec
import v3.createPeriodSummary.def1.Def1_CreatePeriodSummaryValidator
import v3.createPeriodSummary.def2.Def2_CreatePeriodSummaryValidator
import v3.createPeriodSummary.model.request.CreatePeriodSummaryRequestData

class CreatePeriodSummaryValidatorFactorySpec extends UnitSpec with MockAppConfig {

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

  private def validatorFactory = new CreatePeriodSummaryValidatorFactory()

  private def setupMocks(): Unit = {
    MockAppConfig.featureSwitchConfig.returns(Configuration("cl290.enabled" -> true)).anyNumberOfTimes()
  }

  "validator()" when {
    "given a tax year before 2023-24" should {
      "return the Validator for schema definition 1" in {
        setupMocks()
        val requestBody = validBody("2019-08-24", "2020-08-24")
        val result: Validator[CreatePeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, requestBody, includeNegatives = true)
        result shouldBe a[Def1_CreatePeriodSummaryValidator]
      }
    }

    "given the 2023-24 tax year" should {
      "return the Validator for schema definition 2" in {
        setupMocks()
        val requestBody = validBody("2023-08-24", "2024-08-24")
        val result: Validator[CreatePeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, requestBody, includeNegatives = true)
        result shouldBe a[Def2_CreatePeriodSummaryValidator]
      }
    }

    "given a tax year after 2023-24" should {
      "return the Validator for schema definition 2" in {
        setupMocks()
        val requestBody = validBody("2025-08-24", "2026-08-24")
        val result: Validator[CreatePeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, requestBody, includeNegatives = true)
        result shouldBe a[Def2_CreatePeriodSummaryValidator]
      }
    }

    "given an invalid date" should {
      "return the Invalid Tax Year validator" in {
        setupMocks()
        val requestBody = validBody("2025-08-24", "not-an-iso-year")
        val result: Validator[CreatePeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, requestBody, includeNegatives = true)
        result shouldBe InvalidResultValidator(EndDateFormatError.withPath("periodDates/periodEndDate"))
      }
    }
    "given an empty request body" should {
      "return RULE_INCORRECT_OR_EMPTY_BODY_SUBMITTED" in {
        setupMocks()
        val requestBody = JsObject.empty
        val result: Validator[CreatePeriodSummaryRequestData] =
          validatorFactory.validator(validNino, validBusinessId, requestBody, includeNegatives = true)
        result shouldBe InvalidResultValidator(RuleIncorrectOrEmptyBodyError)
      }
    }
  }

}
