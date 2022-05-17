/*
 * Copyright 2022 HM Revenue & Customs
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

package v1.services

//import v1.controllers.EndpointLogContext
//import v1.mocks.connectors.MockCreatePeriodicConnector
//import v1.models.domain.Nino
//import v1.models.errors._
//import v1.models.outcomes.ResponseWrapper
//import v1.models.request.createPeriodic._
//import v1.models.response.createPeriodic.CreatePeriodicResponse
//
//import scala.concurrent.Future

//class CreatePeriodicServiceSpec extends ServiceSpec {
//
//  val nino: String                   = "AA123456A"
//  val businessId: String             = "XAIS12345678910"
//  implicit val correlationId: String = "X-123"
//
//  private val requestBody = CreatePeriodicBody(
//    "2017-01-25",
//    "2017-01-25",
//    Some(
//      Incomes(
//        Some(IncomesAmountObject(500.12)),
//        Some(IncomesAmountObject(500.12))
//      )),
//    Some(ConsolidatedExpenses(500.12)),
//    Some(
//      Expenses(
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12))),
//        Some(ExpensesAmountObject(500.12, Some(500.12)))
//      ))
//  )
//
//  private val requestData = CreatePeriodicRequest(
//    nino = Nino(nino),
//    businessId = businessId,
//    body = requestBody
//  )
//
//  trait Test extends MockCreatePeriodicConnector {
//    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")
//
//    val service = new CreatePeriodicService(
//      connector = mockCreatePeriodicConnector
//    )
//
//  }
//
//  "CreateSEPeriodic" when {
//    "createPeriodicSummary" must {
//      "return correct result for a success" in new Test {
//        val connectorOutcome = Right(ResponseWrapper(correlationId, ()))
//        val outcome          = Right(ResponseWrapper(correlationId, CreatePeriodicResponse("2017-01-25_2017-01-25")))
//
//        MockCreatePeriodicConnector
//          .createPeriodicSummary(requestData)
//          .returns(Future.successful(connectorOutcome))
//
//        await(service.createPeriodicSummary(requestData)) shouldBe outcome
//      }
//
//      "map errors according to spec" when {
//        def serviceError(desErrorCode: String, error: MtdError): Unit =
//          s"a $desErrorCode error is returned from the service" in new Test {
//
//            MockCreatePeriodicConnector
//              .createPeriodicSummary(requestData)
//              .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))
//
//            await(service.createPeriodicSummary(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
//          }
//
//        val input = Seq(
//          ("INVALID_NINO", NinoFormatError),
//          ("INVALID_INCOME_SOURCE", BusinessIdFormatError),
//          ("INVALID_PERIOD", RuleToDateBeforeFromDateError),
//          ("OVERLAPS_IN_PERIOD", RuleOverlappingPeriod),
//          ("NOT_ALIGN_PERIOD", RuleMisalignedPeriod),
//          ("BOTH_EXPENSES_SUPPLIED", RuleBothExpensesSuppliedError),
//          ("NOT_CONTIGUOUS_PERIOD", RuleNotContiguousPeriod),
//          ("NOT_ALLOWED_SIMPLIFIED_EXPENSES", RuleNotAllowedConsolidatedExpenses),
//          ("NOT_FOUND_INCOME_SOURCE", NotFoundError),
//          ("SERVER_ERROR", DownstreamError),
//          ("SERVICE_UNAVAILABLE", DownstreamError)
//        )
//
//        input.foreach(args => (serviceError _).tupled(args))
//      }
//    }
//  }
//
//}
