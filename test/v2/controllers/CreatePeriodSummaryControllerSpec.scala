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

package v2.controllers

import shared.controllers.{ControllerBaseSpec, ControllerTestRunner}
import shared.hateoas.Method.{GET, PUT}
import shared.hateoas.{HateoasWrapper, Link, MockHateoasFactory}
import shared.models.audit.{AuditEvent, AuditResponse, GenericAuditDetail}
import shared.models.domain.{BusinessId, Nino, TaxYear}
import shared.models.errors._
import shared.models.outcomes.ResponseWrapper
import shared.services.MockAuditService
import play.api.Configuration
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.Result
import shared.config.MockAppConfig
import v2.controllers.validators.MockCreatePeriodSummaryValidatorFactory
import v2.models.request.createPeriodSummary._
import v2.models.response.createPeriodSummary.{CreatePeriodSummaryHateoasData, CreatePeriodSummaryResponse}
import v2.services.MockCreatePeriodSummaryService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CreatePeriodSummaryControllerSpec
    extends ControllerBaseSpec
    with ControllerTestRunner
    with MockCreatePeriodSummaryService
    with MockCreatePeriodSummaryValidatorFactory
    with MockAppConfig
    with MockHateoasFactory
    with MockAuditService {

  private val businessId = "XAIS12345678910"
  private val periodId   = "2017-01-25_2017-01-25"

  private val testHateoasLinks: Seq[Link] = List(
    Link(s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", PUT, "amend-self-employment-period-summary"),
    Link(s"/individuals/business/self-employment/$nino/$businessId/period/$periodId", GET, "self"),
    Link(s"/individuals/business/self-employment/$nino/$businessId/period", GET, "list-self-employment-period-summaries")
  )

  private val requestJson = Json.parse(
    """
      |{
      |     "periodDates": {
      |           "periodStartDate": "2019-08-24",
      |           "periodEndDate": "2019-08-24"
      |     },
      |     "periodIncome": {
      |          "turnover": 1000.99,
      |          "other": 1000.99
      |     },
      |     "periodAllowableExpenses": {
      |          "costOfGoodsAllowable": 1000.99,
      |          "paymentsToSubcontractorsAllowable": 1000.99,
      |          "wagesAndStaffCostsAllowable": 1000.99,
      |          "carVanTravelExpensesAllowable": 1000.99,
      |          "premisesRunningCostsAllowable": -99999.99,
      |          "maintenanceCostsAllowable": -1000.99,
      |          "adminCostsAllowable": 1000.99,
      |          "businessEntertainmentCostsAllowable": 1000.99,
      |          "advertisingCostsAllowable": 1000.99,
      |          "interestOnBankOtherLoansAllowable": -1000.99,
      |          "financeChargesAllowable": -1000.99,
      |          "irrecoverableDebtsAllowable": -1000.99,
      |          "professionalFeesAllowable": -99999999999.99,
      |          "depreciationAllowable": -1000.99,
      |          "otherExpensesAllowable": 1000.99
      |      },
      |     "periodDisallowableExpenses": {
      |          "costOfGoodsDisallowable": 1000.99,
      |          "paymentsToSubcontractorsDisallowable": 1000.99,
      |          "wagesAndStaffCostsDisallowable": 1000.99,
      |          "carVanTravelExpensesDisallowable": 1000.99,
      |          "premisesRunningCostsDisallowable": -1000.99,
      |          "maintenanceCostsDisallowable": -999.99,
      |          "adminCostsDisallowable": 1000.99,
      |          "businessEntertainmentCostsDisallowable": 1000.99,
      |          "advertisingCostsDisallowable": 1000.99,
      |          "interestOnBankOtherLoansDisallowable": -1000.99,
      |          "financeChargesDisallowable": -9999.99,
      |          "irrecoverableDebtsDisallowable": -1000.99,
      |          "professionalFeesDisallowable": -99999999999.99,
      |          "depreciationDisallowable": -99999999999.99,
      |          "otherExpensesDisallowable": 1000.99
      |      }
      |}
    """.stripMargin
  )

  private val requestBody: CreatePeriodSummaryRequestBody =
    CreatePeriodSummaryRequestBody(
      PeriodDates("2019-08-24", "2019-08-24"),
      Some(
        PeriodIncome(
          Some(1000.99),
          Some(1000.99)
        )),
      Some(
        PeriodExpenses(
          None,
          Some(1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(-99999.99),
          Some(-1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(-1000.99),
          Some(-1000.99),
          Some(-1000.99),
          Some(-99999999999.99),
          Some(-1000.99),
          Some(1000.99)
        )),
      Some(
        PeriodDisallowableExpenses(
          Some(1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(-1000.99),
          Some(-999.99),
          Some(1000.99),
          Some(1000.99),
          Some(1000.99),
          Some(-1000.99),
          Some(-9999.99),
          Some(-1000.99),
          Some(-99999999999.99),
          Some(-99999999999.99),
          Some(1000.99)
        ))
    )

  val responseJson: JsValue = Json.parse(
    s"""
       |{
       |  "periodId": "$periodId",
       |  "links": [
       |    {
       |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
       |      "method": "PUT",
       |      "rel": "amend-self-employment-period-summary"
       |
       |    },
       |    {
       |      "href": "/individuals/business/self-employment/$nino/$businessId/period/$periodId",
       |      "method": "GET",
       |      "rel": "self"
       |
       |    },
       |    {
       |      "href": "/individuals/business/self-employment/$nino/$businessId/period",
       |      "method": "GET",
       |      "rel": "list-self-employment-period-summaries"
       |
       |    }
       |  ]
       |}
    """.stripMargin
  )

  private val requestData = CreatePeriodSummaryRequestData(Nino(nino), BusinessId(businessId), requestBody)

  "handleRequest" should {
    "return a successful response with status 200 (OK)" when {
      "the request received is valid" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockCreatePeriodicService
          .createPeriodic(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, CreatePeriodSummaryResponse(periodId)))))

        MockHateoasFactory
          .wrap(
            CreatePeriodSummaryResponse(periodId),
            CreatePeriodSummaryHateoasData(Nino(nino), BusinessId(businessId), periodId, Some(TaxYear.fromMtd("2019-20"))))
          .returns(HateoasWrapper(CreatePeriodSummaryResponse(periodId), testHateoasLinks))

        runOkTestWithAudit(
          expectedStatus = OK,
          maybeAuditRequestBody = Some(requestJson),
          maybeExpectedResponseBody = Some(responseJson),
          maybeAuditResponseBody = None
        )
      }
    }

    "return the error as per spec" when {
      "the parser validation fails" in new Test {
        willUseValidator(returning(NinoFormatError))

        runErrorTest(NinoFormatError)

      }

      "the service returns an error" in new Test {
        willUseValidator(returningSuccess(requestData))

        MockCreatePeriodicService
          .createPeriodic(requestData)
          .returns(Future.successful(Left(ErrorWrapper(correlationId, RuleTaxYearNotSupportedError))))

        runErrorTest(RuleTaxYearNotSupportedError)
      }
    }
  }

  private trait Test extends ControllerTest with AuditEventChecking {
    MockAppConfig.featureSwitchConfig.returns(Configuration("allowNegativeExpenses.enabled" -> false)).anyNumberOfTimes()

    val controller = new CreatePeriodSummaryController(
      authService = mockEnrolmentsAuthService,
      lookupService = mockMtdIdLookupService,
      validatorFactory = mockCreatePeriodSummaryValidatorFactory,
      service = mockCreatePeriodicService,
      auditService = mockAuditService,
      hateoasFactory = mockHateoasFactory,
      cc = cc,
      idGenerator = mockIdGenerator
    )

    protected def event(auditResponse: AuditResponse, requestBody: Option[JsValue]): AuditEvent[GenericAuditDetail] =
      AuditEvent(
        auditType = "CreatePeriodicEmployment",
        transactionName = "self-employment-periodic-create",
        detail = GenericAuditDetail(
          versionNumber = "2.0",
          userType = "Individual",
          agentReferenceNumber = None,
          params = Map("nino" -> nino, "businessId" -> businessId),
          requestBody = requestBody,
          `X-CorrelationId` = correlationId,
          auditResponse = auditResponse
        )
      )

    protected def callController(): Future[Result] = controller.handleRequest(nino, businessId)(fakePostRequest(requestJson))
  }

}
