/*
 * Copyright 2023 HM Revenue & Customs
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

package v3.listPeriodSummaries.model.response

import play.api.libs.json._
import shared.utils.JsonWritesUtil
import shared.utils.JsonWritesUtil.writesFrom
import v3.listPeriodSummaries.def1.model.response.{Def1_ListPeriodSummariesResponse, Def1_PeriodDetails}

trait ListPeriodSummariesResponse[+E]

object ListPeriodSummariesResponse extends JsonWritesUtil {

  implicit def writes[E: Writes]: OWrites[ListPeriodSummariesResponse[E]] = writesFrom { case a: Def1_ListPeriodSummariesResponse[E] =>
    Json.toJson(a).as[JsObject]
  }

}

trait PeriodDetails

object PeriodDetails {

  implicit val writes: OWrites[PeriodDetails] = writesFrom { case a: Def1_PeriodDetails =>
    Json.toJson(a).as[JsObject]

  }

}
