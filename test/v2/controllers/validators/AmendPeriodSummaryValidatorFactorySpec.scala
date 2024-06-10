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

package v2.controllers.validators

import api.models.domain.PeriodId
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.utils.JsonErrorValidators
import support.UnitSpec
import v2.models.request.amendPeriodSummary._

class AmendPeriodSummaryValidatorFactorySpec extends UnitSpec with JsonErrorValidators {

  private implicit val correlationId: String = "1234"

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validPeriodId   = "2017-01-25_2017-02-28"
  private val validTaxYear    = Some("2023-24")

  private val validPeriodIncome = Json.parse("""
      | {
      |   "turnover": 200.00,
      |   "other": 201.00
      | }
      |""".stripMargin)

  private def validPeriodExpenses(withNegatives: Boolean = false) = {
    val maybeNegative = if (withNegatives) "-" else ""

    Json.parse(s"""
         |{
         |   "costOfGoods": ${maybeNegative}203.00,
         |   "paymentsToSubcontractors": ${maybeNegative}204.00,
         |   "wagesAndStaffCosts": ${maybeNegative}205.00,
         |   "carVanTravelExpenses": ${maybeNegative}206.00,
         |   "premisesRunningCosts": ${maybeNegative}207.00,
         |   "maintenanceCosts": ${maybeNegative}208.00,
         |   "adminCosts": ${maybeNegative}209.00,
         |   "businessEntertainmentCosts": ${maybeNegative}210.00,
         |   "advertisingCosts": ${maybeNegative}211.00,
         |   "interestOnBankOtherLoans": ${maybeNegative}212.00,
         |   "financeCharges": ${maybeNegative}213.00,
         |   "irrecoverableDebts": ${maybeNegative}214.00,
         |   "professionalFees": ${maybeNegative}215.00,
         |   "depreciation": ${maybeNegative}216.00,
         |   "otherExpenses": ${maybeNegative}217.00
         | }
         |""".stripMargin)
  }

  private def validPeriodDisallowableExpenses(withNegatives: Boolean = false) = {
    val maybeNegative = if (withNegatives) "-" else ""
    Json.parse(s"""
         | {
         |   "costOfGoodsDisallowable": ${maybeNegative}218.00,
         |   "paymentsToSubcontractorsDisallowable": ${maybeNegative}219.00,
         |   "wagesAndStaffCostsDisallowable": ${maybeNegative}220.00,
         |   "carVanTravelExpensesDisallowable": ${maybeNegative}221.00,
         |   "premisesRunningCostsDisallowable": ${maybeNegative}222.00,
         |   "maintenanceCostsDisallowable": ${maybeNegative}223.00,
         |   "adminCostsDisallowable": ${maybeNegative}224.00,
         |   "businessEntertainmentCostsDisallowable": ${maybeNegative}225.00,
         |   "advertisingCostsDisallowable": ${maybeNegative}226.00,
         |   "interestOnBankOtherLoansDisallowable": ${maybeNegative}227.00,
         |   "financeChargesDisallowable": ${maybeNegative}228.00,
         |   "irrecoverableDebtsDisallowable": ${maybeNegative}229.00,
         |   "professionalFeesDisallowable": ${maybeNegative}230.00,
         |   "depreciationDisallowable": ${maybeNegative}231.00,
         |   "otherExpensesDisallowable": ${maybeNegative}232.00
         | }
         |""".stripMargin)
  }

  private val validPeriodExpensesConsolidated = Json.parse("""
      |{
      |   "consolidatedExpenses": 1000.99
      |}
      |""".stripMargin)

  private def validBody(periodIncome: JsValue = validPeriodIncome,
                        periodExpenses: JsValue = validPeriodExpenses(),
                        periodDisallowableExpenses: JsValue = validPeriodDisallowableExpenses()) = Json.obj(
    "periodIncome"               -> periodIncome,
    "periodExpenses"             -> periodExpenses,
    "periodDisallowableExpenses" -> periodDisallowableExpenses
  )

