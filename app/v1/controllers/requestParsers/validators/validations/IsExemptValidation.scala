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
import v1.models.request.amendSEAnnual.Class4NicInfo

object IsExemptValidation {

  def validate(class4NicInfo: Class4NicInfo): List[MtdError] = class4NicInfo match {
    case Class4NicInfo(false, None)    => NoValidationErrors
    case Class4NicInfo(true, None)     => List(RuleExemptionCodeError)
    case Class4NicInfo(false, Some(_)) => List(RuleExemptionCodeError)
    case Class4NicInfo(true, Some(_))  => NoValidationErrors
  }
}
