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
import v1.mocks.connectors.MockAmendAnnualSummaryConnector
import v1.models.domain.ex.MtdEx
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.models.request.amendSEAnnual.{Adjustments, Allowances, AmendAnnualSummaryBody, AmendAnnualSummaryRequest, Class4NicInfo, NonFinancials}

import scala.concurrent.Future

class AmendAnnualSummaryServiceSpec extends ServiceSpec {

  private val nino = "AA123456A"
  val businessId = "XAIS12345678910"
  private val taxYear = "2017-18"
  private val correlationId = "X-123"

  private val requestBody =  AmendAnnualSummaryBody(
    Some(Adjustments(Some(1.11), Some(2.22), Some(3.33), Some(4.44), Some(5.55), Some(6.66), Some(7.77), Some(8.88), Some(9.99), Some(10.10))),
<<<<<<< HEAD:test/v1/services/AmendAnnualSummaryServiceSpec.scala
    Some(Allowances(Some(1.11), Some(2.22), Some(3.33), Some(4.44), Some(5.55), Some(6.66), Some(7.77), Some(8.88), Some(9.99))),
    Some(NonFinancials(Some(Class4NicInfo(Some(MtdEx.`001 - Non Resident`)))))
=======
    Some(Allowances(Some(1.11), Some(2.22), Some(3.33), Some(4.44), Some(5.55), Some(6.66), Some(7.77), Some(8.88), Some(9.99), Some(10.10), Some(11.11))),
    Some(NonFinancials(Some(Class4NicInfo(isExempt = true, Some(MtdEx.`001 - Non Resident`)))))
>>>>>>> 8350429... add extra fields to the allowances model in create and amend SE anual summary:test/v1/services/MockAmendAnnualSummaryServiceSpec.scala
  )

  private val requestData = AmendAnnualSummaryRequest(
    nino = Nino(nino),
    businessId = businessId,
    taxYear = taxYear,
    body = requestBody
  )

  trait Test extends MockAmendAnnualSummaryConnector {
    implicit val logContext: EndpointLogContext = EndpointLogContext("c", "ep")

    val service = new AmendAnnualSummaryService(
      connector = mockAmendAnnualSummaryConnector
    )
  }

  "AmendAnnualSummaryService" when {
    "amendAnnualSummary" must {
      "return correct result for a success" in new Test {
        val outcome = Right(ResponseWrapper(correlationId, ()))

        MockAmendAnnualSummaryConnector.amendAnnualSummary(requestData)
          .returns(Future.successful(outcome))

        await(service.amendAnnualSummary(requestData)) shouldBe outcome
      }
    }

    "map errors according to spec" when {

      def serviceError(desErrorCode: String, error: MtdError): Unit =
        s"a $desErrorCode error is returned from the service" in new Test {

          MockAmendAnnualSummaryConnector.amendAnnualSummary(requestData)
            .returns(Future.successful(Left(ResponseWrapper(correlationId, DesErrors.single(DesErrorCode(desErrorCode))))))

          await(service.amendAnnualSummary(requestData)) shouldBe Left(ErrorWrapper(Some(correlationId), error))
        }

      val input = Seq(
        ("INVALID_NINO", NinoFormatError),
        ("INVALID_TAX_YEAR", TaxYearFormatError),
        ("INVALID_INCOME_SOURCE", BusinessIdFormatError),
        ("INVALID_PAYLOAD", DownstreamError),
        ("NOT_FOUND_INCOME_SOURCE", NotFoundError),
        ("GONE", NotFoundError),
        ("SERVER_ERROR", DownstreamError),
        ("SERVICE_UNAVAILABLE", DownstreamError)
      )

      input.foreach(args => (serviceError _).tupled(args))
    }
  }
}
