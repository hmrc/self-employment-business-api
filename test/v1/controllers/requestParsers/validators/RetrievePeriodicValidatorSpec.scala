/*
 * Copyright 2022 HM Revenue & Customs
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

package v1.controllers.requestParsers.validators

import support.UnitSpec
import v1.models.errors._
import v1.models.request.retrievePeriodic.RetrievePeriodicRawData

class RetrievePeriodicValidatorSpec extends UnitSpec {

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678910"
  private val validPeriodId   = "2017-01-25_2017-02-31"

  val validator = new RetrievePeriodicValidator()

  "running a validation" should {
    "return no errors" when {
      "a valid request is supplied" in {
        validator.validate(RetrievePeriodicRawData(validNino, validBusinessId, validPeriodId)) shouldBe Nil
      }
    }

    "return NinoFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(RetrievePeriodicRawData("A12344A", validBusinessId, validPeriodId)) shouldBe
          List(NinoFormatError)
      }
    }

    "return BusinessIdFormatError error" when {
      "an invalid nino is supplied" in {
        validator.validate(RetrievePeriodicRawData(validNino, "Walruses", validPeriodId)) shouldBe
          List(BusinessIdFormatError)
      }
    }

    "return PeriodIdFormatError error" when {
      "an invalid period id is supplied" in {
        validator.validate(RetrievePeriodicRawData(validNino, validBusinessId, "2647667456")) shouldBe
          List(PeriodIdFormatError)
      }
    }

    "return multiple errors" when {
      "request supplied has multiple errors" in {
        validator.validate(RetrievePeriodicRawData("A12344A", "Baked Beans", "21742624")) shouldBe
          List(NinoFormatError, BusinessIdFormatError, PeriodIdFormatError)
      }
    }
  }

}
