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

package v1.models.request.amendAnnualSummary

<<<<<<< HEAD
<<<<<<< HEAD
import play.api.libs.json.{Json, Reads, Writes}
=======
import play.api.libs.json.{Format, Json}
>>>>>>> a67f837... Amend Annual summary models
=======
import play.api.libs.json.{Json, Reads, Writes}
>>>>>>> 68f75cf... finish models and testing

case class NonFinancials(class4NicInfo: Option[Class4NicInfo])

object NonFinancials {
<<<<<<< HEAD
<<<<<<< HEAD
  implicit val reads: Reads[NonFinancials] = Json.reads[NonFinancials]
  implicit val writes: Writes[NonFinancials] = (o: NonFinancials) => Json.toJson(o.class4NicInfo)
=======
  implicit val format: Format[NonFinancials] = Json.format[NonFinancials]
>>>>>>> a67f837... Amend Annual summary models
=======
  implicit val reads: Reads[NonFinancials] = Json.reads[NonFinancials]
  implicit val writes: Writes[NonFinancials] = (o: NonFinancials) => Json.toJson(o.class4NicInfo)
>>>>>>> 68f75cf... finish models and testing
}