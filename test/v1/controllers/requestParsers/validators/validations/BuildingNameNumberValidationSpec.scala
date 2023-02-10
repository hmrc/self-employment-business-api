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

package v1.controllers.requestParsers.validators.validations

import api.models.errors.RuleBuildingNameNumberError
import support.UnitSpec
import v1.models.request.amendSEAnnual.Building

class BuildingNameNumberValidationSpec extends UnitSpec {

  val buildingWithName: Building          = Building(Some("TestName"), None, "HGH 232")
  val buildingWithNumber: Building        = Building(None, Some("4815162342"), "HGH 232")
  val buildingWithNameAndNumber: Building = Building(Some("TestName"), Some("4815162342"), "HGH 232")
  val invalidBuilding: Building           = Building(None, None, "HGH 232")

  "validate" should {
    "return no errors" when {
      "a building with a name and postcode is submitted" in {
        val validationResult = BuildingNameNumberValidation.validate(buildingWithName, "/building")

        validationResult.isEmpty shouldBe true
      }
      "a building with a number and postcode is submitted" in {
        val validationResult = BuildingNameNumberValidation.validate(buildingWithNumber, "/building")

        validationResult.isEmpty shouldBe true
      }
      "a building with a number, name and postcode is submitted" in {
        val validationResult = BuildingNameNumberValidation.validate(buildingWithNameAndNumber, "/building")

        validationResult.isEmpty shouldBe true
      }
    }

    "return an error" when {
      "a building with only a postcode is submitted" in {
        val validationResult = BuildingNameNumberValidation.validate(invalidBuilding, "/building")

        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe RuleBuildingNameNumberError.copy(paths = Some(Seq("/building")))
      }
    }
  }

}
