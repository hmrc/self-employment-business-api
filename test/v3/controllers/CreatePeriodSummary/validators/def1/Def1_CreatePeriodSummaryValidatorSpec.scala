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

package v3.controllers.CreatePeriodSummary.validators.def1

import api.models.domain.{BusinessId, Nino}
import api.models.errors._
import api.models.utils.JsonErrorValidators
import mocks.MockAppConfig
import play.api.libs.json._
import support.UnitSpec
import v3.models.request.createPeriodSummary._
import v3.models.request.createPeriodSummary.def1._

class Def1_CreatePeriodSummaryValidatorSpec extends UnitSpec with JsonErrorValidators with MockAppConfig {

  private implicit val correlationId: String = "1234"

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"

  private val validPeriodDates = Json.parse("""
      |{
      |  "periodStartDate": "2019-08-24",
      |  "periodEndDate": "2020-08-24"
      |}
      |""".stripMargin)

  private val validPeriodIncome = Json.parse("""
      |{
      |   "turnover": 1000.99,
      |   "other": 1001.99,
      |   "taxTakenOffTradingIncome": 1002.99
      |}
      |""".stripMargin)

  private def validPeriodExpenses(withNegatives: Boolean = false) = {
    val maybeNegative = if (withNegatives) "-" else ""

    Json.parse(s"""
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

  private def validPeriodDisallowableExpenses(withNegatives: Boolean = false) = {
    val maybeNegative = if (withNegatives) "-" else ""

    Json.parse(s"""
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
                        periodDisallowableExpenses: JsValue = validPeriodDisallowableExpenses()) =
    Json.obj(
      "periodDates"                -> periodDates,
      "periodIncome"               -> periodIncome,
      "periodExpenses"             -> periodExpenses,
      "periodDisallowableExpenses" -> periodDisallowableExpenses
    )

  private val validBodyWithNegatives =
    validBody(periodExpenses = validPeriodExpenses(true), periodDisallowableExpenses = validPeriodDisallowableExpenses(true))

  private val validBodyConsolidated = validBody()
    .removeProperty("/periodDisallowableExpenses")
    .replaceWithEmptyObject("/periodExpenses")
    .update("/periodExpenses", JsObject(List(("consolidatedExpenses", JsString("999999999.99")))))

  private val parsedNino       = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)

  private val parsedPeriodDates  = Def1_Create_PeriodDates("2019-08-24", "2020-08-24")
  private val parsedPeriodIncome = Def1_Create_PeriodIncome(Some(1000.99), Some(1001.99))

  private def numericValue(isNegative: Boolean)(number: BigDecimal): BigDecimal =
    if (isNegative) -1 * number else number

  // @formatter:off
  private def parsedPeriodExpenses(withNegatives: Boolean = false) = {
    val number = numericValue(withNegatives)(_)

    Def1_Create_PeriodExpenses(
      None, Some(number(1003.99)), Some(number(1004.99)), Some(number(1005.99)),
      Some(number(1006.99)), Some(number(1007.99)), Some(number(1008.99)), Some(number(1009.99)),
      Some(number(1010.99)), Some(number(1011.99)), Some(number(1012.99)), Some(number(1013.99)),
      Some(number(1014.99)), Some(number(1015.99)), Some(number(1016.99)), Some(number(1017.99))
    )
  }

  private val parsedPeriodExpensesConsolidated = Def1_Create_PeriodExpenses(
    Some(999999999.99),
    None, None, None, None,
    None, None, None, None,
    None, None, None, None,
    None, None, None
  )

  private def parsedPeriodDisallowableExpenses(withNegatives: Boolean = false) = {
    val number = numericValue(withNegatives)(_)

    Def1_Create_PeriodDisallowableExpenses(
      Some(number(1018.99)), Some(number(1019.99)), Some(number(1020.99)),
      Some(number(1021.99)), Some(number(1022.99)), Some(number(1023.99)), Some(number(1024.99)),
      Some(number(1025.99)), Some(number(1026.99)), Some(number(1027.99)), Some(number(1028.99)),
      Some(number(1029.99)), Some(number(1030.99)), Some(number(1031.99)), Some(number(1032.99))
    )
  }
  // @formatter:on

  private def parsedBody(periodDates: Def1_Create_PeriodDates = parsedPeriodDates,
                         periodIncome: Option[Def1_Create_PeriodIncome] = Some(parsedPeriodIncome),
                         periodExpenses: Option[Def1_Create_PeriodExpenses] = Some(parsedPeriodExpenses()),
                         periodDisallowableExpenses: Option[Def1_Create_PeriodDisallowableExpenses] = Some(parsedPeriodDisallowableExpenses())) =
    Def1_CreatePeriodSummaryRequestBody(periodDates, periodIncome, periodExpenses, periodDisallowableExpenses)

