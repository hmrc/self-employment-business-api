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

package v3.createAmendAnnualSubmission.def2.request

import config.FeatureSwitchesImpl
import play.api.Configuration
import play.api.libs.json.Json
import support.UnitSpec

class Def2_CreateAmend_AdjustmentsSpec extends UnitSpec {

  val model: Def2_CreateAmend_Adjustments =
    Def2_CreateAmend_Adjustments(
      includedNonTaxableProfits = Some(1.12),
      basisAdjustment = Some(2.12),
      overlapReliefUsed = Some(3.12),
      accountingAdjustment = Some(4.12),
      averagingAdjustment = Some(5.12),
      outstandingBusinessIncome = Some(6.12),
      balancingChargeBpra = Some(7.12),
      balancingChargeOther = Some(8.12),
      goodsAndServicesOwnUse = Some(9.12),
      transitionProfitAmount = Some(9.12),
      transitionProfitAccelerationAmount = Some(9.12)
    )

  "reads" when {
    val json = Json
      .parse(s"""{
           |  "includedNonTaxableProfits": 1.12,
           |  "basisAdjustment": 2.12,
           |  "overlapReliefUsed": 3.12,
           |  "accountingAdjustment": 4.12,
           |  "averagingAdjustment": 5.12,
           |  "outstandingBusinessIncome": 6.12,
           |  "balancingChargeBpra": 7.12,
           |  "balancingChargeOther": 8.12,
           |  "goodsAndServicesOwnUse": 9.12,
           |  "transitionProfitAmount": 9.12,
           |  "transitionProfitAccelerationAmount": 9.12
           |}
           |""".stripMargin)

    "additional fields are switched on" should {
      "return the full model" in {
        implicit val featureSwitches: FeatureSwitchesImpl = FeatureSwitchesImpl(Configuration("adjustmentsAdditionalFields.enabled" -> true))

        json
          .as[Def2_CreateAmend_Adjustments] shouldBe model
      }
    }

    "additional fields are switched off" should {
      "return the model without the additional fields" in {
        implicit val featureSwitches: FeatureSwitchesImpl = FeatureSwitchesImpl(Configuration("adjustmentsAdditionalFields.enabled" -> false))

        json
          .as[Def2_CreateAmend_Adjustments] shouldBe model.copy(transitionProfitAmount = None, transitionProfitAccelerationAmount = None)
      }

      "ignore ill-typed additional fields" in {
        implicit val featureSwitches: FeatureSwitchesImpl = FeatureSwitchesImpl(Configuration("adjustmentsAdditionalFields.enabled" -> false))

        Json
          .parse(s"""{
                    |  "includedNonTaxableProfits": 1.12,
                    |  "basisAdjustment": 2.12,
                    |  "overlapReliefUsed": 3.12,
                    |  "accountingAdjustment": 4.12,
                    |  "averagingAdjustment": 5.12,
                    |  "outstandingBusinessIncome": 6.12,
                    |  "balancingChargeBpra": 7.12,
                    |  "balancingChargeOther": 8.12,
                    |  "goodsAndServicesOwnUse": 9.12,
                    |  "transitionProfitAmount": "XXX",
                    |  "transitionProfitAccelerationAmount": true
                    |}
                    |""".stripMargin)
          .as[Def2_CreateAmend_Adjustments] shouldBe model.copy(transitionProfitAmount = None, transitionProfitAccelerationAmount = None)
      }
    }
  }

  "writes" when {
    "passed a model" should {
      "return downstream JSON" in {
        Json.toJson(model) shouldBe Json.parse(s"""{
             |  "includedNonTaxableProfits": 1.12,
             |  "basisAdjustment": 2.12,
             |  "overlapReliefUsed": 3.12,
             |  "accountingAdjustment": 4.12,
             |  "averagingAdjustment": 5.12,
             |  "outstandingBusinessIncome": 6.12,
             |  "balancingChargeBpra": 7.12,
             |  "balancingChargeOther": 8.12,
             |  "goodsAndServicesOwnUse": 9.12,
             |  "transitionProfitAmount": 9.12,
             |  "transitionProfitAccelerationAmount": 9.12
             |}
             |""".stripMargin)
      }
    }
  }

}
