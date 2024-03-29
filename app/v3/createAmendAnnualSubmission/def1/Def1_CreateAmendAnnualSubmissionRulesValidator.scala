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

package v3.createAmendAnnualSubmission.def1

import api.controllers.validators.RulesValidator
import api.controllers.validators.resolvers.{ResolveIsoDate, ResolveParsedNumber, ResolveStringPattern}
import api.models.errors._
import cats.data.Validated
import cats.data.Validated.Invalid
import cats.implicits._
import v3.createAmendAnnualSubmission.def1.model.request._
import v3.createAmendAnnualSubmission.model.request.Def1_CreateAmendAnnualSubmissionRequestData

object Def1_CreateAmendAnnualSubmissionRulesValidator extends RulesValidator[Def1_CreateAmendAnnualSubmissionRequestData] {

  private val resolveNonNegativeParsedNumber       = ResolveParsedNumber()
  private val resolveMaybeNegativeParsedNumber     = ResolveParsedNumber(min = -99999999999.99)
  private val resolveNonNegativeCappedParsedNumber = ResolveParsedNumber(max = 1000)
  private val regex                                = "^[0-9a-zA-Z{À-˿’}\\- _&`():.'^]{1,90}$".r
  private val resolveStringPattern                 = new ResolveStringPattern(regex, StringFormatError)

  def validateBusinessRules(
      parsed: Def1_CreateAmendAnnualSubmissionRequestData): Validated[Seq[MtdError], Def1_CreateAmendAnnualSubmissionRequestData] = {
    import parsed.body._
    combine(
      adjustments.map(validateAdjustments).getOrElse(valid),
      allowances.map(validateBothAllowancesSupplied).getOrElse(valid),
      allowances.map(validateAllowances).getOrElse(valid)
    ).onSuccess(parsed)
  }

  private def validateAdjustments(adjustments: Def1_CreateAmend_Adjustments): Validated[Seq[MtdError], Unit] = {
    import adjustments._

    val validatedNonNegatives = List(
      (includedNonTaxableProfits, "/adjustments/includedNonTaxableProfits"),
      (overlapReliefUsed, "/adjustments/overlapReliefUsed"),
      (accountingAdjustment, "/adjustments/accountingAdjustment"),
      (outstandingBusinessIncome, "/adjustments/outstandingBusinessIncome"),
      (balancingChargeBpra, "/adjustments/balancingChargeBpra"),
      (balancingChargeOther, "/adjustments/balancingChargeOther"),
      (goodsAndServicesOwnUse, "/adjustments/goodsAndServicesOwnUse")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path = Some(path))
    }

    val validatedMaybeNegatives = List(
      (basisAdjustment, "/adjustments/basisAdjustment"),
      (averagingAdjustment, "/adjustments/averagingAdjustment")
    ).traverse_ { case (value, path) =>
      resolveMaybeNegativeParsedNumber(value, path = Some(path))
    }

