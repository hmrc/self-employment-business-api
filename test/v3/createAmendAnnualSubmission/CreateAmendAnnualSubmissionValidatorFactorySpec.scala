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

package v3.createAmendAnnualSubmission

import api.controllers.validators.Validator
import mocks.MockAppConfig
import play.api.Configuration
import play.api.libs.json._
import support.UnitSpec
import v3.createAmendAnnualSubmission.def1.Def1_CreateAmendAnnualSubmissionValidator
import v3.createAmendAnnualSubmission.model.request.CreateAmendAnnualSubmissionRequestData

class CreateAmendAnnualSubmissionValidatorFactorySpec extends UnitSpec with MockAppConfig {

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

  private val validatorFactory = new CreateAmendAnnualSubmissionValidatorFactory(mockAppConfig)

  private def setupMocks(): Unit =
    MockAppConfig.featureSwitches.returns(Configuration("cl290.enabled" -> true)).anyNumberOfTimes()

  "validator()" when {
    "given any tax year" should {
      "return the Validator for schema definition 1" in {
        setupMocks()
        val requestBody = validBody("2019-08-24", "2020-08-24")
        val result: Validator[CreateAmendAnnualSubmissionRequestData] =
          validatorFactory.validator(validNino, validBusinessId, "2022-23", requestBody)
        result shouldBe a[Def1_CreateAmendAnnualSubmissionValidator]
      }
    }
  }

}