  // private val validatorFactory = new CreatePeriodSummaryValidatorFactory(appConfig)

  private def validator(nino: String, businessId: String, body: JsValue, includeNegatives: Boolean = false) = {
    new Def1_CreatePeriodSummaryValidator(nino, businessId, body, includeNegatives)
  }

  "validator" should {
    "return the parsed domain object" when {
      "passed a valid request" in {
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validBody()).validateAndWrapResult()

        result shouldBe Right(Def1_CreatePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedBody()))
      }

      "passed a valid request with negative expenses and includeNegatives is enabled" in {
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] = {
          validator(
            validNino,
            validBusinessId,
            validBody(periodExpenses = validPeriodExpenses(true), periodDisallowableExpenses = validPeriodDisallowableExpenses(true)),
            includeNegatives = true
          ).validateAndWrapResult()
        }

        result shouldBe Right(
          Def1_CreatePeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedBody(periodExpenses = Some(parsedPeriodExpenses(true)), periodDisallowableExpenses = Some(parsedPeriodDisallowableExpenses(true)))
          ))
      }

      "passed a valid request with consolidated expenses" in {
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validBodyConsolidated).validateAndWrapResult()

        result shouldBe Right(
          Def1_CreatePeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedBody(periodExpenses = Some(parsedPeriodExpensesConsolidated), periodDisallowableExpenses = None)))
      }

      "passed a valid request a body containing the minimum fields" in {
        val body = validBody()
          .removeProperty("/periodIncome")
          .removeProperty("/periodExpenses")
          .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, body).validateAndWrapResult()

        result shouldBe Right(
          Def1_CreatePeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedBody(periodIncome = None, periodExpenses = None, periodDisallowableExpenses = None)))
      }

      "passed a valid request a body containing only period dates and period incomes" in {
        val body = validBody()
          .removeProperty("/periodExpenses")
          .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, body).validateAndWrapResult()

        result shouldBe Right(
          Def1_CreatePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedBody(periodExpenses = None, periodDisallowableExpenses = None)))
      }

      "passed a valid request a body without period disallowable expenses" in {
        val body = validBody()
          .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, body).validateAndWrapResult()

        result shouldBe Right(Def1_CreatePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedBody(periodDisallowableExpenses = None)))
      }

      "passed a valid request a body without period allowable expenses" in {
        val body = validBody()
          .removeProperty("/periodExpenses")

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, body).validateAndWrapResult()

        result shouldBe Right(Def1_CreatePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedBody(periodExpenses = None)))
      }
    }

    "return a single error" when {
      "passed an invalid nino is supplied" in {
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator("invalid", validBusinessId, validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "passed an invalid business id is supplied" in {
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, "invalid", validBody()).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BusinessIdFormatError))
      }

      "passed an empty body" in {
        val invalidBody = JsObject.empty
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError))
      }

      def test(error: MtdError)(invalidBody: JsValue, path: String, withNegatives: Boolean = false): Unit =
        s"return $error when passed an invalid value for $path with negatives ${if (withNegatives) "enabled" else "disabled"}" in {
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

      "passed a body with an invalid periodStartDate" in {
        val invalidBody = validBody().update("/periodDates/periodStartDate", JsString("2019-08-025"))
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, StartDateFormatError))
      }

      "passed a body with an invalid periodEndDate" in {
        val invalidBody = validBody().update("/periodDates/periodEndDate", JsString("2019-08-025"))
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, EndDateFormatError))
      }

      "passed a body with a period start date out of range" in {
        val invalidBody = validBody().update("/periodDates/periodStartDate", JsString("0010-01-01"))
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, StartDateFormatError))
      }

      "passed a body with a period end date out of range" in {
        val invalidBody = validBody().update("/periodDates/periodEndDate", JsString("2101-01-01"))
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, EndDateFormatError))
      }

      "passed a body with a period end date before period start date" in {
        val invalidBody = validBody()
          .update("/periodDates/periodStartDate", JsString("2020-08-25"))
          .update("/periodDates/periodEndDate", JsString("2019-08-24"))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleEndBeforeStartDateError))
      }

      "passed a body with invalid periodStartDate and periodEndDate" in {
        val invalidBody = validBody()
          .update("/periodDates/periodStartDate", JsString("invalid"))
          .update("/periodDates/periodEndDate", JsString("invalid"))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BadRequestError, Some(List(EndDateFormatError, StartDateFormatError))))
      }

      "passed a body with expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "passed a body with disallowable expenses and consolidated expenses" in {
        val invalidBody = validBody()
          .removeProperty("/periodExpenses")
          .update("/periodExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "passed a body with allowable expenses and consolidated expenses" in {
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
