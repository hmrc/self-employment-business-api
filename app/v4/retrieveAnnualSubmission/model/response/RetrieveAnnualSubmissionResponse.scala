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

package v4.retrieveAnnualSubmission.model.response

import play.api.libs.json.OWrites
import shared.utils.JsonWritesUtil
import v4.retrieveAnnualSubmission.def1.model.response.Def1_RetrieveAnnualSubmissionResponse
import v4.retrieveAnnualSubmission.def2.model.response.Def2_RetrieveAnnualSubmissionResponse
import v4.retrieveAnnualSubmission.def3.model.response.Def3_RetrieveAnnualSubmissionResponse

trait RetrieveAnnualSubmissionResponse

object RetrieveAnnualSubmissionResponse extends JsonWritesUtil {

  implicit val writes: OWrites[RetrieveAnnualSubmissionResponse] = writesFrom {
    case def1: Def1_RetrieveAnnualSubmissionResponse => implicitly[OWrites[Def1_RetrieveAnnualSubmissionResponse]].writes(def1)
    case def2: Def2_RetrieveAnnualSubmissionResponse => implicitly[OWrites[Def2_RetrieveAnnualSubmissionResponse]].writes(def2)
    case def3: Def3_RetrieveAnnualSubmissionResponse => implicitly[OWrites[Def3_RetrieveAnnualSubmissionResponse]].writes(def3)
  }

}
