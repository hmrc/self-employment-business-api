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

package api

import api.models.errors.ErrorWrapper
import api.models.outcomes.ResponseWrapper
import v1.models.response.createPeriodSummary.CreatePeriodSummaryResponse
import v1.models.response.listPeriodSummaries.{ListPeriodSummariesResponse, PeriodDetails}
import v1.models.response.retrieveAnnual.RetrieveAnnualSubmissionResponse
import v1.models.response.retrievePeriodSummary.RetrievePeriodSummaryResponse

package object services {

  type ServiceOutcome[A] = Either[ErrorWrapper, ResponseWrapper[A]]

  type AmendAnnualSubmissionServiceOutcome = ServiceOutcome[Unit]

  type AmendPeriodSummaryServiceOutcome = ServiceOutcome[Unit]

  type CreatePeriodSummaryServiceOutcome = ServiceOutcome[CreatePeriodSummaryResponse]

  type DeleteAnnualSubmissionServiceOutcome = ServiceOutcome[Unit]

  type ListPeriodSummariesServiceOutcome = ServiceOutcome[ListPeriodSummariesResponse[PeriodDetails]]

  type RetrieveAnnualSubmissionServiceOutcome = ServiceOutcome[RetrieveAnnualSubmissionResponse]

  type RetrievePeriodSummaryServiceOutcome = ServiceOutcome[RetrievePeriodSummaryResponse]

}
