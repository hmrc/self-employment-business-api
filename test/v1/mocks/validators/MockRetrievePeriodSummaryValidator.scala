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

package v1.mocks.validators

import anyVersion.controllers.requestParsers.validators.RetrievePeriodSummaryValidator
import anyVersion.models.request.retrievePeriodSummary.RetrievePeriodSummaryRawData
import api.models.errors.MtdError
import org.scalamock.handlers.CallHandler1
import org.scalamock.scalatest.MockFactory

trait MockRetrievePeriodSummaryValidator extends MockFactory {

  val mockRetrievePeriodSummaryValidator: RetrievePeriodSummaryValidator = mock[RetrievePeriodSummaryValidator]

  object MockRetrievePeriodSummaryValidator {

    def validate(data: RetrievePeriodSummaryRawData): CallHandler1[RetrievePeriodSummaryRawData, List[MtdError]] = {
      (mockRetrievePeriodSummaryValidator
        .validate(_: RetrievePeriodSummaryRawData))
        .expects(data)
    }

  }

}
