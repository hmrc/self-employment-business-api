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
import utils.IdGenerator
import v1.controllers.requestParsers.RequestParser
import v1.models.errors.ErrorWrapper
import v1.models.request.RawData
import v1.services.BaseService

import javax.inject.{ Inject, Singleton }
import scala.concurrent.ExecutionContext

// FIXME need to handle:
// - auditing
// - nrs
// - more general service that doesn't implement trait
// - logging context (requires class to automate - ok for mix-in but not for builder usage)
// - rename: not actually building a controller but a request handler of some sorts
// - test generically(!) for various scenarios

trait StandardControllerBuilder[InputRaw <: RawData, Input, Output] {
  def withResultCreator(resultCreator: ResultCreator[InputRaw, Output]): StandardControllerBuilder[InputRaw, Input, Output]
  def withErrorHandling(errorHandling: PartialFunction[ErrorWrapper, Result]): StandardControllerBuilder[InputRaw, Input, Output]
  def createController(implicit ec: ExecutionContext): StandardController[InputRaw, Input, Output]
}

@Singleton
final class StandardControllerFactory @Inject()(idGenerator: IdGenerator, commonErrorHandling: CommonErrorHandling) {

  def using[InputRaw <: RawData, Input, Output](parser: RequestParser[InputRaw, Input],
                                                service: BaseService[Input, Output]): StandardControllerBuilder[InputRaw, Input, Output] =
    StandardControllerBuilderImpl(parser, service)

  private case class StandardControllerBuilderImpl[InputRaw <: RawData, Input, Output](
      parser: RequestParser[InputRaw, Input],
      service: BaseService[Input, Output],
      errorHandling: PartialFunction[ErrorWrapper, Result] = PartialFunction.empty,
      resultCreator: ResultCreator[InputRaw, Output] = ResultCreator.noContent[InputRaw, Output])
      extends StandardControllerBuilder[InputRaw, Input, Output] {

    def withResultCreator(resultCreator: ResultCreator[InputRaw, Output]): StandardControllerBuilder[InputRaw, Input, Output] =
      copy(resultCreator = resultCreator)

    def withErrorHandling(errorHandling: PartialFunction[ErrorWrapper, Result]): StandardControllerBuilder[InputRaw, Input, Output] =
      copy(errorHandling = errorHandling)

    def createController(implicit ec: ExecutionContext): StandardController[InputRaw, Input, Output] =
      StandardController(parser, service, errorHandling, resultCreator, idGenerator, commonErrorHandling)
  }
}