  private val validBodyWithNegatives =
    validBody(periodExpenses = validPeriodExpenses(true), periodDisallowableExpenses = validPeriodDisallowableExpenses(true))

  private val parsedNino       = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)
  private val parsedPeriodId   = PeriodId(validPeriodId)
  private val parsedTaxYear    = validTaxYear.map(TaxYear.fromMtd)

  private val parsedPeriodIncome = PeriodIncome(Some(200.00), Some(201.00))

  private def numericValue(isNegative: Boolean)(number: BigDecimal): BigDecimal =
    if (isNegative) -1 * number else number

  // @formatter:off
  private def parsedPeriodExpenses(withNegatives: Boolean = false) = {
   val number = numericValue(withNegatives)(_)
    PeriodExpenses(
      None, Some(number(203.00)), Some(number(204.00)), Some(number(205.00)), Some(number(206.00)),
      Some(number(207.00)), Some(number(208.00)), Some(number(209.00)), Some(number(210.00)),
      Some(number(211.00)), Some(number(212.00)), Some(number(213.00)), Some(number(214.00)),
      Some(number(215.00)), Some(number(216.00)), Some(number(217.00))
    )
  }

  private val parsedPeriodExpensesConsolidated = PeriodExpenses(
    Some(1000.99), None, None, None, None, None, None, None,
    None, None, None, None, None, None, None, None
  )

  private def parsedPeriodDisallowableExpenses(withNegatives: Boolean = false) = {
    val number = numericValue(withNegatives)(_)

    PeriodDisallowableExpenses(
      Some(number(218.00)), Some(number(219.00)), Some(number(220.00)), Some(number(221.00)), Some(number(222.00)),
      Some(number(223.00)), Some(number(224.00)), Some(number(225.00)), Some(number(226.00)), Some(number(227.00)),
      Some(number(228.00)), Some(number(229.00)), Some(number(230.00)), Some(number(231.00)), Some(number(232.00))
    )
  }
  // @formatter:on

  private val parsedBody =
    AmendPeriodSummaryBody(Some(parsedPeriodIncome), Some(parsedPeriodExpenses()), Some(parsedPeriodDisallowableExpenses()))

  private val parsedBodyWithNegatives =
    AmendPeriodSummaryBody(Some(parsedPeriodIncome), Some(parsedPeriodExpenses(true)), Some(parsedPeriodDisallowableExpenses(true)))

  private val validatorFactory = new AmendPeriodSummaryValidatorFactory

  private def validator(nino: String,
                        businessId: String,
                        periodId: String,
                        taxYear: Option[String],
                        body: JsValue,
                        includeNegatives: Boolean = false) =
    validatorFactory.validator(nino, businessId, periodId, taxYear, body, includeNegatives)

  "validator" should {
    "return the parsed domain object" when {
      "passed a valid request without a tax year" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, None, validBody()).validateAndWrapResult()

        result shouldBe Right(AmendPeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedPeriodId, None, parsedBody))
      }

      "passed a valid request with negative expenses, includeNegatives is true and without a tax year" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, None, validBodyWithNegatives, includeNegatives = true).validateAndWrapResult()

        result shouldBe Right(AmendPeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedPeriodId, None, parsedBodyWithNegatives))
      }

      "passed a valid request with a TYS tax year" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, validBody()).validateAndWrapResult()

        result shouldBe Right(AmendPeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedPeriodId, parsedTaxYear, parsedBody))
      }

      "passed a valid request with consolidated expenses" in {
        val body: JsValue =
          validBody(periodExpenses = validPeriodExpensesConsolidated).removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, None, body).validateAndWrapResult()

        result shouldBe Right(
          AmendPeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedPeriodId,
            None,
            parsedBody.copy(periodExpenses = Some(parsedPeriodExpensesConsolidated), periodDisallowableExpenses = None)
          ))
      }

      "passed a valid request where only periodIncome is supplied" in {
        val body: JsValue =
          validBody()
            .removeProperty("/periodExpenses")
            .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, None, body).validateAndWrapResult()

        result shouldBe Right(
          AmendPeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedPeriodId,
            None,
            parsedBody.copy(periodExpenses = None, periodDisallowableExpenses = None)
          ))
      }

      "passed a valid request where only expenses is supplied" in {
        val body: JsValue =
          validBody()
            .removeProperty("/periodIncome")

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, None, body).validateAndWrapResult()

        result shouldBe Right(
          AmendPeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedPeriodId,
            None,
            parsedBody.copy(periodIncome = None)
          ))
      }
    }

    "return a single error" when {
      "passed an invalid nino is supplied" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator("invalid nino", validBusinessId, validPeriodId, validTaxYear, validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "passed an invalid business id is supplied" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, "invalid business id", validPeriodId, validTaxYear, validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BusinessIdFormatError))
      }

      "passed an invalid period id is supplied" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "invalid period id", validTaxYear, validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, PeriodIdFormatError))
      }

      "passed a period id outside of range is supplied" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "0010-01-01_2017-02-31", validTaxYear, validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, PeriodIdFormatError))
      }

      "passed an invalid tax year is supplied" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, Some("invalid tax year"), validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, TaxYearFormatError))
      }

      "passed an invalid tax year range is supplied" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, Some("2023-25"), validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearRangeInvalidError))
      }

      "passed a non-TYS tax year is supplied" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, Some("2021-22"), validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, InvalidTaxYearParameterError))
      }

      def test(error: MtdError)(body: JsValue, path: String, withNegatives: Boolean = false): Unit = {
        s"passed a bad value at $path returning $error with negatives ${if (withNegatives) "enabled" else "disabled"}" in {
          val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
            validator(validNino, validBusinessId, validPeriodId, validTaxYear, body, withNegatives).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, error))
        }
      }

      val badNumber = JsNumber(123.123)

      List(
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
      ).foreach(path => test(ValueFormatError.withPath(path))(validBody().update(path, badNumber), path))

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
        "/periodDisallowableExpenses/depreciationDisallowable"
      ).foreach(path =>
        test(ValueFormatError.forPathAndRange(path, min = "-99999999999.99", max = "99999999999.99"))(validBody().update(path, badNumber), path))

      test(ValueFormatError.withPath("/periodExpenses/consolidatedExpenses"))(
        validBody(periodExpenses = Json.obj("consolidatedExpenses" -> badNumber)).removeProperty("/periodDisallowableExpenses"),
        "/PeriodExpenses/consolidatedExpenses"
      )

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
        test(ValueFormatError.forPathAndRange(path, min = "-99999999999.99", max = "99999999999.99"))(
          validBodyWithNegatives.update(path, badNumber),
          path,
          withNegatives = true))

      test(
        ValueFormatError.forPathAndRange("/periodExpenses/consolidatedExpenses", min = "-99999999999.99", max = "99999999999.99")
      )(
        validBodyWithNegatives
          .removeProperty("/periodDisallowableExpenses")
          .removeProperty("/periodExpenses")
          .update("/periodExpenses/consolidatedExpenses", badNumber),
        "/periodExpenses/consolidatedExpenses",
        withNegatives = true
      )

      "passed a body with negative expenses without negative expenses enabled" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, None, validBodyWithNegatives).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            ValueFormatError.withPaths(List(
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
            ))
          ))
      }

      "passed a body with expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "passed a body with disallowable expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .removeProperty("/periodExpenses")
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "passed a body with allowable expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .removeProperty("/periodDisallowableExpenses")
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "passed an empty body" in {
        val invalidBody = JsObject.empty

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError))
      }

      "passed a body containing empty periodIncome, periodExpenses, and periodDisallowableExpenses" in {
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
          validator("invalid", "invalid", "invalid", Some("invalid"), validBody()).validateAndWrapResult()

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
