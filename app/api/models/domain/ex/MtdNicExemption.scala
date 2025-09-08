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

package api.models.domain.ex

import play.api.libs.json.*
import shared.utils.EmptinessChecker
import shared.utils.enums.Enums

enum MtdNicExemption(val toDownstream: String) {
  case `non-resident`           extends MtdNicExemption("001")
  case trustee                  extends MtdNicExemption("002")
  case diver                    extends MtdNicExemption("003")
  case `ITTOIA-2005`            extends MtdNicExemption("004")
  case `over-state-pension-age` extends MtdNicExemption("005")
  case `under-16`               extends MtdNicExemption("006")
}

object MtdNicExemption {
  val parser: PartialFunction[String, MtdNicExemption] = Enums.parser(values)

  given Format[MtdNicExemption]           = Enums.format(values)
  given EmptinessChecker[MtdNicExemption] = EmptinessChecker.primitive
}
