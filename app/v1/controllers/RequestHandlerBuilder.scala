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

import play.api.mvc.Result
import uk.gov.hmrc.http.HeaderCarrier
import utils.IdGenerator
import v1.controllers.requestParsers.RequestParser
import v1.models.errors.ErrorWrapper
import v1.models.outcomes.ResponseWrapper
import v1.models.request.RawData
import v1.services.BaseService

import javax.inject.{ Inject, Singleton }
import scala.concurrent.{ ExecutionContext, Future }

// FIXME need to handle:
// - auditing
// - nrs
// - logging context (requires class to automate - ok for mix-in but not for builder usage)
// - rename: not actually building a controller but a request handler of some sorts
// - test generically(!) for various scenarios

trait ParserOnlyBuilder[InputRaw <: RawData, Input] {
  def withService[Output](service: BaseService[Input, Output]): RequestHandlerBuilder[InputRaw, Input, Output]

  def withService[Output](serviceFunction: Input => RequestContext => Future[Either[ErrorWrapper, ResponseWrapper[Output]]])
    : RequestHandlerBuilder[InputRaw, Input, Output]
}

trait RequestHandlerBuilder[InputRaw <: RawData, Input, Output] {
  def withResultCreator(resultCreator: ResultCreator[InputRaw, Output]): RequestHandlerBuilder[InputRaw, Input, Output]
  def withErrorHandling(errorHandling: PartialFunction[ErrorWrapper, Result]): RequestHandlerBuilder[InputRaw, Input, Output]
  def createRequestHandler(implicit ec: ExecutionContext): RequestHandler[InputRaw, Input, Output]
}

@Singleton
final class RequestHandlerFactory @Inject()(idGenerator: IdGenerator, commonErrorHandling: CommonErrorHandling) {

  def withParser[InputRaw <: RawData, Input](parser: RequestParser[InputRaw, Input]): ParserOnlyBuilder[InputRaw, Input] =
    ParserOnlyBuilderImpl(parser)

  private case class ParserOnlyBuilderImpl[InputRaw <: RawData, Input](parser: RequestParser[InputRaw, Input])
      extends ParserOnlyBuilder[InputRaw, Input] {

    def withService[Output](service: BaseService[Input, Output]): RequestHandlerBuilderImpl[InputRaw, Input, Output] =
      RequestHandlerBuilderImpl(parser, service)

    def withService[Output](serviceFunction: Input => RequestContext => Future[Either[ErrorWrapper, ResponseWrapper[Output]]])
      : RequestHandlerBuilder[InputRaw, Input, Output] = {
      val service = new BaseService[Input, Output] {
        override def doService(request: Input)(implicit hc: HeaderCarrier,
                                               ec: ExecutionContext,
                                               logContext: EndpointLogContext,
                                               correlationId: String): Future[Either[ErrorWrapper, ResponseWrapper[Output]]] = {
          serviceFunction(request)(RequestContext(hc, correlationId))
        }
      }

      RequestHandlerBuilderImpl(parser, service)
    }
  }

  private case class RequestHandlerBuilderImpl[InputRaw <: RawData, Input, Output](
      parser: RequestParser[InputRaw, Input],
      service: BaseService[Input, Output],
      errorHandling: PartialFunction[ErrorWrapper, Result] = PartialFunction.empty,
      resultCreator: ResultCreator[InputRaw, Output] = ResultCreator.noContent[InputRaw, Output])
      extends RequestHandlerBuilder[InputRaw, Input, Output] {

    def withResultCreator(resultCreator: ResultCreator[InputRaw, Output]): RequestHandlerBuilder[InputRaw, Input, Output] =
      copy(resultCreator = resultCreator)

    def withErrorHandling(errorHandling: PartialFunction[ErrorWrapper, Result]): RequestHandlerBuilder[InputRaw, Input, Output] =
      copy(errorHandling = errorHandling)

    def createRequestHandler(implicit ec: ExecutionContext): RequestHandler[InputRaw, Input, Output] =
      RequestHandler(parser, service, errorHandling, resultCreator, idGenerator, commonErrorHandling)
  }
}
