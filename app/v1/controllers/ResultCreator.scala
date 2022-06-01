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

import play.api.http.Status
import play.api.libs.json.{ Json, OWrites }
import play.api.mvc.{ Result, Results }
import v1.hateoas.{ HateoasFactory, HateoasLinksFactory }
import v1.models.hateoas.{ HateoasData, HateoasDataBuilder, HateoasWrapper }
import v1.models.request.RawData

trait ResultCreatorComponent {
  type InputRaw <: RawData
  type Output

  def resultCreator: ResultCreator.Aux[InputRaw, Output]
}

trait ResultCreator {
  type InputRaw <: RawData
  type Output

  def createResult(raw: InputRaw, output: Output): Result
}

object ResultCreator {

  type Aux[InputRaw0 <: RawData, Output0] = ResultCreator {
    type InputRaw = InputRaw0
    type Output   = Output0
  }

  def noContent[InputRaw0 <: RawData, Output0]: ResultCreator.Aux[InputRaw0, Output0] =
    new ResultCreator {
      override type InputRaw = InputRaw0
      override type Output   = Output0

      override def createResult(raw: InputRaw, output: Output): Result =
        Results.NoContent
    }

  def simple[InputRaw0 <: RawData, Output0](successStatus: Int = Status.OK)(implicit ws: OWrites[Output0]): ResultCreator.Aux[InputRaw0, Output0] =
    new ResultCreator {
      override type InputRaw = InputRaw0
      override type Output   = Output0

      override def createResult(raw: InputRaw, output: Output): Result = {
        Results.Status(successStatus)(Json.toJson(output))
      }
    }

  def hateoasWrapping[InputRaw0 <: RawData, Output0, HData <: HateoasData](hateoasFactory: HateoasFactory, successStatus: Int = Status.OK)(
      implicit linksFactory: HateoasLinksFactory[Output0, HData],
      hateoasDataBuilder: HateoasDataBuilder[InputRaw0, HData],
      writes: OWrites[Output0]): ResultCreator.Aux[InputRaw0, Output0] = {
    new ResultCreator {
      override type InputRaw = InputRaw0
      override type Output   = Output0

      override def createResult(raw: InputRaw, output: Output): Result = {
        Results.Status(successStatus)(Json.toJson(wrap(raw, output)))
      }

      private def wrap(raw: InputRaw, output: Output): HateoasWrapper[Output] = {
        val data: HData = hateoasDataBuilder.dataFor(raw)

        hateoasFactory.wrap(output, data)
      }
    }
  }
}
