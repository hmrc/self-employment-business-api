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

import support.UnitSpec
import utils.enums.EnumJsonSpecSupport

class MtdExemptionCodeSpec extends UnitSpec with EnumJsonSpecSupport {

  testRoundTrip[MtdExemptionCode](
    ("001 - Non Resident", MtdExemptionCode.`001 - Non Resident`),
    ("002 - Trustee", MtdExemptionCode.`002 - Trustee`),
    ("003 - Diver", MtdExemptionCode.`003 - Diver`),
    ("004 - Employed earner taxed under ITTOIA 2005", MtdExemptionCode.`004 - Employed earner taxed under ITTOIA 2005`),
    ("005 - Over state pension age", MtdExemptionCode.`005 - Over state pension age`),
    ("006 - Under 16", MtdExemptionCode.`006 - Under 16`)
  )

  "toDes" should {
    Seq(
      (MtdExemptionCode.`001 - Non Resident`, DesExemptionCode.`001`),
      (MtdExemptionCode.`002 - Trustee`, DesExemptionCode.`002`),
      (MtdExemptionCode.`003 - Diver`, DesExemptionCode.`003`),
      (MtdExemptionCode.`004 - Employed earner taxed under ITTOIA 2005`, DesExemptionCode.`004`),
      (MtdExemptionCode.`005 - Over state pension age`, DesExemptionCode.`005`),
      (MtdExemptionCode.`006 - Under 16`, DesExemptionCode.`006`),
    ).foreach {
      case (mtdCode, desCode) =>
        s"return ${desCode.toString} when passed input ${mtdCode.toString}" in {
          mtdCode.toDes shouldBe desCode
        }
    }
  }
}
