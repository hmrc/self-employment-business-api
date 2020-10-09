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

package v1.models.domain

import play.api.libs.json.Format
import utils.enums.Enums

sealed trait SampleMtdEnum {
  def toDesEnum: SampleDesEnum
}

object SampleMtdEnum {

  case object One extends SampleMtdEnum {
    override def toDesEnum: SampleDesEnum = SampleDesEnum.Type1
  }

  case object Two extends SampleMtdEnum {
    override def toDesEnum: SampleDesEnum = SampleDesEnum.Type2
  }

  case object Three extends SampleMtdEnum {
    override def toDesEnum: SampleDesEnum = SampleDesEnum.Type3
  }

  case object Four extends SampleMtdEnum {
    override def toDesEnum: SampleDesEnum = SampleDesEnum.Type4
  }

  implicit val format: Format[SampleMtdEnum] = Enums.format[SampleMtdEnum]
}


