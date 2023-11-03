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

import api.models.domain.{BusinessId, Nino}
import api.models.errors._
import api.models.utils.JsonErrorValidators
import play.api.libs.json._
import support.UnitSpec
import v1.models.request.createPeriodSummary._

class CreatePeriodSummaryValidatorFactorySpec extends UnitSpec with JsonErrorValidators {

  private implicit val correlationId: String = "1234"

  private val validNino       = "AA123456A"
  private val validBusinessId = "XAIS12345678901"

  private val validBody = Json.parse(
    """
      |{
      |   "periodDates": {
      |     "periodStartDate": "2019-08-24",
      |     "periodEndDate": "2020-08-24"
      |    },
      |    "periodIncome": {
      |      "turnover": 1000.99,
      |      "other": 1000.99
      |    },
      |    "periodAllowableExpenses": {
      |      "costOfGoodsAllowable": 1000.99,
      |      "paymentsToSubcontractorsAllowable": 1000.99,
      |      "wagesAndStaffCostsAllowable": 1000.99,
      |      "carVanTravelExpensesAllowable": 1000.99,
      |      "premisesRunningCostsAllowable": -99999.99,
      |      "maintenanceCostsAllowable": -1000.99,
      |      "adminCostsAllowable": 1000.99,
      |      "businessEntertainmentCostsAllowable": 1000.99,
      |      "advertisingCostsAllowable": 1000.99,
      |      "interestOnBankOtherLoansAllowable": -1000.99,
      |      "financeChargesAllowable": -1000.99,
      |      "irrecoverableDebtsAllowable": -1000.99,
      |      "professionalFeesAllowable": -99999999999.99,
      |      "depreciationAllowable": -1000.99,
      |      "otherExpensesAllowable": 1000.99
      |    },
      |    "periodDisallowableExpenses": {
      |      "costOfGoodsDisallowable": 1000.99,
      |      "paymentsToSubcontractorsDisallowable": 1000.99,
      |      "wagesAndStaffCostsDisallowable": 1000.99,
      |      "carVanTravelExpensesDisallowable": 1000.99,
      |      "premisesRunningCostsDisallowable": -1000.99,
      |      "maintenanceCostsDisallowable": -999.99,
      |      "adminCostsDisallowable": 1000.99,
      |      "businessEntertainmentCostsDisallowable": 1000.99,
      |      "advertisingCostsDisallowable": 1000.99,
      |      "interestOnBankOtherLoansDisallowable": -1000.99,
      |      "financeChargesDisallowable": -9999.99,
      |      "irrecoverableDebtsDisallowable": 1000.99,
      |      "professionalFeesDisallowable": 9999999999.99,
      |      "depreciationDisallowable": -99999999999.99,
      |      "otherExpensesDisallowable": 1000.99
      |     }
      |}
    """.stripMargin
  )

  private val validBodyConsolidated = validBody
    .removeProperty("/periodDisallowableExpenses")
    .replaceWithEmptyObject("/periodAllowableExpenses")
    .update("/periodAllowableExpenses", JsObject(List(("consolidatedExpenses", JsString("999999999.99")))))

  private val parsedNino       = Nino(validNino)
  private val parsedBusinessId = BusinessId(validBusinessId)

  private val parsedPeriodDates  = PeriodDates("2019-08-24", "2020-08-24")
  private val parsedPeriodIncome = PeriodIncome(Some(1000.99), Some(1000.99))

  // @formatter:off
  private val parsedPeriodAllowableExpenses = PeriodAllowableExpenses(
    None, Some(1000.99), Some(1000.99), Some(1000.99), Some(1000.99),
    Some(-99999.99), Some(-1000.99), Some(1000.99), Some(1000.99),
    Some(1000.99), Some(-1000.99), Some(-1000.99), Some(-1000.99),
    Some(-99999999999.99), Some(-1000.99), Some(1000.99)
  )

  private val parsedPeriodAllowableExpensesConsolidated = PeriodAllowableExpenses(
    Some(999999999.99),
    None, None, None, None,
    None, None, None, None,
    None, None, None, None,
    None, None, None
  )

  private val parsedPeriodDisallowableExpenses = PeriodDisallowableExpenses(
    Some(1000.99), Some(1000.99), Some(1000.99), Some(1000.99),
    Some(-1000.99), Some(-999.99), Some(1000.99), Some(1000.99),
    Some(1000.99), Some(-1000.99), Some(-9999.99), Some(1000.99),
    Some(9999999999.99), Some(-99999999999.99), Some(1000.99)
  )
  // @formatter:on

