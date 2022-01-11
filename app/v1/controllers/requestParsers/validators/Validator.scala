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

package v1.controllers.requestParsers.validators

import v1.models.errors.MtdError
import v1.models.request.RawData

trait Validator[A <: RawData] {

  type ValidationLevel[T] = T => List[MtdError]

  def validate(data: A): List[MtdError]

  def run(validationSet: List[A => List[List[MtdError]]], data: A): List[MtdError] = {

    validationSet match {
      case Nil => List()
      case thisLevel :: remainingLevels =>
        thisLevel(data).flatten match {
          case x if x.isEmpty => run(remainingLevels, data)
          case x if x.nonEmpty => x
        }
    }
  }
}

object Validator {

  def flattenErrors(errors: List[List[MtdError]]): List[MtdError] = {
    errors.flatten.groupBy(_.message).map { case (_, errors) =>

      val baseError = errors.head.copy(paths = None)

      errors.fold(baseError)(
        (error1: MtdError, error2: MtdError) => (error1, error2) match {
          case (MtdError(_, _, Some(paths1)), MtdError(_, _, Some(paths2))) => error1.copy(paths = Some(paths1 ++ paths2))
          case (MtdError(_, _, Some(_)), MtdError(_, _, None)) => error1
          case (MtdError(_, _, None), MtdError(_, _, Some(_))) => error2
          case _ => error1
        }
      )
    }.toList
  }
}
