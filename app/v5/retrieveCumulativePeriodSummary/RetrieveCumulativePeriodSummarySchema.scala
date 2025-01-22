/*
 * Copyright 2024 HM Revenue & Customs
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

package v5.retrieveCumulativePeriodSummary

import play.api.libs.json.Reads
import shared.schema.DownstreamReadable
import v5.retrieveCumulativePeriodSummary.def1.model.response.Def1_RetrieveCumulativePeriodSummaryResponse
import v5.retrieveCumulativePeriodSummary.model.response.RetrieveCumulativePeriodSummaryResponse

sealed trait RetrieveCumulativePeriodSummarySchema extends DownstreamReadable[RetrieveCumulativePeriodSummaryResponse]

object RetrieveCumulativePeriodSummarySchema {

  case object Def1 extends RetrieveCumulativePeriodSummarySchema {
    type DownstreamResp = Def1_RetrieveCumulativePeriodSummaryResponse
    val connectorReads: Reads[DownstreamResp] = Def1_RetrieveCumulativePeriodSummaryResponse.reads
  }

}
