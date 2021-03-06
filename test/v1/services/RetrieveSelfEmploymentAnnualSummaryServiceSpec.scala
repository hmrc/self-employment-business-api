/*
 * Copyright 2021 HM Revenue & Customs
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

import support.UnitSpec
import v1.controllers.EndpointLogContext
import uk.gov.hmrc.domain.Nino
import v1.mocks.connectors.MockRetrieveSelfEmploymentAnnualSummaryConnector
import v1.models.domain.ex.MtdEx._
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import uk.gov.hmrc.http.HeaderCarrier
import v1.models.request.retrieveSEAnnual.RetrieveSelfEmploymentAnnualSummaryRequest
import v1.models.response.retrieveSEAnnual.{Adjustments, Allowances, Class4NicInfo, NonFinancials, RetrieveSelfEmploymentAnnualSummaryResponse}
import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

class RetrieveSelfEmploymentAnnualSummaryServiceSpec extends UnitSpec {

  val nino = Nino("AA123456A")
  val businessId = "XAIS12345678910"
  val taxYear = "2019-20"
  implicit val correlationId = "X-123"

  val response = RetrieveSelfEmploymentAnnualSummaryResponse(
    Some(Adjustments(
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25))),
    Some(Allowances(
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25),
      Some(100.25)
    )),
    Some(NonFinancials(
      Some(Class4NicInfo(
        Some(`001 - Non Resident`))))))

  private val requestData = RetrieveSelfEmploymentAnnualSummaryRequest(nino, businessId, taxYear)

  trait Test extends MockRetrieveSelfEmploymentAnnualSummaryConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new RetrieveSelfEmploymentAnnualSummaryService(
      retrieveSelfEmploymentAnnualSummaryConnector = mockRetrieveSelfEmploymentAnnualSummaryConnector
    )
  }

  "service" should {
    "service call successful" when {
      "return mapped result" in new Test {
        MockRetrieveSelfEmploymentConnector.retrieveSelfEmployment(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieveSelfEmploymentAnnualSummary(requestData)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockRetrieveSelfEmploymentConnector.retrieveSelfEmployment(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.retrieveSelfEmploymentAnnualSummary(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(
        "INVALID_NINO" -> NinoFormatError,
        "INVALID_INCOME_SOURCE" -> BusinessIdFormatError,
        "INVALID_TAX_YEAR" -> TaxYearFormatError,
        "NOT_FOUND_INCOME_SOURCE" -> NotFoundError,
        "SERVER_ERROR" -> DownstreamError,
        "SERVICE_UNAVAILABLE" -> DownstreamError
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}
