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

import v1.hateoas.{HateoasFactory, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, HateoasDataBuilder, HateoasWrapper}
import v1.models.request.RawData

trait HateoasWrapping {
  type InputRaw <: RawData
  type Output
  type HData <: HateoasData

  val hateoasDataBuilder: HateoasDataBuilder[InputRaw, HData]
  val hateoasLinksFactory: HateoasLinksFactory[Output, HData]

  def doWrap(raw: InputRaw, output: Output): HateoasWrapper[Output]
}

trait SimpleHateoasWrapping
  extends HateoasWrapping {

  implicit val hateoasLinksFactory: HateoasLinksFactory[Output, HData]

  val hateoasDataBuilder: HateoasDataBuilder[InputRaw, HData]

  val hateoasFactory: HateoasFactory

  def doWrap(raw: InputRaw, output: Output): HateoasWrapper[Output] = {
    val data: HData = hateoasDataBuilder.dataFor(raw)

    hateoasFactory.wrap(output, data)
  }
}