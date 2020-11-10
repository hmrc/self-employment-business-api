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

package v1.mocks.services

import org.scalamock.handlers.CallHandler
import org.scalamock.scalatest.MockFactory
import uk.gov.hmrc.http.HeaderCarrier
import v1.controllers.EndpointLogContext
import v1.models.errors.ErrorWrapper
import v1.models.outcomes.ResponseWrapper
import v1.models.request.createSEPeriodic.CreateSelfEmploymentPeriodicRequest
import v1.models.response.createSEPeriodic.CreateSelfEmploymentPeriodicResponse
import v1.services.CreateSelfEmploymentPeriodicService

import scala.concurrent.{ExecutionContext, Future}

trait MockCreateSelfEmploymentPeriodicService extends MockFactory {

  val mockCreateSelfEmploymentPeriodicService: CreateSelfEmploymentPeriodicService = mock[CreateSelfEmploymentPeriodicService]

  object MockCreateSelfEmploymentPeriodicService {

    def createPeriodic(requestData: CreateSelfEmploymentPeriodicRequest): CallHandler[Future[Either[ErrorWrapper, ResponseWrapper[CreateSelfEmploymentPeriodicResponse]]]] = {
      (mockCreateSelfEmploymentPeriodicService
        .createPeriodic(_: CreateSelfEmploymentPeriodicRequest)(_: HeaderCarrier, _: ExecutionContext, _: EndpointLogContext, _: String))
        .expects(requestData, *, *, *, *)
    }
  }
}
