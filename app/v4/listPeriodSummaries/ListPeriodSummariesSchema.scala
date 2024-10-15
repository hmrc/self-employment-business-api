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

package v4.listPeriodSummaries

import play.api.libs.json.Reads
import shared.schema.DownstreamReadable
import v4.listPeriodSummaries.def1.model.response.{Def1_ListPeriodSummariesResponse, Def1_PeriodDetails}
import v4.listPeriodSummaries.model.response.{ListPeriodSummariesResponse, PeriodDetails}

sealed trait ListPeriodSummariesSchema extends DownstreamReadable[ListPeriodSummariesResponse[PeriodDetails]]

object ListPeriodSummariesSchema {

  case object Def1 extends ListPeriodSummariesSchema {
    type DownstreamResp = Def1_ListPeriodSummariesResponse[Def1_PeriodDetails]
    val connectorReads: Reads[DownstreamResp] = Def1_ListPeriodSummariesResponse.reads
  }

}
