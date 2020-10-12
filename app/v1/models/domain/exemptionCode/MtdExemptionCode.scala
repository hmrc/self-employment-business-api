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

package v1.models.domain.exemptionCode

import play.api.libs.json.Format
import utils.enums.Enums

sealed trait MtdExemptionCode  {
  def toDes: DesExemptionCode
}

object MtdExemptionCode {
  case object `001 - Non Resident` extends MtdExemptionCode {
    override def toDes: DesExemptionCode = DesExemptionCode.`001`
  }
  case object `002 - Trustee` extends MtdExemptionCode {
    override def toDes: DesExemptionCode = DesExemptionCode.`002`
  }
  case object `003 - Diver` extends MtdExemptionCode {
    override def toDes: DesExemptionCode = DesExemptionCode.`003`
  }
  case object `004 - Employed earner taxed under ITTOIA 2005` extends MtdExemptionCode {
    override def toDes: DesExemptionCode = DesExemptionCode.`004`
  }
  case object `005 - Over state pension age` extends MtdExemptionCode {
    override def toDes: DesExemptionCode = DesExemptionCode.`005`
  }
  case object `006 - Under 16` extends MtdExemptionCode {
    override def toDes: DesExemptionCode = DesExemptionCode.`006`
  }

  implicit val format: Format[MtdExemptionCode] = Enums.format[MtdExemptionCode]
}