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

package v1.models.domain.ex

import play.api.libs.json.Format
import utils.enums.Enums

sealed trait DownstreamNicExemption {
  def toMtd: MtdNicExemption
}

object DownstreamNicExemption {

  case object `001` extends DownstreamNicExemption {
    override def toMtd: MtdNicExemption = MtdNicExemption.`non-resident`
  }

  case object `002` extends DownstreamNicExemption {
    override def toMtd: MtdNicExemption = MtdNicExemption.trustee
  }

  case object `003` extends DownstreamNicExemption {
    override def toMtd: MtdNicExemption = MtdNicExemption.diver
  }

  case object `004` extends DownstreamNicExemption {
    override def toMtd: MtdNicExemption = MtdNicExemption.`ITTOIA-2005`
  }

  case object `005` extends DownstreamNicExemption {
    override def toMtd: MtdNicExemption = MtdNicExemption.`over-state-pension-age`
  }

  case object `006` extends DownstreamNicExemption {
    override def toMtd: MtdNicExemption = MtdNicExemption.`under-16`
  }

  implicit val format: Format[DownstreamNicExemption] = Enums.format[DownstreamNicExemption]
}
