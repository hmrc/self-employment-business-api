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

package v1.controllers.validators

import api.models.domain.{BusinessId, Nino, PeriodId, TaxYear}
import api.models.errors._
import api.models.utils.JsonErrorValidators
import play.api.libs.json.{JsNumber, JsValue, Json}
import support.UnitSpec
import v1.models.request.amendPeriodSummary.{
  AmendPeriodSummaryBody,
  AmendPeriodSummaryRequestData,
  PeriodAllowableExpenses,
  PeriodDisallowableExpenses,
  PeriodIncome
}

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

  private val validPeriodAllowableExpenses = Json.parse("""
      |{
      |   "costOfGoodsAllowable": 203.00,
      |   "paymentsToSubcontractorsAllowable": 204.00,
      |   "wagesAndStaffCostsAllowable": 205.00,
      |   "carVanTravelExpensesAllowable": 206.00,
      |   "premisesRunningCostsAllowable": 207.00,
      |   "maintenanceCostsAllowable": 208.00,
      |   "adminCostsAllowable": 209.00,
      |   "businessEntertainmentCostsAllowable": 210.00,
      |   "advertisingCostsAllowable": 211.00,
      |   "interestOnBankOtherLoansAllowable": 212.00,
      |   "financeChargesAllowable": 213.00,
      |   "irrecoverableDebtsAllowable": 214.00,
      |   "professionalFeesAllowable": 215.00,
      |   "depreciationAllowable": 216.00,
      |   "otherExpensesAllowable": 217.00
      | }
      |""".stripMargin)

  private val validPeriodDisallowableExpenses = Json.parse("""
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

  private val validPeriodAllowableExpensesConsolidated = Json.parse("""
      |{
      |   "consolidatedExpenses": 1000.99
      |}
      |""".stripMargin)

  private def validBody(periodIncome: JsValue = validPeriodIncome,
                        periodAllowableExpenses: JsValue = validPeriodAllowableExpenses,
                        periodDisallowableExpenses: JsValue = validPeriodDisallowableExpenses) = Json.obj(
    "periodIncome"               -> periodIncome,
    "periodAllowableExpenses"    -> periodAllowableExpenses,
    "periodDisallowableExpenses" -> periodDisallowableExpenses
  )

  private val parsedNino       = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)
  private val parsedPeriodId   = PeriodId(validPeriodId)
  private val parsedTaxYear    = validTaxYear.map(TaxYear.fromMtd)

  private val parsedPeriodIncome = PeriodIncome(Some(200.00), Some(201.00))

  // @formatter:off
  private val parsedPeriodAllowableExpenses = PeriodAllowableExpenses(
    None, Some(203.00), Some(204.00), Some(205.00), Some(206.00),
    Some(207.00), Some(208.00), Some(209.00), Some(210.00), Some(211.00),
    Some(212.00), Some(213.00), Some(214.00), Some(215.00), Some(216.00),
    Some(217.00)
  )

  private val parsedPeriodAllowableExpensesConsolidated = PeriodAllowableExpenses(
    Some(1000.99), None, None, None, None, None, None, None,
    None, None, None, None, None, None, None, None
  )

  private val parsedPeriodDisallowableExpenses = PeriodDisallowableExpenses(
    Some(218.00), Some(219.00), Some(220.00), Some(221.00), Some(222.00),
    Some(223.00), Some(224.00), Some(225.00), Some(226.00), Some(227.00),
    Some(228.00), Some(229.00), Some(230.00), Some(231.00), Some(232.00)
  )
  // @formatter:on

  private val parsedBody =
    AmendPeriodSummaryBody(Some(parsedPeriodIncome), Some(parsedPeriodAllowableExpenses), Some(parsedPeriodDisallowableExpenses))

  private val validatorFactory = new AmendPeriodSummaryValidatorFactory

  private def validator(nino: String, businessId: String, periodId: String, taxYear: Option[String], body: JsValue) =
    validatorFactory.validator(nino, businessId, periodId, taxYear, body)

  "validator" should {
    "return the parsed domain object" when {
      "passed a valid request without a tax year" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, None, validBody()).validateAndWrapResult()

        result shouldBe Right(AmendPeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedPeriodId, None, parsedBody))
      }

      "passed a valid request with a TYS tax year" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, validBody()).validateAndWrapResult()

        result shouldBe Right(AmendPeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedPeriodId, parsedTaxYear, parsedBody))
      }

      "passed a valid request with consolidated expenses" in {
        val body: JsValue =
          validBody(periodAllowableExpenses = validPeriodAllowableExpensesConsolidated).removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, None, body).validateAndWrapResult()

        result shouldBe Right(
          AmendPeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedPeriodId,
            None,
            parsedBody.copy(periodAllowableExpenses = Some(parsedPeriodAllowableExpensesConsolidated), periodDisallowableExpenses = None)
          ))
      }

      "passed a valid request where only periodIncome is supplied" in {
        val body: JsValue =
          validBody()
            .removeProperty("/periodAllowableExpenses")
            .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, None, body).validateAndWrapResult()

        result shouldBe Right(
          AmendPeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedPeriodId,
            None,
            parsedBody.copy(periodAllowableExpenses = None, periodDisallowableExpenses = None)
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

      def test(error: MtdError)(body: JsValue, path: String): Unit = {
        s"passed a bad value at $path returning $error" in {
          val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
            validator(validNino, validBusinessId, validPeriodId, validTaxYear, body).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, error))
        }
      }

      val badNumber = JsNumber(123.123)

      List(
        "/periodAllowableExpenses/paymentsToSubcontractorsAllowable",
        "/periodAllowableExpenses/wagesAndStaffCostsAllowable",
        "/periodAllowableExpenses/carVanTravelExpensesAllowable",
        "/periodAllowableExpenses/adminCostsAllowable",
        "/periodAllowableExpenses/businessEntertainmentCostsAllowable",
        "/periodAllowableExpenses/advertisingCostsAllowable",
        "/periodAllowableExpenses/otherExpensesAllowable",
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
        "/periodAllowableExpenses/costOfGoodsAllowable",
        "/periodAllowableExpenses/premisesRunningCostsAllowable",
        "/periodAllowableExpenses/maintenanceCostsAllowable",
        "/periodAllowableExpenses/interestOnBankOtherLoansAllowable",
        "/periodAllowableExpenses/financeChargesAllowable",
        "/periodAllowableExpenses/irrecoverableDebtsAllowable",
        "/periodAllowableExpenses/professionalFeesAllowable",
        "/periodAllowableExpenses/depreciationAllowable",
        "/periodDisallowableExpenses/costOfGoodsDisallowable",
        "/periodDisallowableExpenses/premisesRunningCostsDisallowable",
        "/periodDisallowableExpenses/maintenanceCostsDisallowable",
        "/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable",
        "/periodDisallowableExpenses/financeChargesDisallowable",
        "/periodDisallowableExpenses/irrecoverableDebtsDisallowable",
        "/periodDisallowableExpenses/depreciationDisallowable"
      ).foreach(path =>
        test(ValueFormatError.forPathAndRange(path, min = "-99999999999.99", max = "99999999999.99"))(validBody().update(path, badNumber), path))

      test(ValueFormatError.withPath("/periodAllowableExpenses/consolidatedExpenses"))(
        validBody(periodAllowableExpenses = Json.obj("consolidatedExpenses" -> badNumber)).removeProperty("/periodDisallowableExpenses"),
        "/periodAllowableExpenses/consolidatedExpenses"
      )

      "passed a body with expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .update("/periodAllowableExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "passed a body with disallowable expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .removeProperty("/periodAllowableExpenses")
          .update("/periodAllowableExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "passed a body with allowable expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .removeProperty("/periodDisallowableExpenses")
          .update("/periodAllowableExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validTaxYear, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
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