    combine(validatedNonNegatives, validatedMaybeNegatives)
  }

  private def validateAllowances(allowances: Def1_CreateAmend_Allowances): Validated[Seq[MtdError], Unit] = {
    import allowances._

    val validatedNonNegatives = List(
      (annualInvestmentAllowance, "/allowances/annualInvestmentAllowance"),
      (businessPremisesRenovationAllowance, "/allowances/businessPremisesRenovationAllowance"),
      (capitalAllowanceMainPool, "/allowances/capitalAllowanceMainPool"),
      (capitalAllowanceSpecialRatePool, "/allowances/capitalAllowanceSpecialRatePool"),
      (zeroEmissionsGoodsVehicleAllowance, "/allowances/zeroEmissionsGoodsVehicleAllowance"),
      (enhancedCapitalAllowance, "/allowances/enhancedCapitalAllowance"),
      (allowanceOnSales, "/allowances/allowanceOnSales"),
      (capitalAllowanceSingleAssetPool, "/allowances/capitalAllowanceSingleAssetPool"),
      (electricChargePointAllowance, "/allowances/electricChargePointAllowance"),
      (zeroEmissionsCarAllowance, "/allowances/zeroEmissionsCarAllowance")
    ).traverse_ { case (value, path) =>
      resolveNonNegativeParsedNumber(value, path = Some(path))
    }

    val validatedTradingIncomeAllowance =
      tradingIncomeAllowance.map(resolveNonNegativeCappedParsedNumber(_, "/allowances/tradingIncomeAllowance").toUnit).getOrElse(valid)

    val validatedStructuredBuildingAllowance = structuredBuildingAllowance
      .map(_.zipWithIndex.traverse_ { case (entry, i) =>
        validateStructuredBuildingAllowance(entry, i, "structuredBuildingAllowance")
      })
      .getOrElse(valid)

    val validatedEnhancedStructuredBuildingAllowance = enhancedStructuredBuildingAllowance
      .map(_.zipWithIndex.traverse_ { case (entry, i) =>
        validateStructuredBuildingAllowance(entry, i, "enhancedStructuredBuildingAllowance")
      })
      .getOrElse(valid)

    combine(
      validatedNonNegatives,
      validatedTradingIncomeAllowance,
      validatedStructuredBuildingAllowance,
      validatedEnhancedStructuredBuildingAllowance)
  }

  private def validateBothAllowancesSupplied(allowances: Def1_CreateAmend_Allowances): Validated[Seq[MtdError], Unit] = {
    if (allowances.tradingIncomeAllowance.isDefined) {
      allowances match {
        case Def1_CreateAmend_Allowances(None, None, None, None, None, None, None, None, Some(_), None, None, None, None) => valid
        case _ => Invalid(List(RuleBothAllowancesSuppliedError))
      }
    } else {
      valid
    }
  }

  private def validateStructuredBuildingAllowance(structuredBuildingAllowance: Def1_CreateAmend_StructuredBuildingAllowance,
                                                  index: Int,
                                                  typeOfBuildingAllowance: String): Validated[Seq[MtdError], Unit] = {
    import structuredBuildingAllowance._

    val validatedAmount = resolveNonNegativeParsedNumber(amount, s"/allowances/$typeOfBuildingAllowance/$index/amount").toUnit

    val validatedQualifyingAmountExpenditure = firstYear
      .map(year =>
        resolveNonNegativeParsedNumber(
          year.qualifyingAmountExpenditure,
          s"/allowances/$typeOfBuildingAllowance/$index/firstYear/qualifyingAmountExpenditure").toUnit)
      .getOrElse(valid)

    val validatedOptionalStrings = List(
      (building.name, s"/allowances/$typeOfBuildingAllowance/$index/building/name"),
      (building.number, s"/allowances/$typeOfBuildingAllowance/$index/building/number")
    ).traverse_ { case (value, path) =>
      resolveStringPattern(value, path = Some(path))
    }

    val validatedString = resolveStringPattern(building.postcode, s"/allowances/$typeOfBuildingAllowance/$index/building/postcode").toUnit

    val validatedDate = firstYear
      .map(year =>
        ResolveIsoDate(year.qualifyingDate, DateFormatError.withPath(s"/allowances/$typeOfBuildingAllowance/$index/firstYear/qualifyingDate")).toUnit)
      .getOrElse(valid)

    val validatedBuildingNameNumber = validateBuildingNameNumber(building, s"/allowances/$typeOfBuildingAllowance/$index/building")

    combine(
      validatedAmount,
      validatedQualifyingAmountExpenditure,
      validatedOptionalStrings,
      validatedString,
      validatedDate,
      validatedBuildingNameNumber
    )

  }

  private def validateBuildingNameNumber(building: Def1_CreateAmend_Building, path: String): Validated[Seq[MtdError], Unit] = {
    building match {
      case Def1_CreateAmend_Building(None, None, _) => Invalid(List(RuleBuildingNameNumberError.withPath(path)))
      case _                                        => valid
    }
  }

}
