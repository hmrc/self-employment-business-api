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

import config.AppConfig
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsPath, Json, OWrites, Reads}
import utils.JsonUtils
import v1.hateoas.{HateoasLinks, HateoasLinksFactory}
import v1.models.hateoas.{HateoasData, Link}

case class RetrieveSampleResponse(completedItems: Option[Seq[SampleArrayItem]],
                                  taxableForeignIncome: Option[SampleObject],
                                  availableCharitableDeduction: Option[SampleOptionalObject],
                                  broughtForwardLosses: Option[Seq[SampleOptionalObject]])

object RetrieveSampleResponse extends HateoasLinks with JsonUtils {

  val empty: RetrieveSampleResponse = RetrieveSampleResponse(None, None, None, None)

  implicit val reads: Reads[RetrieveSampleResponse] = (
    (JsPath \ "historicalIncomeSubmissions").readNullable[Seq[SampleArrayItem]].mapEmptySeqToNone and
      (JsPath \ "currentIncomeSubmission").readNullable[SampleObject].map {
        case Some(SampleObject(_, None)) => None
        case other => other
      } and
      (JsPath \ "totalCharitableContribution").readNullable[SampleOptionalObject].map{
        case Some(SampleOptionalObject.empty) => None
        case other => other
      } and
      (JsPath \ "broughtForwardSubmissions").readNullable[Seq[SampleOptionalObject]]
        (filteredArrayReads("typeOfItem", "Type4")).mapEmptySeqToNone
    ) (RetrieveSampleResponse.apply _)

  implicit val writes: OWrites[RetrieveSampleResponse] = Json.writes[RetrieveSampleResponse]

  implicit object RetrieveSampleLinksFactory extends HateoasLinksFactory[RetrieveSampleResponse, RetrieveSampleHateoasData] {
    override def links(appConfig: AppConfig, data: RetrieveSampleHateoasData): Seq[Link] = {
      import data._
      Seq(
        amendSample(appConfig, nino, taxYear),
        retrieveSample(appConfig, nino, taxYear, isSelf = true),
        deleteSample(appConfig, nino, taxYear)
      )
    }
  }
}

case class RetrieveSampleHateoasData(nino: String, taxYear: String) extends HateoasData
