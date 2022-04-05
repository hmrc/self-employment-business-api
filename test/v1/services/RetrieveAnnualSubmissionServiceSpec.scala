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

import v1.controllers.EndpointLogContext
import v1.models.domain.{BusinessId, Nino, TaxYear}
import v1.mocks.connectors.MockRetrieveAnnualSubmissionConnector
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.retrieveAnnual.RetrieveAnnualSubmissionRequest
import v1.models.response.retrieveAnnual.{Adjustments, Allowances, NonFinancials, RetrieveAnnualSubmissionFixture, RetrieveAnnualSubmissionResponse}

import scala.concurrent.Future

class RetrieveAnnualSubmissionServiceSpec extends ServiceSpec with RetrieveAnnualSubmissionFixture {

  val nino: String                   = "AA123456A"
  val businessId: String             = "XAIS12345678910"
  val taxYear: String                = "2019-20"
  implicit val correlationId: String = "X-123"

  val response: RetrieveAnnualSubmissionResponse = RetrieveAnnualSubmissionResponse(
    allowances = Some(Allowances(None, None, None, None, None, None, None, None, None, None, None, None, None)),
    adjustments = Some(Adjustments(None, None, None, None, None, None, None, None, None)),
    nonFinancials = Some(NonFinancials(businessDetailsChangedRecently = true, None))
  )

  private val requestData = RetrieveAnnualSubmissionRequest(
    nino = Nino(nino),
    BusinessId(businessId),
    TaxYear.fromMtd(taxYear)
  )

  trait Test extends MockRetrieveAnnualSubmissionConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new RetrieveAnnualSubmissionService(
      connector = mockRetrieveAnnualSubmissionConnector
    )

  }

  "service" should {
    "service call successful" when {
      "return mapped result" in new Test {
        MockRetrieveConnector
          .retrieveAnnualSubmission(requestData)
          .returns(Future.successful(Right(ResponseWrapper(correlationId, response))))

        await(service.retrieveAnnualSubmission(requestData)) shouldBe Right(ResponseWrapper(correlationId, response))
      }
    }
  }

  "unsuccessful" should {
    "map errors according to spec" when {
      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockRetrieveConnector
            .retrieveAnnualSubmission(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.retrieveAnnualSubmission(requestData)) shouldBe Left(ErrorWrapper(correlationId, error))
        }

      val input = Seq(
        "INVALID_NINO"            -> NinoFormatError,
        "INVALID_INCOMESOURCEID"  -> BusinessIdFormatError,
        "INVALID_TAX_YEAR"        -> TaxYearFormatError,
        "INCOME_SOURCE_NOT_FOUND" -> NotFoundError,
        "NOT_FOUND_PERIOD"        -> NotFoundError,
        "INVALID_CORRELATIONID"   -> DownstreamError,
        "SERVER_ERROR"            -> DownstreamError,
        "SERVICE_UNAVAILABLE"     -> DownstreamError
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }

}
