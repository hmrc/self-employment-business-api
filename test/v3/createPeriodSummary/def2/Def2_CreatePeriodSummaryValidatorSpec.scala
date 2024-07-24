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

package v3.createPeriodSummary.def2

import api.models.errors.RuleBothExpensesSuppliedError
import play.api.Configuration
import play.api.libs.json._
import shared.UnitSpec
import shared.config.MockAppConfig
import shared.models.domain.{BusinessId, Nino}
import shared.models.errors._
import shared.models.utils.JsonErrorValidators
import v3.createPeriodSummary.def2.model.request._
import v3.createPeriodSummary.model.request.{CreatePeriodSummaryRequestData, Def2_CreatePeriodSummaryRequestBody, Def2_CreatePeriodSummaryRequestData}

class Def2_CreatePeriodSummaryValidatorSpec extends UnitSpec with JsonErrorValidators with MockAppConfig {

  private implicit val correlationId: String = "1234"

  private val validNino = "AA123456A"
  private val validBusinessId = "XAIS12345678901"

  private val validPeriodDates = Json.parse(
    """
      |{
      |  "periodStartDate": "2019-08-24",
      |  "periodEndDate": "2020-08-24"
      |}
      |""".stripMargin)

  private val validPeriodIncome = Json.parse(
    """
      |{
      |   "turnover": 1000.99,
      |   "other": 1001.99,
      |   "taxTakenOffTradingIncome": 1002.99
      |}
      |""".stripMargin)

  private def validPeriodExpenses(withNegatives: Boolean = false): JsValue = {
    val maybeNegative = if (withNegatives) "-" else ""

    Json.parse(
      s"""
         |{
         |   "costOfGoods": ${maybeNegative}1003.99,
         |   "paymentsToSubcontractors": ${maybeNegative}1004.99,
         |   "wagesAndStaffCosts": ${maybeNegative}1005.99,
         |   "carVanTravelExpenses": ${maybeNegative}1006.99,
         |   "premisesRunningCosts": ${maybeNegative}1007.99,
         |   "maintenanceCosts": ${maybeNegative}1008.99,
         |   "adminCosts": ${maybeNegative}1009.99,
         |   "businessEntertainmentCosts": ${maybeNegative}1010.99,
         |   "advertisingCosts": ${maybeNegative}1011.99,
         |   "interestOnBankOtherLoans": ${maybeNegative}1012.99,
         |   "financeCharges": ${maybeNegative}1013.99,
         |   "irrecoverableDebts": ${maybeNegative}1014.99,
         |   "professionalFees": ${maybeNegative}1015.99,
         |   "depreciation": ${maybeNegative}1016.99,
         |   "otherExpenses": ${maybeNegative}1017.99
         |}
         |""".stripMargin)
  }

  private def validPeriodDisallowableExpenses(withNegatives: Boolean = false): JsValue = {
    val maybeNegative = if (withNegatives) "-" else ""

    Json.parse(
      s"""
         |{
         |   "costOfGoodsDisallowable": ${maybeNegative}1018.99,
         |   "paymentsToSubcontractorsDisallowable": ${maybeNegative}1019.99,
         |   "wagesAndStaffCostsDisallowable": ${maybeNegative}1020.99,
         |   "carVanTravelExpensesDisallowable": ${maybeNegative}1021.99,
         |   "premisesRunningCostsDisallowable": ${maybeNegative}1022.99,
         |   "maintenanceCostsDisallowable": ${maybeNegative}1023.99,
         |   "adminCostsDisallowable": ${maybeNegative}1024.99,
         |   "businessEntertainmentCostsDisallowable": ${maybeNegative}1025.99,
         |   "advertisingCostsDisallowable": ${maybeNegative}1026.99,
         |   "interestOnBankOtherLoansDisallowable": ${maybeNegative}1027.99,
         |   "financeChargesDisallowable": ${maybeNegative}1028.99,
         |   "irrecoverableDebtsDisallowable": ${maybeNegative}1029.99,
         |   "professionalFeesDisallowable": ${maybeNegative}1030.99,
         |   "depreciationDisallowable": ${maybeNegative}1031.99,
         |   "otherExpensesDisallowable": ${maybeNegative}1032.99
         |}
         |""".stripMargin)
  }

