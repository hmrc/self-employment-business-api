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

package v1.controllers.requestParsers

import play.api.libs.json.Json
import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import v1.models.errors._

class AmendAnnaulSummaryRequestParserSpec extends UnitSpec {
  val nino = "AA123456B"
  val businessId = "XAIS12345678910"
  val taxYear = "2019-20"

  private val requestBodyJson = Json.parse(
    """
      |{
      |   "adjustments": {
      |        "includedNonTaxableProfits": 216.12,
      |        "basisAdjustment": 626.53,
      |        "overlapReliefUsed": 153.89,
      |        "accountingAdjustment": 514.24,
      |        "averagingAdjustment": 124.98,
      |        "lossBroughtForward": 571.27,
      |        "outstandingBusinessIncome": 751.03,
      |        "balancingChargeBPRA": 719.23,
      |        "balancingChargeOther": 956.47,
      |        "goodsAndServicesOwnUse": 157.43
      |    },
      |    "allowances": {
      |        "annualInvestmentAllowance": 561.32,
      |        "businessPremisesRenovationAllowance": 198.45,
      |        "capitalAllowanceMainPool": 825.34,
      |        "capitalAllowanceSpecialRatePool": 647.12,
      |        "zeroEmissionGoodsVehicleAllowance": 173.64,
      |        "enhancedCapitalAllowance": 115.98,
      |        "allowanceOnSales": 548.15,
      |        "capitalAllowanceSingleAssetPool": 901.67,
      |        "tradingAllowance": 521.34
      |    },
      |    "nonFinancials": {
      |        "class4NicInfo":{
      |            "isExempt": true,
      |            "exemptionCode": "002 - Trustee"
      |        }
      |    }
      |}
        """.stripMargin)

  val inputData =
    AmendAnnualSummaryRawData(nino, businessId, taxYear, requestBodyJson)

  trait Test extends MockAmendAnnualSummaryValidator {
    lazy val parser = new AmendAnnualSummaryRequestParser(mockValidator)
  }

  "parse" should {

    "return a request object" when {
      "valid request data is supplied" in new Test {
        MockAmendAnnualSummaryValidator.validate(inputData).returns(Nil)

        val amendAnnualSummaryRequestBody =
          AmendAnnualSummaryRequestBody(
            Some(Adjustments(
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25)
              )),
              Some(Allowances(
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25),
                Some(100.25)
              )),
              Some(nonFinancials(
                Some(class4NicInfo(
                  true,
                  "001 - Non Resident"
              ))))
          )

        parser.parseRequest(inputData) shouldBe
          Right(AmendAnnualSummaryRequest(Nino(nino), businessId, taxYear, amendAnnualSummaryRequestBody))
      }
    }
    "return an ErrroWrapper" when {
      "a single validation error occurs" in new Test {
        MockAmendAnnualSummaryValidator.validate(inputData)
          .returns(List(NinoFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(None, NinoFormatError, None))
      }

      "multiple validation errors occur" in new Test {
        MockAmendAnnualSummaryValidator.validate(inputData)
          .returns(List(NinoFormatError, BusinessIdFormatError))

        parser.parseRequest(inputData) shouldBe
          Left(ErrorWrapper(None, BadRequestError, Some(Seq(NinoFormatError, BusinessIdFormatError))))
      }
    }
  }
}
