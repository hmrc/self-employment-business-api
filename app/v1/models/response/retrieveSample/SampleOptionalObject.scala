/*
 * Copyright 2020 HM Revenue & Customs
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

package v1.models.response.retrieveSample

import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import utils.JsonUtils
import v1.models.domain.{SampleDesEnum, SampleMtdEnum}

case class SampleOptionalObject(itemIdentifier: Option[String],
                                itemType: Option[SampleMtdEnum],
                                deductibleAmount: Option[BigDecimal])

object SampleOptionalObject extends JsonUtils {
  val empty: SampleOptionalObject = SampleOptionalObject(None, None, None)

  implicit val reads: Reads[SampleOptionalObject] = for {
    submittedId <- (JsPath \ "requestedItemId").readNullable[String]
    generatedId <- (JsPath \ "generatedItemId").readNullable[String]
    itemType <- (JsPath \ "typeOfItem").readNullable[SampleDesEnum].map(_.map(_.toMtdEnum))
    amount <- (JsPath \ "paymentAmount").readNullable[BigDecimal]
  } yield {
    if (submittedId.nonEmpty) {
      SampleOptionalObject(submittedId, itemType, amount)
    }
    else {
      SampleOptionalObject(generatedId, itemType, amount)
    }
  }

  implicit val writes: OWrites[SampleOptionalObject] = Json.writes[SampleOptionalObject]
}
