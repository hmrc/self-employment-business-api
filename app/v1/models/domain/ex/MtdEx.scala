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

package v1.models.domain.ex

import play.api.libs.json.Format
import utils.enums.Enums

sealed trait MtdEx  {
  def toDes: DesEx
}

object MtdEx {
  case object `001 - Non Resident` extends MtdEx {
    override def toDes: DesEx = DesEx.`001`
  }
  case object `002 - Trustee` extends MtdEx {
    override def toDes: DesEx = DesEx.`002`
  }
  case object `003 - Diver` extends MtdEx {
    override def toDes: DesEx = DesEx.`003`
  }
  case object `004 - Employed earner taxed under ITTOIA 2005` extends MtdEx {
    override def toDes: DesEx = DesEx.`004`
  }
  case object `005 - Over state pension age` extends MtdEx {
    override def toDes: DesEx = DesEx.`005`
  }
  case object `006 - Under 16` extends MtdEx {
    override def toDes: DesEx = DesEx.`006`
  }

  implicit val format: Format[MtdEx] = Enums.format[MtdEx]
}
