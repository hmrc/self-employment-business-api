/*
 * Copyright 2021 HM Revenue & Customs
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

package v1

import v1.models.errors.ErrorWrapper
import v1.models.outcomes.ResponseWrapper
import v1.models.response.listSEPeriodic.{ListSelfEmploymentPeriodicResponse, PeriodDetails}
import v1.models.response.retrieveSEAnnual.RetrieveSelfEmploymentAnnualSummaryResponse
import v1.models.response.retrieveSEPeriodic.RetrieveSelfEmploymentPeriodicResponse

package object services {

  private type ServiceOutcome[A] = Either[ErrorWrapper, ResponseWrapper[A]]

  type DeleteSelfEmploymentAnnualSummaryServiceOutcome = ServiceOutcome[Unit]

  type RetrieveSelfEmploymentAnnualSummaryServiceOutcome = ServiceOutcome[RetrieveSelfEmploymentAnnualSummaryResponse]

  type RetrieveSelfEmploymentPeriodicUpdateServiceOutcome = ServiceOutcome[RetrieveSelfEmploymentPeriodicResponse]

  type ListSelfEmploymentUpdatePeriodsServiceOutcome = ServiceOutcome[ListSelfEmploymentPeriodicResponse[PeriodDetails]]

}
