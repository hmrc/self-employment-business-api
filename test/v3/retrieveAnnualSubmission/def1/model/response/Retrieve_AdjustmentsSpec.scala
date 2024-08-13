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

package v3.retrieveAnnualSubmission.def1.model.response

import play.api.libs.json.Json
import shared.utils.UnitSpec

class Retrieve_AdjustmentsSpec extends UnitSpec {

  val adjustments: Retrieve_Adjustments =
    Retrieve_Adjustments(
      includedNonTaxableProfits = Some(1.12),
      basisAdjustment = Some(2.12),
      overlapReliefUsed = Some(3.12),
      accountingAdjustment = Some(4.12),
      averagingAdjustment = Some(5.12),
      outstandingBusinessIncome = Some(6.12),
      balancingChargeBpra = Some(7.12),
      balancingChargeOther = Some(8.12),
      goodsAndServicesOwnUse = Some(9.12)
    )

  "reads" when {
    "given valid mtd JSON" should {
      "return the deserialised case class" in {
        val result = Json
          .parse(s"""{
             |  "includedNonTaxableProfits": 1.12,
             |  "basisAdjustment": 2.12,
             |  "overlapReliefUsed": 3.12,
             |  "accountingAdjustment": 4.12,
             |  "averagingAdjustment": 5.12,
             |  "outstandingBusinessIncome": 6.12,
             |  "balancingChargeBpra": 7.12,
             |  "balancingChargeOther": 8.12,
             |  "goodsAndServicesOwnUse": 9.12
             |}
             |""".stripMargin)
          .as[Retrieve_Adjustments]

        result shouldBe adjustments
      }
    }
  }

  "writes" when {
    "given a Scala object" should {
      "return downstream JSON" in {
        val result = Json.toJson(adjustments)

        result shouldBe Json.parse(s"""{
             |  "includedNonTaxableProfits": 1.12,
             |  "basisAdjustment": 2.12,
             |  "overlapReliefUsed": 3.12,
             |  "accountingAdjustment": 4.12,
             |  "averagingAdjustment": 5.12,
             |  "outstandingBusinessIncome": 6.12,
             |  "balancingChargeBpra": 7.12,
             |  "balancingChargeOther": 8.12,
             |  "goodsAndServicesOwnUse": 9.12
             |}
             |""".stripMargin)
      }
    }
  }

}
