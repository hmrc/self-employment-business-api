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

import scala.language.higherKinds
import cats.Functor
import play.api.http.Status
import play.api.libs.json.{ Json, OWrites, Writes }
import play.api.mvc.{ Result, Results }
import v1.hateoas.{ HateoasFactory, HateoasLinksFactory, HateoasListLinksFactory }
import v1.models.hateoas.{ HateoasData, HateoasDataBuilder, HateoasWrapper }
import v1.models.request.RawData

trait ResultCreatorComponent[InputRaw <: RawData, Output] {

  def resultCreator: ResultCreator[InputRaw, Output]
}

trait ResultCreator[InputRaw <: RawData, Output] {

  def createResult(raw: InputRaw, output: Output): Result
}

object ResultCreator {

  def noContent[InputRaw <: RawData, Output]: ResultCreator[InputRaw, Output] =
    (_: InputRaw, _: Output) => {
      Results.NoContent
    }

  def simple[InputRaw <: RawData, Output](successStatus: Int = Status.OK)(implicit ws: OWrites[Output]): ResultCreator[InputRaw, Output] =
    (_: InputRaw, output: Output) => {
      Results.Status(successStatus)(Json.toJson(output))
    }

  def hateoasWrapping[InputRaw <: RawData, Output, HData <: HateoasData](hateoasFactory: HateoasFactory, successStatus: Int = Status.OK)(
      implicit linksFactory: HateoasLinksFactory[Output, HData],
      hateoasDataBuilder: HateoasDataBuilder[InputRaw, Output, HData],
      writes: OWrites[Output]): ResultCreator[InputRaw, Output] =
    (raw: InputRaw, output: Output) => {
      val data: HData = hateoasDataBuilder.dataFor(raw, output)
      val wrapped     = hateoasFactory.wrap(output, data)

      Results.Status(successStatus)(Json.toJson(wrapped))
    }

  def hateoasListWrapping[InputRaw <: RawData, Output[_]: Functor, I, HData <: HateoasData](hateoasFactory: HateoasFactory,
                                                                                            successStatus: Int = Status.OK)(
      implicit linksFactory: HateoasListLinksFactory[Output, I, HData],
      hateoasDataBuilder: HateoasDataBuilder[InputRaw, Output[I], HData],
      writes: Writes[HateoasWrapper[Output[HateoasWrapper[I]]]]): ResultCreator[InputRaw, Output[I]] =
    (raw: InputRaw, output: Output[I]) => {
      val data: HData = hateoasDataBuilder.dataFor(raw, output)
      val wrapped     = hateoasFactory.wrapList(output, data)

      Results.Status(successStatus)(Json.toJson(wrapped))
    }
}
