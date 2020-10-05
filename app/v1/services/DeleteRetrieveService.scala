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

import cats.data.EitherT
import cats.implicits._
import javax.inject.{Inject, Singleton}
import play.api.libs.json.Format
import uk.gov.hmrc.http.HeaderCarrier
import utils.Logging
import v1.connectors.{DeleteRetrieveConnector, DesUri}
import v1.controllers.EndpointLogContext
import v1.models.errors._
import v1.models.outcomes.ResponseWrapper
import v1.support.DesResponseMappingSupport

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DeleteRetrieveService @Inject()(connector: DeleteRetrieveConnector) extends DesResponseMappingSupport with Logging {

  def delete(desErrorMap: Map[String, MtdError] = defaultDesErrorMap)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    logContext: EndpointLogContext,
    desUri: DesUri[Unit]): Future[Either[ErrorWrapper, ResponseWrapper[Unit]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.delete()).leftMap(mapDesErrors(desErrorMap))
    } yield desResponseWrapper

    result.value
  }

  def retrieve[Resp: Format](desErrorMap: Map[String, MtdError] = defaultDesErrorMap)(
    implicit hc: HeaderCarrier,
    ec: ExecutionContext,
    logContext: EndpointLogContext,
    desUri: DesUri[Resp]): Future[Either[ErrorWrapper, ResponseWrapper[Resp]]] = {

    val result = for {
      desResponseWrapper <- EitherT(connector.retrieve[Resp]()).leftMap(mapDesErrors(desErrorMap))
      mtdResponseWrapper <- EitherT.fromEither[Future](validateRetrieveResponse(desResponseWrapper))
    } yield mtdResponseWrapper

    result.value
  }

  private def defaultDesErrorMap: Map[String, MtdError] = Map(
    "INVALID_NINO" -> NinoFormatError,
    "INVALID_TAX_YEAR" -> TaxYearFormatError,
    "NOT_FOUND" -> NotFoundError,
    "SERVER_ERROR" -> DownstreamError,
    "SERVICE_UNAVAILABLE" -> DownstreamError
  )
}
