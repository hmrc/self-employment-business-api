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

package v3.amendPeriodSummary.def1

import api.controllers.validators.Validator
import api.models.domain.{BusinessId, Nino, PeriodId}
import api.models.errors._
import api.models.utils.JsonErrorValidators
import mocks.MockAppConfig
import play.api.Configuration
import play.api.libs.json.{JsNumber, JsObject, JsValue, Json}
import support.UnitSpec
import v3.amendPeriodSummary.def1.model.request.{
  Def1_Amend_PeriodDisallowableExpenses,
  Def1_Amend_PeriodExpenses,
  Def1_Amend_PeriodIncome
}
import v3.amendPeriodSummary.model.request.{
  AmendPeriodSummaryRequestData,
  Def1_AmendPeriodSummaryRequestBody,
  Def1_AmendPeriodSummaryRequestData
}

class Def1_AmendPeriodSummaryValidatorSpec extends UnitSpec with JsonErrorValidators with MockAppConfig {

  private implicit val correlationId: String = "1234"

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"
  private val validPeriodId   = "2017-01-25_2017-02-28"

  private val validPeriodIncome = Json.parse("""
      | {
      |   "turnover": 200.00,
      |   "other": 201.00,
      |   "taxTakenOffTradingIncome": 202.00
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

  private val parsedPeriodIncome = Def1_Amend_PeriodIncome(Some(200.00), Some(201.00))

  private def numericValue(isNegative: Boolean)(number: BigDecimal): BigDecimal =
    if (isNegative) -1 * number else number

  // @formatter:off
  private def parsedPeriodExpenses(withNegatives: Boolean = false) = {
    val number = numericValue(withNegatives)(_)
    Def1_Amend_PeriodExpenses(
      None, Some(number(203.00)), Some(number(204.00)), Some(number(205.00)), Some(number(206.00)),
      Some(number(207.00)), Some(number(208.00)), Some(number(209.00)), Some(number(210.00)),
      Some(number(211.00)), Some(number(212.00)), Some(number(213.00)), Some(number(214.00)),
      Some(number(215.00)), Some(number(216.00)), Some(number(217.00))
    )
  }

  private val parsedPeriodExpensesConsolidated = Def1_Amend_PeriodExpenses(
    Some(1000.99), None, None, None, None, None, None, None,
    None, None, None, None, None, None, None, None
  )

  private def parsedPeriodDisallowableExpenses(withNegatives: Boolean = false) = {
    val number = numericValue(withNegatives)(_)

    Def1_Amend_PeriodDisallowableExpenses(
      Some(number(218.00)), Some(number(219.00)), Some(number(220.00)), Some(number(221.00)), Some(number(222.00)),
      Some(number(223.00)), Some(number(224.00)), Some(number(225.00)), Some(number(226.00)), Some(number(227.00)),
      Some(number(228.00)), Some(number(229.00)), Some(number(230.00)), Some(number(231.00)), Some(number(232.00))
    )
  }
  // @formatter:on

  private val parsedBody =
    Def1_AmendPeriodSummaryRequestBody(Some(parsedPeriodIncome), Some(parsedPeriodExpenses()), Some(parsedPeriodDisallowableExpenses()))

  private val parsedBodyWithNegatives =
    Def1_AmendPeriodSummaryRequestBody(
      Some(parsedPeriodIncome),
      Some(parsedPeriodExpenses(withNegatives = true)),
      Some(parsedPeriodDisallowableExpenses(withNegatives = true)))

  private def validator(nino: String,
                        businessId: String,
                        periodId: String,
                        body: JsValue,
                        includeNegatives: Boolean = false): Validator[AmendPeriodSummaryRequestData] =
    new Def1_AmendPeriodSummaryValidator(nino, businessId, periodId, body, includeNegatives)

  private def setupMocks(): Unit =
    MockAppConfig.featureSwitches.returns(Configuration("cl290.enabled" -> true)).anyNumberOfTimes()

  "validate()" should {
    "return the parsed domain object" when {
      "given a valid request with negative expenses, includeNegatives is true" in {
        setupMocks()

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validBodyWithNegatives, includeNegatives = true).validateAndWrapResult()

        result shouldBe Right(Def1_AmendPeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedPeriodId, parsedBodyWithNegatives))
      }

      "given a valid request with consolidated expenses" in {
        setupMocks()

        val body: JsValue =
          validBody(periodExpenses = validPeriodExpensesConsolidated).removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, body).validateAndWrapResult()

        result shouldBe Right(
          Def1_AmendPeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedPeriodId,
            parsedBody.copy(periodExpenses = Some(parsedPeriodExpensesConsolidated), periodDisallowableExpenses = None)
          ))
      }

      "given a valid request where only periodIncome" in {
        setupMocks()

        val body: JsValue =
          validBody()
            .removeProperty("/periodExpenses")
            .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, body).validateAndWrapResult()

        result shouldBe Right(
          Def1_AmendPeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedPeriodId,
            parsedBody.copy(periodExpenses = None, periodDisallowableExpenses = None)
          ))
      }

      "given a valid request where only expenses" in {
        val body: JsValue =
          validBody()
            .removeProperty("/periodIncome")

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, body).validateAndWrapResult()

        result shouldBe Right(
          Def1_AmendPeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedPeriodId,
            parsedBody.copy(periodIncome = None)
          ))
      }
    }

    "return a single error" when {
      "given an invalid nino" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator("invalid nino", validBusinessId, validPeriodId, validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "given an invalid business id" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, "invalid business id", validPeriodId, validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BusinessIdFormatError))
      }

      "given an invalid period id" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "invalid period id", validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, PeriodIdFormatError))
      }

      "given a period id outside of range" in {
        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, "0010-01-01_2017-02-31", validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, PeriodIdFormatError))
      }

      def test(error: MtdError)(body: JsValue, path: String, withNegatives: Boolean = false): Unit = {
        s"given a bad value at $path returning $error with negatives ${if (withNegatives) "enabled" else "disabled"}" in {
          setupMocks()

          val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
            validator(validNino, validBusinessId, validPeriodId, body, withNegatives).validateAndWrapResult()

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

      "given a body with negative expenses without negative expenses enabled" in {
        setupMocks()

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, validBodyWithNegatives).validateAndWrapResult()

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

      "given a body with expenses and consolidated expenses" in {
        setupMocks()

        val invalidBody = validBody()
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "given a body with disallowable expenses and consolidated expenses" in {
        setupMocks()

        val invalidBody = validBody()
          .removeProperty("/periodExpenses")
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "given a body with allowable expenses and consolidated expenses" in {
        setupMocks()

        val invalidBody = validBody()
          .removeProperty("/periodDisallowableExpenses")
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "given an empty body" in {
        val invalidBody = JsObject.empty

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError))
      }

      "given a body containing empty periodIncome, periodExpenses, and periodDisallowableExpenses" in {
        val invalidBody = validBody(JsObject.empty, JsObject.empty, JsObject.empty)

        val result: Either[ErrorWrapper, AmendPeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validPeriodId, invalidBody).validateAndWrapResult()

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
          validator("invalid", "invalid", "invalid", validBody()).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(BusinessIdFormatError, NinoFormatError, PeriodIdFormatError))
          )
        )
      }
    }
  }

}
