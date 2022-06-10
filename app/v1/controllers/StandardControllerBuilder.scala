/*
 * Copyright 2021 HM Revenue & Customs
 *
 */

package v1.controllers

import play.api.mvc.Result
import utils.IdGenerator
import v1.controllers.requestParsers.RequestParser
import v1.models.errors.ErrorWrapper
import v1.models.request.RawData
import v1.services.BaseService

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
  def createController(idGenerator: IdGenerator)(implicit ec: ExecutionContext): StandardController[InputRaw, Input, Output]
}

object StandardControllerBuilder {

  def apply[InputRaw <: RawData, Input, Output](parser: RequestParser[InputRaw, Input],
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

    def createController(idGenerator: IdGenerator)(implicit ec: ExecutionContext): StandardController[InputRaw, Input, Output] =
      StandardController(parser, service, errorHandling, resultCreator, idGenerator)
  }
}