  private def validBody(periodDates: JsValue = validPeriodDates,
                        periodIncome: JsValue = validPeriodIncome,
                        periodExpenses: JsValue = validPeriodExpenses(),
                        periodDisallowableExpenses: JsValue = validPeriodDisallowableExpenses()): JsObject =
    Json.obj(
      "periodDates" -> periodDates,
      "periodIncome" -> periodIncome,
      "periodExpenses" -> periodExpenses,
      "periodDisallowableExpenses" -> periodDisallowableExpenses
    )

  private val validBodyWithNegatives =
    validBody(
      periodExpenses = validPeriodExpenses(withNegatives = true),
      periodDisallowableExpenses = validPeriodDisallowableExpenses(withNegatives = true))

  private val validBodyConsolidated = validBody()
    .removeProperty("/periodDisallowableExpenses")
    .replaceWithEmptyObject("/periodExpenses")
    .update("/periodExpenses", JsObject(List(("consolidatedExpenses", JsString("999999999.99")))))

  private val parsedNino = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)

  private val parsedPeriodDates = Def2_Create_PeriodDates("2019-08-24", "2020-08-24")
  private val parsedPeriodIncome = Def2_Create_PeriodIncome(Some(1000.99), Some(1001.99), taxTakenOffTradingIncome = Some(1002.99))

  private def numericValue(isNegative: Boolean)(number: BigDecimal): BigDecimal =
    if (isNegative) -1 * number else number

  // @formatter:off
  private def parsedPeriodExpenses(withNegatives: Boolean = false) = {
    val number = numericValue(withNegatives)(_)

    Def2_Create_PeriodExpenses(
      None, Some(number(1003.99)), Some(number(1004.99)), Some(number(1005.99)),
      Some(number(1006.99)), Some(number(1007.99)), Some(number(1008.99)), Some(number(1009.99)),
      Some(number(1010.99)), Some(number(1011.99)), Some(number(1012.99)), Some(number(1013.99)),
      Some(number(1014.99)), Some(number(1015.99)), Some(number(1016.99)), Some(number(1017.99))
    )
  }

  private val parsedPeriodExpensesConsolidated = Def2_Create_PeriodExpenses(
    Some(999999999.99),
    None, None, None, None,
    None, None, None, None,
    None, None, None, None,
    None, None, None
  )

  private def parsedPeriodDisallowableExpenses(withNegatives: Boolean = false) = {
    val number = numericValue(withNegatives)(_)

    Def2_Create_PeriodDisallowableExpenses(
      Some(number(1018.99)), Some(number(1019.99)), Some(number(1020.99)),
      Some(number(1021.99)), Some(number(1022.99)), Some(number(1023.99)), Some(number(1024.99)),
      Some(number(1025.99)), Some(number(1026.99)), Some(number(1027.99)), Some(number(1028.99)),
      Some(number(1029.99)), Some(number(1030.99)), Some(number(1031.99)), Some(number(1032.99))
    )
  }
  // @formatter:on

  private def parsedBody(periodDates: Def2_Create_PeriodDates = parsedPeriodDates,
                         periodIncome: Option[Def2_Create_PeriodIncome] = Some(parsedPeriodIncome),
                         periodExpenses: Option[Def2_Create_PeriodExpenses] = Some(parsedPeriodExpenses()),
                         periodDisallowableExpenses: Option[Def2_Create_PeriodDisallowableExpenses] = Some(parsedPeriodDisallowableExpenses())) =
    Def2_CreatePeriodSummaryRequestBody(periodDates, periodIncome, periodExpenses, periodDisallowableExpenses)

  private def validator(nino: String, businessId: String, body: JsValue, includeNegatives: Boolean = false) =
    new Def2_CreatePeriodSummaryValidator(nino, businessId, body, includeNegatives)(mockAppConfig)

  private def setupMocks(): Unit =
    MockAppConfig.featureSwitchConfig.returns(Configuration("cl290.enabled" -> true)).anyNumberOfTimes()

  "validator" should {
    "return the parsed domain object" when {
      "given a valid request" in {
        setupMocks()

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validBody()).validateAndWrapResult()

        result shouldBe Right(Def2_CreatePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedBody()))
      }

      "given a valid request with negative expenses and includeNegatives is enabled" in {
        setupMocks()

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] = {
          validator(
            validNino,
            validBusinessId,
            validBody(
              periodExpenses = validPeriodExpenses(withNegatives = true),
              periodDisallowableExpenses = validPeriodDisallowableExpenses(withNegatives = true)),
            includeNegatives = true
          ).validateAndWrapResult()
        }

        result shouldBe Right(
          Def2_CreatePeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedBody(
              periodExpenses = Some(parsedPeriodExpenses(withNegatives = true)),
              periodDisallowableExpenses = Some(parsedPeriodDisallowableExpenses(withNegatives = true)))
          ))
      }

      "given a valid request with consolidated expenses" in {
        setupMocks()

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validBodyConsolidated).validateAndWrapResult()

        result shouldBe Right(
          Def2_CreatePeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedBody(periodExpenses = Some(parsedPeriodExpensesConsolidated), periodDisallowableExpenses = None)))
      }

      "given a valid request a body containing the minimum fields" in {
        setupMocks()

        val body = validBody()
          .removeProperty("/periodIncome")
          .removeProperty("/periodExpenses")
          .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, body).validateAndWrapResult()

        result shouldBe Right(
          Def2_CreatePeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedBody(periodIncome = None, periodExpenses = None, periodDisallowableExpenses = None)))
      }

      "given a valid request a body containing only period dates and period incomes" in {
        setupMocks()

        val body = validBody()
          .removeProperty("/periodExpenses")
          .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, body).validateAndWrapResult()

        result shouldBe Right(
          Def2_CreatePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedBody(periodExpenses = None, periodDisallowableExpenses = None)))
      }

      "given a valid request a body without period disallowable expenses" in {
        setupMocks()

        val body = validBody()
          .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, body).validateAndWrapResult()

        result shouldBe Right(Def2_CreatePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedBody(periodDisallowableExpenses = None)))
      }

      "given a valid request a body without period allowable expenses" in {
        setupMocks()

        val body = validBody()
          .removeProperty("/periodExpenses")

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, body).validateAndWrapResult()

        result shouldBe Right(Def2_CreatePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedBody(periodExpenses = None)))
      }
    }

    "return a single error" when {
      "given an invalid nino" in {
        setupMocks()

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator("invalid", validBusinessId, validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "given an invalid business id" in {
        setupMocks()

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, "invalid", validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BusinessIdFormatError))
      }

      "given an empty body" in {
        val invalidBody = JsObject.empty
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError))
      }

      def test(error: MtdError)(invalidBody: JsValue, path: String, withNegatives: Boolean = false): Unit =
        s"return $error when given an invalid value for $path with negatives ${if (withNegatives) "enabled" else "disabled"}" in {
          val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
            validator(validNino, validBusinessId, invalidBody, withNegatives).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, error))
        }

      List(
        "/periodIncome",
        "/periodIncome/turnover",
        "/periodIncome/other",
        "/periodExpenses",
        "/periodExpenses/costOfGoods",
        "/periodExpenses/paymentsToSubcontractors",
        "/periodExpenses/wagesAndStaffCosts",
        "/periodDisallowableExpenses",
        "/periodDisallowableExpenses/costOfGoodsDisallowable",
        "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable"
      ).foreach(path => test(RuleIncorrectOrEmptyBodyError.withPath(path))(validBody().replaceWithEmptyObject(path), path))

      List(
        "/periodIncome/turnover",
        "/periodIncome/other",
        "/periodExpenses/paymentsToSubcontractors",
        "/periodExpenses/wagesAndStaffCosts",
        "/periodExpenses/carVanTravelExpenses",
        "/periodExpenses/adminCosts",
        "/periodExpenses/businessEntertainmentCosts",
        "/periodExpenses/advertisingCosts",
        "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable",
        "/periodDisallowableExpenses/wagesAndStaffCostsDisallowable",
        "/periodDisallowableExpenses/carVanTravelExpensesDisallowable",
        "/periodDisallowableExpenses/adminCostsDisallowable",
        "/periodDisallowableExpenses/businessEntertainmentCostsDisallowable",
        "/periodDisallowableExpenses/professionalFeesDisallowable",
        "/periodDisallowableExpenses/advertisingCostsDisallowable",
        "/periodDisallowableExpenses/otherExpensesDisallowable"
      ).foreach(path =>
        test(ValueFormatError.forPathAndRange(path, min = "0", max = "99999999999.99"))(
          validBody().update(path, JsNumber(99999999999.99 + 0.01)),
          path))

      List(
        "/periodExpenses/costOfGoods",
        "/periodExpenses/premisesRunningCosts",
        "/periodExpenses/maintenanceCosts",
        "/periodExpenses/interestOnBankOtherLoans",
        "/periodExpenses/financeCharges",
        "/periodExpenses/irrecoverableDebts",
        "/periodExpenses/professionalFees",
        "/periodExpenses/depreciation",
        "/periodExpenses/otherExpenses",
        "/periodDisallowableExpenses/costOfGoodsDisallowable",
        "/periodDisallowableExpenses/premisesRunningCostsDisallowable",
        "/periodDisallowableExpenses/maintenanceCostsDisallowable",
        "/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable",
        "/periodDisallowableExpenses/financeChargesDisallowable",
        "/periodDisallowableExpenses/irrecoverableDebtsDisallowable",
        "/periodDisallowableExpenses/depreciationDisallowable"
      ).foreach(path =>
        test(ValueFormatError.forPathAndRange(path, min = "-99999999999.99", max = "99999999999.99"))(
          validBody().update(path, JsNumber(99999999999.99 + 0.01)),
          path))

      List(
        "/periodIncome/turnover",
        "/periodIncome/other",
        "/periodExpenses/adminCosts",
        "/periodExpenses/advertisingCosts",
        "/periodExpenses/businessEntertainmentCosts",
        "/periodExpenses/carVanTravelExpenses",
        "/periodExpenses/costOfGoods",
        "/periodExpenses/paymentsToSubcontractors",
        "/periodExpenses/wagesAndStaffCosts",
        "/periodDisallowableExpenses/adminCostsDisallowable",
        "/periodDisallowableExpenses/advertisingCostsDisallowable",
        "/periodDisallowableExpenses/businessEntertainmentCostsDisallowable",
        "/periodDisallowableExpenses/carVanTravelExpensesDisallowable",
        "/periodDisallowableExpenses/costOfGoodsDisallowable",
        "/periodDisallowableExpenses/otherExpensesDisallowable",
        "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable",
        "/periodDisallowableExpenses/professionalFeesDisallowable",
        "/periodDisallowableExpenses/wagesAndStaffCostsDisallowable"
      ).foreach(path =>
        test(ValueFormatError.forPathAndRange(path, min = "-99999999999.99", max = "99999999999.99"))(
          validBodyWithNegatives.update(path, JsNumber(123.456)),
          path,
          withNegatives = true))

      test(ValueFormatError.forPathAndRange("/periodExpenses/consolidatedExpenses", min = "0", max = "99999999999.99"))(
        validBodyConsolidated.update("/periodExpenses/consolidatedExpenses", JsNumber(99999999999.99 + 0.01)),
        "/periodExpenses/consolidatedExpenses"
      )

      "given a body with an invalid periodStartDate" in {
        val invalidBody = validBody().update("/periodDates/periodStartDate", JsString("2019-08-025"))
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, StartDateFormatError))
      }

      "given a body with an invalid periodEndDate" in {
        val invalidBody = validBody().update("/periodDates/periodEndDate", JsString("2019-08-025"))
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, EndDateFormatError))
      }

      "given a body with a period start date out of range" in {
        val invalidBody = validBody().update("/periodDates/periodStartDate", JsString("0010-01-01"))
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))
      }

      "given a body with a period end date out of range" in {
        val invalidBody = validBody().update("/periodDates/periodEndDate", JsString("2025-04-06"))
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))
      }

      "given a body with a period end date before period start date" in {
        val invalidBody = validBody()
          .update("/periodDates/periodStartDate", JsString("2020-08-25"))
          .update("/periodDates/periodEndDate", JsString("2019-08-24"))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleEndBeforeStartDateError))
      }

      "given a body with invalid periodStartDate and periodEndDate" in {
        val invalidBody = validBody()
          .update("/periodDates/periodStartDate", JsString("invalid"))
          .update("/periodDates/periodEndDate", JsString("invalid"))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BadRequestError, Some(List(EndDateFormatError, StartDateFormatError))))
      }

      "given a body with expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "given a body with disallowable expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .removeProperty("/periodExpenses")
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "given a body with allowable expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .removeProperty("/periodDisallowableExpenses")
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

    }

    "return multiple errors" when {
      "invalid parameters are supplied" in {
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator("invalid", "invalid", validBody()).validateAndWrapResult()

        result shouldBe Left(
          ErrorWrapper(
            correlationId,
            BadRequestError,
            Some(List(BusinessIdFormatError, NinoFormatError))
          )
        )
      }
    }
  }

}