  private def parsedBody(periodDates: PeriodDates = parsedPeriodDates,
                         periodIncome: Option[PeriodIncome] = Some(parsedPeriodIncome),
                         periodAllowableExpenses: Option[PeriodAllowableExpenses] = Some(parsedPeriodAllowableExpenses),
                         periodDisallowableExpenses: Option[PeriodDisallowableExpenses] = Some(parsedPeriodDisallowableExpenses)) =
    CreatePeriodSummaryBody(periodDates, periodIncome, periodAllowableExpenses, periodDisallowableExpenses)

  private val validatorFactory = new CreatePeriodSummaryValidatorFactory

  private def validator(nino: String, businessId: String, body: JsValue) =
    validatorFactory.validator(nino, businessId, body)

  "validator" should {
    "return the parsed domain object" when {
      "passed a valid request" in {
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validBody).validateAndWrapResult()

        result shouldBe Right(CreatePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedBody()))
      }

      "passed a valid request with consolidated expenses" in {
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, validBodyConsolidated).validateAndWrapResult()

        result shouldBe Right(
          CreatePeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedBody(periodAllowableExpenses = Some(parsedPeriodAllowableExpensesConsolidated), periodDisallowableExpenses = None)))
      }

      "passed a valid request a body containing the minimum fields" in {
        val body = validBody
          .removeProperty("/periodIncome")
          .removeProperty("/periodAllowableExpenses")
          .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, body).validateAndWrapResult()

        result shouldBe Right(
          CreatePeriodSummaryRequestData(
            parsedNino,
            parsedBusinessId,
            parsedBody(periodIncome = None, periodAllowableExpenses = None, periodDisallowableExpenses = None)))
      }

      "passed a valid request a body containing only period dates and period incomes" in {
        val body = validBody
          .removeProperty("/periodAllowableExpenses")
          .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, body).validateAndWrapResult()

        result shouldBe Right(
          CreatePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedBody(periodAllowableExpenses = None, periodDisallowableExpenses = None)))
      }

      "passed a valid request a body without period disallowable expenses" in {
        val body = validBody
          .removeProperty("/periodDisallowableExpenses")

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, body).validateAndWrapResult()

        result shouldBe Right(CreatePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedBody(periodDisallowableExpenses = None)))
      }

      "passed a valid request a body without period allowable expenses" in {
        val body = validBody
          .removeProperty("/periodAllowableExpenses")

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, body).validateAndWrapResult()

        result shouldBe Right(CreatePeriodSummaryRequestData(parsedNino, parsedBusinessId, parsedBody(periodAllowableExpenses = None)))
      }
    }

    "return a single error" when {
      "passed an invalid nino is supplied" in {
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator("invalid", validBusinessId, validBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, NinoFormatError))
      }

      "passed an invalid business id is supplied" in {
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, "invalid", validBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BusinessIdFormatError))
      }

      "passed an empty body" in {
        val invalidBody = JsObject.empty
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleIncorrectOrEmptyBodyError))
      }

      def test(error: MtdError)(invalidBody: JsValue, path: String): Unit =
        s"return $error when passed an invalid value for $path" in {
          val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
            validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

          result shouldBe Left(ErrorWrapper(correlationId, error))
        }

      List(
        "/periodIncome",
        "/periodIncome/turnover",
        "/periodIncome/other",
        "/periodAllowableExpenses",
        "/periodAllowableExpenses/costOfGoodsAllowable",
        "/periodAllowableExpenses/paymentsToSubcontractorsAllowable",
        "/periodAllowableExpenses/wagesAndStaffCostsAllowable",
        "/periodDisallowableExpenses",
        "/periodDisallowableExpenses/costOfGoodsDisallowable",
        "/periodDisallowableExpenses/paymentsToSubcontractorsDisallowable"
      ).foreach(path => test(RuleIncorrectOrEmptyBodyError.withPath(path))(validBody.replaceWithEmptyObject(path), path))

      List(
        "/periodIncome/turnover",
        "/periodIncome/other",
        "/periodAllowableExpenses/paymentsToSubcontractorsAllowable",
        "/periodAllowableExpenses/wagesAndStaffCostsAllowable",
        "/periodAllowableExpenses/carVanTravelExpensesAllowable",
        "/periodAllowableExpenses/adminCostsAllowable",
        "/periodAllowableExpenses/businessEntertainmentCostsAllowable",
        "/periodAllowableExpenses/advertisingCostsAllowable",
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
          validBody.update(path, JsNumber(99999999999.99 + 0.01)),
          path))

      List(
        "/periodAllowableExpenses/costOfGoodsAllowable",
        "/periodAllowableExpenses/premisesRunningCostsAllowable",
        "/periodAllowableExpenses/maintenanceCostsAllowable",
        "/periodAllowableExpenses/interestOnBankOtherLoansAllowable",
        "/periodAllowableExpenses/financeChargesAllowable",
        "/periodAllowableExpenses/irrecoverableDebtsAllowable",
        "/periodAllowableExpenses/professionalFeesAllowable",
        "/periodAllowableExpenses/depreciationAllowable",
        "/periodAllowableExpenses/otherExpensesAllowable",
        "/periodDisallowableExpenses/costOfGoodsDisallowable",
        "/periodDisallowableExpenses/premisesRunningCostsDisallowable",
        "/periodDisallowableExpenses/maintenanceCostsDisallowable",
        "/periodDisallowableExpenses/interestOnBankOtherLoansDisallowable",
        "/periodDisallowableExpenses/financeChargesDisallowable",
        "/periodDisallowableExpenses/irrecoverableDebtsDisallowable",
        "/periodDisallowableExpenses/depreciationDisallowable"
      ).foreach(path =>
        test(ValueFormatError.forPathAndRange(path, min = "-99999999999.99", max = "99999999999.99"))(
          validBody.update(path, JsNumber(99999999999.99 + 0.01)),
          path))

      test(ValueFormatError.forPathAndRange("/periodAllowableExpenses/consolidatedExpenses", min = "0", max = "99999999999.99"))(
        validBodyConsolidated.update("/periodAllowableExpenses/consolidatedExpenses", JsNumber(99999999999.99 + 0.01)),
        "/periodAllowableExpenses/consolidatedExpenses"
      )

      "passed a body with an invalid periodStartDate" in {
        val invalidBody = validBody.update("/periodDates/periodStartDate", JsString("2019-08-025"))
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, StartDateFormatError))
      }

      "passed a body with an invalid periodEndDate" in {
        val invalidBody = validBody.update("/periodDates/periodEndDate", JsString("2019-08-025"))
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, EndDateFormatError))
      }

      "passed a body with a period start date out of range" in {
        val invalidBody = validBody.update("/periodDates/periodStartDate", JsString("0010-01-01"))
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, StartDateFormatError))
      }

      "passed a body with a period end date out of range" in {
        val invalidBody = validBody.update("/periodDates/periodEndDate", JsString("2101-01-01"))
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, EndDateFormatError))
      }

      "passed a body with a period end date before period start date" in {
        val invalidBody = validBody
          .update("/periodDates/periodStartDate", JsString("2020-08-25"))
          .update("/periodDates/periodEndDate", JsString("2019-08-24"))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleEndBeforeStartDateError))
      }

      "passed a body with invalid periodStartDate and periodEndDate" in {
        val invalidBody = validBody
          .update("/periodDates/periodStartDate", JsString("invalid"))
          .update("/periodDates/periodEndDate", JsString("invalid"))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, BadRequestError, Some(List(EndDateFormatError, StartDateFormatError))))
      }

      "passed a body with expenses and consolidated expenses" in {
        val invalidBody = validBody
          .update("/periodAllowableExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "passed a body with disallowable expenses and consolidated expenses" in {
        val invalidBody = validBody
          .removeProperty("/periodAllowableExpenses")
          .update("/periodAllowableExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

      "passed a body with allowable expenses and consolidated expenses" in {
        val invalidBody = validBody
          .removeProperty("/periodDisallowableExpenses")
          .update("/periodAllowableExpenses/consolidatedExpenses", JsNumber(1000.99))

        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator(validNino, validBusinessId, invalidBody).validateAndWrapResult()

        result shouldBe Left(ErrorWrapper(correlationId, RuleBothExpensesSuppliedError))
      }

    }

    "return multiple errors" when {
      "invalid parameters are supplied" in {
        val result: Either[ErrorWrapper, CreatePeriodSummaryRequestData] =
          validator("invalid", "invalid", validBody).validateAndWrapResult()

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
