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

class DesExemptionCodeSpec extends UnitSpec with EnumJsonSpecSupport {

  testRoundTrip[DesExemptionCode](
    ("001", DesExemptionCode.`001`),
    ("002", DesExemptionCode.`002`),
    ("003", DesExemptionCode.`003`),
    ("004", DesExemptionCode.`004`),
    ("005", DesExemptionCode.`005`),
    ("006", DesExemptionCode.`006`)
  )

  "toMtd" should {
    Seq(
      (DesExemptionCode.`001`, MtdExemptionCode.`001 - Non Resident`),
      (DesExemptionCode.`002`, MtdExemptionCode.`002 - Trustee`),
      (DesExemptionCode.`003`, MtdExemptionCode.`003 - Diver`),
      (DesExemptionCode.`004`, MtdExemptionCode.`004 - Employed earner taxed under ITTOIA 2005`),
      (DesExemptionCode.`005`, MtdExemptionCode.`005 - Over state pension age`),
      (DesExemptionCode.`006`, MtdExemptionCode.`006 - Under 16`)
    ).foreach {
      case (desCode, mtdCode) =>
        s"return ${mtdCode.toString} when passed input ${desCode.toString}" in {
          desCode.toMtd shouldBe mtdCode
        }
    }
  }
}
