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
import utils.EmptinessChecker
import utils.enums.Enums

sealed trait MtdNicExemption {
  def toDownstream: DownstreamNicExemption
}

object MtdNicExemption {

  case object `non-resident` extends MtdNicExemption {
    override def toDownstream: DownstreamNicExemption = DownstreamNicExemption.`001`
  }

  case object trustee extends MtdNicExemption {
    override def toDownstream: DownstreamNicExemption = DownstreamNicExemption.`002`
  }

  case object diver extends MtdNicExemption {
    override def toDownstream: DownstreamNicExemption = DownstreamNicExemption.`003`
  }

  case object `ITTOIA-2005` extends MtdNicExemption {
    override def toDownstream: DownstreamNicExemption = DownstreamNicExemption.`004`
  }

  case object `over-state-pension-age` extends MtdNicExemption {
    override def toDownstream: DownstreamNicExemption = DownstreamNicExemption.`005`
  }

  case object `under-16` extends MtdNicExemption {
    override def toDownstream: DownstreamNicExemption = DownstreamNicExemption.`006`
  }

  implicit val format: Format[MtdNicExemption]                     = Enums.format[MtdNicExemption]
  implicit val emptinessChecker: EmptinessChecker[MtdNicExemption] = EmptinessChecker.primitive
  val parser: PartialFunction[String, MtdNicExemption]             = Enums.parser[MtdNicExemption]
}
