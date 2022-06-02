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
import play.api.mvc.Results._
import uk.gov.hmrc.http.HeaderCarrier
import utils.{ IdGenerator, Logging }
import v1.controllers.requestParsers.RequestParser
import v1.models.errors.ErrorWrapper.WithCode
import v1.models.errors._
import v1.models.request.RawData
import v1.services.{ BaseService, ServiceComponent }

import scala.annotation.nowarn
import scala.concurrent.{ ExecutionContext, Future }

class ControllerBuilder[InputRaw <: RawData, Input, Output](parser: RequestParser[InputRaw, Input],
                                                            service: BaseService[Input, Output],
                                                            errorHandling: PartialFunction[ErrorWrapper, Result] = PartialFunction.empty,
                                                            resultsCreator: ResultCreator[InputRaw, Output] =
                                                              ResultCreator.noContent[InputRaw, Output]) {

  def withResultCreator(resultCreator: ResultCreator[InputRaw, Output]): ControllerBuilder[InputRaw, Input, Output] =
    new ControllerBuilder(parser, service, errorHandling, resultCreator)

  def withErrorHandling(errorHandling: PartialFunction[ErrorWrapper, Result]): ControllerBuilder[InputRaw, Input, Output] =
    new ControllerBuilder(parser, service, errorHandling, resultsCreator)

  // FIXME need to handle:
  // - hateoas lists
  // - case when hateoas data reqs response info (e.g. an id from a create)
  // - auditing
  // - more general service that doesn't implement trait
  // - logging context (requires class to automate - ok for mix-in but not for builder usage)

  def createController(idGenerator: IdGenerator)(implicit ec0: ExecutionContext): StandardController[InputRaw, Input, Output] = {
    StandardController(parser, service, errorHandling, resultsCreator: ResultCreator[InputRaw, Output], idGenerator: IdGenerator)
  }
}

trait StandardController[InputRaw <: RawData, Input, Output] extends BaseController {
  self: Logging with ServiceComponent[Input, Output] with ResultCreatorComponent[InputRaw, Output] =>

  val idGenerator: IdGenerator

  val parser: RequestParser[InputRaw, Input]

  implicit val ec: ExecutionContext

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
      .orElse(stdErrorResultPF)
      .applyOrElse(errorWrapper, unhandledError)

  protected def errorResultPF(implicit @nowarn endpointLogContext: EndpointLogContext): PartialFunction[ErrorWrapper, Result] =
    PartialFunction.empty

  // TODO these are just some examples error mappings and would need to cover all controller errors
  //  (or at least all those that are in common - deferring to errorResultPF).
  // The RHS of the = maybe should go in a separate object to keep this base controller fixed
  // between different microservices??
  private def stdErrorResultPF: PartialFunction[ErrorWrapper, Result] = {
    case errorWrapper @ (WithCode(BadRequestError.code) | WithCode(NinoFormatError.code) | WithCode(BusinessIdFormatError.code) | WithCode(
          TaxYearFormatError.code) | WithCode(RuleIncorrectOrEmptyBodyError.code) | WithCode(RuleTaxYearNotSupportedError.code) | WithCode(
          RuleTaxYearRangeInvalidError.code)) =>
      BadRequest(Json.toJson(errorWrapper))
    case errorWrapper @ WithCode(NotFoundError.code)   => NotFound(Json.toJson(errorWrapper))
    case errorWrapper @ WithCode(DownstreamError.code) => InternalServerError(Json.toJson(errorWrapper))
  }
}

object StandardController {

  def apply[InputRaw <: RawData, Input, Output](
      parser0: RequestParser[InputRaw, Input],
      service0: BaseService[Input, Output],
      errorHandling0: PartialFunction[ErrorWrapper, Result],
      resultsCreator0: ResultCreator[InputRaw, Output],
      idGenerator0: IdGenerator)(implicit ec0: ExecutionContext): StandardController[InputRaw, Input, Output] =
    new StandardController[InputRaw, Input, Output] with ResultCreatorComponent[InputRaw, Output] with ServiceComponent[Input, Output] with Logging {

      override def resultCreator: ResultCreator[InputRaw, Output] = resultsCreator0

      override protected def errorResultPF(implicit endpointLogContext: EndpointLogContext): PartialFunction[ErrorWrapper, Result] =
        errorHandling0

      override val idGenerator: IdGenerator               = idGenerator0
      override val parser: RequestParser[InputRaw, Input] = parser0
      override val service: BaseService[Input, Output]    = service0

      override implicit val ec: ExecutionContext = ec0
    }
}
