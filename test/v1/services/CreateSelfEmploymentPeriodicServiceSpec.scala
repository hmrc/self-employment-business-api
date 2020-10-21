/*
 * Copyright 2020 HM Revenue & Customs
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

import uk.gov.hmrc.domain.Nino
import v1.controllers.EndpointLogContext
import v1.mocks.connectors.MockCreateSelfEmploymentPeriodicConnector
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.createSEPeriodic._
import v1.models.response.createSEPeriodic.CreateSelfEmploymentPeriodicResponseBody

import scala.concurrent.Future

class CreateSelfEmploymentPeriodicServiceSpec extends ServiceSpec {

  private val nino = "AA123456A"
  private val businessId = "XAIS12345678910"
  private val correlationId = "X-123"


  private val requestBody = CreateSelfEmploymentPeriodicBody(
    "2017-01-25",
    "2017-01-25",
    Some(Incomes(
      Some(IncomesAmountObject(500.12)),
      Some(IncomesAmountObject(500.12))
    )),
    Some(ConsolidatedExpenses(500.12)),
    Some(Expenses(
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12))),
      Some(ExpensesAmountObject(500.12, Some(500.12)))
    ))
  )

  private val requestData = CreateSelfEmploymentPeriodicRequest(
    nino = Nino(nino),
    businessId = businessId,
    body = requestBody
  )

  trait Test extends MockCreateSelfEmploymentPeriodicConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new CreateSelfEmploymentPeriodicService(
      connector = mockCreateSelfEmploymentPeriodicConnector
    )
  }

  "CreateSEPeriodic" when {
    "createPeriodic" must {
      "return correct result for a success" in new Test {
        val connectorOutcome = Right(ResponseWrapper(correlationId, ()))
        val outcome = Right(ResponseWrapper(correlationId, CreateSelfEmploymentPeriodicResponseBody("2017012520170125")))

        MockCreateSelfEmploymentPeriodicConnector.createSelfEmploymentPeriodic(requestData)
          .returns(Future.successful(connectorOutcome))

        await(service.createPeriodic(requestData)) shouldBe outcome
      }
    }


    "map errors according to spec" when {

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockCreateSelfEmploymentPeriodicConnector.createSelfEmploymentPeriodic(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.createPeriodic(requestData)) shouldBe Left(ErrorWrapper(Some(correlationId), error))
        }

      val input = Seq(
        ("INVALID_TAXABLE_ENTITY_ID", NinoFormatError),
        ("INVALID_INCOME_SOURCE", BusinessIdFormatError),
        ("RULE_OVERLAPPING_PERIOD", RuleOverlappingPeriod),
        ("RULE_MISALIGNED_PERIOD", RuleMisalignedPeriod),
        ("RULE_NOT_CONTIGUOUS_PERIOD", RuleNotContiguousPeriod),
        ("RULE_NOT_ALLOWED_CONSOLIDATED_EXPENSES", RuleNotAllowedConsolidatedExpenses),
        ("NOT_FOUND", NotFoundError),
        ("SERVER_ERROR", DownstreamError),
        ("SERVICE_UNAVAILABLE", DownstreamError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }

}
