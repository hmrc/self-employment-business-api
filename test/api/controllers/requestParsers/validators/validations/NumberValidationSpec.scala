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

package api.controllers.requestParsers.validators.validations

import api.models.errors.ValueFormatError
import config.FeatureSwitches
import org.scalacheck.Arbitrary
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import play.api.Configuration
import support.UnitSpec

class NumberValidationSpec extends UnitSpec with ScalaCheckDrivenPropertyChecks {

  val validNumber: Option[BigDecimal]                    = Some(9000.42)
  val lowestAllowedNumber: Option[BigDecimal]            = Some(0)
  val highestAllowedNumber: Option[BigDecimal]           = Some(99999999999.99)
  val negativeNumber: Option[BigDecimal]                 = Some(-9000.42)
  val numberWithTooManyDecimalPlaces: Option[BigDecimal] = Some(9000.4345684532)

  val path = "/some/path"

  "validate" when {
    "min and max are specified" must {
      val min: BigDecimal = -100
      val max: BigDecimal = 100.99
      val error           = ValueFormatError.copy(paths = Some(Seq(path)), message = "The value must be between -100 and 100.99")

      "return the error with the correct message if and only if the value is outside the inclusive range" when {
        implicit val arbitraryMoney: Arbitrary[BigDecimal] = Arbitrary(Arbitrary.arbitrary[BigInt].map(x => BigDecimal(x) / 100))

        "using validate" in forAll { money: BigDecimal =>
          NumberValidation.validate(money, path, min, max) shouldBe
            (if (min <= money && money <= max) Nil else List(error))
        }

        "using validateOptional" in forAll { money: BigDecimal =>
          NumberValidation.validateOptional(Some(money), path, min, max) shouldBe
            (if (min <= money && money <= max) Nil else List(error))
        }
      }

      "more than two significant decimals are provided" when {
        "return an error for validateOptional" in {
          NumberValidation.validateOptional(Some(100.123), path, min, max) shouldBe List(error)
        }

        "return an error for validate" in {
          NumberValidation.validate(100.123, path, min, max) shouldBe List(error)
        }
      }

      "no number is supplied to validateOptional" when {
        "return no error" in {
          NumberValidation.validateOptional(None, path, min, max) shouldBe Nil
        }
      }
    }

    "min and max are not specified" must {
      val error = ValueFormatError.copy(paths = Some(Seq(path)), message = "The value must be between 0 and 99999999999.99")

      "allow 0" in {
        NumberValidation.validate(0, path) shouldBe Nil
      }

      "disallow less than 0" in {
        NumberValidation.validate(-0.01, path) shouldBe List(error)
      }

      "allow 99999999999.99" in {
        NumberValidation.validate(99999999999.99, path) shouldBe Nil
      }

      "disallow more than 99999999999.99" in {
        NumberValidation.validate(100000000000.00, path) shouldBe List(error)
      }
    }
  }

  "validateIncludeNegatives" should {
    "return no errors" when {
      "a valid number is supplied" in {
        val validationResult = NumberValidation.validateOptionalIncludeNegatives(validNumber, "/vctSubscription/1/amountInvested")
        validationResult.isEmpty shouldBe true
      }
      "no number is supplied" in {
        val validationResult = NumberValidation.validateOptionalIncludeNegatives(None, "/vctSubscription/1/amountInvested")
        validationResult.isEmpty shouldBe true
      }
      "the lowest allowed number (0) is supplied" in {
        val validationResult = NumberValidation.validateOptionalIncludeNegatives(lowestAllowedNumber, "/vctSubscription/1/amountInvested")
        validationResult.isEmpty shouldBe true
      }
      "the highest allowed number (99999999999.99) is supplied" in {
        val validationResult = NumberValidation.validateOptionalIncludeNegatives(highestAllowedNumber, "/vctSubscription/1/amountInvested")
        validationResult.isEmpty shouldBe true
      }
      "a negative number is supplied" in {
        val validationResult = NumberValidation.validateOptionalIncludeNegatives(negativeNumber, "/vctSubscription/1/amountInvested")
        validationResult.isEmpty shouldBe true
      }
    }

    "return an error" when {
      "a number with too many decimal places is supplied" in {
        val validationResult = NumberValidation.validateOptionalIncludeNegatives(numberWithTooManyDecimalPlaces, "/vctSubscription/1/amountInvested")
        validationResult.isEmpty shouldBe false
        validationResult.length shouldBe 1
        validationResult.head shouldBe ValueFormatError.copy(paths = Some(Seq("/vctSubscription/1/amountInvested")))
      }
    }
  }

  "validateOptionalWithFeatureFlag" should {
    def configuration(enable: Boolean): Configuration = Configuration("allowNegativeExpenses.enabled" -> enable)

    "return the validation result using validationOptionalIncludeNegatives" when {
      "the feature switch is enabled" in {
        implicit val featureSwitches: FeatureSwitches = FeatureSwitches(configuration(true))

        val validationResult = NumberValidation.validateOptionalWithFeatureFlag(negativeNumber, path, 0, 100)
        validationResult shouldBe NumberValidation.validateOptionalIncludeNegatives(negativeNumber, path)
      }
    }

    "return the validation result using validateOptional" when {
      "the feature switch is disabled" in {
        implicit val featureSwitches: FeatureSwitches = FeatureSwitches(configuration(false))

        val validationResult = NumberValidation.validateOptionalWithFeatureFlag(negativeNumber, path, 0, 100)
        validationResult shouldBe NumberValidation.validateOptional(negativeNumber, path, 0, 100)
      }
    }

    "return no errors" when {
      "the feature switch is enabled and a valid number is supplied" in {
        implicit val featureSwitches: FeatureSwitches = FeatureSwitches(configuration(true))

        val validationResult = NumberValidation.validateOptionalWithFeatureFlag(validNumber, path)
        validationResult.isEmpty shouldBe true
      }

      "the feature switch is disabled and a valid number is supplied" in {
        implicit val featureSwitches: FeatureSwitches = FeatureSwitches(configuration(false))

        val validationResult = NumberValidation.validateOptionalWithFeatureFlag(validNumber, path)
        validationResult.isEmpty shouldBe true
      }
    }

    "return an error" when {
      "the feature switch is disabled and a negative number is supplied" in {
        implicit val featureSwitches: FeatureSwitches = FeatureSwitches(configuration(false))

        val validationResult = NumberValidation.validateOptionalWithFeatureFlag(negativeNumber, path)
        validationResult shouldBe List(ValueFormatError.forPathAndRange(path, "0", "99999999999.99"))
      }
    }
  }

}
