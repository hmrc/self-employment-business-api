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

sealed trait DesExemptionCode  {
  def toMtd: MtdExemptionCode
}

object DesExemptionCode {
  case object `001` extends DesExemptionCode {
    override def toMtd: MtdExemptionCode = MtdExemptionCode.`001 - Non Resident`
  }
  case object `002` extends DesExemptionCode {
    override def toMtd: MtdExemptionCode = MtdExemptionCode.`002 - Trustee`
  }
  case object `003` extends DesExemptionCode {
    override def toMtd: MtdExemptionCode = MtdExemptionCode.`003 - Diver`
  }
  case object `004` extends DesExemptionCode {
    override def toMtd: MtdExemptionCode = MtdExemptionCode.`004 - Employed earner taxed under ITTOIA 2005`
  }
  case object `005` extends DesExemptionCode {
    override def toMtd: MtdExemptionCode = MtdExemptionCode.`005 - Over state pension age`
  }
  case object `006` extends DesExemptionCode {
    override def toMtd: MtdExemptionCode = MtdExemptionCode.`006 - Under 16`
  }

  implicit val format: Format[DesExemptionCode] = Enums.format[DesExemptionCode]
}

