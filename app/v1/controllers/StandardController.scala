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
import play.api.libs.json.{Json, OWrites}
import play.api.mvc.Result
import play.api.mvc.Results._
import uk.gov.hmrc.http.HeaderCarrier
import utils.{IdGenerator, Logging}
import v1.controllers.requestParsers.RequestParser
import v1.hateoas.HateoasFactory
import v1.models.errors.ErrorWrapper.WithCode
import v1.models.errors._
import v1.models.hateoas.HateoasData
import v1.models.request.RawData
import v1.services.BaseService

import scala.concurrent.{ExecutionContext, Future}
import scala.annotation.nowarn

trait StandardController extends BaseController {
  self: Logging with HateoasWrapping =>

  type InputRaw <: RawData
  type Input
  type Output
  type HData <: HateoasData

  val idGenerator: IdGenerator

  val parser: RequestParser[InputRaw, Input]

  val service: BaseService.Aux[Input, Output]

  val hateoasFactory: HateoasFactory

  implicit val ec: ExecutionContext

  protected def doHandleRequest(rawData: InputRaw)(implicit
                                                   headerCarrier: HeaderCarrier,
                                                   endpointLogContext: EndpointLogContext, writes: OWrites[Output]): Future[Result] = {

    implicit val correlationId: String = idGenerator.getCorrelationId

    logger.info(
      message = s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] " +
        s"with correlationId : $correlationId")

    val result =
      for {
        parsedRequest <- EitherT.fromEither[Future](parser.parseRequest(rawData))
        serviceResponse <- EitherT(service.doService(parsedRequest))
        vendorResponse <- EitherT.fromEither[Future](doWrap(rawData, serviceResponse.responseData)
          .asRight[ErrorWrapper])
      } yield {
        logger.info(
          s"[${endpointLogContext.controllerName}][${endpointLogContext.endpointName}] - " +
            s"Success response received with CorrelationId: ${serviceResponse.correlationId}")

        Ok(Json.toJson(vendorResponse))
          .withApiHeaders(serviceResponse.correlationId)
      }

    result.leftMap { errorWrapper =>
      val resCorrelationId = errorWrapper.correlationId
      val result = errorResult(errorWrapper).withApiHeaders(resCorrelationId)

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

  // TODO implement in controllers if necessary
  protected def errorResultPF(implicit @nowarn endpointLogContext: EndpointLogContext): PartialFunction[ErrorWrapper, Result] =
    PartialFunction.empty

  // TODO these are just some examples error mappings and would need to cover all controller errors
  //  (or at least all those that are in common - deferring to errorResultPF).
  // The RHS of the = maybe should go in a separate object to keep this base controller fixed
  // between different microservices??
  private def stdErrorResultPF: PartialFunction[ErrorWrapper, Result] = {
    case errorWrapper@(WithCode(BadRequestError.code) |
                       WithCode(NinoFormatError.code) |
                       WithCode(BusinessIdFormatError.code) |
                       WithCode(TaxYearFormatError.code) |
                       WithCode(RuleIncorrectOrEmptyBodyError.code) |
                       WithCode(RuleTaxYearNotSupportedError.code) |
                       WithCode(RuleTaxYearRangeInvalidError.code)) => BadRequest(Json.toJson(errorWrapper))
    case errorWrapper@WithCode(NotFoundError.code) => NotFound(Json.toJson(errorWrapper))
    case errorWrapper@WithCode(DownstreamError.code) => InternalServerError(Json.toJson(errorWrapper))
  }
}
