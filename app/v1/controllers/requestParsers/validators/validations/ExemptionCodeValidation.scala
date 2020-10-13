/*
 * Copyright 2020 HM Revenue & Customs
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

import v1.models.errors.{MtdError, RuleExemptionCodeError}

object ExemptionCodeValidation {
  val valueList = List("001 - Non Resident" +
                      "002 - Trustee" +
                      "003 - Diver" +
                      "004 - Employed earner taxed under ITTOIA 2005" +
                      "005 - Over state pension age" +
                      "006 - Under 16")

  def validate(isExempt: Boolean, exemptionCode: Option[String]): List[MtdError] = (isExempt, exemptionCode) match {
    case (false, None) => NoValidationErrors
    case (true, None) => List(RuleExemptionCodeError)
    case (_, Some(value)) => if (valueList.contains(value)) NoValidationErrors else List(RuleExemptionCodeError)
  }
}
