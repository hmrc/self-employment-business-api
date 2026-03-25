/*
 * Copyright 2026 HM Revenue & Customs
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

package v5.amendPeriodSummary.def2

import api.models.domain.PeriodId
import api.models.errors.{PeriodIdFormatError, RuleBothExpensesSuppliedError}
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import shared.config.MockSharedAppConfig
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors.*
import shared.models.utils.JsonErrorValidators
import shared.utils.UnitSpec
import v5.amendPeriodSummary.def2.model.request.{
  Def2_AmendPeriodSummaryRequestBody,
  Def2_AmendPeriodSummaryRequestData,
  Def2_Amend_PeriodDisallowableExpenses,
  Def2_Amend_PeriodExpenses,
  Def2_Amend_PeriodIncome
}
import v5.amendPeriodSummary.model.request.AmendPeriodSummaryRequestData

class Def2_AmendPeriodSummaryValidatorSpec extends UnitSpec with JsonErrorValidators with MockSharedAppConfig {

  private implicit val correlationId: String = "1234"

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validPeriodId   = "2017-01-25_2017-02-28"
  private val validTaxYear    = "2023-24"

  private val validPeriodIncome = Json.parse("""
      | {
      |   "turnover": 200.00,
      |   "other": 201.00,
      |   "taxTakenOffTradingIncome": 202.00
      | }
      |""".stripMargin)

  private def validPeriodExpenses =
    Json.parse(s"""
         |{
         |   "costOfGoods": 203.00,
         |   "paymentsToSubcontractors": 204.00,
         |   "wagesAndStaffCosts": 205.00,
         |   "carVanTravelExpenses": 206.00,
         |   "premisesRunningCosts": 207.00,
         |   "maintenanceCosts": 208.00,
         |   "adminCosts": 209.00,
         |   "businessEntertainmentCosts": 210.00,
         |   "advertisingCosts": 211.00,
         |   "interestOnBankOtherLoans": 212.00,
         |   "financeCharges": 213.00,
         |   "irrecoverableDebts": 214.00,
         |   "professionalFees": 215.00,
         |   "depreciation": 216.00,
         |   "otherExpenses": 217.00
         | }
         |""".stripMargin)

  private def validPeriodDisallowableExpenses =
    Json.parse(s"""
         | {
         |   "costOfGoodsDisallowable": 218.00,
         |   "paymentsToSubcontractorsDisallowable": 219.00,
         |   "wagesAndStaffCostsDisallowable": 220.00,
         |   "carVanTravelExpensesDisallowable": 221.00,
         |   "premisesRunningCostsDisallowable": 222.00,
         |   "maintenanceCostsDisallowable": 223.00,
         |   "adminCostsDisallowable": 224.00,
         |   "businessEntertainmentCostsDisallowable": 225.00,
         |   "advertisingCostsDisallowable": 226.00,
         |   "interestOnBankOtherLoansDisallowable": 227.00,
         |   "financeChargesDisallowable": 228.00,
         |   "irrecoverableDebtsDisallowable": 229.00,
         |   "professionalFeesDisallowable": 230.00,
         |   "depreciationDisallowable": 231.00,
         |   "otherExpensesDisallowable": 232.00
         | }
         |""".stripMargin)

  private val validPeriodExpensesConsolidated = Json.parse("""
      |{
      |   "consolidatedExpenses": 1000.99
      |}
      |""".stripMargin)

  private def validBody(periodIncome: JsValue = validPeriodIncome,
                        periodExpenses: JsValue = validPeriodExpenses,
                        periodDisallowableExpenses: JsValue = validPeriodDisallowableExpenses) = Json.obj(
    "periodIncome"               -> periodIncome,
    "periodExpenses"             -> periodExpenses,
    "periodDisallowableExpenses" -> periodDisallowableExpenses
  )

  private val parsedNino       = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)
  private val parsedPeriodId   = PeriodId(validPeriodId)
  private val parsedTaxYear    = TaxYear.fromMtd(validTaxYear)

  private val parsedPeriodIncome = Def2_Amend_PeriodIncome(Some(200.00), Some(201.00), Some(202.00))

  // @formatter:off
  private def parsedPeriodExpenses =
    Def2_Amend_PeriodExpenses(
      None, Some(203.00), Some(204.00), Some(205.00), Some(206.00),
      Some(207.00), Some(208.00), Some(209.00), Some(210.00),
      Some(211.00), Some(212.00), Some(213.00), Some(214.00),
      Some(215.00), Some(216.00), Some(217.00)
    )

  private val parsedPeriodExpensesConsolidated = Def2_Amend_PeriodExpenses(
    Some(1000.99), None, None, None, None, None, None, None,
    None, None, None, None, None, None, None, None
  )

  private def parsedPeriodDisallowableExpenses =
    Def2_Amend_PeriodDisallowableExpenses(
      Some(218.00), Some(219.00), Some(220.00), Some(221.00), Some(222.00),
      Some(223.00), Some(224.00), Some(225.00), Some(226.00), Some(227.00),
      Some(228.00), Some(229.00), Some(230.00), Some(231.00), Some(232.00)
    )

  // @formatter:on

  private val parsedBody =
    Def2_AmendPeriodSummaryRequestBody(Some(parsedPeriodIncome), Some(parsedPeriodExpenses), Some(parsedPeriodDisallowableExpenses))

  private val parsedBodyWithNegatives =
    Def2_AmendPeriodSummaryRequestBody(Some(parsedPeriodIncome), Some(parsedPeriodExpenses), Some(parsedPeriodDisallowableExpenses))

  private def validator(nino: String, businessId: String, periodId: String, taxYear: String, body: JsValue) =
    new Def2_AmendPeriodSummaryValidator(nino, businessId, periodId, taxYear, body)

  "validate()" should {
    "return the parsed domain object" when {

      "given a valid TYS request" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, validBody()).validateAndWrapResult()

        val expected = Def2_AmendPeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedPeriodId, parsedTaxYear, parsedBody)
        result shouldBe Right(expected)
      }

      "given a valid request with consolidated expenses" in {
        val body: JsValue =
          validBody(periodExpenses = validPeriodExpensesConsolidated).removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Right(
          Def2_AmendPeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedPeriodId,
            parsedTaxYear,
            parsedBody.copy(periodExpenses = Some(parsedPeriodExpensesConsolidated), periodDisallowableExpenses = None)
          ))
      }

      "given a valid request with negative expenses" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, validBody())
            .validateAndWrapResult()

        val expected = Def2_AmendPeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedPeriodId, parsedTaxYear, parsedBodyWithNegatives)
        result shouldBe Right(expected)
      }

      "given a valid request where only periodIncome" in {
        val body: JsValue =
          validBody()
            .removeProperty("/periodExpenses")
            .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Right(
          Def2_AmendPeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedPeriodId,
            parsedTaxYear,
            parsedBody.copy(periodExpenses = None, periodDisallowableExpenses = None)
          ))
      }

      "given a valid request where only expenses" in {
        val body: JsValue =
          validBody()
            .removeProperty("/periodIncome")

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, body).validateAndWrapResult()

        result shouldBe Right(
          Def2_AmendPeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedPeriodId,
            parsedTaxYear,
            parsedBody.copy(periodIncome = None)
          ))
      }
    }

    "return a single error" when {
      "given an invalid nino" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator("invalid nino", validBusinessId, validPeriodId, validTaxYear, validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "given an invalid business id" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, "invalid business id", validPeriodId, validTaxYear, validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BusinessIdFormatError))
      }

      "given an invalid period id" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "invalid period id", validTaxYear, validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, PeriodIdFormatError))
      }

      "given a period id outside of range" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "0010-01-01_2017-02-31", validTaxYear, validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, PeriodIdFormatError))
      }

      "given an invalid tax year" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, "invalid tax year", validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, TaxYearFormatError))
      }

      "given an invalid tax year range" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, "2023-25", validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError))
      }

      "a tax year 2025 or over is passed" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, "2025-26", validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))
      }

      "given a non-TYS tax year" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, "2021-22", validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, InvalidTaxYearParameterError))
      }

      def test(error: MtdError)(body: JsValue, path: String): Unit = {
        s"given a bad value at $path returning $error" in {
          val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
            validator(validNino, validBusinessId, validPeriodId, validTaxYear, body).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, error))
        }
      }

      val badNumber = JsNumber(123.123)

      List(
        "/periodExpenses/costOfGoods",
        "/periodExpenses/premisesRunningCosts",
        "/periodExpenses/maintenanceCosts",
        "/periodExpenses/interestOnBankOtherLoans",
        "/periodExpenses/financeCharges",
        "/periodExpenses/irrecoverableDebts",
        "/periodExpenses/professionalFees",
        "/periodExpenses/depreciation",
        "/periodDisallowableExpenses/costOfGoodsDisallowable",
        "/periodDisallowableExpenses/premisesRunningCostsDisallowable",
        "/periodDisallowableExpenses/maintenanceCostsDisallowable",
        "/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable",
        "/periodDisallowableExpenses/financeChargesDisallowable",
        "/periodDisallowableExpenses/irrecoverableDebtsDisallowable",
        "/periodDisallowableExpenses/depreciationDisallowable",
        "/periodExpenses/paymentsToSubcontractors",
        "/periodExpenses/wagesAndStaffCosts",
        "/periodExpenses/carVanTravelExpenses",
        "/periodExpenses/adminCosts",
        "/periodExpenses/businessEntertainmentCosts",
        "/periodExpenses/advertisingCosts",
        "/periodExpenses/otherExpenses",
        "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable",
        "/periodDisallowableExpenses/wagesAndStaffCostsDisallowable",
        "/periodDisallowableExpenses/carVanTravelExpensesDisallowable",
        "/periodDisallowableExpenses/adminCostsDisallowable",
        "/periodDisallowableExpenses/businessEntertainmentCostsDisallowable",
        "/periodDisallowableExpenses/advertisingCostsDisallowable",
        "/periodDisallowableExpenses/professionalFeesDisallowable",
        "/periodDisallowableExpenses/otherExpensesDisallowable"
      ).foreach(path =>
        test(ValueFormatError.forPathAndRange(path, min = "-99999999999.99", max = "99999999999.99"))(validBody().update(path, badNumber), path))

      test(
        ValueFormatError.forPathAndRange("/periodExpenses/consolidatedExpenses", min = "-99999999999.99", max = "99999999999.99")
      )(
        validBody()
          .removeProperty("/periodDisallowableExpenses")
          .removeProperty("/periodExpenses")
          .update("/periodExpenses/consolidatedExpenses", badNumber),
        "/periodExpenses/consolidatedExpenses"
      )

      "given a body with expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "given a body with disallowable expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .removeProperty("/periodExpenses")
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "given a body with allowable expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .removeProperty("/periodDisallowableExpenses")
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "given an empty body" in {
        val invalidBody = JsObject.empty

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError))
      }

      "given a body containing empty periodIncome, periodExpenses, and periodDisallowableExpenses" in {
        val invalidBody = validBody(JsObject.empty, JsObject.empty, JsObject.empty)

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, invalidBody).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            RuleIncorrectOrEmptyBodyError.withPaths(List("/periodIncome", "/periodExpenses", "/periodDisallowableExpenses"))
          )
        )
      }
    }

    "return multiple errors" when {
      "invalid parameters are supplied" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator("invalid", "invalid", "invalid", "invalid", validBody()).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(BusinessIdFormatError, NinoFormatError, PeriodIdFormatError, TaxYearFormatError))
          )
        )
      }
    }
  }

}
