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

import support.UnitSpec
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.http.HeaderCarrier
import v1.controllers.EndpointLogContext
import v1.mocks.connectors.MockDeleteSelfEmploymentAnnualSummaryConnector
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.deleteSEAnnual.DeleteSelfEmploymentAnnualSummaryRequest

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DeleteSelfEmploymentAnnualSummaryServiceSpec extends UnitSpec {

  val taxYear = "2017-18"
  val nino = Nino("AA123456A")
  val businessId = "XAIS12345678910"
  private val correlationId = "X-123"

  private val requestData = DeleteSelfEmploymentAnnualSummaryRequest(nino, businessId, taxYear)

  trait Test extends MockDeleteSelfEmploymentAnnualSummaryConnector {
    implicit val hc: HeaderCarrier = HeaderCarrier()
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new DeleteSelfEmploymentAnnualSummaryService(
      deleteSelfEmploymentAnnualSummaryConnector = mockDeleteSelfEmploymentAnnualSummaryConnector
    )
  }

  "service" should {
    "service call successful" when {
      "return mapped result" in new Test {
        MockDeleteSelfEmploymentAnnualSummaryConnector.deleteSelfEmploymentAnnualSummary(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, ()))))

        await(service.deleteSelfEmploymentAnnualSummary(requestData)) shouldBe Right(ResponseWrapper(correlationId, ()))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockDeleteSelfEmploymentAnnualSummaryConnector.deleteSelfEmploymentAnnualSummary(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.deleteSelfEmploymentAnnualSummary(requestData)) shouldBe Left(ErrorWrapper(Some(correlationId), error))
        }

      val input = Seq(
        "INVALID_NINO" -> NinoFormatError,
        "INVALID_INCOME_SOURCE" -> BusinessIdFormatError,
        "INVALID_TAX_YEAR" -> TaxYearFormatError,
        "NOT_FOUND_INCOME_SOURCE" -> NotFoundError,
        "GONE" -> NotFoundError,
        "INVALID_PAYLOAD" -> DownstreamError,
        "SERVER_ERROR" -> DownstreamError,
        "SERVICE_UNAVAILABLE" -> DownstreamError
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}