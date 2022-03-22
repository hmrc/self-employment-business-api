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

import cats.data.EitherT
import utils.Logging
import cats.implicits._
import javax.inject.{Inject, Singleton}
import uk.gov.hmrc.http.HeaderCarrier
import v1.connectors.DeleteSelfEmploymentAnnualSummaryConnector
import v1.controllers.EndpointLogContext
import v1.models.errors._
import v1.models.request.deleteSEAnnual.DeleteAnnualSubmissionRequest
import v1.support.DesResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteSelfEmploymentAnnualSummaryService @Inject()(deleteSelfEmploymentAnnualSummaryConnector: DeleteSelfEmploymentAnnualSummaryConnector)
  extends DesResponseMappingSupport with Logging {

    def deleteSelfEmploymentAnnualSummary(request: DeleteAnnualSubmissionRequest)(
                                         implicit hc: HeaderCarrier,
                                         ec: ExecutionContext,
                                         logContext: EndpointLogContext,
                                         correlationId: String): Future[DeleteSelfEmploymentAnnualSummaryServiceOutcome] = {

      val result = for {
        desResponseWrapper <- EitherT(deleteSelfEmploymentAnnualSummaryConnector.deleteSEAnnual(request)).leftMap(mapDesErrors(desErrorMap))
      } yield desResponseWrapper
      result.value
    }
  private def desErrorMap: Map[String, MtdError] =
    Map(
      "INVALID_NINO" -> NinoFormatError,
      "INVALID_INCOME_SOURCE" -> BusinessIdFormatError,
      "INVALID_TAX_YEAR" -> TaxYearFormatError,
      "NOT_FOUND_INCOME_SOURCE" -> NotFoundError,
      "GONE" -> NotFoundError,
      "INVALID_PAYLOAD" -> DownstreamError,
      "SERVER_ERROR" -> DownstreamError,
      "SERVICE_UNAVAILABLE" -> DownstreamError
    )
}
