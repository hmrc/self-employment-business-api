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

package v1.controllers

import cats.data.EitherT
import cats.implicits._
import play.api.libs.json.Json
import play.api.mvc.Result
import play.api.mvc.Results.InternalServerError
import uk.gov.hmrc.http.HeaderCarrier
import utils.{ IdGenerator, Logging }
import v1.controllers.requestParsers.RequestParser
import v1.models.errors._
import v1.models.request.RawData
import v1.services.{ BaseService, ServiceComponent }

import scala.annotation.nowarn
import scala.concurrent.{ ExecutionContext, Future }

trait RequestHandler[InputRaw <: RawData, Input, Output] {
  self: Logging with ServiceComponent[Input, Output] with ResultCreatorComponent[InputRaw, Output] with CommonErrorHandlingComponent =>

  val idGenerator: IdGenerator

  val parser: RequestParser[InputRaw, Input]

  implicit val ec: ExecutionContext

  implicit class Response(result: Result) {

    def withApiHeaders(correlationId: String, responseHeaders: (String, String)*): Result = {

      val newHeaders: Seq[(String, String)] = responseHeaders ++ Seq(
        "X-CorrelationId"        -> correlationId,
        "X-Content-Type-Options" -> "nosniff",
        "Content-Type"           -> "application/json"
      )

      result.copy(header = result.header.copy(headers = result.header.headers ++ newHeaders))
    }
  }

  protected def unhandledError(errorWrapper: ErrorWrapper)(implicit endpointLogContext: EndpointLogContext): Result = {
    logger.error(
      s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
        s"Unhandled error: $errorWrapper")
    InternalServerError(Json.toJson(DownstreamError))
  }

  def handleRequest(rawData: InputRaw)(implicit
                                       headerCarrier: HeaderCarrier,
                                       endpointLogContext: EndpointLogContext): Future[Result] = {

    implicit val correlationId: String = idGenerator.getCorrelationId

    logger.info(
      message = s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
        s"with correlationId : $correlationId")

    val result =
      for {
        parsedRequest   <- EitherT.fromEither[Future](parser.parseRequest(rawData))
        serviceResponse <- EitherT(service.doService(parsedRequest))
      } yield {
        logger.info(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

        resultCreator
          .createResult(rawData, serviceResponse.responseData)
          .withApiHeaders(serviceResponse.correlationId)
      }

    result.leftMap { errorWrapper =>
      val resCorrelationId = errorWrapper.correlationId
      val result           = errorResult(errorWrapper).withApiHeaders(resCorrelationId)

      logger.warn(
        s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
          s"Error response received with CorrelationId: $resCorrelationId")
      result
    }.merge
  }

  private def errorResult(errorWrapper: ErrorWrapper)(implicit endpointLogContext: EndpointLogContext): Result =
    errorResultPF
      .orElse(commonErrorHandling.errorResultPF)
      .applyOrElse(errorWrapper, unhandledError)

  protected def errorResultPF(implicit @nowarn endpointLogContext: EndpointLogContext): PartialFunction[ErrorWrapper, Result] =
    PartialFunction.empty
}

object RequestHandler {

  def apply[InputRaw <: RawData, Input, Output](
      parser0: RequestParser[InputRaw, Input],
      service0: BaseService[Input, Output],
      errorHandling0: PartialFunction[ErrorWrapper, Result],
      resultsCreator0: ResultCreator[InputRaw, Output],
      idGenerator0: IdGenerator,
      commonErrorHandling0: CommonErrorHandling)(implicit ec0: ExecutionContext): RequestHandler[InputRaw, Input, Output] =
    new RequestHandler[InputRaw, Input, Output] with ResultCreatorComponent[InputRaw, Output] with ServiceComponent[Input, Output]
    with CommonErrorHandlingComponent with Logging {

      override def resultCreator: ResultCreator[InputRaw, Output] = resultsCreator0

      override protected def errorResultPF(implicit endpointLogContext: EndpointLogContext): PartialFunction[ErrorWrapper, Result] =
        errorHandling0

      override def commonErrorHandling: CommonErrorHandling = commonErrorHandling0

      override val idGenerator: IdGenerator               = idGenerator0
      override val parser: RequestParser[InputRaw, Input] = parser0
      override val service: BaseService[Input, Output]    = service0

      override implicit val ec: ExecutionContext = ec0
    }
}
